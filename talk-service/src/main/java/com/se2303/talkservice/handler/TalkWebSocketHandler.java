package com.se2303.talkservice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.se2303.talkservice.dto.WsContent;
import com.se2303.talkservice.manager.UserSessionManager;
import com.se2303.talkservice.model.OnlineUser;
import com.se2303.talkservice.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.se2303.talkservice.model.Message;

/**
 * WebSocket 处理器，负责处理聊天消息的收发和心跳检测
 */
@Component
public class TalkWebSocketHandler implements WebSocketHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(TalkWebSocketHandler.class);
    
    private final ObjectMapper objectMapper;
    
    public TalkWebSocketHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        // 配置时间序列化为ISO字符串格式，而不是数组格式
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    // 存储会话ID到WebSocketSession的映射
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    @Autowired
    private UserSessionManager userSessionManager;
    
    @Autowired
    private MessageService messageService;
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            // 从URL参数中提取用户信息
            URI uri = session.getUri();
            String query = uri != null ? uri.getQuery() : null;
            
            if (query == null) {
                logger.warn("连接缺少必要参数，关闭连接: {}", session.getId());
                session.close(CloseStatus.BAD_DATA.withReason("Missing required parameters"));
                return;
            }
            
            Map<String, String> params = parseQuery(query);
            String userId = params.get("x-userId"); // 从x-userId参数获取userId
            
            if (userId == null) {
                logger.warn("缺少x-userId参数，关闭连接: {}", session.getId());
                session.close(CloseStatus.BAD_DATA.withReason("Missing x-userId parameter"));
                return;
            }
            
            // 存储会话
            sessions.put(session.getId(), session);
            
            // 添加到用户会话管理器
            OnlineUser onlineUser = new OnlineUser();
            onlineUser.setUserId(userId);
            onlineUser.setUsername(userId); // 暂时使用userId作为username
            onlineUser.setSessionId(session.getId());
            onlineUser.setStatus("online");
            
            userSessionManager.addUser(onlineUser);
            
            logger.info("用户连接成功: userId={}, sessionId={}", 
                       userId, session.getId());
            
        } catch (Exception e) {
            logger.error("处理连接建立时发生错误: {}", e.getMessage(), e);
            session.close(CloseStatus.SERVER_ERROR);
        }
    }
    
    @Override
    public void handleMessage(WebSocketSession session, org.springframework.web.socket.WebSocketMessage<?> message) throws Exception {
        try {
            String jsonMessage = null;
            if (message instanceof TextMessage) {
                jsonMessage = ((TextMessage) message).getPayload();
            } else {
                logger.warn("收到非文本消息，忽略: {}", message.getClass().getSimpleName());
                return;
            }
            
            // 检查是否是心跳包
            if ("heart".equals(jsonMessage.trim())) {
                handleHeartbeat(session);
                return;
            }
            
            // 处理聊天消息
            handleChatMessage(session, jsonMessage);
            
        } catch (Exception e) {
            logger.error("处理消息时发生错误: {}", e.getMessage(), e);
        }
    }
    
    private void handleHeartbeat(WebSocketSession session) {
        try {
            // 获取用户信息
            OnlineUser user = userSessionManager.getUserBySessionId(session.getId());
            if (user != null) {
                // 更新用户活跃时间并处理心跳
                userSessionManager.updateUserActivityByUserId(user.getUserId());
                userSessionManager.handleHeartbeat(user.getUserId(), session.getId());
                
                logger.debug("收到心跳包: userId={}, sessionId={}", user.getUserId(), session.getId());
            }
        } catch (Exception e) {
            logger.warn("处理心跳包失败: {}", e.getMessage());
        }
    }
    
    private void handleChatMessage(WebSocketSession session, String jsonMessage) {
        try {
            // 解析消息
            Map<String, Object> messageData = objectMapper.readValue(jsonMessage, Map.class);
            String content = (String) messageData.get("content");
            
            // 获取发送者信息
            OnlineUser sender = userSessionManager.getUserBySessionId(session.getId());
            if (sender == null) {
                logger.warn("未找到发送者信息，sessionId: {}", session.getId());
                return;
            }
            
            // 保存消息到数据库
            Long fromUserId = Long.valueOf(sender.getUserId());
            Message savedMessage = messageService.saveMessage(fromUserId, content);
            
            // 构建WsContent对象用于推送
            WsContent wsContent = new WsContent();
            WsContent.ContentData contentData = new WsContent.ContentData();
            contentData.setId(savedMessage.getId()); // 设置消息ID
            contentData.setFrom_userId(fromUserId);
            contentData.setContent(content);
            contentData.setCreatedAt(savedMessage.getCreatedAt()); // 使用数据库中的创建时间
            wsContent.setContent(contentData);
            
            // 推送给所有在线用户
            broadcastToAllUsers(wsContent);
            
            logger.info("处理聊天消息成功: messageId={}, senderId={}, content={}, 在线用户数={}", 
                       savedMessage.getId(), sender.getUserId(), content, sessions.size());
                       
        } catch (Exception e) {
            logger.error("处理聊天消息失败: {}", e.getMessage(), e);
        }
    }
    
    public void broadcastToAllUsers(WsContent wsContent) {

        logger.info("开始把wsContent推送给所有在线用户，WsContent为：" + wsContent);
        
        try {
            String jsonMessage = objectMapper.writeValueAsString(wsContent);
            TextMessage textMessage = new TextMessage(jsonMessage);
            
            int successCount = 0;
            int failCount = 0;
            
            // 遍历所有在线用户的session，通过websocket推送给这些在线用户
            for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
                String sessionId = entry.getKey();
                WebSocketSession session = entry.getValue();
                
                if (session != null && session.isOpen()) {
                    try {
                        session.sendMessage(textMessage);
                        successCount++;
                        logger.debug("消息推送成功: sessionId={}", sessionId);
                    } catch (Exception e) {
                        failCount++;
                        logger.error("推送消息失败: sessionId={}, error={}", sessionId, e.getMessage());
                        // 如果推送失败，可以考虑清理这个session
                        sessions.remove(sessionId);
                    }
                } else {
                    // 如果session已关闭，从映射中移除
                    sessions.remove(sessionId);
                    logger.debug("移除已关闭的session: sessionId={}", sessionId);
                }
            }
            
            logger.info("消息广播完成: 成功={}, 失败={}, 总在线用户数={}", 
                       successCount, failCount, sessions.size());
            
        } catch (Exception e) {
            logger.error("广播消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 从HTTP API调用时推送消息给所有在线用户
     * @param wsContent 要推送的消息内容
     */
    public void broadcastMessageFromHttp(WsContent wsContent) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(wsContent);
            TextMessage textMessage = new TextMessage(jsonMessage);
            
            int successCount = 0;
            int failCount = 0;
            
            // 遍历所有在线用户的session，通过websocket推送给这些在线用户
            for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
                String sessionId = entry.getKey();
                WebSocketSession session = entry.getValue();
                
                if (session != null && session.isOpen()) {
                    try {
                        session.sendMessage(textMessage);
                        successCount++;
                        logger.debug("HTTP API消息推送成功: sessionId={}", sessionId);
                    } catch (Exception e) {
                        failCount++;
                        logger.error("HTTP API推送消息失败: sessionId={}, error={}", sessionId, e.getMessage());
                        // 如果推送失败，清理这个session
                        sessions.remove(sessionId);
                    }
                } else {
                    // 如果session已关闭，从映射中移除
                    sessions.remove(sessionId);
                    logger.debug("移除已关闭的session: sessionId={}", sessionId);
                }
            }
            
            logger.info("HTTP API消息广播完成: 成功={}, 失败={}, 总在线用户数={}", 
                       successCount, failCount, sessions.size());
            
        } catch (Exception e) {
            logger.error("HTTP API广播消息失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("WebSocket传输错误: sessionId={}, error={}", 
                   session.getId(), exception.getMessage());
        cleanupSession(session);
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        logger.info("WebSocket连接关闭: sessionId={}, status={}", 
                  session.getId(), closeStatus);
        cleanupSession(session);
    }
    
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    
    private void cleanupSession(WebSocketSession session) {
        try {
            // 从会话映射中移除
            sessions.remove(session.getId());
            
            // 从用户会话管理器中移除
            OnlineUser user = userSessionManager.getUserBySessionId(session.getId());
            if (user != null) {
                // 移除用户会话
                userSessionManager.userOffline(session.getId());
                
                logger.info("用户离线: userId={}, sessionId={}", 
                          user.getUserId(), session.getId());
            }
            
        } catch (Exception e) {
            logger.error("清理会话时发生错误: {}", e.getMessage());
        }
    }
    
    private Map<String, String> parseQuery(String query) {
        Map<String, String> params = new java.util.HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }
    
    public int getActiveSessionCount() {
        return sessions.size();
    }
} 