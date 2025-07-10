package com.se2303.moodservice.controller;

import com.se2303.moodservice.dto.ApiResponse;
import com.se2303.moodservice.dao.MoodDAO;
import com.se2303.moodservice.dao.MoodItemDAO;
import com.se2303.moodservice.service.DatabaseHealthService;
import com.se2303.moodservice.service.DatabaseInitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库管理控制器
 * 使用DAO层进行数据访问
 * 注意：已删除@CrossOrigin注解，因为网关统一处理CORS
 */
@RestController
@RequestMapping("/api/database")
public class DatabaseController {
    
    @Autowired
    private DatabaseHealthService databaseHealthService;
    
    @Autowired
    private DatabaseInitService databaseInitService;
    
    @Autowired
    private MoodDAO moodDAO;
    
    @Autowired
    private MoodItemDAO moodItemDAO;
    
    /**
     * 检查数据库健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> checkHealth() {
        Map<String, Object> healthReport = databaseHealthService.getHealthReport();
        return ResponseEntity.ok(ApiResponse.success(healthReport, "数据库健康检查完成"));
    }
    
    /**
     * 获取数据库统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getStats() {
        try {
            long totalMoods = moodDAO.countByUserId("1") + moodDAO.countByUserId("2");
            long totalMoodItems = moodItemDAO.countByLabel("开心") + moodItemDAO.countByLabel("焦虑");
            
            Map<String, Object> data = new HashMap<>();
            data.put("totalMoods", totalMoods);
            data.put("totalMoodItems", totalMoodItems);
            
            return ResponseEntity.ok(ApiResponse.success(data, "获取数据库统计成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取统计失败: " + e.getMessage()));
        }
    }
    
    /**
     * 初始化示例数据
     */
    @PostMapping("/init")
    public ResponseEntity<ApiResponse> initializeData() {
        try {
            databaseInitService.manualInit();
            return ResponseEntity.ok(ApiResponse.success("示例数据初始化成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("初始化失败: " + e.getMessage()));
        }
    }
    
    /**
     * 清空所有数据
     */
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse> clearAllData() {
        try {
            databaseInitService.clearAllData();
            return ResponseEntity.ok(ApiResponse.success("所有数据已清空"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("清空失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取数据统计
     */
    @GetMapping("/data-stats")
    public ResponseEntity<ApiResponse> getDataStats() {
        try {
            String stats = databaseInitService.getDataStats();
            Map<String, Object> data = new HashMap<>();
            data.put("stats", stats);
            return ResponseEntity.ok(ApiResponse.success(data, "获取数据统计成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取统计失败: " + e.getMessage()));
        }
    }
    
    /**
     * 检查数据库连接
     */
    @GetMapping("/connection")
    public ResponseEntity<ApiResponse> checkConnection() {
        boolean connected = databaseHealthService.isDatabaseConnected();
        boolean tablesExist = databaseHealthService.checkTablesExist();
        
        Map<String, Object> data = new HashMap<>();
        data.put("connected", connected);
        data.put("tablesExist", tablesExist);
        data.put("status", connected && tablesExist ? "healthy" : "unhealthy");
        
        return ResponseEntity.ok(ApiResponse.success(data, "数据库连接检查完成"));
    }
} 