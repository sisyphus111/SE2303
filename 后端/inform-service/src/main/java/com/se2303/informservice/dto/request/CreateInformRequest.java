package com.se2303.informservice.dto.request;

import com.se2303.informservice.entity.InformType;

public class CreateInformRequest {
    
    private Long userId;
    private InformType type;
    private String content;
    private String relatedId;
    private Long fromUserId;
    
    // 构造函数
    public CreateInformRequest() {}
    
    public CreateInformRequest(Long userId, InformType type, String content, String relatedId, Long fromUserId) {
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.relatedId = relatedId;
        this.fromUserId = fromUserId;
    }
    
    // Getters and Setters
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
}
