package com.se2303.talkservice.controller;

import com.se2303.talkservice.dto.ApiResponse;
import com.se2303.talkservice.manager.UserSessionManager;
import com.se2303.talkservice.model.OnlineUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 用户会话控制器
 * 提供用户会话和心跳相关的REST API接口
 */
@RestController
@RequestMapping("/api/session")
public class UserSessionController {
    
    @Autowired
    private UserSessionManager userSessionManager;
    
    /**
     * 获取在线用户列表
     * GET /api/session/online-users
     */
    @GetMapping("/online-users")
    public ResponseEntity<ApiResponse> getOnlineUsers() {
        try {
            List<OnlineUser> onlineUsers = userSessionManager.getAllOnlineUsers();
            return ResponseEntity.ok(ApiResponse.success(onlineUsers, "获取在线用户列表成功"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取在线用户列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取在线用户数量
     * GET /api/session/online-count
     */
    @GetMapping("/online-count")
    public ResponseEntity<ApiResponse> getOnlineUserCount() {
        try {
            int count = userSessionManager.getOnlineUserCount();
            return ResponseEntity.ok(ApiResponse.success(Map.of("count", count), "获取在线用户数量成功"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取在线用户数量失败: " + e.getMessage()));
        }
    }
    
    /**
     * 检查用户是否在线
     * GET /api/session/online-status/{userId}
     */
    @GetMapping("/online-status/{userId}")
    public ResponseEntity<ApiResponse> checkUserOnlineStatus(@PathVariable String userId) {
        try {
            boolean isOnline = userSessionManager.isUserOnline(userId);
            return ResponseEntity.ok(ApiResponse.success(Map.of("isOnline", isOnline), "检查用户在线状态成功"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("检查用户在线状态失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户完整状态信息
     * GET /api/session/user-status/{userId}
     */
    @GetMapping("/user-status/{userId}")
    public ResponseEntity<ApiResponse> getUserStatus(@PathVariable String userId) {
        try {
            Map<String, Object> userStatus = userSessionManager.getUserStatus(userId);
            return ResponseEntity.ok(ApiResponse.success(userStatus, "获取用户状态信息成功"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取用户状态信息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取在线用户统计信息
     * GET /api/session/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getSessionStats() {
        try {
            Map<String, Object> stats = userSessionManager.getCompleteStats();
            return ResponseEntity.ok(ApiResponse.success(stats, "获取会话统计信息成功"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取会话统计信息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取心跳统计信息
     * GET /api/session/heartbeat-stats
     */
    @GetMapping("/heartbeat-stats")
    public ResponseEntity<ApiResponse> getHeartbeatStats() {
        try {
            Map<String, Object> stats = userSessionManager.getHeartbeatStats();
            return ResponseEntity.ok(ApiResponse.success(stats, "获取心跳统计信息成功"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取心跳统计信息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 手动清理超时用户
     * POST /api/session/cleanup-timeout
     */
    @PostMapping("/cleanup-timeout")
    public ResponseEntity<ApiResponse> cleanupTimeoutUsers() {
        try {
            userSessionManager.cleanupTimeoutUsers();
            return ResponseEntity.ok(ApiResponse.success(null, "清理超时用户成功"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("清理超时用户失败: " + e.getMessage()));
        }
    }
    
    /**
     * 手动清理非活跃用户
     * POST /api/session/cleanup-inactive?minutes=30
     */
    @PostMapping("/cleanup-inactive")
    public ResponseEntity<ApiResponse> cleanupInactiveUsers(
            @RequestParam(defaultValue = "30") long minutes) {
        try {
            int cleanedCount = userSessionManager.cleanupInactiveUsers(minutes);
            return ResponseEntity.ok(ApiResponse.success(
                Map.of("cleanedCount", cleanedCount), 
                "清理非活跃用户成功，清理了 " + cleanedCount + " 个用户"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("清理非活跃用户失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取特定类型的在线用户
     * GET /api/session/users-by-type?userType=user
     */
    @GetMapping("/users-by-type")
    public ResponseEntity<ApiResponse> getUsersByType(@RequestParam String userType) {
        try {
            List<OnlineUser> users = userSessionManager.getOnlineUsersByType(userType);
            return ResponseEntity.ok(ApiResponse.success(users, "获取用户类型列表成功"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取用户类型列表失败: " + e.getMessage()));
        }
    }
    

} 