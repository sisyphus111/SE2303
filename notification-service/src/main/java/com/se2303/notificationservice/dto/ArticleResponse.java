package com.se2303.notificationservice.dto;

import java.time.LocalDateTime;

public class ArticleResponse {
    private Long id;
    private String title;
    private String introduction;
    private String description;
    private String cover;
    private String link;
    private String category;
    private LocalDateTime createdAt;

    public ArticleResponse(Long id, String title, String introduction, String description, String cover, String link, String category, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.introduction = introduction;
        this.description = description;
        this.cover = cover;
        this.link = link;
        this.category = category;
        this.createdAt = createdAt;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getIntroduction() {
        return introduction;
    }
    
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCover() {
        return cover;
    }
    
    public void setCover(String cover) {
        this.cover = cover;
    }
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 