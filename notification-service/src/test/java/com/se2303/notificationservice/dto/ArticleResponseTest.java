package com.se2303.notificationservice.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ArticleResponseTest {
    @Test
    public void testConstructorAndGetterSetter() {
        LocalDateTime now = LocalDateTime.now();
        ArticleResponse resp = new ArticleResponse(1L, "t", "i", "d", "c", "l", "cat", now);
        assertEquals(1L, resp.getId());
        assertEquals("t", resp.getTitle());
        assertEquals("i", resp.getIntroduction());
        assertEquals("d", resp.getDescription());
        assertEquals("c", resp.getCover());
        assertEquals("l", resp.getLink());
        assertEquals("cat", resp.getCategory());
        assertEquals(now, resp.getCreatedAt());
        resp.setId(2L);
        resp.setTitle("t2");
        resp.setIntroduction("i2");
        resp.setDescription("d2");
        resp.setCover("c2");
        resp.setLink("l2");
        resp.setCategory("cat2");
        LocalDateTime now2 = now.plusDays(1);
        resp.setCreatedAt(now2);
        assertEquals(2L, resp.getId());
        assertEquals("t2", resp.getTitle());
        assertEquals("i2", resp.getIntroduction());
        assertEquals("d2", resp.getDescription());
        assertEquals("c2", resp.getCover());
        assertEquals("l2", resp.getLink());
        assertEquals("cat2", resp.getCategory());
        assertEquals(now2, resp.getCreatedAt());
    }
} 