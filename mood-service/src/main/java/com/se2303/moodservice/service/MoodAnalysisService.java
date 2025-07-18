package com.se2303.moodservice.service;

/**
 * 心情分析服务接口
 * 用于生成AI分析报告
 */
public interface MoodAnalysisService {
    
    /**
     * 根据用户ID生成心情分析报告
     * @param userId 用户ID
     * @return AI生成的分析报告
     */
    String generateAnalysisReport(String userId);
    
    /**
     * 根据用户ID和日期范围生成心情分析报告
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return AI生成的分析报告
     */
    String generateAnalysisReport(String userId, String startDate, String endDate);
} 