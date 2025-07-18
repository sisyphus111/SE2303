package com.se2303.notificationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se2303.notificationservice.dto.ApiResponse;
import com.se2303.notificationservice.dto.CreateArticleRequest;
import com.se2303.notificationservice.dto.UpdateArticleRequest;
import com.se2303.notificationservice.service.ArticleService;
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

@WebMvcTest(ArticleController.class)
public class ArticleControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @InjectMocks
    private ArticleController articleController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(articleController).build();
    }

    @Test
    public void testHealth() throws Exception {
        mockMvc.perform(get("/api/notification/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testCreateArticleSuccess() throws Exception {
        CreateArticleRequest req = new CreateArticleRequest();
        req.setTitle("test"); // 必须设置 title
        when(articleService.createArticle(any(CreateArticleRequest.class)))
                .thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(post("/api/notification/articles/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testCreateArticleFail() throws Exception {
        CreateArticleRequest req = new CreateArticleRequest();
        req.setTitle("test"); // 必须设置 title
        when(articleService.createArticle(any(CreateArticleRequest.class)))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/notification/articles/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testCreateArticleValidationFail() throws Exception {
        CreateArticleRequest req = new CreateArticleRequest();
        // 不设置 title
        mockMvc.perform(post("/api/notification/articles/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetRecommendedArticles() throws Exception {
        when(articleService.getRecommendedArticles(eq(1), eq(10), any()))
                .thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(get("/api/notification/articles/recommended"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testSearchArticles() throws Exception {
        when(articleService.searchArticles(any())).thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(get("/api/notification/articles/search").param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testGetArticlesByCategory() throws Exception {
        when(articleService.getArticlesByCategory(any())).thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(get("/api/notification/articles/category/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testGetAllArticles() throws Exception {
        when(articleService.getAllArticles()).thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(get("/api/notification/articles/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testDeleteArticleSuccess() throws Exception {
        when(articleService.deleteArticle(eq(1L))).thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(delete("/api/notification/articles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testDeleteArticleFail() throws Exception {
        when(articleService.deleteArticle(eq(1L))).thenReturn(ApiResponse.error("fail"));
        mockMvc.perform(delete("/api/notification/articles/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testDeleteArticleException() throws Exception {
        when(articleService.deleteArticle(eq(1L))).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(delete("/api/notification/articles/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testUpdateArticleSuccess() throws Exception {
        UpdateArticleRequest req = new UpdateArticleRequest();
        when(articleService.updateArticle(eq(1L), any(UpdateArticleRequest.class)))
                .thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(put("/api/notification/articles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testUpdateArticleFail() throws Exception {
        UpdateArticleRequest req = new UpdateArticleRequest();
        when(articleService.updateArticle(eq(1L), any(UpdateArticleRequest.class)))
                .thenReturn(ApiResponse.error("fail"));
        mockMvc.perform(put("/api/notification/articles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testUpdateArticleException() throws Exception {
        UpdateArticleRequest req = new UpdateArticleRequest();
        when(articleService.updateArticle(eq(1L), any(UpdateArticleRequest.class)))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(put("/api/notification/articles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }
} 