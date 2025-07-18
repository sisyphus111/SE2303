package com.se2303.moodservice.dto;

import com.se2303.moodservice.model.WeatherType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 心情打卡响应DTO
 */
public class MoodCheckInResponse {
    
    private Long id;
    private String userId;
    private LocalDate date;
    private Integer score;
    private Boolean scoreCommitted;
    private String diaryDescription;
    private Boolean diaryCommitted;
    private String diaryTitle;
    private WeatherType weather;
    private List<String> moodLabels;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public MoodCheckInResponse() {}
    
    public MoodCheckInResponse(Long id, String userId, LocalDate date, Integer score, 
                              Boolean scoreCommitted, String diaryDescription, 
                              Boolean diaryCommitted, String diaryTitle, WeatherType weather, List<String> moodLabels,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.score = score;
        this.scoreCommitted = scoreCommitted;
        this.diaryDescription = diaryDescription;
        this.diaryCommitted = diaryCommitted;
        this.diaryTitle = diaryTitle;
        this.weather = weather;
        this.moodLabels = moodLabels;
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
    
    public Boolean getScoreCommitted() {
        return scoreCommitted;
    }

    public String getDiaryDescription() {
        return diaryDescription;
    }
    
    public void setDiaryDescription(String diaryDescription) {
        this.diaryDescription = diaryDescription;
    }
    
    public Boolean getDiaryCommitted() {
        return diaryCommitted;
    }

    public String getDiaryTitle() {
        return diaryTitle;
    }
    
    public void setDiaryTitle(String diaryTitle) {
        this.diaryTitle = diaryTitle;
    }
    
    public WeatherType getWeather() {
        return weather;
    }

    public List<String> getMoodLabels() {
        return moodLabels;
    }
    
    public void setMoodLabels(List<String> moodLabels) {
        this.moodLabels = moodLabels;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "MoodCheckInResponse{" +
                "id=" + id +
                ", userId=" + userId +
                ", date=" + date +
                ", score=" + score +
                ", scoreCommitted=" + scoreCommitted +
                ", diaryDescription='" + diaryDescription + '\'' +
                ", diaryCommitted=" + diaryCommitted +
                ", diaryTitle='" + diaryTitle + '\'' +
                ", weather=" + weather +
                ", moodLabels=" + moodLabels +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 