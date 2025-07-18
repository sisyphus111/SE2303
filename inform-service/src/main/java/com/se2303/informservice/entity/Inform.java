package com.se2303.informservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "informs")
public class Inform {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private InformType type;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "related_id")
    private String relatedId;
    
    @Column(name = "from_user_id")
    private Long fromUserId;

    @Column(name = "is_read", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isRead = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    // 构造函数
    public Inform() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Inform(Long userId, InformType type, String content) {
        this();
        this.userId = userId;
        this.type = type;
        this.content = content;
    }
    
    // Getters and Setters
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
    
    public InformType getType() {
        return type;
    }
    
    public void setType(InformType type) {
        this.type = type;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getRelatedId() {
        return relatedId;
    }
    
    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }
    
    public Long getFromUserId() {
        return fromUserId;
    }
    
    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
        if (isRead && this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }
    
    // 添加兼容的方法
    public boolean isRead() {
        return isRead != null && isRead;
    }
    
    public void setRead(boolean read) {
        this.isRead = read;
        if (read && this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
    
    // 添加 updatedAt 相关方法
    public LocalDateTime getUpdatedAt() {
        return readAt; // 使用 readAt 作为 updatedAt
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        // 可以选择性地更新 readAt 或添加新的字段
        this.readAt = updatedAt;
    }
    
    /**
     * 动态生成标题（根据类型）
     */
    public String getTitle() {
        if (this.type == InformType.COMMENT) {
            return "新评论通知";
        } else if (this.type == InformType.LIKE) {
            return "新点赞通知";
        }
        return "通知";
    }
    
    /**
     * 动态生成显示内容（需要用户名参数）
     * @param fromUsername 来源用户名
     * @return 格式化的显示内容
     */
    public String getDisplayContent(String fromUsername) {
        String username = fromUsername != null ? fromUsername : ("用户" + this.fromUserId);
        
        if (this.type == InformType.COMMENT) {
            return username + " 评论了你的帖子：" + (this.content != null ? this.content : "");
        } else if (this.type == InformType.LIKE) {
            return username + " 点赞了你的帖子";
        }
        return this.content;
    }
    
    /**
     * 获取原始评论内容（仅用于评论类型）
     * @return 评论内容
     */
    public String getRawContent() {
        return this.content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inform inform = (Inform) o;
        return Objects.equals(id, inform.id) &&
                Objects.equals(userId, inform.userId) &&
                type == inform.type &&
                Objects.equals(content, inform.content) &&
                Objects.equals(relatedId, inform.relatedId) &&
                Objects.equals(fromUserId, inform.fromUserId) &&
                Objects.equals(isRead, inform.isRead) &&
                Objects.equals(createdAt, inform.createdAt) &&
                Objects.equals(readAt, inform.readAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, type, content, relatedId, fromUserId, isRead, createdAt, readAt);
    }
}
