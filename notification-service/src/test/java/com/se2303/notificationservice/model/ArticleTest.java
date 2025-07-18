package com.se2303.notificationservice.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class ArticleTest {
    @Test
    public void testAllFields() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("t");
        article.setIntroduction("i");
        article.setDescription("d");
        article.setCover("c");
        article.setLink("l");
        article.setCategory("cat");
        LocalDateTime now = LocalDateTime.now();
        article.setCreatedAt(now);
        assertEquals(1L, article.getId());
        assertEquals("t", article.getTitle());
        assertEquals("i", article.getIntroduction());
        assertEquals("d", article.getDescription());
        assertEquals("c", article.getCover());
        assertEquals("l", article.getLink());
        assertEquals("cat", article.getCategory());
        assertEquals(now, article.getCreatedAt());
        assertNotNull(article.toString());
        assertEquals(article, article);
        assertEquals(article.hashCode(), article.hashCode());
    }
} 