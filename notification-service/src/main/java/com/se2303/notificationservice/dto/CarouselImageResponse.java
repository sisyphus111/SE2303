package com.se2303.notificationservice.dto;

import java.time.LocalDateTime;

public class CarouselImageResponse {
    private Long id;
    private String imageUrl;
    private String title;
    private String description;
    private String category;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    
    public CarouselImageResponse() {}
    
    public CarouselImageResponse(Long id, String imageUrl, String title, String description, String category, Integer sortOrder, LocalDateTime createdAt) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.category = category;
        this.sortOrder = sortOrder;
        this.createdAt = createdAt;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 