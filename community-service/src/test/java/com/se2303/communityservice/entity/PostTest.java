package com.se2303.communityservice.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PostTest {
    private Post post;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        post = new Post();
        now = LocalDateTime.now();
    }

    @Test
    void testDefaultConstructorAndGetters() {
        assertNull(post.getId());
        assertNull(post.getTitle());
        assertNull(post.getContent());
        assertNull(post.getUserId());
        assertNull(post.getCreatedAt());
        assertEquals("PENDING", post.getReviewStatus());
        assertNull(post.getTags());
    }

    @Test
    void testSettersAndGetters() {
        post.setId(1L);
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setUserId("user123");
        post.setCreatedAt(now);
        post.setReviewStatus("APPROVED");
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        tags.add("tag2");
        post.setTags(tags);

        assertEquals(1L, post.getId());
        assertEquals("Test Title", post.getTitle());
        assertEquals("Test Content", post.getContent());
        assertEquals("user123", post.getUserId());
        assertEquals(now, post.getCreatedAt());
        assertEquals("APPROVED", post.getReviewStatus());
        assertEquals(tags, post.getTags());
    }

    @Test
    void testPrePersist() {
        post.onCreate();
        assertNotNull(post.getCreatedAt());
        assertTrue(post.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testToString() {
        post.setId(1L);
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setUserId("user123");
        post.setCreatedAt(now);
        post.setReviewStatus("APPROVED");
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        post.setTags(tags);

        String toString = post.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("title='Test Title'"));
        assertTrue(toString.contains("tags=[tag1]"));
        assertTrue(toString.contains("content.length=12"));
        assertTrue(toString.contains("userId='user123'"));
        assertTrue(toString.contains("reviewStatus=APPROVED"));
    }
}