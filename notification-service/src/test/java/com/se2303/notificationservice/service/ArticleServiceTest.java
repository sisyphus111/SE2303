package com.se2303.notificationservice.service;

import com.se2303.notificationservice.dto.ApiResponse;
import com.se2303.notificationservice.dto.CreateArticleRequest;
import com.se2303.notificationservice.dto.UpdateArticleRequest;
import com.se2303.notificationservice.model.Article;
import com.se2303.notificationservice.repository.ArticleRepository;
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

public class ArticleServiceTest {
    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService articleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateArticleSuccess() {
        CreateArticleRequest req = new CreateArticleRequest();
        Article article = new Article();
        when(articleRepository.save(any(Article.class))).thenReturn(article);
        ApiResponse resp = articleService.createArticle(req);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testCreateArticleException() {
        CreateArticleRequest req = new CreateArticleRequest();
        when(articleRepository.save(any(Article.class))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = articleService.createArticle(req);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetRecommendedArticlesSuccess() {
        Article article = new Article();
        Page<Article> page = new PageImpl<>(Collections.singletonList(article));
        when(articleRepository.findAllByOrderByIdAsc(any(Pageable.class))).thenReturn(page);
        ApiResponse resp = articleService.getRecommendedArticles(1, 10, null);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testGetRecommendedArticlesException() {
        when(articleRepository.findAllByOrderByIdAsc(any(Pageable.class))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = articleService.getRecommendedArticles(1, 10, null);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testSearchArticlesSuccess() {
        Article article = new Article();
        when(articleRepository.findByKeyword(anyString())).thenReturn(Arrays.asList(article));
        ApiResponse resp = articleService.searchArticles("test");
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testSearchArticlesKeywordNull() {
        ApiResponse resp = articleService.searchArticles(null);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testSearchArticlesException() {
        when(articleRepository.findByKeyword(anyString())).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = articleService.searchArticles("test");
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetArticlesByCategorySuccess() {
        Article article = new Article();
        when(articleRepository.findByCategoryOrderByCreatedAtDesc(anyString())).thenReturn(Arrays.asList(article));
        ApiResponse resp = articleService.getArticlesByCategory("cat");
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testGetArticlesByCategoryNull() {
        ApiResponse resp = articleService.getArticlesByCategory(null);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetArticlesByCategoryException() {
        when(articleRepository.findByCategoryOrderByCreatedAtDesc(anyString())).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = articleService.getArticlesByCategory("cat");
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetAllArticlesSuccess() {
        Article article = new Article();
        when(articleRepository.findAll()).thenReturn(Arrays.asList(article));
        ApiResponse resp = articleService.getAllArticles();
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testGetAllArticlesException() {
        when(articleRepository.findAll()).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = articleService.getAllArticles();
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testDeleteArticleSuccess() {
        when(articleRepository.existsById(eq(1L))).thenReturn(true);
        ApiResponse resp = articleService.deleteArticle(1L);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testDeleteArticleIdNull() {
        ApiResponse resp = articleService.deleteArticle(null);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testDeleteArticleNotFound() {
        when(articleRepository.existsById(eq(1L))).thenReturn(false);
        ApiResponse resp = articleService.deleteArticle(1L);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testDeleteArticleException() {
        when(articleRepository.existsById(eq(1L))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = articleService.deleteArticle(1L);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testUpdateArticleSuccess() {
        UpdateArticleRequest req = new UpdateArticleRequest();
        Article article = new Article();
        when(articleRepository.existsById(eq(1L))).thenReturn(true);
        when(articleRepository.findById(eq(1L))).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenReturn(article);
        ApiResponse resp = articleService.updateArticle(1L, req);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testUpdateArticleIdNull() {
        UpdateArticleRequest req = new UpdateArticleRequest();
        ApiResponse resp = articleService.updateArticle(null, req);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testUpdateArticleNotFound() {
        UpdateArticleRequest req = new UpdateArticleRequest();
        when(articleRepository.existsById(eq(1L))).thenReturn(false);
        ApiResponse resp = articleService.updateArticle(1L, req);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testUpdateArticleFindNull() {
        UpdateArticleRequest req = new UpdateArticleRequest();
        when(articleRepository.existsById(eq(1L))).thenReturn(true);
        when(articleRepository.findById(eq(1L))).thenReturn(Optional.empty());
        ApiResponse resp = articleService.updateArticle(1L, req);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testUpdateArticleException() {
        UpdateArticleRequest req = new UpdateArticleRequest();
        when(articleRepository.existsById(eq(1L))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = articleService.updateArticle(1L, req);
        assertFalse(resp.isSuccess());
    }
} 