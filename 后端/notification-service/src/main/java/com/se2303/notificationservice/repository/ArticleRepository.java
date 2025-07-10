package com.se2303.notificationservice.repository;

import com.se2303.notificationservice.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    /**
     * 根据分类查找文章
     */
    List<Article> findByCategoryOrderByCreatedAtDesc(String category);
    
    /**
     * 分页查询文章
     */
    Page<Article> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 根据分类分页查询文章
     */
    Page<Article> findByCategoryOrderByCreatedAtDesc(String category, Pageable pageable);
    
    /**
     * 根据标题模糊查询
     */
    @Query("SELECT a FROM Article a WHERE a.title LIKE %:keyword% OR a.description LIKE %:keyword%")
    List<Article> findByKeyword(@Param("keyword") String keyword);
} 