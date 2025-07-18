package com.se2303.notificationservice.dto;

import jakarta.validation.constraints.Size;

/**
 * 更新文章请求DTO
 */
public class UpdateArticleRequest {
    
    @Size(max = 255, message = "文章标题长度不能超过255个字符")
    private String title;
    
    @Size(max = 1000, message = "文章简介长度不能超过1000个字符")
    private String introduction;
    
    @Size(max = 5000, message = "文章描述长度不能超过5000个字符")
    private String description;
    
    @Size(max = 500, message = "封面图片URL长度不能超过500个字符")
    private String cover;
    
    @Size(max = 500, message = "文章链接长度不能超过500个字符")
    private String link;
    
    @Size(max = 100, message = "文章分类长度不能超过100个字符")
    private String category;
    
    // 构造函数
    public UpdateArticleRequest() {}

    // Getter和Setter方法
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
} 