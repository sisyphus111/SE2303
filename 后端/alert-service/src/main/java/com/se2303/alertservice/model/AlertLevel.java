package com.se2303.alertservice.model;

/**
 * 警报级别枚举
 */
public enum AlertLevel {
    /**
     * 低级别警报
     * 需要关注但紧急程度较低
     */
    LOW("低"),
    
    /**
     * 中级别警报
     * 需要及时处理
     */
    MEDIUM("中"),
    
    /**
     * 高级别警报
     * 需要立即处理
     */
    HIGH("高"),
    
    /**
     * 紧急警报
     * 需要立即干预
     */
    URGENT("紧急");
    
    private final String description;
    
    AlertLevel(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 