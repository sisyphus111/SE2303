package com.se2303.moodservice.service;

import com.se2303.moodservice.dto.CheckInRequest;
import com.se2303.moodservice.dto.DiaryRequest;
import com.se2303.moodservice.dto.MoodCheckInRequest;
import com.se2303.moodservice.dto.MoodCheckInResponse;
import com.se2303.moodservice.dto.MoodQueryRequest;
import com.se2303.moodservice.dto.TodayMoodResponse;
import com.se2303.moodservice.model.Mood;

import java.time.LocalDate;
import java.util.List;

/**
 * 心情服务接口
 */
public interface MoodService {
    
    /**
     * 用户心情打卡（包含日记）
     */
    MoodCheckInResponse checkIn(String userId, MoodCheckInRequest request);
    
    /**
     * 只打卡不写日记
     */
    MoodCheckInResponse checkInOnly(String userId, CheckInRequest request);
    
    /**
     * 只写日记不打卡
     */
    MoodCheckInResponse writeDiaryOnly(String userId, DiaryRequest request);
    
    /**
     * 根据ID查找心情记录
     */
    MoodCheckInResponse findById(Long id);
    
    /**
     * 根据用户ID和日期查找心情记录
     */
    MoodCheckInResponse findByUserIdAndDate(String userId, LocalDate date);
    
    /**
     * 根据用户ID查找所有心情记录
     */
    List<MoodCheckInResponse> findByUserId(String userId);
    
    /**
     * 根据用户ID和日期范围查找心情记录
     */
    List<MoodCheckInResponse> findByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 检查用户在某一天是否已经打卡
     */
    boolean hasCheckedIn(String userId, LocalDate date);
    
    /**
     * 检查用户在某一天是否已经写日记
     */
    boolean hasWrittenDiary(String userId, LocalDate date);
    
    /**
     * 更新心情记录
     */
    MoodCheckInResponse updateMood(Long id, MoodCheckInRequest request);
    
    /**
     * 删除心情记录
     */
    void deleteMood(Long id);
    
    /**
     * 删除某一天的日记内容
     */
    MoodCheckInResponse deleteDiary(String userId, LocalDate date);
    
    /**
     * 获取用户打卡统计
     */
    long getCheckInCount(String userId);
    
    /**
     * 获取用户在指定日期范围内的打卡统计
     */
    long getCheckInCount(String userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取用户写日记统计
     */
    long getDiaryCount(String userId);
    
    /**
     * 获取用户在指定日期范围内的写日记统计
     */
    long getDiaryCount(String userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取今日心情（包含标签权值计算）
     * 积极标签权值为1，中性标签权值为0，消极标签权值为-1
     */
    TodayMoodResponse getTodayMood(String userId, LocalDate date);
    
    /**
     * 获取今日心情（使用当前日期）
     */
    TodayMoodResponse getTodayMood(String userId);
    
    /**
     * 灵活查询心情记录
     * 支持按用户、情绪标签、时间范围进行筛选
     * 如果不提供任何筛选条件，则返回所有数据
     */
    List<MoodCheckInResponse> queryMoods(MoodQueryRequest request);
} 