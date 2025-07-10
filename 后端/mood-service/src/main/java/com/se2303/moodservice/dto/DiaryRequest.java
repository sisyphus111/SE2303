package com.se2303.moodservice.dto;

import com.se2303.moodservice.model.WeatherType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 日记请求DTO
 */
public class DiaryRequest {
    
    @NotNull(message = "日期不能为空")
    private LocalDate date;
    
    @NotNull(message = "日记内容不能为空")
    private String diaryDescription;
    
    private String diaryTitle;
    
    private WeatherType weather;
    
    // 构造函数
    public DiaryRequest() {}
    
    public DiaryRequest(LocalDate date, String diaryDescription) {
        this.date = date;
        this.diaryDescription = diaryDescription;
    }
    
    public DiaryRequest(LocalDate date, String diaryDescription, WeatherType weather) {
        this.date = date;
        this.diaryDescription = diaryDescription;
        this.weather = weather;
    }
    
    public DiaryRequest(LocalDate date, String diaryTitle, String diaryDescription, WeatherType weather) {
        this.date = date;
        this.diaryTitle = diaryTitle;
        this.diaryDescription = diaryDescription;
        this.weather = weather;
    }
    
    // Getter和Setter方法
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
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
        return "DiaryRequest{" +
                "date=" + date +
                ", diaryDescription='" + diaryDescription + '\'' +
                ", diaryTitle='" + diaryTitle + '\'' +
                ", weather=" + weather +
                '}';
    }
} 