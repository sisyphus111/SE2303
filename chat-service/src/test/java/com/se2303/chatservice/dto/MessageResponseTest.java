package com.se2303.chatservice.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class MessageResponseTest {
    @Test
    public void testConstructorAndGetterSetter() {
        LocalDateTime now = LocalDateTime.now();
        MessageResponse resp = new MessageResponse(1L, 2L, "c", now, true);
        assertEquals(1L, resp.getId());
        assertEquals(2L, resp.getUserId());
        assertEquals("c", resp.getContent());
        assertEquals(now, resp.getCreatedAt());
        assertTrue(resp.getValid());
        resp.setId(3L);
        resp.setUserId(4L);
        resp.setContent("d");
        resp.setCreatedAt(now.plusDays(1));
        resp.setValid(false);
        assertEquals(3L, resp.getId());
        assertEquals(4L, resp.getUserId());
        assertEquals("d", resp.getContent());
        assertEquals(now.plusDays(1), resp.getCreatedAt());
        assertFalse(resp.getValid());
    }
} 