package com.se2303.chatservice.dto;

import java.time.LocalDateTime;

public class MessageResponse {
    private Long id;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private Boolean valid;

    public MessageResponse() {}

    public MessageResponse(Long id, Long userId, String content, LocalDateTime createdAt, Boolean valid) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
        this.valid = valid;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
    
    public Boolean getValid() {
        return valid;
    }
    
    public void setValid(Boolean valid) {
        this.valid = valid;
    }
} 