package com.se2303.moodservice.model;

import java.util.Arrays;
import java.util.List;

/**
 * 心情标签枚举
 * 定义常用的心情标签
 */
public enum MoodLabel {
    
    // 积极情绪
    HAPPY("开心"),
    EXCITED("兴奋"),
    CONTENT("满足"),
    PEACEFUL("宁静"),
    GRATEFUL("感恩"),
    CONFIDENT("自信"),
    ENERGIZED("充满活力"),
    OPTIMISTIC("乐观"),
    
    // 中性情绪
    NEUTRAL("平静"),
    CALM("冷静"),
    FOCUSED("专注"),
    RELAXED("放松"),
    
    // 消极情绪
    SAD("悲伤"),
    ANGRY("愤怒"),
    ANXIOUS("焦虑"),
    STRESSED("压力"),
    TIRED("疲惫"),
    LONELY("孤独"),
    FRUSTRATED("沮丧"),
    WORRIED("担心"),
    DISAPPOINTED("失望"),
    IRRITATED("烦躁"),
    CONFUSED("困惑");
    
    private final String label;
    
    MoodLabel(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
    
    /**
     * 根据标签文本获取枚举值
     */
    public static MoodLabel fromLabel(String label) {
        for (MoodLabel moodLabel : values()) {
            if (moodLabel.label.equals(label)) {
                return moodLabel;
            }
        }
        throw new IllegalArgumentException("Unknown mood label: " + label);
    }
    
    /**
     * 检查标签是否存在
     */
    public static boolean isValidLabel(String label) {
        for (MoodLabel moodLabel : values()) {
            if (moodLabel.label.equals(label)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取标签的权值
     * 积极标签权值为1，中性标签权值为0，消极标签权值为-1
     */
    public int getWeight() {
        if (isPositiveMood()) {
            return 1;
        } else if (isNegativeMood()) {
            return -1;
        } else {
            return 0; // 中性标签
        }
    }
    
    /**
     * 判断是否为积极情绪
     */
    public boolean isPositiveMood() {
        return Arrays.asList(getPositiveMoods()).contains(this);
    }
    
    /**
     * 判断是否为消极情绪
     */
    public boolean isNegativeMood() {
        return Arrays.asList(getNegativeMoods()).contains(this);
    }
    
    /**
     * 判断是否为中性情绪
     */
    public boolean isNeutralMood() {
        return Arrays.asList(getNeutralMoods()).contains(this);
    }
    
    /**
     * 计算标签列表的总权值
     */
    public static int calculateTotalWeight(List<String> labels) {
        if (labels == null || labels.isEmpty()) {
            return 0;
        }
        
        return labels.stream()
                .filter(MoodLabel::isValidLabel)
                .mapToInt(label -> MoodLabel.fromLabel(label).getWeight())
                .sum();
    }
    
    /**
     * 统计积极标签数量
     */
    public static int countPositiveLabels(List<String> labels) {
        if (labels == null || labels.isEmpty()) {
            return 0;
        }
        
        return (int) labels.stream()
                .filter(MoodLabel::isValidLabel)
                .filter(label -> MoodLabel.fromLabel(label).isPositiveMood())
                .count();
    }
    
    /**
     * 统计中性标签数量
     */
    public static int countNeutralLabels(List<String> labels) {
        if (labels == null || labels.isEmpty()) {
            return 0;
        }
        
        return (int) labels.stream()
                .filter(MoodLabel::isValidLabel)
                .filter(label -> MoodLabel.fromLabel(label).isNeutralMood())
                .count();
    }
    
    /**
     * 统计消极标签数量
     */
    public static int countNegativeLabels(List<String> labels) {
        if (labels == null || labels.isEmpty()) {
            return 0;
        }
        
        return (int) labels.stream()
                .filter(MoodLabel::isValidLabel)
                .filter(label -> MoodLabel.fromLabel(label).isNegativeMood())
                .count();
    }
    
    /**
     * 根据总权值判断心情趋势
     */
    public static String getMoodTrend(int totalWeight) {
        if (totalWeight > 0) {
            return "positive";
        } else if (totalWeight < 0) {
            return "negative";
        } else {
            return "neutral";
        }
    }
    
    /**
     * 获取所有积极情绪标签
     */
    public static MoodLabel[] getPositiveMoods() {
        return new MoodLabel[]{
            HAPPY, EXCITED, CONTENT, PEACEFUL, GRATEFUL, CONFIDENT, ENERGIZED, OPTIMISTIC
        };
    }
    
    /**
     * 获取所有消极情绪标签
     */
    public static MoodLabel[] getNegativeMoods() {
        return new MoodLabel[]{
            SAD, ANGRY, ANXIOUS, STRESSED, TIRED, LONELY, FRUSTRATED, WORRIED, DISAPPOINTED, IRRITATED, CONFUSED
        };
    }
    
    /**
     * 获取所有中性情绪标签
     */
    public static MoodLabel[] getNeutralMoods() {
        return new MoodLabel[]{
            NEUTRAL, CALM, FOCUSED, RELAXED
        };
    }
} 