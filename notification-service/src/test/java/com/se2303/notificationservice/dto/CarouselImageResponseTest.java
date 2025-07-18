package com.se2303.notificationservice.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class CarouselImageResponseTest {
    @Test
    public void testAllFields() {
        LocalDateTime now = LocalDateTime.now();
        CarouselImageResponse resp = new CarouselImageResponse(1L, "url", "title", "desc", "cat", 1, now);
        assertEquals(1L, resp.getId());
        assertEquals("url", resp.getImageUrl());
        assertEquals("title", resp.getTitle());
        assertEquals("desc", resp.getDescription());
        assertEquals("cat", resp.getCategory());
        assertEquals(1, resp.getSortOrder());
        assertEquals(now, resp.getCreatedAt());
        resp.setId(2L);
        resp.setImageUrl("url2");
        resp.setTitle("title2");
        resp.setDescription("desc2");
        resp.setCategory("cat2");
        resp.setSortOrder(2);
        LocalDateTime now2 = now.plusDays(1);
        resp.setCreatedAt(now2);
        assertEquals(2L, resp.getId());
        assertEquals("url2", resp.getImageUrl());
        assertEquals("title2", resp.getTitle());
        assertEquals("desc2", resp.getDescription());
        assertEquals("cat2", resp.getCategory());
        assertEquals(2, resp.getSortOrder());
        assertEquals(now2, resp.getCreatedAt());
        assertNotNull(resp.toString());
        assertEquals(resp, resp);
        assertEquals(resp.hashCode(), resp.hashCode());
    }
} 