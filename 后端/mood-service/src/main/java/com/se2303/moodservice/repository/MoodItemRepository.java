package com.se2303.moodservice.repository;

import com.se2303.moodservice.model.MoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MoodItem实体的Repository接口
 */
@Repository
public interface MoodItemRepository extends JpaRepository<MoodItem, Long> {
    
    /**
     * 根据mood_id查找所有心情标签
     */
    List<MoodItem> findByMoodId(Long moodId);
    
    /**
     * 根据标签名称查找所有心情标签
     */
    List<MoodItem> findByLabel(String label);
    
    /**
     * 根据标签名称模糊查找
     */
    List<MoodItem> findByLabelContainingIgnoreCase(String label);
    
    /**
     * 根据mood_id删除所有心情标签
     */
    void deleteByMoodId(Long moodId);
    
    /**
     * 统计某个标签的使用次数
     */
    long countByLabel(String label);
    
    /**
     * 查找最常用的标签（按使用次数降序）
     */
    @Query("SELECT mi.label, COUNT(mi) as count FROM MoodItem mi GROUP BY mi.label ORDER BY count DESC")
    List<Object[]> findMostUsedLabels();
    
    /**
     * 查找指定用户的所有心情标签
     */
    @Query("SELECT mi FROM MoodItem mi JOIN mi.mood m WHERE m.userId = :userId")
    List<MoodItem> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查找指定用户在指定日期范围内的心情标签
     */
    @Query("SELECT mi FROM MoodItem mi JOIN mi.mood m WHERE m.userId = :userId AND m.date BETWEEN :startDate AND :endDate")
    List<MoodItem> findByUserIdAndDateBetween(@Param("userId") Long userId, 
                                             @Param("startDate") String startDate, 
                                             @Param("endDate") String endDate);
} 