package com.se2303.chatservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "valid", nullable = false)
    private Boolean valid = true;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (valid == null) {
            valid = true;
        }
    }
    
    // 构造函数
    public Message() {}
    
    public Message(Long userId, String content) {
        this.userId = userId;
        this.content = content;
        this.valid = true;
    }
    
    public Message(Long userId, String content, Boolean valid) {
        this.userId = userId;
        this.content = content;
        this.valid = valid != null ? valid : true;
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

    public String getContent() {
        return content;
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