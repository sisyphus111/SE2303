package com.se2303.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 创建轮播图请求DTO
 */
public class CreateCarouselImageRequest {
    
    @NotBlank(message = "图片URL不能为空")
    @Size(max = 500, message = "图片URL长度不能超过500个字符")
    private String imageUrl;
    
    @Size(max = 255, message = "标题长度不能超过255个字符")
    private String title;
    
    @Size(max = 1000, message = "描述长度不能超过1000个字符")
    private String description;
    
    @Size(max = 100, message = "分类长度不能超过100个字符")
    private String category = "心理健康";
    
    @NotNull(message = "排序不能为空")
    private Integer sortOrder = 0;
    
    // 构造函数
    public CreateCarouselImageRequest() {}
    
    public CreateCarouselImageRequest(String imageUrl, String title, String description, String category, Integer sortOrder) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.category = category;
        this.sortOrder = sortOrder;
    }
    
    // Getter和Setter方法
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
} 