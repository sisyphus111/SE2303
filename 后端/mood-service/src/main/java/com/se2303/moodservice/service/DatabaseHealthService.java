package com.se2303.moodservice.service;

import com.se2303.moodservice.dao.MoodDAO;
import com.se2303.moodservice.dao.MoodItemDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库健康检查服务
 * 使用DAO层进行数据访问
 */
@Service
public class DatabaseHealthService {
    
    @Autowired
    private MoodDAO moodDAO;
    
    @Autowired
    private MoodItemDAO moodItemDAO;
    
    /**
     * 检查数据库连接状态
     */
    public boolean isDatabaseConnected() {
        try {
            // 尝试执行一个简单的查询来测试连接
            moodDAO.findAll();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取数据库统计信息
     */
    public Map<String, Object> getDatabaseStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            long totalMoods = moodDAO.countByUserId("1") + moodDAO.countByUserId("2");
            long totalMoodItems = moodItemDAO.countByLabel("开心") + moodItemDAO.countByLabel("焦虑");
            
            stats.put("totalMoods", totalMoods);
            stats.put("totalMoodItems", totalMoodItems);
            stats.put("connected", true);
            stats.put("status", "healthy");
        } catch (Exception e) {
            stats.put("connected", false);
            stats.put("status", "error");
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * 检查数据库表是否存在
     */
    public boolean checkTablesExist() {
        try {
            // 尝试访问两个主要表
            moodDAO.findAll();
            moodItemDAO.findAll();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取详细的健康状态报告
     */
    public Map<String, Object> getHealthReport() {
        Map<String, Object> report = new HashMap<>();
        
        boolean connected = isDatabaseConnected();
        boolean tablesExist = checkTablesExist();
        
        report.put("databaseConnected", connected);
        report.put("tablesExist", tablesExist);
        report.put("overallStatus", connected && tablesExist ? "healthy" : "unhealthy");
        
        if (connected && tablesExist) {
            Map<String, Object> stats = getDatabaseStats();
            report.put("statistics", stats);
        }
        
        return report;
    }
} 