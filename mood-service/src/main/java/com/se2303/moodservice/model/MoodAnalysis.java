package com.se2303.moodservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "mood_analysis")
public class MoodAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Lob
    @Column(name = "analysis", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String analysis;

    @Column(name = "valid", nullable = false)
    private Boolean valid;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public MoodAnalysis(){}
    
    public MoodAnalysis(String userId, String analysis, Boolean valid, LocalDateTime updatedAt) {
        this.userId = userId;
        this.analysis = analysis;
        this.valid = valid;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getAnalysis() {
        return analysis;
    }

    public Boolean getValid() {
        return valid;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


}
