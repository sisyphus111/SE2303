package com.se2303.botservice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se2303.botservice.model.ChatMessage;
import com.se2303.botservice.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private AIService aiService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<ChatMessage>> sessionHistory = new ConcurrentHashMap<>();
    private final int MAX_HISTORY_SIZE = 20; // 最大历史消息数量

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        sessionHistory.put(session.getId(), new ArrayList<>());
        System.out.println("WebSocket连接建立: " + session.getId());
        
        // 发送欢迎消息
        ChatMessage welcomeMessage = new ChatMessage(
            UUID.randomUUID().toString(),
            "ai", 
            "assistant",
            "你好！我是小暖，你的心理健康陪伴助手。很高兴认识你！无论是开心的事还是困扰，我都很愿意倾听和陪伴。有什么想和我分享的吗？",
            System.currentTimeMillis()
        );
        
        // 将欢迎消息添加到历史记录
        addToHistory(session.getId(), welcomeMessage);
        sendMessage(session, welcomeMessage);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
            System.out.println("收到消息: " + chatMessage.getContent());

            // 设置消息ID和角色
            if (chatMessage.getId() == null) {
                chatMessage.setId(UUID.randomUUID().toString());
            }
            chatMessage.setRole("user");
            chatMessage.setTimestamp(System.currentTimeMillis());

            // 将用户消息添加到历史记录
            addToHistory(session.getId(), chatMessage);

            // 获取当前会话的历史记录
            List<ChatMessage> history = sessionHistory.get(session.getId());
            if (history == null) {
                history = new ArrayList<>();
                sessionHistory.put(session.getId(), history);
            }

            // 使用原子引用确保结束消息只发送一次
            final boolean[] endMessageSent = {false};

            // 调用AI服务获取流式回复，传递历史消息
            aiService.getStreamingResponse(chatMessage.getContent(), history, (chunk) -> {
                try {
                    ChatMessage aiMessage = new ChatMessage();
                    aiMessage.setType("ai_chunk");
                    aiMessage.setContent(chunk);
                    aiMessage.setTimestamp(System.currentTimeMillis());
                    sendMessage(session, aiMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, (fullResponse) -> {
                // 将完整的AI回复添加到历史记录
                ChatMessage aiCompleteMessage = new ChatMessage(
                    UUID.randomUUID().toString(),
                    "ai",
                    "assistant", 
                    fullResponse,
                    System.currentTimeMillis()
                );
                addToHistory(session.getId(), aiCompleteMessage);
            }, () -> {
                // 流式响应结束，发送结束消息（确保只发送一次）
                synchronized (endMessageSent) {
                    if (!endMessageSent[0]) {
                        try {
                            System.out.println("流式响应结束，发送ai_end消息");
                            ChatMessage endMessage = new ChatMessage();
                            endMessage.setType("ai_end");
                            endMessage.setContent("");
                            endMessage.setTimestamp(System.currentTimeMillis());
                            sendMessage(session, endMessage);
                            endMessageSent[0] = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("ai_end消息已发送，跳过重复发送");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            // 发送错误消息
            ChatMessage errorMessage = new ChatMessage();
            errorMessage.setType("error");
            errorMessage.setContent("抱歉，处理您的消息时出现了错误，请稍后重试。");
            errorMessage.setTimestamp(System.currentTimeMillis());
            sendMessage(session, errorMessage);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        sessionHistory.remove(session.getId());
        System.out.println("WebSocket连接关闭: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket传输错误: " + exception.getMessage());
        sessions.remove(session.getId());
        sessionHistory.remove(session.getId());
    }

    private void addToHistory(String sessionId, ChatMessage message) {
        List<ChatMessage> history = sessionHistory.get(sessionId);
        if (history != null) {
            history.add(message);
            // 限制历史记录大小
            if (history.size() > MAX_HISTORY_SIZE) {
                history.remove(0); // 移除最旧的消息
            }
        }
    }

    private void sendMessage(WebSocketSession session, ChatMessage message) throws IOException {
        if (session.isOpen()) {
            String jsonMessage = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(jsonMessage));
        }
    }
} 