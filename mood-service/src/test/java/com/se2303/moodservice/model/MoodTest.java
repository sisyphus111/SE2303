package com.se2303.moodservice.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class MoodTest {
    @Test
    void testGetterSetterAndEqualsHashCode() {
        Mood mood1 = new Mood();
        mood1.setId(1L);
        mood1.setUserId("user1");
        mood1.setDate(LocalDate.now());
        mood1.setScore(5);
        mood1.setDiaryTitle("title");
        mood1.setDiaryDescription("desc");
        mood1.setWeather(WeatherType.SUNNY); // 假设有 SUNNY
        mood1.setScoreCommitted(true);
        mood1.setDiaryCommitted(true);
        assertEquals(1L, mood1.getId());
        assertEquals("user1", mood1.getUserId());
        assertEquals(5, mood1.getScore());
        assertEquals("title", mood1.getDiaryTitle());
        assertEquals("desc", mood1.getDiaryDescription());
        assertEquals(WeatherType.SUNNY, mood1.getWeather());
        assertTrue(mood1.getScoreCommitted());
        assertTrue(mood1.getDiaryCommitted());
        assertNotNull(mood1.toString());
    }
} 