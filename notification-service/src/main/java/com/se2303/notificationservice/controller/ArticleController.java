package com.se2303.notificationservice.controller;

import com.se2303.notificationservice.dto.ApiResponse;
import com.se2303.notificationservice.dto.CreateArticleRequest;
import com.se2303.notificationservice.dto.UpdateArticleRequest;
import com.se2303.notificationservice.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 文章控制器
 */
@RestController
@RequestMapping("/api/notification")
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;
    
    /**
     * 健康检查接口
     * GET /api/notification/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Notification Service");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(ApiResponse.success(health, "服务健康"));
    }
    
    /**
     * 创建文章
     * POST /api/notification/articles/create
     */
    @PostMapping("/articles/create")
    public ResponseEntity<ApiResponse> createArticle(@Valid @RequestBody CreateArticleRequest request) {
        try {
            ApiResponse response = articleService.createArticle(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("创建文章失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取推荐文章列表
     * GET /api/notification/articles/recommended
     */
    @GetMapping("/articles/recommended")
    public ResponseEntity<ApiResponse> getRecommendedArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(articleService.getRecommendedArticles(page, size, category));
    }
    
    /**
     * 搜索文章
     * GET /api/notification/articles/search
     */
    @GetMapping("/articles/search")
    public ResponseEntity<ApiResponse> searchArticles(@RequestParam String keyword) {
        return ResponseEntity.ok(articleService.searchArticles(keyword));
    }
    
    /**
     * 根据分类获取文章
     * GET /api/notification/articles/category/{category}
     */
    @GetMapping("/articles/category/{category}")
    public ResponseEntity<ApiResponse> getArticlesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(articleService.getArticlesByCategory(category));
    }
    
    /**
     * 获取所有文章
     * GET /api/notification/articles/all
     */
    @GetMapping("/articles/all")
    public ResponseEntity<ApiResponse> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }
    
    /**
     * 删除文章
     * DELETE /api/notification/articles/{id}
     */
    @DeleteMapping("/articles/{id}")
    public ResponseEntity<ApiResponse> deleteArticle(@PathVariable Long id) {
        try {
            ApiResponse response = articleService.deleteArticle(id);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("删除文章失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新文章
     * PUT /api/notification/articles/{id}
     */
    @PutMapping("/articles/{id}")
    public ResponseEntity<ApiResponse> updateArticle(@PathVariable Long id, @Valid @RequestBody UpdateArticleRequest request) {
        try {
            ApiResponse response = articleService.updateArticle(id, request);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("更新文章失败: " + e.getMessage()));
        }
    }
} 