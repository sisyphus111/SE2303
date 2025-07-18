package com.se2303.alertservice.model;

/**
 * 警报状态枚举
 */
public enum AlertStatus {
    /**
     * 待处理
     * 警报已创建，等待心理咨询师处理
     */
    PENDING("待处理"),
    
    /**
     * 已分配
     * 警报已分配给心理咨询师
     */
    ASSIGNED("已分配"),
    
    /**
     * 处理中
     * 心理咨询师正在处理
     */
    IN_PROGRESS("处理中"),
    
    /**
     * 已完成
     * 警报已处理完成
     */
    COMPLETED("已完成"),
    
    /**
     * 已关闭
     * 警报已关闭
     */
    CLOSED("已关闭"),
    
    /**
     * 已取消
     * 警报已取消
     */
    CANCELLED("已取消");
    
    private final String description;
    
    AlertStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 