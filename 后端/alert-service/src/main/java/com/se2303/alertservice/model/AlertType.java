package com.se2303.alertservice.model;

/**
 * 警报类型枚举
 */
public enum AlertType {
    /**
     * 危险操作警报
     * 用户进行了可能有害的操作
     */
    DANGEROUS_OPERATION("危险操作"),
    
    /**
     * AI超时警报
     * 用户使用AI服务超时
     */
    AI_TIMEOUT("AI超时"),
    
    /**
     * 情绪异常警报
     * 用户情绪状态异常
     */
    EMOTIONAL_CRISIS("情绪异常"),
    
    /**
     * 自残倾向警报
     * 检测到自残倾向
     */
    SELF_HARM("自残倾向"),
    
    /**
     * 暴力倾向警报
     * 检测到暴力倾向
     */
    VIOLENT_TENDENCY("暴力倾向"),
    
    /**
     * 其他警报
     * 其他需要关注的警报
     */
    OTHER("其他");
    
    private final String description;
    
    AlertType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 