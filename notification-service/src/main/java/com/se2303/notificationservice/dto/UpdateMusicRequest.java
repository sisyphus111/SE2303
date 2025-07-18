package com.se2303.notificationservice.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

/**
 * 更新音乐请求DTO
 */
public class UpdateMusicRequest {
    @Size(max = 255, message = "音乐标题长度不能超过255个字符")
    private String title;

    @Size(max = 255, message = "音乐分类长度不能超过255个字符")
    private String category;

    @Size(max = 500, message = "封面图片URL长度不能超过500个字符")
    private String cover;

    @Size(max = 500, message = "音乐文件URL长度不能超过500个字符")
    private String url;

    @Positive(message = "音乐时长必须为正数")
    private Integer duration;

    public UpdateMusicRequest() {}

    public UpdateMusicRequest(String title, String category, String cover, String url, Integer duration) {
        this.title = title;
        this.category = category;
        this.cover = cover;
        this.url = url;
        this.duration = duration;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
} 