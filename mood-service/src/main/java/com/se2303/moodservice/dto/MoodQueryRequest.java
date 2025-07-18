package com.se2303.moodservice.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * 心情查询请求DTO
 * 支持按用户、情绪标签、时间范围进行筛选
 */
public class MoodQueryRequest {
    
    /**
     * 用户ID（可选）
     */
    private String userId;
    
    /**
     * 情绪标签列表（可选）
     */
    private List<String> moodLabels;
    
    /**
     * 开始日期（可选）
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    
    /**
     * 结束日期（可选）
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    
    /**
     * 页码（可选，默认为0）
     */
    private Integer page = 0;
    
    /**
     * 每页大小（可选，默认为20）
     */
    private Integer size = 20;
    
    // 构造函数
    public MoodQueryRequest() {}
    
    public MoodQueryRequest(String userId, List<String> moodLabels, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.moodLabels = moodLabels;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Getter和Setter方法
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public List<String> getMoodLabels() {
        return moodLabels;
    }
    
    public void setMoodLabels(List<String> moodLabels) {
        this.moodLabels = moodLabels;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
    
    /**
     * 检查是否有任何筛选条件
     */
    public boolean hasFilters() {
        return userId != null || 
               (moodLabels != null && !moodLabels.isEmpty()) || 
               startDate != null || 
               endDate != null;
    }
    
    @Override
    public String toString() {
        return "MoodQueryRequest{" +
                "userId='" + userId + '\'' +
                ", moodLabels=" + moodLabels +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
} 