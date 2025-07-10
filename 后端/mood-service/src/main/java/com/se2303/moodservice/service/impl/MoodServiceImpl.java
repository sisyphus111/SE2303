package com.se2303.moodservice.service.impl;

import com.se2303.moodservice.dao.MoodDAO;
import com.se2303.moodservice.dao.MoodItemDAO;
import com.se2303.moodservice.dto.CheckInRequest;
import com.se2303.moodservice.dto.DiaryRequest;
import com.se2303.moodservice.dto.MoodCheckInRequest;
import com.se2303.moodservice.dto.MoodCheckInResponse;
import com.se2303.moodservice.dto.MoodQueryRequest;
import com.se2303.moodservice.dto.TodayMoodResponse;
import com.se2303.moodservice.model.Mood;
import com.se2303.moodservice.model.MoodItem;
import com.se2303.moodservice.model.MoodLabel;
import com.se2303.moodservice.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 心情服务实现类
 * 使用DAO层进行数据访问
 */
@Service
@Transactional
public class MoodServiceImpl implements MoodService {
    
    @Autowired
    private MoodDAO moodDAO;
    
    @Autowired
    private MoodItemDAO moodItemDAO;
    
    @Override
    public MoodCheckInResponse checkIn(String userId, MoodCheckInRequest request) {
        // 检查是否已经打卡
        Optional<Mood> existingMood = moodDAO.findByUserIdAndDate(userId, request.getDate());
        
        Mood mood;
        if (existingMood.isPresent()) {
            // 更新现有记录
            mood = existingMood.get();
            mood.setScore(request.getScore());
            mood.setScoreCommitted(true);
            mood.setDiaryDescription(request.getDiaryDescription());
            mood.setDiaryTitle(request.getDiaryTitle());
            mood.setDiaryCommitted(request.getDiaryDescription() != null && !request.getDiaryDescription().trim().isEmpty());
            mood.setWeather(request.getWeather());
            
            // 删除旧的心情标签
            moodItemDAO.deleteByMoodId(mood.getId());
        } else {
            // 创建新记录
            mood = new Mood();
            mood.setUserId(userId);
            mood.setDate(request.getDate());
            mood.setScore(request.getScore());
            mood.setScoreCommitted(true);
            mood.setDiaryDescription(request.getDiaryDescription());
            mood.setDiaryTitle(request.getDiaryTitle());
            mood.setDiaryCommitted(request.getDiaryDescription() != null && !request.getDiaryDescription().trim().isEmpty());
            mood.setWeather(request.getWeather());
        }
        
        // 保存心情记录
        mood = moodDAO.save(mood);
        
        // 保存心情标签
        if (request.getMoodLabels() != null && !request.getMoodLabels().isEmpty()) {
            for (String label : request.getMoodLabels()) {
                // 验证标签是否有效
                if (MoodLabel.isValidLabel(label)) {
                    MoodItem moodItem = new MoodItem(label, mood);
                    moodItemDAO.save(moodItem);
                }
            }
        }
        
        return convertToResponse(mood);
    }
    
    @Override
    public MoodCheckInResponse checkInOnly(String userId, CheckInRequest request) {
        // 检查是否已经打卡
        Optional<Mood> existingMood = moodDAO.findByUserIdAndDate(userId, request.getDate());
        
        Mood mood;
        if (existingMood.isPresent()) {
            // 更新现有记录，只更新打卡相关字段
            mood = existingMood.get();
            mood.setScore(request.getScore());
            mood.setScoreCommitted(true);
            // 保持原有的日记内容不变
            
            // 删除旧的心情标签
            moodItemDAO.deleteByMoodId(mood.getId());
        } else {
            // 创建新记录，只包含打卡信息
            mood = new Mood();
            mood.setUserId(userId);
            mood.setDate(request.getDate());
            mood.setScore(request.getScore());
            mood.setScoreCommitted(true);
            mood.setDiaryCommitted(false); // 没有写日记
        }
        
        // 保存心情记录
        mood = moodDAO.save(mood);
        
        // 保存心情标签
        if (request.getMoodLabels() != null && !request.getMoodLabels().isEmpty()) {
            for (String label : request.getMoodLabels()) {
                // 验证标签是否有效
                if (MoodLabel.isValidLabel(label)) {
                    MoodItem moodItem = new MoodItem(label, mood);
                    moodItemDAO.save(moodItem);
                }
            }
        }
        
        return convertToResponse(mood);
    }
    
    @Override
    public MoodCheckInResponse writeDiaryOnly(String userId, DiaryRequest request) {
        // 检查是否已经存在记录
        Optional<Mood> existingMood = moodDAO.findByUserIdAndDate(userId, request.getDate());
        
        Mood mood;
        if (existingMood.isPresent()) {
            // 更新现有记录，只更新日记相关字段
            mood = existingMood.get();
            mood.setDiaryDescription(request.getDiaryDescription());
            mood.setDiaryTitle(request.getDiaryTitle());
            mood.setDiaryCommitted(true);
            mood.setWeather(request.getWeather());
            // 保持原有的打卡信息不变
        } else {
            // 创建新记录，只包含日记信息
            mood = new Mood();
            mood.setUserId(userId);
            mood.setDate(request.getDate());
            mood.setDiaryDescription(request.getDiaryDescription());
            mood.setDiaryTitle(request.getDiaryTitle());
            mood.setDiaryCommitted(true);
            mood.setWeather(request.getWeather());
            mood.setScoreCommitted(false); // 没有打卡
        }
        
        // 保存心情记录
        mood = moodDAO.save(mood);
        
        return convertToResponse(mood);
    }
    
    @Override
    @Transactional(readOnly = true)
    public MoodCheckInResponse findById(Long id) {
        Optional<Mood> mood = moodDAO.findById(id);
        return mood.map(this::convertToResponse).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public MoodCheckInResponse findByUserIdAndDate(String userId, LocalDate date) {
        Optional<Mood> mood = moodDAO.findByUserIdAndDate(userId, date);
        return mood.map(this::convertToResponse).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MoodCheckInResponse> findByUserId(String userId) {
        List<Mood> moods = moodDAO.findByUserIdOrderByDateDesc(userId);
        return moods.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MoodCheckInResponse> findByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate) {
        List<Mood> moods = moodDAO.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
        return moods.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasCheckedIn(String userId, LocalDate date) {
        return moodDAO.hasCheckedIn(userId, date);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasWrittenDiary(String userId, LocalDate date) {
        return moodDAO.hasWrittenDiary(userId, date);
    }
    
    @Override
    public MoodCheckInResponse updateMood(Long id, MoodCheckInRequest request) {
        Optional<Mood> existingMood = moodDAO.findById(id);
        if (existingMood.isPresent()) {
            Mood mood = existingMood.get();
            mood.setScore(request.getScore());
            mood.setScoreCommitted(true);
            mood.setDiaryDescription(request.getDiaryDescription());
            mood.setDiaryTitle(request.getDiaryTitle());
            mood.setDiaryCommitted(request.getDiaryDescription() != null && !request.getDiaryDescription().trim().isEmpty());
            mood.setWeather(request.getWeather());
            
            // 删除旧的心情标签
            moodItemDAO.deleteByMoodId(mood.getId());
            
            // 保存新的心情标签
            if (request.getMoodLabels() != null && !request.getMoodLabels().isEmpty()) {
                for (String label : request.getMoodLabels()) {
                    if (MoodLabel.isValidLabel(label)) {
                        MoodItem moodItem = new MoodItem(label, mood);
                        moodItemDAO.save(moodItem);
                    }
                }
            }
            
            mood = moodDAO.save(mood);
            return convertToResponse(mood);
        }
        return null;
    }
    
    @Override
    public void deleteMood(Long id) {
        moodDAO.deleteById(id);
    }
    
    @Override
    public MoodCheckInResponse deleteDiary(String userId, LocalDate date) {
        Optional<Mood> moodOptional = moodDAO.findByUserIdAndDate(userId, date);
        
        if (moodOptional.isPresent()) {
            Mood mood = moodOptional.get();
            
            // 清空日记内容并设置日记状态为false
            mood.setDiaryDescription(null);
            mood.setDiaryTitle(null);
            mood.setDiaryCommitted(false);
            
            // 保存更新后的记录
            mood = moodDAO.save(mood);
            
            return convertToResponse(mood);
        } else {
            // 如果没有找到记录，返回null
            return null;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getCheckInCount(String userId) {
        return moodDAO.countByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getCheckInCount(String userId, LocalDate startDate, LocalDate endDate) {
        return moodDAO.countByUserIdAndDateBetween(userId, startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getDiaryCount(String userId) {
        return moodDAO.findByUserIdAndDiaryCommittedTrueOrderByDateDesc(userId).size();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getDiaryCount(String userId, LocalDate startDate, LocalDate endDate) {
        List<Mood> moods = moodDAO.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
        return moods.stream()
                .filter(mood -> Boolean.TRUE.equals(mood.getDiaryCommitted()))
                .count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public TodayMoodResponse getTodayMood(String userId, LocalDate date) {
        Optional<Mood> moodOptional = moodDAO.findByUserIdAndDate(userId, date);
        
        if (moodOptional.isPresent()) {
            Mood mood = moodOptional.get();
            return convertToTodayMoodResponse(mood);
        } else {
            // 如果没有找到记录，返回空记录但包含日期信息
            return new TodayMoodResponse(
                    null, userId, date, null, false, null,
                    0, 0, 0, 0, "neutral", null, null
            );
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public TodayMoodResponse getTodayMood(String userId) {
        return getTodayMood(userId, LocalDate.now());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MoodCheckInResponse> queryMoods(MoodQueryRequest request) {
        List<Mood> moods;
        
        // 如果没有提供任何筛选条件，返回所有数据
        if (!request.hasFilters()) {
            moods = moodDAO.findAllByOrderByDateDesc();
        } else {
            // 构建查询条件
            if (request.getUserId() != null && 
                (request.getMoodLabels() == null || request.getMoodLabels().isEmpty()) && 
                request.getStartDate() == null && 
                request.getEndDate() == null) {
                // 只按用户ID查询
                moods = moodDAO.findByUserIdOrderByDateDesc(request.getUserId());
            } else if (request.getUserId() != null && 
                       request.getStartDate() != null && 
                       request.getEndDate() != null && 
                       (request.getMoodLabels() == null || request.getMoodLabels().isEmpty())) {
                // 按用户ID和时间范围查询
                moods = moodDAO.findByUserIdAndDateBetweenOrderByDateDesc(
                    request.getUserId(), request.getStartDate(), request.getEndDate());
            } else {
                // 复杂查询：需要按标签筛选
                moods = moodDAO.findAllByOrderByDateDesc();
                
                // 在内存中进行筛选
                moods = moods.stream()
                    .filter(mood -> {
                        // 用户ID筛选
                        if (request.getUserId() != null && !mood.getUserId().equals(request.getUserId())) {
                            return false;
                        }
                        
                        // 时间范围筛选
                        if (request.getStartDate() != null && mood.getDate().isBefore(request.getStartDate())) {
                            return false;
                        }
                        if (request.getEndDate() != null && mood.getDate().isAfter(request.getEndDate())) {
                            return false;
                        }
                        
                        // 情绪标签筛选
                        if (request.getMoodLabels() != null && !request.getMoodLabels().isEmpty()) {
                            List<String> moodLabels = moodItemDAO.findByMoodId(mood.getId())
                                .stream()
                                .map(MoodItem::getLabel)
                                .collect(Collectors.toList());
                            
                            // 检查是否包含任何指定的标签
                            boolean hasMatchingLabel = request.getMoodLabels().stream()
                                .anyMatch(moodLabels::contains);
                            
                            if (!hasMatchingLabel) {
                                return false;
                            }
                        }
                        
                        return true;
                    })
                    .collect(Collectors.toList());
            }
        }
        
        // 应用分页
        int start = request.getPage() * request.getSize();
        int end = Math.min(start + request.getSize(), moods.size());
        
        if (start >= moods.size()) {
            return List.of();
        }
        
        List<Mood> pagedMoods = moods.subList(start, end);
        
        // 转换为响应对象
        return pagedMoods.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * 将Mood实体转换为MoodCheckInResponse
     */
    private MoodCheckInResponse convertToResponse(Mood mood) {
        // 获取心情标签
        List<String> moodLabels = moodItemDAO.findByMoodId(mood.getId())
                .stream()
                .map(MoodItem::getLabel)
                .collect(Collectors.toList());
        
        return new MoodCheckInResponse(
                mood.getId(),
                mood.getUserId(),
                mood.getDate(),
                mood.getScore(),
                mood.getScoreCommitted(),
                mood.getDiaryDescription(),
                mood.getDiaryCommitted(),
                mood.getDiaryTitle(),
                mood.getWeather(),
                moodLabels,
                mood.getCreatedAt(),
                mood.getUpdatedAt()
        );
    }
    
    /**
     * 将Mood实体转换为TodayMoodResponse，包含标签权值计算
     */
    private TodayMoodResponse convertToTodayMoodResponse(Mood mood) {
        // 获取心情标签
        List<String> moodLabels = moodItemDAO.findByMoodId(mood.getId())
                .stream()
                .map(MoodItem::getLabel)
                .collect(Collectors.toList());
        
        // 计算标签权值
        int positiveLabelCount = MoodLabel.countPositiveLabels(moodLabels);
        int neutralLabelCount = MoodLabel.countNeutralLabels(moodLabels);
        int negativeLabelCount = MoodLabel.countNegativeLabels(moodLabels);
        int totalMoodWeight = MoodLabel.calculateTotalWeight(moodLabels);
        String moodTrend = MoodLabel.getMoodTrend(totalMoodWeight);
        
        return new TodayMoodResponse(
                mood.getId(),
                mood.getUserId(),
                mood.getDate(),
                mood.getScore(),
                mood.getScoreCommitted(),
                moodLabels,
                positiveLabelCount,
                neutralLabelCount,
                negativeLabelCount,
                totalMoodWeight,
                moodTrend,
                mood.getCreatedAt(),
                mood.getUpdatedAt()
        );
    }
} 