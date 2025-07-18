package com.se2303.talkservice.dto;

/**
 * 发送消息DTO
 */
public class SentMessage {
    private String type = "text";    // 消息类型——为"text"
    private String content;          // 消息内容文本
    
    // 构造函数
    public SentMessage() {}
    
    public SentMessage(String content) {
        this.content = content;
    }
    
    public SentMessage(String type, String content) {
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
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "SentMessage{" +
                "type='" + type + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
} 