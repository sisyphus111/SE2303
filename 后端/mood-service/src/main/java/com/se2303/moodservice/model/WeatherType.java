package com.se2303.moodservice.model;

/**
 * 天气类型枚举
 */
public enum WeatherType {
    SUNNY("晴天", "☀️"),
    CLOUDY("多云", "☁️"),
    RAINY("雨天", "🌧️"),
    SNOWY("雪天", "❄️"),
    WINDY("风天", "💨"),
    FOGGY("雾天", "🌫️");
    
    private final String displayName;
    private final String emoji;
    
    WeatherType(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji = emoji;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getEmoji() {
        return emoji;
    }
    
    @Override
    public String toString() {
        return this.name();
    }
} 