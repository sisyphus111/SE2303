package com.se2303.moodservice.dto;

import com.se2303.moodservice.model.WeatherType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

/**
 * 心情打卡请求DTO
 */
public class MoodCheckInRequest {
    
    @NotNull(message = "日期不能为空")
    private LocalDate date;
    
    @NotNull(message = "心情评分不能为空")
    @Min(value = 1, message = "心情评分最小为1")
    @Max(value = 5, message = "心情评分最大为5")
    private Integer score;
    
    @NotEmpty(message = "情绪标签不能为空")
    @Size(max = 5, message = "最多选择5个情绪标签")
    private List<String> moodLabels;
    
    private String diaryDescription;
    
    private String diaryTitle;
    
    private WeatherType weather;
    
    // 构造函数
    public MoodCheckInRequest() {}

    // Getter和Setter方法
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
    
    public List<String> getMoodLabels() {
        return moodLabels;
    }
    
    public void setMoodLabels(List<String> moodLabels) {
        this.moodLabels = moodLabels;
    }
    
    public String getDiaryDescription() {
        return diaryDescription;
    }
    
    public void setDiaryDescription(String diaryDescription) {
        this.diaryDescription = diaryDescription;
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
    
    public void setWeather(WeatherType weather) {
        this.weather = weather;
    }
    
    @Override
    public String toString() {
        return "MoodCheckInRequest{" +
                "date=" + date +
                ", score=" + score +
                ", moodLabels=" + moodLabels +
                ", diaryDescription='" + diaryDescription + '\'' +
                ", diaryTitle='" + diaryTitle + '\'' +
                ", weather=" + weather +
                '}';
    }
} 