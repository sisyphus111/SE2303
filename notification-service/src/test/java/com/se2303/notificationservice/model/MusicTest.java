package com.se2303.notificationservice.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class MusicTest {
    @Test
    public void testAllFields() {
        Music music = new Music();
        music.setId(1L);
        music.setTitle("t");
        music.setCategory("cat");
        music.setCover("c");
        music.setUrl("u");
        music.setDuration(100);
        LocalDateTime now = LocalDateTime.now();
        music.setCreatedAt(now);
        assertEquals(1L, music.getId());
        assertEquals("t", music.getTitle());
        assertEquals("cat", music.getCategory());
        assertEquals("c", music.getCover());
        assertEquals("u", music.getUrl());
        assertEquals(100, music.getDuration());
        assertEquals(now, music.getCreatedAt());
        assertNotNull(music.toString());
        assertEquals(music, music);
        assertEquals(music.hashCode(), music.hashCode());
    }
} 