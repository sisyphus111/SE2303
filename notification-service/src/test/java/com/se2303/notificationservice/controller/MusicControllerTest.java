package com.se2303.notificationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se2303.notificationservice.dto.ApiResponse;
import com.se2303.notificationservice.dto.CreateMusicRequest;
import com.se2303.notificationservice.dto.UpdateMusicRequest;
import com.se2303.notificationservice.service.MusicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MusicController.class)
public class MusicControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private MusicService musicService;

    @InjectMocks
    private MusicController musicController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(musicController).build();
    }

    @Test
    public void testCreateMusicSuccess() throws Exception {
        CreateMusicRequest req = new CreateMusicRequest();
        req.setTitle("test");
        req.setUrl("http://test.com/music.mp3"); // 必须设置
        req.setDuration(100); // 必须设置
        when(musicService.createMusic(any(CreateMusicRequest.class)))
                .thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(post("/api/notification/music/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testCreateMusicFail() throws Exception {
        CreateMusicRequest req = new CreateMusicRequest();
        req.setTitle("test");
        req.setUrl("http://test.com/music.mp3"); // 必须设置
        req.setDuration(100); // 必须设置
        when(musicService.createMusic(any(CreateMusicRequest.class)))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/notification/music/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testCreateMusicValidationFail() throws Exception {
        CreateMusicRequest req = new CreateMusicRequest();
        // 不设置 title
        mockMvc.perform(post("/api/notification/music/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetMusicList() throws Exception {
        when(musicService.getMusicList(eq(1), eq(20))).thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(get("/api/notification/music/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testSearchMusic() throws Exception {
        when(musicService.searchMusic(any())).thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(get("/api/notification/music/search").param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testGetMusicByCategory() throws Exception {
        when(musicService.getMusicByCategory(any())).thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(get("/api/notification/music/category/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testGetAllMusic() throws Exception {
        when(musicService.getAllMusic()).thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(get("/api/notification/music/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testGetMusicListByIdAsc() throws Exception {
        when(musicService.getMusicListByIdAsc(eq(1), eq(20))).thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(get("/api/notification/music/listByIdAsc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testDeleteMusicSuccess() throws Exception {
        when(musicService.deleteMusic(eq(1L))).thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(delete("/api/notification/music/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testDeleteMusicFail() throws Exception {
        when(musicService.deleteMusic(eq(1L))).thenReturn(ApiResponse.error("fail"));
        mockMvc.perform(delete("/api/notification/music/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testDeleteMusicException() throws Exception {
        when(musicService.deleteMusic(eq(1L))).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(delete("/api/notification/music/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testUpdateMusicSuccess() throws Exception {
        UpdateMusicRequest req = new UpdateMusicRequest();
        req.setTitle("test");
        when(musicService.updateMusic(eq(1L), any(UpdateMusicRequest.class)))
                .thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(put("/api/notification/music/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testUpdateMusicFail() throws Exception {
        UpdateMusicRequest req = new UpdateMusicRequest();
        req.setTitle("test");
        when(musicService.updateMusic(eq(1L), any(UpdateMusicRequest.class)))
                .thenReturn(ApiResponse.error("fail"));
        mockMvc.perform(put("/api/notification/music/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testUpdateMusicException() throws Exception {
        UpdateMusicRequest req = new UpdateMusicRequest();
        req.setTitle("test");
        when(musicService.updateMusic(eq(1L), any(UpdateMusicRequest.class)))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(put("/api/notification/music/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }
} 