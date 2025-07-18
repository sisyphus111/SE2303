package com.se2303.talkservice.model;

import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;

/**
 * 在线用户模型
 */
public class OnlineUser {
    
    private String sessionId;           // WebSocket会话ID
    private String userId;              // 用户ID
    private String username;            // 用户名
    private String userType;            // 用户类型 (user/admin/counselor)
    private String status;              // 用户状态 (online/offline/away)
    private WebSocketSession session;   // WebSocket会话对象
    private LocalDateTime connectTime;  // 连接时间
    private LocalDateTime lastActiveTime; // 最后活跃时间
    
    // 构造函数
    public OnlineUser() {}
    
    private OnlineUser(Builder builder) {
        this.sessionId = builder.sessionId;
        this.userId = builder.userId;
        this.username = builder.username;
        this.userType = builder.userType;
        this.status = builder.status;
        this.session = builder.session;
        this.connectTime = builder.connectTime;
        this.lastActiveTime = builder.lastActiveTime;
    }
    
    // Builder模式
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String sessionId;
        private String userId;
        private String username;
        private String userType;
        private String status;
        private WebSocketSession session;
        private LocalDateTime connectTime;
        private LocalDateTime lastActiveTime;
        
        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }
        
        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        
        public Builder userType(String userType) {
            this.userType = userType;
            return this;
        }
        
        public Builder status(String status) {
            this.status = status;
            return this;
        }
        
        public Builder session(WebSocketSession session) {
            this.session = session;
            return this;
        }
        
        public Builder connectTime(LocalDateTime connectTime) {
            this.connectTime = connectTime;
            return this;
        }
        
        public Builder lastActiveTime(LocalDateTime lastActiveTime) {
            this.lastActiveTime = lastActiveTime;
            return this;
        }
        
        public OnlineUser build() {
            return new OnlineUser(this);
        }
    }
    
    // Getter和Setter方法
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public WebSocketSession getSession() {
        return session;
    }
    
    public void setSession(WebSocketSession session) {
        this.session = session;
    }
    
    public LocalDateTime getConnectTime() {
        return connectTime;
    }
    
    public void setConnectTime(LocalDateTime connectTime) {
        this.connectTime = connectTime;
    }
    
    public LocalDateTime getLastActiveTime() {
        return lastActiveTime;
    }
    
    public void setLastActiveTime(LocalDateTime lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }
    
    @Override
    public String toString() {
        return "OnlineUser{" +
                "sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", userType='" + userType + '\'' +
                ", status='" + status + '\'' +
                ", connectTime=" + connectTime +
                ", lastActiveTime=" + lastActiveTime +
                '}';
    }
} 