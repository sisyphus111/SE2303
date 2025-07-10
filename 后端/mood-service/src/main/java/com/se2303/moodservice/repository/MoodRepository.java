package com.se2303.moodservice.repository;

import com.se2303.moodservice.model.Mood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Mood实体的Repository接口
 */
@Repository
public interface MoodRepository extends JpaRepository<Mood, Long> {
    
    /**
     * 根据用户ID和日期查找心情记录
     */
    Optional<Mood> findByUserIdAndDate(String userId, LocalDate date);
    
    /**
     * 根据用户ID查找所有心情记录，按日期降序排列
     */
    List<Mood> findByUserIdOrderByDateDesc(String userId);
    
    /**
     * 根据用户ID和日期范围查找心情记录
     */
    List<Mood> findByUserIdAndDateBetweenOrderByDateDesc(String userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 检查用户在某一天是否已经打卡
     */
    boolean existsByUserIdAndDate(String userId, LocalDate date);
    
    /**
     * 根据用户ID统计打卡次数
     */
    long countByUserId(String userId);
    
    /**
     * 根据用户ID和日期范围统计打卡次数
     */
    long countByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 根据用户ID查找最近的心情记录
     */
    @Query("SELECT m FROM Mood m WHERE m.userId = :userId ORDER BY m.date DESC")
    List<Mood> findRecentMoodsByUserId(@Param("userId") String userId);
    
    /**
     * 根据用户ID查找指定数量的最近心情记录
     */
    @Query("SELECT m FROM Mood m WHERE m.userId = :userId ORDER BY m.date DESC LIMIT :limit")
    List<Mood> findRecentMoodsByUserIdWithLimit(@Param("userId") String userId, @Param("limit") int limit);
    
    /**
     * 根据用户ID查找已打卡的记录
     */
    List<Mood> findByUserIdAndScoreCommittedTrueOrderByDateDesc(String userId);
    
    /**
     * 根据用户ID查找已写日记的记录
     */
    List<Mood> findByUserIdAndDiaryCommittedTrueOrderByDateDesc(String userId);
    
    /**
     * 获取所有心情记录，按日期降序排列
     */
    List<Mood> findAllByOrderByDateDesc();
} 