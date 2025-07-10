package com.se2303.alertservice.controller;

import com.se2303.alertservice.dto.AlertResponse;
import com.se2303.alertservice.dto.ApiResponse;
import com.se2303.alertservice.dto.CreateAlertRequest;
import com.se2303.alertservice.model.AlertLevel;
import com.se2303.alertservice.model.AlertStatus;
import com.se2303.alertservice.model.AlertType;
import com.se2303.alertservice.service.AlertService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 警报控制器
 */
@RestController
@RequestMapping("/api/alert")
public class AlertController {
    
    @Autowired
    private AlertService alertService;
    
    /**
     * 创建警报（供其他服务调用）
     * POST /api/alert/create
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createAlert(@Valid @RequestBody CreateAlertRequest request) {
        try {
            AlertResponse response = alertService.createAlert(request);
            return ResponseEntity.ok(ApiResponse.success(response, "警报创建成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("警报创建失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取待回访用户列表（供心理咨询师查看）
     * GET /api/alert/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse> getPendingAlerts() {
        try {
            List<AlertResponse> alerts = alertService.getPendingAlerts();
            return ResponseEntity.ok(ApiResponse.success(alerts, "获取待处理警报成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取待处理警报失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据ID获取警报详情
     * GET /api/alert/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getAlertById(@PathVariable Long id) {
        AlertResponse response = alertService.findById(id);
        if (response != null) {
            return ResponseEntity.ok(ApiResponse.success(response, "获取警报详情成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("警报不存在"));
        }
    }
    
    /**
     * 根据用户ID获取警报列表
     * GET /api/alert/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getAlertsByUserId(@PathVariable String userId) {
        try {
            List<AlertResponse> alerts = alertService.findByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(alerts, "获取用户警报列表成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取用户警报列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据状态获取警报列表
     * GET /api/alert/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse> getAlertsByStatus(@PathVariable AlertStatus status) {
        try {
            List<AlertResponse> alerts = alertService.findByStatus(status);
            return ResponseEntity.ok(ApiResponse.success(alerts, "获取状态警报列表成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取状态警报列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 分配警报给心理咨询师
     * POST /api/alert/{id}/assign
     */
    @PostMapping("/{id}/assign")
    public ResponseEntity<ApiResponse> assignAlert(
            @PathVariable Long id,
            @RequestParam String counselorId) {
        try {
            AlertResponse response = alertService.assignAlert(id, counselorId);
            if (response != null) {
                return ResponseEntity.ok(ApiResponse.success(response, "警报分配成功"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("警报不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("警报分配失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新警报状态
     * PUT /api/alert/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateAlertStatus(
            @PathVariable Long id,
            @RequestParam AlertStatus status) {
        try {
            AlertResponse response = alertService.updateAlertStatus(id, status);
            if (response != null) {
                return ResponseEntity.ok(ApiResponse.success(response, "警报状态更新成功"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("警报不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("警报状态更新失败: " + e.getMessage()));
        }
    }
    
    /**
     * 添加回访备注
     * POST /api/alert/{id}/follow-up
     */
    @PostMapping("/{id}/follow-up")
    public ResponseEntity<ApiResponse> addFollowUpNotes(
            @PathVariable Long id,
            @RequestParam String notes) {
        try {
            AlertResponse response = alertService.addFollowUpNotes(id, notes);
            if (response != null) {
                return ResponseEntity.ok(ApiResponse.success(response, "回访备注添加成功"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("警报不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("回访备注添加失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据心理咨询师ID获取分配的警报
     * GET /api/alert/counselor/{counselorId}
     */
    @GetMapping("/counselor/{counselorId}")
    public ResponseEntity<ApiResponse> getAlertsByCounselorId(@PathVariable String counselorId) {
        try {
            List<AlertResponse> alerts = alertService.findByCounselorId(counselorId);
            return ResponseEntity.ok(ApiResponse.success(alerts, "获取心理咨询师警报列表成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取心理咨询师警报列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 分页查询警报
     * GET /api/alert/search
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchAlerts(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) AlertStatus status,
            @RequestParam(required = false) AlertType alertType,
            @RequestParam(required = false) String counselorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AlertResponse> alerts = alertService.findAlertsByConditions(
                    userId, status, alertType, counselorId, pageable);
            return ResponseEntity.ok(ApiResponse.success(alerts, "查询警报成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("查询警报失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取警报统计信息
     * GET /api/alert/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse> getAlertStatistics() {
        try {
            AlertService.AlertStatistics stats = alertService.getAlertStatistics();
            return ResponseEntity.ok(ApiResponse.success(stats, "获取警报统计成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取警报统计失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除警报
     * DELETE /api/alert/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteAlert(@PathVariable Long id) {
        try {
            boolean deleted = alertService.deleteAlert(id);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("警报删除成功"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("警报不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("警报删除失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取所有警报类型
     * GET /api/alert/types
     */
    @GetMapping("/types")
    public ResponseEntity<ApiResponse> getAlertTypes() {
        Map<String, String> types = new HashMap<>();
        for (AlertType type : AlertType.values()) {
            types.put(type.name(), type.getDescription());
        }
        return ResponseEntity.ok(ApiResponse.success(types, "获取警报类型成功"));
    }
    
    /**
     * 获取所有警报级别
     * GET /api/alert/levels
     */
    @GetMapping("/levels")
    public ResponseEntity<ApiResponse> getAlertLevels() {
        Map<String, String> levels = new HashMap<>();
        for (AlertLevel level : AlertLevel.values()) {
            levels.put(level.name(), level.getDescription());
        }
        return ResponseEntity.ok(ApiResponse.success(levels, "获取警报级别成功"));
    }
    
    /**
     * 获取所有警报状态
     * GET /api/alert/statuses
     */
    @GetMapping("/statuses")
    public ResponseEntity<ApiResponse> getAlertStatuses() {
        Map<String, String> statuses = new HashMap<>();
        for (AlertStatus status : AlertStatus.values()) {
            statuses.put(status.name(), status.getDescription());
        }
        return ResponseEntity.ok(ApiResponse.success(statuses, "获取警报状态成功"));
    }
    
    /**
     * 健康检查
     * GET /api/alert/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Alert Service");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(ApiResponse.success(health, "服务健康"));
    }
} 