package com.se2303.moodservice.dao;

import com.se2303.moodservice.model.MoodItem;
import java.util.List;

/**
 * MoodItem数据访问对象接口
 * 定义与MoodItem实体相关的数据访问操作
 */
public interface MoodItemDAO {
    
    /**
     * 保存心情标签
     */
    MoodItem save(MoodItem moodItem);
    
    /**
     * 根据ID查找心情标签
     */
    MoodItem findById(Long id);
    
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
     * 删除心情标签
     */
    void deleteById(Long id);
    
    /**
     * 删除所有心情标签
     */
    void deleteAll();
    
    /**
     * 获取所有心情标签
     */
    List<MoodItem> findAll();
    
    /**
     * 统计某个标签的使用次数
     */
    long countByLabel(String label);
    
    /**
     * 查找最常用的标签（按使用次数降序）
     */
    List<Object[]> findMostUsedLabels();
    
    /**
     * 查找指定用户的所有心情标签
     */
    List<MoodItem> findByUserId(Long userId);
    
    /**
     * 查找指定用户在指定日期范围内的心情标签
     */
    List<MoodItem> findByUserIdAndDateBetween(Long userId, String startDate, String endDate);
    
    /**
     * 批量保存心情标签
     */
    List<MoodItem> saveAll(List<MoodItem> moodItems);
    
    /**
     * 检查标签是否存在
     */
    boolean existsByLabel(String label);
    
    /**
     * 根据mood_id和label查找心情标签
     */
    MoodItem findByMoodIdAndLabel(Long moodId, String label);
} 