package com.se2303.alertservice.service;

import com.se2303.alertservice.dto.AlertResponse;
import com.se2303.alertservice.dto.CreateAlertRequest;
import com.se2303.alertservice.model.Alert;
import com.se2303.alertservice.model.AlertLevel;
import com.se2303.alertservice.model.AlertStatus;
import com.se2303.alertservice.model.AlertType;
import com.se2303.alertservice.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 警报服务类
 */
@Service
@Transactional
public class AlertService {
    
    @Autowired
    private AlertRepository alertRepository;
    
    /**
     * 创建警报
     */
    public AlertResponse createAlert(CreateAlertRequest request) {
        Alert alert = new Alert();
        alert.setUserId(request.getUserId());
        alert.setAlertType(request.getAlertType());
        alert.setAlertLevel(request.getAlertLevel());
        alert.setDescription(request.getDescription());
        alert.setOperationDetails(request.getOperationDetails());
        alert.setAiSessionId(request.getAiSessionId());
        alert.setAiTimeoutDuration(request.getAiTimeoutDuration());
        alert.setStatus(AlertStatus.PENDING);
        
        Alert savedAlert = alertRepository.save(alert);
        return new AlertResponse(savedAlert);
    }
    
    /**
     * 根据ID获取警报
     */
    public AlertResponse findById(Long id) {
        Optional<Alert> alert = alertRepository.findById(id);
        return alert.map(AlertResponse::new).orElse(null);
    }
    
    /**
     * 根据用户ID获取警报列表
     */
    public List<AlertResponse> findByUserId(String userId) {
        List<Alert> alerts = alertRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return alerts.stream().map(AlertResponse::new).collect(Collectors.toList());
    }
    
    /**
     * 根据状态获取警报列表
     */
    public List<AlertResponse> findByStatus(AlertStatus status) {
        List<Alert> alerts = alertRepository.findByStatusOrderByCreatedAtDesc(status);
        return alerts.stream().map(AlertResponse::new).collect(Collectors.toList());
    }
    
    /**
     * 获取待处理警报列表（供心理咨询师查看）
     */
    public List<AlertResponse> getPendingAlerts() {
        List<Alert> alerts = alertRepository.findPendingAlerts();
        return alerts.stream().map(AlertResponse::new).collect(Collectors.toList());
    }
    
    /**
     * 根据心理咨询师ID获取分配的警报
     */
    public List<AlertResponse> findByCounselorId(String counselorId) {
        List<Alert> alerts = alertRepository.findByAssignedCounselorIdOrderByCreatedAtDesc(counselorId);
        return alerts.stream().map(AlertResponse::new).collect(Collectors.toList());
    }
    
    /**
     * 分配警报给心理咨询师
     */
    public AlertResponse assignAlert(Long alertId, String counselorId) {
        Optional<Alert> alertOpt = alertRepository.findById(alertId);
        if (alertOpt.isPresent()) {
            Alert alert = alertOpt.get();
            alert.setAssignedCounselorId(counselorId);
            alert.setStatus(AlertStatus.ASSIGNED);
            alert.setUpdatedAt(LocalDateTime.now());
            
            Alert savedAlert = alertRepository.save(alert);
            return new AlertResponse(savedAlert);
        }
        return null;
    }
    
    /**
     * 更新警报状态
     */
    public AlertResponse updateAlertStatus(Long alertId, AlertStatus status) {
        Optional<Alert> alertOpt = alertRepository.findById(alertId);
        if (alertOpt.isPresent()) {
            Alert alert = alertOpt.get();
            alert.setStatus(status);
            alert.setUpdatedAt(LocalDateTime.now());
            
            Alert savedAlert = alertRepository.save(alert);
            return new AlertResponse(savedAlert);
        }
        return null;
    }
    
    /**
     * 添加回访备注
     */
    public AlertResponse addFollowUpNotes(Long alertId, String notes) {
        Optional<Alert> alertOpt = alertRepository.findById(alertId);
        if (alertOpt.isPresent()) {
            Alert alert = alertOpt.get();
            alert.setFollowUpNotes(notes);
            alert.setFollowUpDate(LocalDateTime.now());
            alert.setUpdatedAt(LocalDateTime.now());
            
            Alert savedAlert = alertRepository.save(alert);
            return new AlertResponse(savedAlert);
        }
        return null;
    }
    
    /**
     * 分页查询警报
     */
    public Page<AlertResponse> findAlertsByConditions(
            String userId,
            com.se2303.alertservice.model.AlertStatus status,
            com.se2303.alertservice.model.AlertType alertType,
            com.se2303.alertservice.model.AlertLevel alertLevel,
            Pageable pageable
    ) {
        Page<Alert> alerts = alertRepository.findByConditions(userId, status, alertType, alertLevel, pageable);
        return alerts.map(AlertResponse::new);
    }
    
    /**
     * 获取警报统计信息
     */
    public AlertStatistics getAlertStatistics() {
        AlertStatistics stats = new AlertStatistics();
        stats.setTotalAlerts(alertRepository.count());
        stats.setPendingAlerts(alertRepository.countByStatus(AlertStatus.PENDING));
        stats.setAssignedAlerts(alertRepository.countByStatus(AlertStatus.ASSIGNED));
        stats.setInProgressAlerts(alertRepository.countByStatus(AlertStatus.IN_PROGRESS));
        stats.setCompletedAlerts(alertRepository.countByStatus(AlertStatus.COMPLETED));
        return stats;
    }
    
    /**
     * 删除警报
     */
    public boolean deleteAlert(Long id) {
        if (alertRepository.existsById(id)) {
            alertRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * 警报统计内部类
     */
    public static class AlertStatistics {
        private long totalAlerts;
        private long pendingAlerts;
        private long assignedAlerts;
        private long inProgressAlerts;
        private long completedAlerts;
        
        // Getter和Setter方法
        public long getTotalAlerts() {
            return totalAlerts;
        }
        
        public void setTotalAlerts(long totalAlerts) {
            this.totalAlerts = totalAlerts;
        }
        
        public long getPendingAlerts() {
            return pendingAlerts;
        }
        
        public void setPendingAlerts(long pendingAlerts) {
            this.pendingAlerts = pendingAlerts;
        }
        
        public long getAssignedAlerts() {
            return assignedAlerts;
        }
        
        public void setAssignedAlerts(long assignedAlerts) {
            this.assignedAlerts = assignedAlerts;
        }
        
        public long getInProgressAlerts() {
            return inProgressAlerts;
        }
        
        public void setInProgressAlerts(long inProgressAlerts) {
            this.inProgressAlerts = inProgressAlerts;
        }
        
        public long getCompletedAlerts() {
            return completedAlerts;
        }
        
        public void setCompletedAlerts(long completedAlerts) {
            this.completedAlerts = completedAlerts;
        }
    }
} 