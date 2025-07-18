package com.se2303.chatservice.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {
    @Test
    public void testConstructorAndGetterSetter() {
        Message msg = new Message(1L, "content");
        assertEquals(1L, msg.getUserId());
        assertEquals("content", msg.getContent());
        assertTrue(msg.getValid());
        msg.setId(10L);
        assertEquals(10L, msg.getId());
        LocalDateTime now = LocalDateTime.now();
        msg.setCreatedAt(now);
        assertEquals(now, msg.getCreatedAt());
        msg.setValid(false);
        assertFalse(msg.getValid());
    }

    @Test
    public void testPrePersist() {
        Message msg = new Message();
        msg.setValid(null);
        msg.onCreate();
        assertNotNull(msg.getCreatedAt());
        assertTrue(msg.getValid());
    }
} 