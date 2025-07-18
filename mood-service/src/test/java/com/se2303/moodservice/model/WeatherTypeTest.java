package com.se2303.moodservice.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WeatherTypeTest {
    @Test
    void testEnumValues() {
        for (WeatherType type : WeatherType.values()) {
            assertNotNull(type.toString());
        }
        assertNotNull(WeatherType.valueOf("SUNNY")); // 假设有 SUNNY
    }
} 