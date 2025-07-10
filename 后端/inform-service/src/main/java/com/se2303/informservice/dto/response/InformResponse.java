package com.se2303.informservice.dto.response;

import com.se2303.informservice.entity.Inform;
import com.se2303.informservice.entity.InformType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class InformResponse {
    
    private Long id;
    private Long userId;
    private InformType type;
    private String title;
    private String content;
    private String displayContent; // 用于前端显示的格式化内容
    private String relatedId;
    private Long fromUserId;
    private String fromUsername; // 动态设置的用户名
    private boolean read;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readAt;

    public InformResponse() {}

    public InformResponse(Inform inform) {
        this.id = inform.getId();
        this.userId = inform.getUserId();
        this.type = inform.getType();
        this.title = inform.getTitle(); // 动态生成的标题
        this.content = inform.getRawContent(); // 原始内容（评论内容）
        this.relatedId = inform.getRelatedId();
        this.fromUserId = inform.getFromUserId();
        // fromUsername 需要单独设置，因为实体中已不存储
        this.read = inform.isRead();
        this.createdAt = inform.getCreatedAt();
        this.readAt = inform.getReadAt();
    }
    
    /**
     * 设置用户名并生成显示内容
     * @param username 用户名
     */
    public void setFromUsernameAndGenerateDisplay(String username) {
        this.fromUsername = username;
        // 根据Inform实体生成显示内容
        if (this.type == InformType.COMMENT) {
            this.displayContent = username + " 评论了你的帖子：" + (this.content != null ? this.content : "");
        } else if (this.type == InformType.LIKE) {
            this.displayContent = username + " 点赞了你的帖子";
        } else {
            this.displayContent = this.content;
        }
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public String getDisplayContent() {
        return displayContent;
    }
    
    public void setDisplayContent(String displayContent) {
        this.displayContent = displayContent;
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

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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
}
