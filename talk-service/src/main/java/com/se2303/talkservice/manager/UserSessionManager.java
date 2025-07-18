package com.se2303.talkservice.manager;

import com.se2303.talkservice.model.OnlineUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 用户会话管理器
 * 基于心跳状态统一管理用户在线状态
 */
@Component
public class UserSessionManager {
    
    private static final Logger logger = LoggerFactory.getLogger(UserSessionManager.class);
    
    @Value("${websocket.heartbeat-interval:30}")
    private int heartbeatInterval; // 心跳间隔（秒）
    
    @Value("${websocket.connection-timeout:300}")
    private int connectionTimeout; // 连接超时时间（秒）
    
    // 用户会话映射: sessionId -> OnlineUser
    private final ConcurrentHashMap<String, OnlineUser> sessions = new ConcurrentHashMap<>();
    
    // 用户心跳状态映射: userId -> HeartbeatStatus
    private final Map<String, HeartbeatStatus> heartbeatStatusMap = new ConcurrentHashMap<>();
    
    /**
     * 心跳状态类 - 同时包含用户会话信息和心跳信息
     */
    private static class HeartbeatStatus {
        private LocalDateTime lastHeartbeat;
        private OnlineUser onlineUser;
        
        public HeartbeatStatus(OnlineUser onlineUser) {
            this.onlineUser = onlineUser;
            this.lastHeartbeat = LocalDateTime.now();
        }
        
        public void updateHeartbeat() {
            this.lastHeartbeat = LocalDateTime.now();
        }
        
        public boolean isExpired(int timeoutSeconds) {
            return LocalDateTime.now().isAfter(lastHeartbeat.plusSeconds(timeoutSeconds));
        }
        
        public String getUserId() {
            return onlineUser.getUserId();
        }
        
        public String getSessionId() {
            return onlineUser.getSessionId();
        }
        
        public OnlineUser getOnlineUser() {
            return onlineUser;
        }
        
        public LocalDateTime getLastHeartbeat() {
            return lastHeartbeat;
        }
    }
    
    // ==================== 用户会话管理 ====================
    
    /**
     * 用户上线
     * @param session WebSocket会话
     * @param userId 用户ID
     * @param userType 用户类型 (user/admin)
     */
    public void userOnline(WebSocketSession session, String userId, String userType) {
        String sessionId = session.getId();
        
        OnlineUser onlineUser = OnlineUser.builder()
                .sessionId(sessionId)
                .userId(userId)
                .userType(userType)
                .session(session)
                .connectTime(LocalDateTime.now())
                .lastActiveTime(LocalDateTime.now())
                .build();
        
        // 存储会话
        sessions.put(sessionId, onlineUser);
        
        // 创建心跳状态（用户上线）
        heartbeatStatusMap.put(userId, new HeartbeatStatus(onlineUser));
        
        logger.info("用户上线: userId={}, sessionId={}, userType={}, 当前在线用户数: {}", 
                   userId, sessionId, userType, heartbeatStatusMap.size());
    }
    
    /**
     * 用户下线
     * @param sessionId 会话ID
     */
    public void userOffline(String sessionId) {
        OnlineUser onlineUser = sessions.remove(sessionId);
        if (onlineUser != null) {
            String userId = onlineUser.getUserId();
            
            // 移除心跳状态（用户下线）
            heartbeatStatusMap.remove(userId);
            
            logger.info("用户下线: userId={}, sessionId={}, 在线时长: {}分钟, 当前在线用户数: {}", 
                       userId, sessionId, 
                       java.time.Duration.between(onlineUser.getConnectTime(), LocalDateTime.now()).toMinutes(),
                       heartbeatStatusMap.size());
        }
    }
    
    /**
     * 添加用户
     * @param onlineUser 在线用户对象
     */
    public void addUser(OnlineUser onlineUser) {
        String sessionId = onlineUser.getSessionId();
        String userId = onlineUser.getUserId();
        
        // 设置连接时间和活跃时间
        if (onlineUser.getConnectTime() == null) {
            onlineUser.setConnectTime(LocalDateTime.now());
        }
        if (onlineUser.getLastActiveTime() == null) {
            onlineUser.setLastActiveTime(LocalDateTime.now());
        }
        
        // 存储会话
        sessions.put(sessionId, onlineUser);
        
        // 创建心跳状态（用户上线）
        heartbeatStatusMap.put(userId, new HeartbeatStatus(onlineUser));
        
        logger.info("用户添加成功: userId={}, username={}, sessionId={}, 当前在线用户数: {}", 
                   userId, onlineUser.getUsername(), sessionId, heartbeatStatusMap.size());
    }
    
    /**
     * 更新用户活跃时间
     * @param sessionId 会话ID
     */
    public void updateUserActivity(String sessionId) {
        OnlineUser onlineUser = sessions.get(sessionId);
        if (onlineUser != null) {
            onlineUser.setLastActiveTime(LocalDateTime.now());
        }
    }
    
    /**
     * 更新用户活跃时间（根据用户ID）
     * @param userId 用户ID
     */
    public void updateUserActivityByUserId(String userId) {
        HeartbeatStatus status = heartbeatStatusMap.get(userId);
        if (status != null) {
            status.getOnlineUser().setLastActiveTime(LocalDateTime.now());
        }
    }
    
    // ==================== 查询方法 ====================
    
    /**
     * 根据会话ID获取用户
     * @param sessionId 会话ID
     * @return 在线用户信息
     */
    public OnlineUser getUserBySessionId(String sessionId) {
        return sessions.get(sessionId);
    }
    
    /**
     * 根据用户ID获取用户
     * @param userId 用户ID
     * @return 在线用户信息
     */
    public OnlineUser getUser(String userId) {
        HeartbeatStatus status = heartbeatStatusMap.get(userId);
        return status != null ? status.getOnlineUser() : null;
    }
    
    /**
     * 检查用户是否在线（基于心跳状态）
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isUserOnline(String userId) {
        HeartbeatStatus status = heartbeatStatusMap.get(userId);
        if (status == null) {
            return false;
        }
        return !status.isExpired(connectionTimeout);
    }
    
    /**
     * 获取所有在线用户
     * @return 在线用户列表
     */
    public List<OnlineUser> getAllOnlineUsers() {
        return heartbeatStatusMap.values().stream()
                .filter(status -> !status.isExpired(connectionTimeout))
                .map(HeartbeatStatus::getOnlineUser)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取在线用户数量
     * @return 在线用户数量
     */
    public int getOnlineUserCount() {
        return (int) heartbeatStatusMap.values().stream()
                .filter(status -> !status.isExpired(connectionTimeout))
                .count();
    }
    
    /**
     * 获取特定类型的在线用户
     * @param userType 用户类型
     * @return 该类型的在线用户列表
     */
    public List<OnlineUser> getOnlineUsersByType(String userType) {
        return heartbeatStatusMap.values().stream()
                .filter(status -> !status.isExpired(connectionTimeout))
                .map(HeartbeatStatus::getOnlineUser)
                .filter(user -> userType.equals(user.getUserType()))
                .collect(Collectors.toList());
    }
    
    /**
     * 移除用户会话
     * @param userId 用户ID
     * @param sessionId 会话ID
     */
    public void removeUserSession(String userId, String sessionId) {
        // 从会话映射中移除
        sessions.remove(sessionId);
        
        // 从心跳状态映射中移除（用户下线）
        heartbeatStatusMap.remove(userId);
        
        logger.info("移除用户会话: userId={}, sessionId={}, 当前在线用户数: {}", 
                   userId, sessionId, heartbeatStatusMap.size());
    }
    
    // ==================== 心跳管理 ====================
    
    /**
     * 处理用户心跳
     * @param userId 用户ID
     * @param sessionId 会话ID
     */
    public void handleHeartbeat(String userId, String sessionId) {
        HeartbeatStatus status = heartbeatStatusMap.get(userId);
        if (status != null) {
            status.updateHeartbeat();
            logger.debug("处理心跳: userId={}, sessionId={}", userId, sessionId);
        } else {
            // 如果心跳状态不存在，重新创建（用户重新上线）
            OnlineUser onlineUser = sessions.get(sessionId);
            if (onlineUser != null) {
                heartbeatStatusMap.put(userId, new HeartbeatStatus(onlineUser));
                logger.debug("重新创建心跳状态: userId={}, sessionId={}", userId, sessionId);
            }
        }
    }
    
    /**
     * 检查用户是否超时
     * @param userId 用户ID
     * @return 是否超时
     */
    public boolean isUserTimeout(String userId) {
        HeartbeatStatus status = heartbeatStatusMap.get(userId);
        if (status == null) {
            return false;
        }
        return status.isExpired(connectionTimeout);
    }
    
    /**
     * 获取用户最后心跳时间
     * @param userId 用户ID
     * @return 最后心跳时间
     */
    public LocalDateTime getLastHeartbeat(String userId) {
        HeartbeatStatus status = heartbeatStatusMap.get(userId);
        return status != null ? status.getLastHeartbeat() : null;
    }
    
    /**
     * 检查用户是否有心跳状态
     * @param userId 用户ID
     * @return 是否有心跳状态
     */
    public boolean hasHeartbeatStatus(String userId) {
        return heartbeatStatusMap.containsKey(userId);
    }
    
    // ==================== 清理和统计 ====================
    
    /**
     * 清理超时用户
     * 定时任务，定期检查并清理超时的用户
     */
    @Scheduled(fixedRate = 10000) // 每10秒执行一次
    public void cleanupTimeoutUsers() {
        try {
            int cleanedCount = 0;
            
            // 检查所有心跳状态
            Iterator<Map.Entry<String, HeartbeatStatus>> iterator = heartbeatStatusMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, HeartbeatStatus> entry = iterator.next();
                String userId = entry.getKey();
                HeartbeatStatus status = entry.getValue();
                
                if (status.isExpired(connectionTimeout)) {
                    logger.warn("用户心跳超时，强制下线: userId={}, sessionId={}, 最后心跳时间={}", 
                               userId, status.getSessionId(), status.getLastHeartbeat());
                    
                    // 强制用户下线
                    sessions.remove(status.getSessionId());
                    iterator.remove();
                    cleanedCount++;
                }
            }
            
            if (cleanedCount > 0) {
                logger.info("清理了 {} 个超时用户", cleanedCount);
            }
            
        } catch (Exception e) {
            logger.error("清理超时用户时发生错误: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 清理非活跃用户
     * @param inactiveMinutes 非活跃时间（分钟）
     * @return 清理的用户数量
     */
    public int cleanupInactiveUsers(long inactiveMinutes) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(inactiveMinutes);
        
        List<String> inactiveUsers = heartbeatStatusMap.values().stream()
                .filter(status -> status.getOnlineUser().getLastActiveTime().isBefore(cutoffTime))
                .map(HeartbeatStatus::getUserId)
                .collect(Collectors.toList());
        
        inactiveUsers.forEach(userId -> {
            HeartbeatStatus status = heartbeatStatusMap.remove(userId);
            if (status != null) {
                sessions.remove(status.getSessionId());
            }
        });
        
        logger.info("清理了 {} 个非活跃用户", inactiveUsers.size());
        return inactiveUsers.size();
    }
    
    /**
     * 获取在线用户统计信息
     * @return 统计信息
     */
    public Map<String, Object> getOnlineUserStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 统计活跃用户
        List<OnlineUser> activeUsers = getAllOnlineUsers();
        stats.put("totalOnlineUsers", activeUsers.size());
        stats.put("uniqueUsers", heartbeatStatusMap.size());
        
        // 按用户类型统计
        Map<String, Long> typeStats = activeUsers.stream()
                .collect(Collectors.groupingBy(OnlineUser::getUserType, Collectors.counting()));
        stats.put("usersByType", typeStats);
        
        return stats;
    }
    
    /**
     * 获取心跳统计信息
     * @return 统计信息
     */
    public Map<String, Object> getHeartbeatStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalHeartbeatUsers", heartbeatStatusMap.size());
        stats.put("heartbeatInterval", heartbeatInterval);
        stats.put("connectionTimeout", connectionTimeout);
        
        // 统计活跃用户数
        long activeUsers = heartbeatStatusMap.values().stream()
                .filter(status -> !status.isExpired(connectionTimeout))
                .count();
        stats.put("activeUsers", activeUsers);
        
        return stats;
    }
    
    /**
     * 获取用户完整状态信息
     * @param userId 用户ID
     * @return 用户状态信息
     */
    public Map<String, Object> getUserStatus(String userId) {
        Map<String, Object> status = new HashMap<>();
        
        // 基于心跳状态判断用户是否在线
        boolean isOnline = isUserOnline(userId);
        status.put("isOnline", isOnline);
        
        if (isOnline) {
            // 获取用户信息
            OnlineUser onlineUser = getUser(userId);
            status.put("user", onlineUser);
            
            // 获取心跳状态
            LocalDateTime lastHeartbeat = getLastHeartbeat(userId);
            boolean isTimeout = isUserTimeout(userId);
            status.put("lastHeartbeat", lastHeartbeat);
            status.put("isTimeout", isTimeout);
        }
        
        return status;
    }
    
    /**
     * 获取完整统计信息
     * @return 包含在线用户和心跳的完整统计信息
     */
    public Map<String, Object> getCompleteStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.putAll(getOnlineUserStats());
        stats.putAll(getHeartbeatStats());
        return stats;
    }
} 