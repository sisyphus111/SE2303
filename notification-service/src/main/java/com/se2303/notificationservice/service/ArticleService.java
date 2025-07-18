package com.se2303.notificationservice.service;

import com.se2303.notificationservice.dto.ApiResponse;
import com.se2303.notificationservice.dto.ArticleResponse;
import com.se2303.notificationservice.dto.CreateArticleRequest;
import com.se2303.notificationservice.dto.UpdateArticleRequest;
import com.se2303.notificationservice.model.Article;
import com.se2303.notificationservice.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    
    @Autowired
    private ArticleRepository articleRepository;
    
    /**
     * 创建文章
     */
    public ApiResponse createArticle(CreateArticleRequest request) {
        try {
            Article article = new Article(
                request.getTitle(),
                request.getIntroduction(),
                request.getDescription(),
                request.getCover(),
                request.getLink(),
                request.getCategory()
            );
            
            Article savedArticle = articleRepository.save(article);
            ArticleResponse response = convertToResponse(savedArticle);
            
            return ApiResponse.success(response, "文章创建成功");
        } catch (Exception e) {
            return ApiResponse.error("创建文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取推荐文章列表
     */
    public ApiResponse getRecommendedArticles(int page, int size, String category) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Article> articles;
            // 按id正序分页
            articles = articleRepository.findAllByOrderByIdAsc(pageable);

            List<ArticleResponse> responses = articles.getContent().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("list", responses);
            long total = articles != null ? articles.getTotalElements() : 0L;
            result.put("total", total);

            return ApiResponse.success(result, "获取推荐文章成功");
        } catch (Exception e) {
            return ApiResponse.error("获取推荐文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据关键词搜索文章
     */
    public ApiResponse searchArticles(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return ApiResponse.error("搜索关键词不能为空");
            }
            
            List<Article> articles = articleRepository.findByKeyword(keyword.trim());
            List<ArticleResponse> responses = articles.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(responses, "搜索文章成功");
        } catch (Exception e) {
            return ApiResponse.error("搜索文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据分类获取文章
     */
    public ApiResponse getArticlesByCategory(String category) {
        try {
            if (category == null || category.trim().isEmpty()) {
                return ApiResponse.error("分类不能为空");
            }
            
            List<Article> articles = articleRepository.findByCategoryOrderByCreatedAtDesc(category.trim());
            List<ArticleResponse> responses = articles.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(responses, "获取分类文章成功");
        } catch (Exception e) {
            return ApiResponse.error("获取分类文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有文章
     */
    public ApiResponse getAllArticles() {
        try {
            List<Article> articles = articleRepository.findAll();
            List<ArticleResponse> responses = articles.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(responses, "获取所有文章成功");
        } catch (Exception e) {
            return ApiResponse.error("获取所有文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除文章
     */
    public ApiResponse deleteArticle(Long articleId) {
        try {
            if (articleId == null) {
                return ApiResponse.error("文章ID不能为空");
            }
            
            if (!articleRepository.existsById(articleId)) {
                return ApiResponse.error("文章不存在");
            }
            
            articleRepository.deleteById(articleId);
            return ApiResponse.success(null, "文章删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新文章
     */
    public ApiResponse updateArticle(Long articleId, UpdateArticleRequest request) {
        try {
            if (articleId == null) {
                return ApiResponse.error("文章ID不能为空");
            }
            
            if (!articleRepository.existsById(articleId)) {
                return ApiResponse.error("文章不存在");
            }
            
            Article article = articleRepository.findById(articleId).orElse(null);
            if (article == null) {
                return ApiResponse.error("文章不存在");
            }
            
            // 只更新非空字段
            if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
                article.setTitle(request.getTitle().trim());
            }
            if (request.getIntroduction() != null) {
                article.setIntroduction(request.getIntroduction());
            }
            if (request.getDescription() != null) {
                article.setDescription(request.getDescription());
            }
            if (request.getCover() != null) {
                article.setCover(request.getCover());
            }
            if (request.getLink() != null) {
                article.setLink(request.getLink());
            }
            if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
                article.setCategory(request.getCategory().trim());
            }
            
            Article updatedArticle = articleRepository.save(article);
            ArticleResponse response = convertToResponse(updatedArticle);
            
            return ApiResponse.success(response, "文章更新成功");
        } catch (Exception e) {
            return ApiResponse.error("更新文章失败: " + e.getMessage());
        }
    }
    
    /**
     * 将Article实体转换为ArticleResponse
     */
    private ArticleResponse convertToResponse(Article article) {
        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getIntroduction(),
                article.getDescription(),
                article.getCover(),
                article.getLink(),
                article.getCategory(),
                article.getCreatedAt()
        );
    }
} 