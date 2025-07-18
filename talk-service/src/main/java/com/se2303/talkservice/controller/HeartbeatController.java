package com.se2303.talkservice.controller;

import com.se2303.talkservice.dto.ApiResponse;
import com.se2303.talkservice.manager.UserSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 心跳控制器
 * 提供心跳相关的API接口
 */
@RestController
@RequestMapping("/api/heartbeat")
public class HeartbeatController {
    
    private static final Logger logger = LoggerFactory.getLogger(HeartbeatController.class);
    
    @Autowired
    private UserSessionManager userSessionManager;
    
    /**
     * 获取心跳统计信息
     * GET /api/heartbeat/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getHeartbeatStats(HttpServletRequest request) {
        try {
            // 从请求头获取用户ID
            String userIdStr = request.getHeader("X-User-Id");
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("缺少用户ID"));
            }
            
            // 获取完整统计信息
            Map<String, Object> stats = userSessionManager.getCompleteStats();
            
            return ResponseEntity.ok(ApiResponse.success(stats, "获取心跳统计信息成功"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取心跳统计信息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户心跳状态
     * GET /api/heartbeat/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserHeartbeatStatus(
            @PathVariable String userId,
            HttpServletRequest request) {
        
        try {
            // 从请求头获取用户ID
            String requestUserId = request.getHeader("X-User-Id");
            if (requestUserId == null || requestUserId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("缺少用户ID"));
            }
            
            // 获取用户心跳状态
            LocalDateTime lastHeartbeat = userSessionManager.getLastHeartbeat(userId);
            boolean isTimeout = userSessionManager.isUserTimeout(userId);
            boolean isOnline = userSessionManager.isUserOnline(userId);
            
            Map<String, Object> status = Map.of(
                "userId", userId,
                "lastHeartbeat", lastHeartbeat,
                "isTimeout", isTimeout,
                "isOnline", isOnline,
                "currentTime", LocalDateTime.now()
            );
            
            return ResponseEntity.ok(ApiResponse.success(status, "获取用户心跳状态成功"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取用户心跳状态失败: " + e.getMessage()));
        }
    }
    
    /**
     * 手动发送心跳（用于测试）
     * POST /api/heartbeat/send
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendHeartbeat(HttpServletRequest request) {
        try {
            // 从请求头获取用户ID
            String userIdStr = request.getHeader("X-User-Id");
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("缺少用户ID"));
            }
            
            // 模拟心跳发送
            // 注意：实际的心跳应该通过WebSocket发送
            logger.info("收到手动心跳请求: userId={}", userIdStr);
            
            Map<String, Object> result = Map.of(
                "userId", userIdStr,
                "timestamp", LocalDateTime.now(),
                "message", "心跳已记录（请通过WebSocket发送心跳）"
            );
            
            return ResponseEntity.ok(ApiResponse.success(result, "心跳已记录"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("发送心跳失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取在线用户列表
     * GET /api/heartbeat/online-users
     */
    @GetMapping("/online-users")
    public ResponseEntity<ApiResponse> getOnlineUsers(HttpServletRequest request) {
        try {
            // 从请求头获取用户ID
            String userIdStr = request.getHeader("X-User-Id");
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("缺少用户ID"));
            }
            
            // 获取在线用户列表
            var onlineUsers = userSessionManager.getAllOnlineUsers();
            
            return ResponseEntity.ok(ApiResponse.success(onlineUsers, "获取在线用户列表成功"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取在线用户列表失败: " + e.getMessage()));
        }
    }
} 