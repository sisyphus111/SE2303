package com.se2303.notificationservice.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreateCarouselImageRequestTest {
    @Test
    public void testAllFields() {
        CreateCarouselImageRequest req = new CreateCarouselImageRequest();
        req.setImageUrl("url");
        req.setTitle("title");
        req.setDescription("desc");
        req.setCategory("cat");
        req.setSortOrder(1);
        assertEquals("url", req.getImageUrl());
        assertEquals("title", req.getTitle());
        assertEquals("desc", req.getDescription());
        assertEquals("cat", req.getCategory());
        assertEquals(1, req.getSortOrder());
        assertNotNull(req.toString());
        assertEquals(req, req);
        assertEquals(req.hashCode(), req.hashCode());
    }
} 