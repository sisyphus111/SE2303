package com.se2303.notificationservice.dto;

import java.time.LocalDateTime;

public class MusicResponse {
    private Long id;
    private String title;
    private String category;
    private String cover;
    private String url;
    private Integer duration;
    private LocalDateTime createdAt;
    
    public MusicResponse() {}
    
    public MusicResponse(Long id, String title, String category, String cover, String url, Integer duration, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.cover = cover;
        this.url = url;
        this.duration = duration;
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
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getCover() {
        return cover;
    }
    
    public void setCover(String cover) {
        this.cover = cover;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 