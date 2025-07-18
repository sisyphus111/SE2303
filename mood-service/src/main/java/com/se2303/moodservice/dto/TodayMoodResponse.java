package com.se2303.moodservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 今日心情响应DTO
 * 包含心情评分、标签、权值计算等信息
 */
public class TodayMoodResponse {
    
    private Long id;
    private String userId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    private Integer score;
    private Boolean scoreCommitted;
    private List<String> moodLabels;
    
    // 新增字段：标签权值计算
    private Integer positiveLabelCount;    // 积极标签数量
    private Integer neutralLabelCount;     // 中性标签数量
    private Integer negativeLabelCount;    // 消极标签数量
    private Integer totalMoodWeight;       // 总心情权值
    private String moodTrend;              // 心情趋势：positive/neutral/negative
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 默认构造函数
    public TodayMoodResponse() {}
    
    // 全参构造函数
    public TodayMoodResponse(Long id, String userId, LocalDate date, Integer score, 
                           Boolean scoreCommitted, List<String> moodLabels,
                           Integer positiveLabelCount, Integer neutralLabelCount,
                           Integer negativeLabelCount, Integer totalMoodWeight,
                           String moodTrend, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.score = score;
        this.scoreCommitted = scoreCommitted;
        this.moodLabels = moodLabels;
        this.positiveLabelCount = positiveLabelCount;
        this.neutralLabelCount = neutralLabelCount;
        this.negativeLabelCount = negativeLabelCount;
        this.totalMoodWeight = totalMoodWeight;
        this.moodTrend = moodTrend;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }

    public String getMoodTrend() {
        return moodTrend;
    }
    
    public void setMoodTrend(String moodTrend) {
        this.moodTrend = moodTrend;
    }

    @Override
    public String toString() {
        return "TodayMoodResponse{" +
                "id=" + id +
                ", userId=" + userId +
                ", date=" + date +
                ", score=" + score +
                ", scoreCommitted=" + scoreCommitted +
                ", moodLabels=" + moodLabels +
                ", positiveLabelCount=" + positiveLabelCount +
                ", neutralLabelCount=" + neutralLabelCount +
                ", negativeLabelCount=" + negativeLabelCount +
                ", totalMoodWeight=" + totalMoodWeight +
                ", moodTrend='" + moodTrend + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 