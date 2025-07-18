package com.se2303.moodservice.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

/**
 * 只打卡不写日记的请求DTO
 */
public class CheckInRequest {
    
    @NotNull(message = "日期不能为空")
    private LocalDate date;
    
    @NotNull(message = "心情评分不能为空")
    @Min(value = 1, message = "心情评分最小为1")
    @Max(value = 5, message = "心情评分最大为5")
    private Integer score;
    
    @NotEmpty(message = "情绪标签不能为空")
    @Size(max = 5, message = "最多选择5个情绪标签")
    private List<String> moodLabels;
    
    // 构造函数
    public CheckInRequest() {}

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
    
    @Override
    public String toString() {
        return "CheckInRequest{" +
                "date=" + date +
                ", score=" + score +
                ", moodLabels=" + moodLabels +
                '}';
    }
} 