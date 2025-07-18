package com.se2303.talkservice.dto;

import java.time.LocalDateTime;

/**
 * WebSocket推送内容DTO
 */
public class WsContent {
    private String type = "text";    // 消息类型——为"text"
    private ContentData content;     // 消息内容
    
    // 构造函数
    public WsContent() {}
    
    public WsContent(ContentData content) {
        this.content = content;
    }
    
    public WsContent(String type, ContentData content) {
        this.type = type;
        this.content = content;
    }
    
    // Getters and Setters
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public ContentData getContent() {
        return content;
    }
    
    public void setContent(ContentData content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "WsContent{" +
                "type='" + type + '\'' +
                ", content=" + content +
                '}';
    }
    
    /**
     * 消息内容数据
     */
    public static class ContentData {
        private Long id;
        private Long from_userId;
        private String content;
        private LocalDateTime createdAt;
        
        // 构造函数
        public ContentData() {}
        
        public ContentData(Long id, Long from_userId, String content, LocalDateTime createdAt) {
            this.id = id;
            this.from_userId = from_userId;
            this.content = content;
            this.createdAt = createdAt;
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public Long getFrom_userId() {
            return from_userId;
        }
        
        public void setFrom_userId(Long from_userId) {
            this.from_userId = from_userId;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
        
        @Override
        public String toString() {
            return "ContentData{" +
                    "id=" + id +
                    ", from_userId=" + from_userId +
                    ", content='" + content + '\'' +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }
} 