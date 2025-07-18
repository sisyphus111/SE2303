package com.se2303.notificationservice.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class MusicResponseTest {
    @Test
    public void testAllFields() {
        LocalDateTime now = LocalDateTime.now();
        MusicResponse resp = new MusicResponse(1L, "title", "cat", "cover", "url", 100, now);
        assertEquals(1L, resp.getId());
        assertEquals("title", resp.getTitle());
        assertEquals("cat", resp.getCategory());
        assertEquals("cover", resp.getCover());
        assertEquals("url", resp.getUrl());
        assertEquals(100, resp.getDuration());
        assertEquals(now, resp.getCreatedAt());
        resp.setId(2L);
        resp.setTitle("t2");
        resp.setCategory("c2");
        resp.setCover("cov2");
        resp.setUrl("u2");
        resp.setDuration(200);
        LocalDateTime now2 = now.plusDays(1);
        resp.setCreatedAt(now2);
        assertEquals(2L, resp.getId());
        assertEquals("t2", resp.getTitle());
        assertEquals("c2", resp.getCategory());
        assertEquals("cov2", resp.getCover());
        assertEquals("u2", resp.getUrl());
        assertEquals(200, resp.getDuration());
        assertEquals(now2, resp.getCreatedAt());
        assertNotNull(resp.toString());
        assertEquals(resp, resp);
        assertEquals(resp.hashCode(), resp.hashCode());
    }
} 