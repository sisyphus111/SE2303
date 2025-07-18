package com.se2303.notificationservice.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

public class UpdateCarouselImageRequest {
    @Size(max = 500, message = "图片URL长度不能超过500个字符")
    private String imageUrl;

    @Size(max = 255, message = "标题长度不能超过255个字符")
    private String title;

    @Size(max = 1000, message = "描述长度不能超过1000个字符")
    private String description;

    @Size(max = 100, message = "分类长度不能超过100个字符")
    private String category;

    @Min(value = 0, message = "排序必须为非负数")
    private Integer sortOrder;

    public UpdateCarouselImageRequest() {}

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
} 