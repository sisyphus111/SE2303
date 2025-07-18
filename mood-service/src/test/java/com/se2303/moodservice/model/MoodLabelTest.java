package com.se2303.moodservice.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MoodLabelTest {
    @Test
    void testEnumMethods() {
        MoodLabel label = MoodLabel.HAPPY;
        assertEquals("开心", label.getLabel());
        assertTrue(label.isPositiveMood());
        assertFalse(label.isNegativeMood());
        assertFalse(label.isNeutralMood());
        assertEquals(1, label.getWeight());
        assertNotNull(label.toString());
        assertEquals(MoodLabel.HAPPY, MoodLabel.fromLabel("开心"));
        assertTrue(MoodLabel.isValidLabel("开心"));
    }
} 