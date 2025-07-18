package com.se2303.moodservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.se2303.moodservice.model.MoodAnalysis;

@Repository
public interface MoodAnalysisRepository extends JpaRepository<MoodAnalysis, Long> {
    Optional<MoodAnalysis> findByUserId(String userId);
}
