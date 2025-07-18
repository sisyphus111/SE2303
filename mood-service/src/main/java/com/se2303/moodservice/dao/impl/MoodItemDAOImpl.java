package com.se2303.moodservice.dao.impl;

import com.se2303.moodservice.dao.MoodItemDAO;
import com.se2303.moodservice.model.MoodItem;
import com.se2303.moodservice.repository.MoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MoodItemDAO实现类
 * 使用Spring Data JPA Repository实现数据访问
 */
@Repository
public class MoodItemDAOImpl implements MoodItemDAO {
    
    @Autowired
    private MoodItemRepository moodItemRepository;
    
    @Override
    public MoodItem save(MoodItem moodItem) {
        return moodItemRepository.save(moodItem);
    }
    
    @Override
    public MoodItem findById(Long id) {
        Optional<MoodItem> optional = moodItemRepository.findById(id);
        return optional.orElse(null);
    }
    
    @Override
    public List<MoodItem> findByMoodId(Long moodId) {
        return moodItemRepository.findByMoodId(moodId);
    }
    
    @Override
    public List<MoodItem> findByLabel(String label) {
        return moodItemRepository.findByLabel(label);
    }
    
    @Override
    public List<MoodItem> findByLabelContainingIgnoreCase(String label) {
        return moodItemRepository.findByLabelContainingIgnoreCase(label);
    }
    
    @Override
    public void deleteByMoodId(Long moodId) {
        moodItemRepository.deleteByMoodId(moodId);
    }
    
    @Override
    public void deleteById(Long id) {
        moodItemRepository.deleteById(id);
    }
    
    @Override
    public void deleteAll() {
        moodItemRepository.deleteAll();
    }
    
    @Override
    public List<MoodItem> findAll() {
        return moodItemRepository.findAll();
    }
    
    @Override
    public long countByLabel(String label) {
        return moodItemRepository.countByLabel(label);
    }
    
    @Override
    public List<Object[]> findMostUsedLabels() {
        return moodItemRepository.findMostUsedLabels();
    }
    
    @Override
    public List<MoodItem> findByUserId(Long userId) {
        return moodItemRepository.findByUserId(userId);
    }
    
    @Override
    public List<MoodItem> findByUserIdAndDateBetween(Long userId, String startDate, String endDate) {
        return moodItemRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }
    
    @Override
    public List<MoodItem> saveAll(List<MoodItem> moodItems) {
        return moodItemRepository.saveAll(moodItems);
    }
    
    @Override
    public boolean existsByLabel(String label) {
        return moodItemRepository.countByLabel(label) > 0;
    }
    
    @Override
    public MoodItem findByMoodIdAndLabel(Long moodId, String label) {
        List<MoodItem> items = moodItemRepository.findByMoodId(moodId);
        return items.stream()
                .filter(item -> label.equals(item.getLabel()))
                .findFirst()
                .orElse(null);
    }
} 