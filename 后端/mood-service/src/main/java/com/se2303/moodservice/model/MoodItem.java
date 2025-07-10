package com.se2303.moodservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mood_item", indexes = {
    @Index(name = "idx_label", columnList = "label"),
    @Index(name = "idx_mood_id", columnList = "mood_id")
})
public class MoodItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    @Column(name = "label", nullable = false, length = 50)
    private String label;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_id", nullable = false)
    private Mood mood;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // 构造函数
    public MoodItem() {}
    
    public MoodItem(String label) {
        this.label = label;
    }
    
    public MoodItem(String label, Mood mood) {
        this.label = label;
        this.mood = mood;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public Mood getMood() {
        return mood;
    }
    
    public void setMood(Mood mood) {
        this.mood = mood;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "MoodItem{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", moodId=" + (mood != null ? mood.getId() : null) +
                ", createdAt=" + createdAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        MoodItem moodItem = (MoodItem) o;
        
        if (id != null ? !id.equals(moodItem.id) : moodItem.id != null) return false;
        return label != null ? label.equals(moodItem.label) : moodItem.label == null;
    }
    
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }
} 