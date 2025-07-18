package com.se2303.communityservice.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {
    private Comment comment;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        now = LocalDateTime.now();
    }

    @Test
    void testDefaultConstructorAndGetters() {
        assertNull(comment.getId());
        assertNull(comment.getPostId());
        assertNull(comment.getUserId());
        assertNull(comment.getContent());
        assertNull(comment.getCreatedAt());
        assertEquals("PENDING", comment.getReviewStatus());
    }

    @Test
    void testSettersAndGetters() {
        comment.setId(1L);
        comment.setPostId(2L);
        comment.setUserId("user123");
        comment.setContent("Test comment");
        comment.setCreatedAt(now);
        comment.setReviewStatus("APPROVED");

        assertEquals(1L, comment.getId());
        assertEquals(2L, comment.getPostId());
        assertEquals("user123", comment.getUserId());
        assertEquals("Test comment", comment.getContent());
        assertEquals(now, comment.getCreatedAt());
        assertEquals("APPROVED", comment.getReviewStatus());
    }

    @Test
    void testPrePersist() {
        comment.onCreate();
        assertNotNull(comment.getCreatedAt());
        assertTrue(comment.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testToString() {
        comment.setId(1L);
        comment.setPostId(2L);
        comment.setUserId("user123");
        comment.setContent("Test comment");
        comment.setCreatedAt(now);
        comment.setReviewStatus("APPROVED");

        String toString = comment.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("postId=2"));
        assertTrue(toString.contains("userId='user123'"));
        assertTrue(toString.contains("content='Test comment'"));
        assertTrue(toString.contains("reviewStatus=APPROVED"));
    }
}