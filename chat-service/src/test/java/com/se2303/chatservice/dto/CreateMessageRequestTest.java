package com.se2303.chatservice.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreateMessageRequestTest {
    @Test
    public void testConstructorAndGetterSetter() {
        CreateMessageRequest req = new CreateMessageRequest(1L, "content");
        assertEquals(1L, req.getUserId());
        assertEquals("content", req.getContent());
        req.setUserId(2L);
        req.setContent("new");
        assertEquals(2L, req.getUserId());
        assertEquals("new", req.getContent());
    }
} 