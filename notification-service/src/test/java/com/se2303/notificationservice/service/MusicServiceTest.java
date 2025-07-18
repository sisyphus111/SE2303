package com.se2303.notificationservice.service;

import com.se2303.notificationservice.dto.ApiResponse;
import com.se2303.notificationservice.dto.CreateMusicRequest;
import com.se2303.notificationservice.dto.UpdateMusicRequest;
import com.se2303.notificationservice.model.Music;
import com.se2303.notificationservice.repository.MusicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

public class MusicServiceTest {
    @Mock
    private MusicRepository musicRepository;

    @InjectMocks
    private MusicService musicService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateMusicSuccess() {
        CreateMusicRequest req = new CreateMusicRequest();
        Music music = new Music();
        when(musicRepository.save(any(Music.class))).thenReturn(music);
        ApiResponse resp = musicService.createMusic(req);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testCreateMusicException() {
        CreateMusicRequest req = new CreateMusicRequest();
        when(musicRepository.save(any(Music.class))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = musicService.createMusic(req);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetMusicListSuccess() {
        Music music = new Music();
        Page<Music> page = new PageImpl<>(Collections.singletonList(music));
        when(musicRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(page);
        ApiResponse resp = musicService.getMusicList(1, 20);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testGetMusicListException() {
        when(musicRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = musicService.getMusicList(1, 20);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testSearchMusicSuccess() {
        Music music = new Music();
        when(musicRepository.findByKeyword(anyString())).thenReturn(Arrays.asList(music));
        ApiResponse resp = musicService.searchMusic("test");
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testSearchMusicKeywordNull() {
        ApiResponse resp = musicService.searchMusic(null);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testSearchMusicException() {
        when(musicRepository.findByKeyword(anyString())).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = musicService.searchMusic("test");
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetMusicByCategorySuccess() {
        Music music = new Music();
        when(musicRepository.findByCategoryOrderByCreatedAtDesc(anyString())).thenReturn(Arrays.asList(music));
        ApiResponse resp = musicService.getMusicByCategory("cat");
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testGetMusicByCategoryNull() {
        ApiResponse resp = musicService.getMusicByCategory(null);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetMusicByCategoryException() {
        when(musicRepository.findByCategoryOrderByCreatedAtDesc(anyString())).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = musicService.getMusicByCategory("cat");
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetAllMusicSuccess() {
        Music music = new Music();
        Page<Music> page = new PageImpl<>(Collections.singletonList(music));
        when(musicRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(page);
        ApiResponse resp = musicService.getAllMusic();
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testGetAllMusicException() {
        when(musicRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = musicService.getAllMusic();
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetMusicListByIdAscSuccess() {
        Music music = new Music();
        Page<Music> page = new PageImpl<>(Collections.singletonList(music));
        when(musicRepository.findAllByOrderByIdAsc(any(Pageable.class))).thenReturn(page);
        ApiResponse resp = musicService.getMusicListByIdAsc(1, 20);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testGetMusicListByIdAscException() {
        when(musicRepository.findAllByOrderByIdAsc(any(Pageable.class))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = musicService.getMusicListByIdAsc(1, 20);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testDeleteMusicSuccess() {
        when(musicRepository.existsById(eq(1L))).thenReturn(true);
        ApiResponse resp = musicService.deleteMusic(1L);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testDeleteMusicIdNull() {
        ApiResponse resp = musicService.deleteMusic(null);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testDeleteMusicNotFound() {
        when(musicRepository.existsById(eq(1L))).thenReturn(false);
        ApiResponse resp = musicService.deleteMusic(1L);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testDeleteMusicException() {
        when(musicRepository.existsById(eq(1L))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = musicService.deleteMusic(1L);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testUpdateMusicSuccess() {
        UpdateMusicRequest req = new UpdateMusicRequest();
        Music music = new Music();
        when(musicRepository.existsById(eq(1L))).thenReturn(true);
        when(musicRepository.findById(eq(1L))).thenReturn(Optional.of(music));
        when(musicRepository.save(any(Music.class))).thenReturn(music);
        ApiResponse resp = musicService.updateMusic(1L, req);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testUpdateMusicIdNull() {
        UpdateMusicRequest req = new UpdateMusicRequest();
        ApiResponse resp = musicService.updateMusic(null, req);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testUpdateMusicNotFound() {
        UpdateMusicRequest req = new UpdateMusicRequest();
        when(musicRepository.existsById(eq(1L))).thenReturn(false);
        ApiResponse resp = musicService.updateMusic(1L, req);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testUpdateMusicFindNull() {
        UpdateMusicRequest req = new UpdateMusicRequest();
        when(musicRepository.existsById(eq(1L))).thenReturn(true);
        when(musicRepository.findById(eq(1L))).thenReturn(Optional.empty());
        ApiResponse resp = musicService.updateMusic(1L, req);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testUpdateMusicException() {
        UpdateMusicRequest req = new UpdateMusicRequest();
        when(musicRepository.existsById(eq(1L))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = musicService.updateMusic(1L, req);
        assertFalse(resp.isSuccess());
    }
} 