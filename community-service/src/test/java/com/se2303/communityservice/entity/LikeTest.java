package com.se2303.communityservice.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class LikeTest {
    private Like like;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        like = new Like();
        now = LocalDateTime.now();
    }

    @Test
    void testDefaultConstructorAndGetters() {
        assertNull(like.getId());
        assertNull(like.getPostId());
        assertNull(like.getUserId());
        assertNull(like.getCreatedAt());
    }

    @Test
    void testSettersAndGetters() {
        like.setId(1L);
        like.setPostId(2L);
        like.setUserId("user123");
        like.setCreatedAt(now);

        assertEquals(1L, like.getId());
        assertEquals(2L, like.getPostId());
        assertEquals("user123", like.getUserId());
        assertEquals(now, like.getCreatedAt());
    }

    @Test
    void testPrePersist() {
        like.onCreate();
        assertNotNull(like.getCreatedAt());
        assertTrue(like.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testToString() {
        like.setId(1L);
        like.setPostId(2L);
        like.setUserId("user123");
        like.setCreatedAt(now);

        String toString = like.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("postId=2"));
        assertTrue(toString.contains("userId=user123"));
        assertTrue(toString.contains("createdAt=" + now.toString()));
    }
}