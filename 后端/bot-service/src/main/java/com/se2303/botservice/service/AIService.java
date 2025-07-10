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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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

    @Autowired
    private AIPromptConfig promptConfig;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

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
        logInfo("=== AI服务调用开始 ===");
        logInfo("时间: " + LocalDateTime.now().format(timeFormatter));
        logInfo("API URL: " + apiUrl);
        logInfo("模型: " + model);
        logInfo("超时时间: " + timeoutSeconds + "秒");
        logInfo("用户消息: " + userMessage);
        logInfo("历史消息数量: " + (history != null ? history.size() : 0));
        logInfo("场景: " + (scenario != null ? scenario : "默认"));
        logInfo("性格: " + (personality != null ? personality : "默认"));
        
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
                    .doOnNext(chunk -> logInfo("收到原始chunk: " + chunk))
                    .doOnError(error -> logError("流式响应出错", error))
                    .doOnComplete(() -> logInfo("流式响应完成"))
                    .subscribe(
                        chunk -> {
                            try {
                                logInfo("处理chunk: " + chunk);
                                // 解析阿里云百炼格式的数据
                                String content = parseStreamChunk(chunk);
                                if (content != null && !content.isEmpty()) {
                                    logInfo("解析出内容: '" + content + "'");
                                    fullResponse.append(content);
                                    onChunk.accept(content);
                                } else if (content == null) {
                                    logInfo("收到结束信号，完成流式响应");
                                    // 调用完整响应回调
                                    onComplete.accept(fullResponse.toString());
                                } else {
                                    logInfo("chunk无有效内容或为控制信息");
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
            logInfo("开始解析chunk: " + chunk);
            
            // 处理特殊的结束标志
            if ("[DONE]".equals(chunk.trim())) {
                logInfo("收到流式响应结束标志");
                return null;
            }
            
            // 阿里云百炼直接返回JSON，不是SSE格式
            JsonNode jsonNode = objectMapper.readTree(chunk);
            logInfo("解析的JSON节点: " + jsonNode.toString());
            
            JsonNode choices = jsonNode.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                logInfo("找到choices数组，长度: " + choices.size());
                JsonNode choice = choices.get(0);
                JsonNode delta = choice.get("delta");
                
                if (delta != null) {
                    logInfo("找到delta对象: " + delta.toString());
                    if (delta.has("content")) {
                        String content = delta.get("content").asText();
                        logInfo("提取的内容: '" + content + "'");
                        
                        // 检查是否是结束标志
                        JsonNode finishReason = choice.get("finish_reason");
                        if (finishReason != null && !"null".equals(finishReason.asText()) && !finishReason.isNull()) {
                            logInfo("检测到结束标志: " + finishReason.asText());
                        }
                        
                        return content;
                    } else {
                        logInfo("delta中没有content字段");
                        
                        // 检查是否是结束信号
                        JsonNode finishReason = choice.get("finish_reason");
                        if (finishReason != null && !"null".equals(finishReason.asText()) && !finishReason.isNull()) {
                            logInfo("检测到结束标志: " + finishReason.asText());
                            return null; // 表示结束
                        }
                    }
                } else {
                    logInfo("choices[0]中没有delta字段");
                }
            } else {
                logInfo("没有找到有效的choices数组");
            }
            
        } catch (Exception e) {
            logError("解析JSON时出错，原始数据: " + chunk, e);
        }
        return "";
    }

    // 模拟流式响应，用于开发和测试
    private void simulateStreamingResponse(String userMessage, Consumer<String> onChunk, Consumer<String> onComplete, Runnable onFinish) {
        logInfo("=== 开始模拟流式响应 ===");
        new Thread(() -> {
            try {
                String response = generateMockResponse(userMessage);
                logInfo("生成的模拟响应: " + response);
                String[] words = response.split("(?<=\\s)"); // 按空格分割但保留空格
                
                logInfo("分割成 " + words.length + " 个词");
                for (int i = 0; i < words.length; i++) {
                    String word = words[i];
                    logInfo("发送第" + (i+1) + "个词: '" + word + "'");
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

    private String generateMockResponse(String userMessage) {
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
} 