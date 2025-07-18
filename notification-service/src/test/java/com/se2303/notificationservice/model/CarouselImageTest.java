package com.se2303.notificationservice.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class CarouselImageTest {
    @Test
    public void testAllFields() {
        CarouselImage img = new CarouselImage();
        img.setId(1L);
        img.setImageUrl("url");
        img.setTitle("t");
        img.setDescription("d");
        img.setCategory("cat");
        img.setSortOrder(1);
        LocalDateTime now = LocalDateTime.now();
        img.setCreatedAt(now);
        assertEquals(1L, img.getId());
        assertEquals("url", img.getImageUrl());
        assertEquals("t", img.getTitle());
        assertEquals("d", img.getDescription());
        assertEquals("cat", img.getCategory());
        assertEquals(1, img.getSortOrder());
        assertEquals(now, img.getCreatedAt());
        assertNotNull(img.toString());
        assertEquals(img, img);
        assertEquals(img.hashCode(), img.hashCode());
    }
} 