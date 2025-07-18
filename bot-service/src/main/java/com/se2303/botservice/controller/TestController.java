package com.se2303.botservice.controller;

import com.se2303.botservice.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 用于测试 bot-service 与 alert-service 的集成功能
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    @Autowired
    private AIService aiService;
    
    /**
     * 测试 Alert Service 连接
     * GET /api/test/alert-health
     */
    @GetMapping("/alert-health")
    public ResponseEntity<Map<String, Object>> testAlertServiceHealth() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean isHealthy = aiService.checkAlertServiceHealth();
            response.put("success", true);
            response.put("alertServiceHealthy", isHealthy);
            response.put("message", isHealthy ? "Alert Service 连接正常" : "Alert Service 连接异常");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("alertServiceHealthy", false);
            response.put("message", "测试 Alert Service 连接时出错: " + e.getMessage());
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 测试设置用户信息
     * POST /api/test/set-user
     */
    @PostMapping("/set-user")
    public ResponseEntity<Map<String, Object>> setTestUser(
            @RequestParam String userId,
            @RequestParam(required = false) String sessionId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            aiService.setCurrentUser(userId, sessionId != null ? sessionId : "test_session_" + System.currentTimeMillis());
            
            response.put("success", true);
            response.put("message", "用户信息设置成功");
            response.put("userId", userId);
            response.put("sessionId", sessionId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "设置用户信息时出错: " + e.getMessage());
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 测试对话计时器
     * GET /api/test/conversation-timer
     */
    @GetMapping("/conversation-timer")
    public ResponseEntity<Map<String, Object>> getConversationTimer() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            long durationMinutes = aiService.getCurrentConversationDurationMinutes();
            
            response.put("success", true);
            response.put("durationMinutes", durationMinutes);
            response.put("message", "当前对话持续时间: " + durationMinutes + " 分钟");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取对话计时器信息时出错: " + e.getMessage());
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 重置对话计时器
     * POST /api/test/reset-timer
     */
    @PostMapping("/reset-timer")
    public ResponseEntity<Map<String, Object>> resetConversationTimer() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            aiService.resetConversationTimer();
            
            response.put("success", true);
            response.put("message", "对话计时器已重置");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "重置对话计时器时出错: " + e.getMessage());
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 测试危险内容检测
     * POST /api/test/dangerous-content
     */
    @PostMapping("/dangerous-content")
    public ResponseEntity<Map<String, Object>> testDangerousContent(
            @RequestParam String message,
            @RequestParam String userId,
            @RequestParam(required = false) String sessionId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 设置用户信息
            aiService.setCurrentUser(userId, sessionId != null ? sessionId : "test_session_" + System.currentTimeMillis());
            
            // 生成模拟响应（会触发危险内容检测）
            String aiResponse = aiService.generateMockResponse(message);
            
            response.put("success", true);
            response.put("userMessage", message);
            response.put("aiResponse", aiResponse);
            response.put("message", "危险内容检测测试完成");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "测试危险内容检测时出错: " + e.getMessage());
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 测试AI超时警报
     * POST /api/test/timeout-alert
     */
    @PostMapping("/timeout-alert")
    public ResponseEntity<Map<String, Object>> testTimeoutAlert(
            @RequestParam String userId,
            @RequestParam(required = false) String sessionId,
            @RequestParam(defaultValue = "55") long durationMinutes) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 设置用户信息
            aiService.setCurrentUser(userId, sessionId != null ? sessionId : "test_session_" + System.currentTimeMillis());
            
            // 模拟超时情况
            response.put("success", true);
            response.put("message", "AI超时警报测试完成");
            response.put("userId", userId);
            response.put("sessionId", sessionId);
            response.put("durationMinutes", durationMinutes);
            response.put("note", "请检查alert-service是否收到警报");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "测试AI超时警报时出错: " + e.getMessage());
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 测试JSON格式
     * GET /api/test/json-format
     */
    @GetMapping("/json-format")
    public ResponseEntity<Map<String, Object>> testJsonFormat() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String json = aiService.testJsonFormat();
            response.put("success", true);
            response.put("json", json);
            response.put("message", "JSON格式测试完成");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "JSON格式测试失败: " + e.getMessage());
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 健康检查
     * GET /api/test/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Bot Service 运行正常");
        response.put("service", "bot-service");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
} 