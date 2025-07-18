package com.se2303.botservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.se2303.botservice.config.AIPromptConfig;
import com.se2303.botservice.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


@Service
public class AIService {

    @Value("${ai.api.url:https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions}")
    private String apiUrl;

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.model:qwen-turbo}")
    private String model;

    @Value("${ai.api.timeout:30}")
    private int timeoutSeconds;

    @Value("${alert.service.url:http://localhost:8086}")
    private String alertServiceUrl;

    @Autowired
    private AIPromptConfig promptConfig;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    // 对话开始时间记录
    private LocalDateTime conversationStartTime;
    private static final long MAX_CONVERSATION_DURATION_MINUTES =1;
    
    // 记录是否已经发送过超时警告，避免重复发送
    private boolean timeoutAlertSent = false;
    
    // 当前用户信息
    private String currentUserId = "unknown";
    private String currentSessionId = "unknown";

    public AIService() {
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
        this.objectMapper = new ObjectMapper();
        logInfo("AIService初始化完成");
    }

    public void getStreamingResponse(String userMessage, List<ChatMessage> history, Consumer<String> onChunk, Consumer<String> onComplete, Runnable onFinish) {
        getStreamingResponse(userMessage, history, null, null, onChunk, onComplete, onFinish);
    }

    public void getStreamingResponse(String userMessage, List<ChatMessage> history, String scenario, String personality, Consumer<String> onChunk, Consumer<String> onComplete, Runnable onFinish) {
        // 检查对话持续时间
        checkConversationDuration();
        
        logInfo("=== AI服务调用开始 ===");
        logInfo("时间: " + LocalDateTime.now().format(timeFormatter));
        logInfo("API URL: " + apiUrl);
        logInfo("模型: " + model);
        logInfo("超时时间: " + timeoutSeconds + "秒");
        logInfo("用户消息: " + userMessage);
        logInfo("历史消息数量: " + (history != null ? history.size() : 0));
        logInfo("场景: " + (scenario != null ? scenario : "默认"));
        logInfo("性格: " + (personality != null ? personality : "默认"));
        
        // 初始化对话开始时间（如果是第一次对话）
        if (conversationStartTime == null) {
            conversationStartTime = LocalDateTime.now();
            logInfo("对话开始时间已记录: " + conversationStartTime.format(timeFormatter));
        }
        
        // 检查API密钥
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logInfo("API密钥未配置，使用模拟响应");
            simulateStreamingResponse(userMessage, onChunk, onComplete, onFinish);
            return;
        }
        
        logInfo("API密钥已配置，前缀: " + apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
        
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("stream", true);
            
            // 构建消息列表，包含系统提示词、历史对话和当前用户消息
            List<Map<String, Object>> messages = new ArrayList<>();
            
            // 获取系统提示词
            String systemPrompt = promptConfig.getFullPrompt(scenario, personality);
            
            // 添加系统提示词（只在没有历史消息时添加，避免重复）
            if (history == null || history.isEmpty()) {
                Map<String, Object> systemMessage = new HashMap<>();
                systemMessage.put("role", "system");
                systemMessage.put("content", systemPrompt);
                messages.add(systemMessage);
                logInfo("添加系统提示词: " + systemPrompt.substring(0, Math.min(100, systemPrompt.length())) + "...");
            }
            
            // 添加历史消息
            if (history != null && !history.isEmpty()) {
                for (ChatMessage msg : history) {
                    // 只添加完整的对话消息，跳过流式响应的chunk
                    if ("user".equals(msg.getRole()) || "assistant".equals(msg.getRole())) {
                        Map<String, Object> messageMap = new HashMap<>();
                        messageMap.put("role", msg.getRole());
                        messageMap.put("content", msg.getContent());
                        messages.add(messageMap);
                        logInfo("添加历史消息 - 角色: " + msg.getRole() + ", 内容: " + msg.getContent().substring(0, Math.min(50, msg.getContent().length())) + "...");
                    }
                }
            }
            
            // 添加当前用户消息
            Map<String, Object> currentMessage = new HashMap<>();
            currentMessage.put("role", "user");
            currentMessage.put("content", userMessage);
            messages.add(currentMessage);
            
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 1000);
            requestBody.put("temperature", 0.7);

            logInfo("构建的请求体: " + objectMapper.writeValueAsString(requestBody));
            logInfo("开始发送HTTP请求...");

            // 用于收集完整的AI响应
            StringBuilder fullResponse = new StringBuilder();

            // 发送流式请求
            webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .timeout(java.time.Duration.ofSeconds(timeoutSeconds))
                    .doOnSubscribe(subscription -> logInfo("订阅流式响应"))
                    .doOnError(error -> logError("流式响应出错", error))
                    .doOnComplete(() -> logInfo("流式响应完成"))
                    .subscribe(
                        chunk -> {
                            try {
                                // 解析阿里云百炼格式的数据
                                String content = parseStreamChunk(chunk);
                                if (content != null && !content.isEmpty()) {
                                    fullResponse.append(content);
                                    onChunk.accept(content);
                                } else if (content == null) {
                                    logInfo("收到结束信号，完成流式响应");
                                    // 调用完整响应回调
                                    onComplete.accept(fullResponse.toString());
                                }
                            } catch (Exception e) {
                                logError("解析chunk时出错", e);
                            }
                        },
                        error -> {
                            logError("=== API调用错误详情 ===", error);
                            
                            if (error instanceof WebClientResponseException) {
                                WebClientResponseException webError = (WebClientResponseException) error;
                                logError("HTTP状态码: " + webError.getStatusCode());
                                logError("响应头: " + webError.getHeaders());
                                logError("响应体: " + webError.getResponseBodyAsString());
                            }
                            
                            logError("错误堆栈:", error);
                            onChunk.accept("抱歉，AI服务调用失败: " + error.getMessage());
                            onFinish.run();
                        },
                        () -> {
                            logInfo("API调用成功完成");
                            onFinish.run();
                        }
                    );
                    
        } catch (Exception e) {
            logError("构建请求时出错", e);
            onChunk.accept("抱歉，构建API请求时出错: " + e.getMessage());
            onFinish.run();
        }
    }

    // 保持向后兼容的方法
    public void getStreamingResponse(String userMessage, Consumer<String> onChunk, Runnable onComplete) {
        getStreamingResponse(userMessage, null, onChunk, (fullResponse) -> {}, onComplete);
    }

    private String parseStreamChunk(String chunk) {
        try {
            // 处理特殊的结束标志
            if ("[DONE]".equals(chunk.trim())) {
                logInfo("收到流式响应结束标志");
                return null;
            }
            
            // 阿里云百炼直接返回JSON，不是SSE格式
            JsonNode jsonNode = objectMapper.readTree(chunk);
            
            JsonNode choices = jsonNode.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode choice = choices.get(0);
                JsonNode delta = choice.get("delta");
                
                if (delta != null) {
                    if (delta.has("content")) {
                        String content = delta.get("content").asText();
                        
                        // 检查是否是结束标志
                        JsonNode finishReason = choice.get("finish_reason");
                        if (finishReason != null && !"null".equals(finishReason.asText()) && !finishReason.isNull()) {
                            logInfo("检测到结束标志: " + finishReason.asText());
                        }
                        
                        return content;
                    } else {
                        // 检查是否是结束信号
                        JsonNode finishReason = choice.get("finish_reason");
                        if (finishReason != null && !"null".equals(finishReason.asText()) && !finishReason.isNull()) {
                            logInfo("检测到结束标志: " + finishReason.asText());
                            return null; // 表示结束
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            logError("解析JSON时出错，原始数据: " + chunk, e);
        }
        return "";
    }

    // 模拟流式响应，用于开发和测试
    private void simulateStreamingResponse(String userMessage, Consumer<String> onChunk, Consumer<String> onComplete, Runnable onFinish) {
        // 检查对话持续时间
        checkConversationDuration();
        
        logInfo("=== 开始模拟流式响应 ===");
        new Thread(() -> {
            try {
                String response = generateMockResponse(userMessage);
                String[] words = response.split("(?<=\\s)"); // 按空格分割但保留空格
                
                for (int i = 0; i < words.length; i++) {
                    String word = words[i];
                    onChunk.accept(word);
                    Thread.sleep(50 + (int)(Math.random() * 100)); // 模拟网络延迟
                }
                
                Thread.sleep(500); // 最后暂停一下
                logInfo("模拟响应发送完成");
                onComplete.accept(response); // 传递完整响应
                onFinish.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logError("模拟响应被中断", e);
            } catch (Exception e) {
                logError("模拟响应出错", e);
                onFinish.run();
            }
        }).start();
    }

    public String generateMockResponse(String userMessage) {
        // 根据用户消息生成相应的模拟回复，符合心理健康陪伴助手的角色
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("难过") || lowerMessage.contains("伤心") || lowerMessage.contains("不开心") || lowerMessage.contains("抑郁")) {
            return "小暖感受到你现在的心情不太好。记住，每个人都会有情绪低落的时候，这是完全正常的。你想聊聊具体发生了什么吗？我在这里倾听，不会评判你。如果这种情绪持续很久，建议你也可以考虑寻求专业的心理咨询师帮助。";
        } else if (lowerMessage.contains("开心") || lowerMessage.contains("高兴") || lowerMessage.contains("快乐")) {
            return "太好了！看到你心情不错小暖也很开心。能分享是什么让你这么开心吗？分享快乐会让快乐加倍哦！";
        } else if (lowerMessage.contains("压力") || lowerMessage.contains("焦虑") || lowerMessage.contains("紧张")) {
            return "压力确实会让人感到困扰。小暖建议你可以试试深呼吸：慢慢吸气数到4，然后慢慢呼气数到6。你愿意和我分享是什么让你感到压力吗？我们可以一起想想解决的方法。记住，一步一步来，不要给自己太大压力。";
        } else if (lowerMessage.contains("谢谢") || lowerMessage.contains("感谢")) {
            return "不用谢！能陪伴你是小暖的荣幸。希望我们的对话能给你带来一些温暖和力量。有什么其他想聊的吗？我随时在这里。";
        } else if (lowerMessage.contains("你好") || lowerMessage.contains("hello") || lowerMessage.contains("小暖")) {
            return "你好！我是小暖，你的心理健康陪伴助手。很高兴和你聊天！今天过得怎么样？有什么想和我分享的吗？无论是开心的事还是困扰，我都很愿意倾听。";
        } else if (lowerMessage.contains("自杀") || lowerMessage.contains("不想活") || lowerMessage.contains("结束生命")) {
            return "小暖非常担心你现在的状态。你的生命非常宝贵，值得被珍惜。如果你有这些想法，请立即寻求专业帮助：可以拨打全国24小时心理援助热线400-161-9995，或者联系你信任的朋友、家人。你并不孤单，有很多人关心你。";
        } else if (lowerMessage.contains("失眠") || lowerMessage.contains("睡不着")) {
            return "失眠确实很困扰人。小暖建议你可以试试：睡前1小时不要看手机，可以听些轻音乐，喝杯温热的牛奶，或者做些简单的放松练习。如果失眠持续影响你的生活，建议咨询医生。";
        } else {
            String[] responses = {
                "小暖理解你的感受，能具体说说吗？我在这里认真倾听。",
                "这确实是一个值得思考的问题。你觉得呢？小暖很想听听你的想法。",
                "听起来很有趣，请继续分享你的想法。我很感兴趣。",
                "小暖在这里陪伴着你，随时可以聊天。你并不孤单。",
                "你的想法很有见地，小暖很想听听更多。",
                "生活中总会有起起落落，重要的是保持积极的心态。小暖相信你有能力面对挑战。",
                "每个人都有自己的节奏，不用着急。小暖支持你按照自己的步调前进。",
                "你的感受是正常的，小暖完全理解。不要压抑自己的情绪。",
                "让我们一起面对这个挑战吧。小暖会一直陪伴着你。",
                "你的坚持让小暖很感动。继续保持下去！我相信你能做到。"
            };
            return responses[(int)(Math.random() * responses.length)];
        }
    }
    
    private void logInfo(String message) {
        System.out.println("[AIService-INFO] " + LocalDateTime.now().format(timeFormatter) + " - " + message);
    }
    
    private void logError(String message, Throwable error) {
        System.err.println("[AIService-ERROR] " + LocalDateTime.now().format(timeFormatter) + " - " + message);
        if (error != null) {
            error.printStackTrace();
        }
    }
    
    private void logError(String message) {
        logError(message, null);
    }
    
    /**
     * 检查对话持续时间，如果超过限制则输出警告信息并发送警报
     */
    private void checkConversationDuration() {
        logInfo("=== 检查对话持续时间 ===");
        logInfo("conversationStartTime: " + (conversationStartTime != null ? conversationStartTime.format(timeFormatter) : "null"));
        logInfo("timeoutAlertSent: " + timeoutAlertSent);
        logInfo("currentUserId: " + currentUserId);
        logInfo("currentSessionId: " + currentSessionId);
        
        if (conversationStartTime != null) {
            LocalDateTime now = LocalDateTime.now();
            long durationMinutes = java.time.Duration.between(conversationStartTime, now).toMinutes();
            
            logInfo("当前对话持续时间: " + durationMinutes + " 分钟");
            logInfo("最大允许时间: " + MAX_CONVERSATION_DURATION_MINUTES + " 分钟");
            
            if (durationMinutes >= MAX_CONVERSATION_DURATION_MINUTES) {
                logInfo("超过时间限制，准备发送警报");
                
                String warningMessage = String.format(
                    "⚠️ 对话持续时间警告 ⚠️\n" +
                    "当前对话已持续 %d 分钟，超过了建议的 %d 分钟限制。\n" +
                    "建议您：\n" +
                    "1. 适当休息，避免长时间使用电子设备\n" +
                    "2. 如果感到疲劳，请暂停对话\n" +
                    "3. 如果需要继续，建议稍后重新开始对话\n" +
                    "对话开始时间: %s",
                    durationMinutes, MAX_CONVERSATION_DURATION_MINUTES, 
                    conversationStartTime.format(timeFormatter)
                );
                
                logInfo("=== 对话超时警告 ===");
                logInfo(warningMessage);
                System.err.println("[AIService-WARNING] " + LocalDateTime.now().format(timeFormatter) + " - " + warningMessage);
                System.err.println("[AIService-WARNING] " + LocalDateTime.now().format(timeFormatter) + " - 用户ID: " + currentUserId + ", 会话ID: " + currentSessionId + ", 持续时间: " + durationMinutes + " 分钟");
                
                // 发送AI超时警报到alert-service
                if (!timeoutAlertSent) {
                    logInfo("开始发送超时警报...");
                    System.out.println("[AIService-INFO] " + LocalDateTime.now().format(timeFormatter) + " - 开始向alert-service发送超时警报");
                    sendTimeoutAlert(durationMinutes);
                    timeoutAlertSent = true;
                    logInfo("警报发送标志已设置为true");
                } else {
                    logInfo("警报已发送过，跳过重复发送");
                    System.out.println("[AIService-INFO] " + LocalDateTime.now().format(timeFormatter) + " - 超时警报已发送过，跳过重复发送");
                }
            }
        }
    }
    
    /**
     * 重置对话开始时间（可用于新对话开始时调用）
     */
    public void resetConversationTimer() {
        conversationStartTime = LocalDateTime.now();
        timeoutAlertSent = false; // 重置警报发送状态
        logInfo("对话计时器已重置，新对话开始时间: " + conversationStartTime.format(timeFormatter));
    }
    
    /**
     * 获取当前对话持续时间（分钟）
     */
    public long getCurrentConversationDurationMinutes() {
        if (conversationStartTime == null) {
            return 0;
        }
        return java.time.Duration.between(conversationStartTime, LocalDateTime.now()).toMinutes();
    }
    
    /**
     * 发送AI超时警报到alert-service
     */
    private void sendTimeoutAlert(long durationMinutes) {
        try {
            logInfo("=== 开始发送AI超时警报 ===");
            logInfo("Alert Service URL: " + alertServiceUrl);
            logInfo("当前用户ID: " + currentUserId);
            logInfo("当前会话ID: " + currentSessionId);
            logInfo("超时持续时间: " + durationMinutes + " 分钟");
            
            // 构建警报请求 - 按照curl示例格式
            Map<String, Object> alertRequest = new HashMap<>();
            alertRequest.put("userId", currentUserId);
            alertRequest.put("alertType", "AI_TIMEOUT");
            alertRequest.put("alertLevel", "MEDIUM");
            alertRequest.put("description", String.format("AI对话超时警告 - 对话持续%d分钟", durationMinutes));
            alertRequest.put("operationDetails", String.format(
                "对话开始时间: %s\n当前持续时间: %d 分钟\n建议限制: %d 分钟\n服务: bot-service",
                conversationStartTime.format(timeFormatter),
                durationMinutes,
                MAX_CONVERSATION_DURATION_MINUTES
            ));
            
            String requestJson = objectMapper.writeValueAsString(alertRequest);
            logInfo("警报请求内容: " + requestJson);
            
            // 使用同步调用确保请求发送完成
            try {
                logInfo("尝试使用WebClient发送警报到: " + alertServiceUrl + "/api/alert/create");
                String response = webClient.post()
                        .uri(alertServiceUrl + "/api/alert/create")
                        .header("Content-Type", "application/json")
                        .bodyValue(alertRequest)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(10))  // 设置超时时间
                        .block();  // 同步等待响应
                
                logInfo("AI超时警报发送成功: " + response);
                System.out.println("[AIService-SUCCESS] " + LocalDateTime.now().format(timeFormatter) + " - AI超时警报发送成功");
                
            } catch (Exception httpError) {
                logError("发送AI超时警报HTTP请求失败", httpError);
                System.err.println("[AIService-ERROR] " + LocalDateTime.now().format(timeFormatter) + " - WebClient发送警报失败: " + httpError.getMessage());
                
                // 尝试使用 RestTemplate 作为备选方案
                try {
                    logInfo("尝试使用RestTemplate作为备选方案发送警报");
                    RestTemplate restTemplate = new RestTemplate();
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
                    
                    ResponseEntity<String> restResponse = restTemplate.postForEntity(
                        alertServiceUrl + "/api/alert/create", 
                        entity, 
                        String.class
                    );
                    
                    logInfo("使用RestTemplate发送警报成功: " + restResponse.getBody());
                    System.out.println("[AIService-SUCCESS] " + LocalDateTime.now().format(timeFormatter) + " - RestTemplate发送警报成功");
                    
                } catch (Exception restError) {
                    logError("RestTemplate发送警报也失败", restError);
                    System.err.println("[AIService-ERROR] " + LocalDateTime.now().format(timeFormatter) + " - RestTemplate发送警报也失败: " + restError.getMessage());
                    System.err.println("[AIService-ERROR] " + LocalDateTime.now().format(timeFormatter) + " - 所有发送警报的方法都失败了，请检查alert-service是否正常运行");
                    
                    // 输出详细的错误堆栈信息
                    System.err.println("[AIService-ERROR] WebClient错误详情:");
                    httpError.printStackTrace();
                    System.err.println("[AIService-ERROR] RestTemplate错误详情:");
                    restError.printStackTrace();
                }
            }
                    
        } catch (Exception e) {
            logError("构建AI超时警报请求时出错", e);
            System.err.println("[AIService-ERROR] " + LocalDateTime.now().format(timeFormatter) + " - 构建警报请求时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 检查alert-service健康状态
     */
    public boolean checkAlertServiceHealth() {
        try {
            String response = webClient.get()
                    .uri(alertServiceUrl + "/api/alert/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            logInfo("Alert Service 健康检查响应: " + response);
            return response != null && response.contains("UP");
        } catch (Exception e) {
            logError("Alert Service 健康检查失败", e);
            return false;
        }
    }
    
    /**
     * 设置当前用户信息
     */
    public void setCurrentUser(String userId, String sessionId) {
        this.currentUserId = userId;
        this.currentSessionId = sessionId;
        logInfo("设置当前用户 - 用户ID: " + userId + ", 会话ID: " + sessionId);
    }
    
    /**
     * 获取当前用户ID
     */
    public String getCurrentUserId() {
        return currentUserId;
    }
    
    /**
     * 获取当前会话ID
     */
    public String getCurrentSessionId() {
        return currentSessionId;
    }

    public void forceTimeoutAlert() {
        logInfo("=== 强制触发超时警报（测试用） ===");
        if (conversationStartTime == null) {
            conversationStartTime = LocalDateTime.now().minusMinutes(10);
        }
        timeoutAlertSent = false;
        sendTimeoutAlert(10);
    }

    /**
     * 测试JSON格式 - 用于测试目的
     */
    public String testJsonFormat() {
        try {
            Map<String, Object> testData = new HashMap<>();
            testData.put("userId", currentUserId);
            testData.put("alertType", "AI_TIMEOUT");
            testData.put("alertLevel", "MEDIUM");
            testData.put("description", "测试AI超时警报");
            testData.put("operationDetails", "这是一个测试警报");
            
            return objectMapper.writeValueAsString(testData);
        } catch (Exception e) {
            logError("生成测试JSON时出错", e);
            return "{\"error\": \"生成JSON失败: " + e.getMessage() + "\"}";
        }
    }
} 