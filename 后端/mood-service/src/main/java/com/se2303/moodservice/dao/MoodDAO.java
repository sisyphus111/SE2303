package com.se2303.moodservice.dao;

import com.se2303.moodservice.model.Mood;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Mood数据访问对象接口
 * 定义与Mood实体相关的数据访问操作
 */
public interface MoodDAO {
    
    /**
     * 保存心情记录
     */
    Mood save(Mood mood);
    
    /**
     * 根据ID查找心情记录
     */
    Optional<Mood> findById(Long id);
    
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
     * 根据用户ID查找已打卡的记录
     */
    List<Mood> findByUserIdAndScoreCommittedTrueOrderByDateDesc(String userId);
    
    /**
     * 根据用户ID查找已写日记的记录
     */
    List<Mood> findByUserIdAndDiaryCommittedTrueOrderByDateDesc(String userId);
    
    /**
     * 删除心情记录
     */
    void deleteById(Long id);
    
    /**
     * 删除所有心情记录
     */
    void deleteAll();
    
    /**
     * 获取所有心情记录
     */
    List<Mood> findAll();
    
    /**
     * 获取所有心情记录，按日期降序排列
     */
    List<Mood> findAllByOrderByDateDesc();
    
    /**
     * 检查用户是否已打卡（基于scoreCommitted字段）
     */
    boolean hasCheckedIn(String userId, LocalDate date);
    
    /**
     * 检查用户是否已写日记（基于diaryCommitted字段）
     */
    boolean hasWrittenDiary(String userId, LocalDate date);
} 