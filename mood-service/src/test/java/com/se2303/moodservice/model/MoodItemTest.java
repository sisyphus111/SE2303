package com.se2303.moodservice.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MoodItemTest {
    @Test
    void testGetterSetterAndEqualsHashCode() {
        MoodItem item1 = new MoodItem("happy");
        item1.setId(1L);
        Mood mood = new Mood();
        mood.setId(2L);
        item1.setMood(mood);
        assertEquals(1L, item1.getId());
        assertEquals("happy", item1.getLabel());
        assertEquals(mood, item1.getMood());
        MoodItem item2 = new MoodItem("happy");
        item2.setId(1L);
        item2.setMood(mood);
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
        assertNotNull(item1.toString());
    }
} 