package com.se2303.alertservice.repository;

import com.se2303.alertservice.model.Alert;
import com.se2303.alertservice.model.AlertStatus;
import com.se2303.alertservice.model.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 警报数据访问接口
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    /**
     * 根据用户ID查找警报
     */
    List<Alert> findByUserIdOrderByCreatedAtDesc(String userId);
    
    /**
     * 根据状态查找警报
     */
    List<Alert> findByStatusOrderByCreatedAtDesc(AlertStatus status);
    
    /**
     * 根据状态分页查找警报
     */
    Page<Alert> findByStatusOrderByCreatedAtDesc(AlertStatus status, Pageable pageable);
    
    /**
     * 根据类型查找警报
     */
    List<Alert> findByAlertTypeOrderByCreatedAtDesc(AlertType alertType);
    
    /**
     * 根据用户ID和状态查找警报
     */
    List<Alert> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, AlertStatus status);
    
    /**
     * 根据心理咨询师ID查找分配的警报
     */
    List<Alert> findByAssignedCounselorIdOrderByCreatedAtDesc(String counselorId);
    
    /**
     * 根据心理咨询师ID和状态查找警报
     */
    List<Alert> findByAssignedCounselorIdAndStatusOrderByCreatedAtDesc(String counselorId, AlertStatus status);
    
    /**
     * 查找待处理的警报（未分配）
     */
    @Query("SELECT a FROM Alert a WHERE a.status = 'PENDING' ORDER BY a.createdAt DESC")
    List<Alert> findPendingAlerts();
    
    /**
     * 查找指定时间范围内的警报
     */
    @Query("SELECT a FROM Alert a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    List<Alert> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    /**
     * 统计指定状态的警报数量
     */
    long countByStatus(AlertStatus status);
    
    /**
     * 统计指定用户的警报数量
     */
    long countByUserId(String userId);
    
    /**
     * 统计指定心理咨询师分配的警报数量
     */
    long countByAssignedCounselorId(String counselorId);
    
    /**
     * 查找最近创建的警报
     */
    @Query("SELECT a FROM Alert a ORDER BY a.createdAt DESC")
    Page<Alert> findRecentAlerts(Pageable pageable);
    
    /**
     * 根据多个条件查找警报
     */
    @Query("SELECT a FROM Alert a WHERE " +
           "(:userId IS NULL OR a.userId = :userId) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:alertType IS NULL OR a.alertType = :alertType) AND " +
           "(:alertLevel IS NULL OR a.alertLevel = :alertLevel) " +
           "ORDER BY a.createdAt DESC")
    Page<Alert> findByConditions(
        @Param("userId") String userId,
        @Param("status") com.se2303.alertservice.model.AlertStatus status,
        @Param("alertType") com.se2303.alertservice.model.AlertType alertType,
        @Param("alertLevel") com.se2303.alertservice.model.AlertLevel alertLevel,
        Pageable pageable
    );
} 