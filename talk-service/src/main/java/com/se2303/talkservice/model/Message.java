package com.se2303.talkservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息实体类
 */
@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "from_user_id", nullable = false)
    private Long fromUserId;          // 发送者用户ID
    
    @Column(name = "valid", nullable = false)
    private Boolean valid = true; // 消息是否有效
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false; // 消息是否已删除
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false; // 消息是否已读
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;       // 消息内容
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;    // 创建时间
    
    // 构造函数
    public Message() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Message(Long fromUserId, String content) {
        this();
        this.fromUserId = fromUserId;
        this.content = content;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getFromUserId() {
        return fromUserId;
    }
    
    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }
    
    // 为了向后兼容，保留getUserId和setUserId方法
    public Long getUserId() {
        return fromUserId;
    }
    
    public void setUserId(Long userId) {
        this.fromUserId = userId;
    }
    
    public Boolean getValid() {
        return valid;
    }
    
    public void setValid(Boolean valid) {
        this.valid = valid;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
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
        return "Message{" +
                "id=" + id +
                ", fromUserId=" + fromUserId +
                ", valid=" + valid +
                ", isDeleted=" + isDeleted +
                ", isRead=" + isRead +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
} 