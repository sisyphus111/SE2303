package com.se2303.notificationservice.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreateArticleRequestTest {
    @Test
    public void testConstructorAndGetterSetter() {
        CreateArticleRequest req = new CreateArticleRequest();
        req.setTitle("title");
        req.setIntroduction("intro");
        req.setDescription("desc");
        req.setCover("cover");
        req.setLink("link");
        req.setCategory("cat");
        assertEquals("title", req.getTitle());
        assertEquals("intro", req.getIntroduction());
        assertEquals("desc", req.getDescription());
        assertEquals("cover", req.getCover());
        assertEquals("link", req.getLink());
        assertEquals("cat", req.getCategory());
    }
} 