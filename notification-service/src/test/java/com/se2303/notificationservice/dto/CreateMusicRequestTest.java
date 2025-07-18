package com.se2303.notificationservice.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreateMusicRequestTest {
    @Test
    public void testAllFields() {
        CreateMusicRequest req = new CreateMusicRequest();
        req.setTitle("title");
        req.setCategory("cat");
        req.setCover("cover");
        req.setUrl("url");
        req.setDuration(100);
        assertEquals("title", req.getTitle());
        assertEquals("cat", req.getCategory());
        assertEquals("cover", req.getCover());
        assertEquals("url", req.getUrl());
        assertEquals(100, req.getDuration());
        assertNotNull(req.toString());
        assertEquals(req, req);
        assertEquals(req.hashCode(), req.hashCode());
    }
} 