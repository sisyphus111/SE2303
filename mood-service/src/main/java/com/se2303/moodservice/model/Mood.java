package com.se2303.moodservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mood", indexes = {
    @Index(name = "idx_date", columnList = "date"),
    @Index(name = "idx_user_date", columnList = "user_id, date")
})
public class Mood {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @NotNull
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Min(1)
    @Max(10)
    @Column(name = "score")
    private Integer score;
    
    @Column(name = "score_committed")
    private Boolean scoreCommitted = false;
    
    @Column(name = "diary_description", columnDefinition = "TEXT")
    private String diaryDescription;
    
    @Column(name = "diary_title")
    private String diaryTitle;
    
    @Column(name = "diary_committed")
    private Boolean diaryCommitted = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "weather")
    private WeatherType weather;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "mood", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MoodItem> moodItems = new ArrayList<>();
    
    // 构造函数
    public Mood() {}
    
    public Mood(LocalDate date, String userId) {
        this.date = date;
        this.userId = userId;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
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
    
    public void setScoreCommitted(Boolean scoreCommitted) {
        this.scoreCommitted = scoreCommitted;
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
    
    public Boolean getDiaryCommitted() {
        return diaryCommitted;
    }
    
    public void setDiaryCommitted(Boolean diaryCommitted) {
        this.diaryCommitted = diaryCommitted;
    }
    
    public WeatherType getWeather() {
        return weather;
    }
    
    public void setWeather(WeatherType weather) {
        this.weather = weather;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void removeMoodItem(MoodItem moodItem) {
        moodItems.remove(moodItem);
        moodItem.setMood(null);
    }
    
    @Override
    public String toString() {
        return "Mood{" +
                "id=" + id +
                ", date=" + date +
                ", userId=" + userId +
                ", score=" + score +
                ", scoreCommitted=" + scoreCommitted +
                ", diaryCommitted=" + diaryCommitted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 