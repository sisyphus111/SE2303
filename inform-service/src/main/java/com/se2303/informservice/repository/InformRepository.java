package com.se2303.informservice.repository;

import com.se2303.informservice.entity.Inform;
import com.se2303.informservice.entity.InformType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InformRepository extends JpaRepository<Inform, Long> {
    
    // 获取用户的通知列表，按创建时间倒序
    Page<Inform> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // 添加兼容的方法
    Page<Inform> findByUserId(Long userId, Pageable pageable);
    
    // 获取用户未读通知数量
    long countByUserIdAndIsReadFalse(Long userId);
    
    // 获取用户未读通知列表
    List<Inform> findByUserIdAndIsReadFalse(Long userId);
    
    // 获取用户最近的未读通知
    List<Inform> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    
    // 获取指定时间后的新通知
    List<Inform> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(Long userId, LocalDateTime after);
    
    // 删除过期通知
    void deleteByCreatedAtBefore(LocalDateTime cutoffTime);
    
    // 批量标记已读
    @Modifying
    @Query(value = "UPDATE informs SET is_read = TRUE, read_at = CURRENT_TIMESTAMP WHERE user_id = :userId AND is_read = FALSE", nativeQuery = true)
    int markAllAsReadByUserId(@Param("userId") Long userId);

    @Modifying
    @Query(value = "UPDATE informs SET is_read = TRUE, read_at = CURRENT_TIMESTAMP WHERE id = :id AND user_id = :userId AND is_read = FALSE", nativeQuery = true)
    int markAsReadByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
    
    // 删除过期通知（超过30天的已读通知）
    @Modifying
    @Query(value = "DELETE FROM informs WHERE is_read = TRUE AND read_at < :before", nativeQuery = true)
    int deleteReadNotificationsBefore(@Param("before") LocalDateTime before);
    
    // 按类型获取用户通知
    Page<Inform> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, InformType type, Pageable pageable);
}
