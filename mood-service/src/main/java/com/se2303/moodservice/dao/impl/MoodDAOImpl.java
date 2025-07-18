package com.se2303.moodservice.dao.impl;

import com.se2303.moodservice.dao.MoodDAO;
import com.se2303.moodservice.model.Mood;
import com.se2303.moodservice.repository.MoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * MoodDAO实现类
 * 使用Spring Data JPA Repository实现数据访问
 */
@Repository
public class MoodDAOImpl implements MoodDAO {
    
    @Autowired
    private MoodRepository moodRepository;
    
    @Override
    public Mood save(Mood mood) {
        return moodRepository.save(mood);
    }
    
    @Override
    public Optional<Mood> findById(Long id) {
        return moodRepository.findById(id);
    }
    
    @Override
    public Optional<Mood> findByUserIdAndDate(String userId, LocalDate date) {
        return moodRepository.findByUserIdAndDate(userId, date);
    }
    
    @Override
    public List<Mood> findByUserIdOrderByDateDesc(String userId) {
        return moodRepository.findByUserIdOrderByDateDesc(userId);
    }
    
    @Override
    public List<Mood> findByUserIdAndDateBetweenOrderByDateDesc(String userId, LocalDate startDate, LocalDate endDate) {
        return moodRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
    }
    
    @Override
    public boolean existsByUserIdAndDate(String userId, LocalDate date) {
        return moodRepository.existsByUserIdAndDate(userId, date);
    }
    
    @Override
    public long countByUserId(String userId) {
        return moodRepository.countByUserId(userId);
    }
    
    @Override
    public long countByUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate) {
        return moodRepository.countByUserIdAndDateBetween(userId, startDate, endDate);
    }
    
    @Override
    public List<Mood> findByUserIdAndScoreCommittedTrueOrderByDateDesc(String userId) {
        return moodRepository.findByUserIdAndScoreCommittedTrueOrderByDateDesc(userId);
    }
    
    @Override
    public List<Mood> findByUserIdAndDiaryCommittedTrueOrderByDateDesc(String userId) {
        return moodRepository.findByUserIdAndDiaryCommittedTrueOrderByDateDesc(userId);
    }
    
    @Override
    public void deleteById(Long id) {
        moodRepository.deleteById(id);
    }
    
    @Override
    public void deleteAll() {
        moodRepository.deleteAll();
    }
    
    @Override
    public List<Mood> findAll() {
        return moodRepository.findAll();
    }
    
    @Override
    public List<Mood> findAllByOrderByDateDesc() {
        return moodRepository.findAllByOrderByDateDesc();
    }
    
    @Override
    public boolean hasCheckedIn(String userId, LocalDate date) {
        Optional<Mood> mood = moodRepository.findByUserIdAndDate(userId, date);
        return mood.map(Mood::getScoreCommitted).orElse(false);
    }
    
    @Override
    public boolean hasWrittenDiary(String userId, LocalDate date) {
        Optional<Mood> mood = moodRepository.findByUserIdAndDate(userId, date);
        return mood.map(Mood::getDiaryCommitted).orElse(false);
    }
} 