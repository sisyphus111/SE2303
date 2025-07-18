package com.se2303.botservice.model;

public class ChatMessage {
    private String id; // 消息唯一标识
    private String type; // "user", "ai", "ai_chunk", "ai_end", "error"
    private String role; // "user", "assistant", "system"
    private String content;
    private long timestamp;
    
    public ChatMessage() {}
    
    public ChatMessage(String type, String content, long timestamp) {
        this.type = type;
        this.content = content;
        this.timestamp = timestamp;
    }
    
    public ChatMessage(String id, String type, String role, String content, long timestamp) {
        this.id = id;
        this.type = type;
        this.role = role;
        this.content = content;
        this.timestamp = timestamp;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", role='" + role + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
} 