package com.se2303.communityservice.repository;

import com.se2303.communityservice.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // 修复：使用参数化的 reviewStatus
    // 根据createdAt排序
    @Query("SELECT p FROM Post p WHERE p.reviewStatus = :status ORDER BY p.createdAt DESC")
    Page<Post> findByReviewStatus(String status, Pageable pageable);

    @Query("SELECT p FROM Post p  WHERE p.reviewStatus = :status AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> findByKeywordAndReviewStatus(String keyword, String status, Pageable pageable);

    @Query("SELECT p FROM Post p  WHERE p.reviewStatus = :status AND :tag MEMBER OF p.tags")
    Page<Post> findByTagAndReviewStatus(String tag, String status, Pageable pageable);

    @Query("SELECT p FROM Post p  WHERE p.userId = :userId AND p.reviewStatus = :status")
    List<Post> findByUserIdAndReviewStatus(String userId, String status);

    @Query("SELECT p FROM Post p  WHERE p.reviewStatus = :status")
    List<Post> findAllByReviewStatus(String status);

    // 新增：获取所有状态的帖子（管理员专用）
    @Query("SELECT p FROM Post p ")
    Page<Post> findAllPosts(Pageable pageable);

    // 新增：根据关键词获取所有状态的帖子（管理员专用）
    @Query("SELECT p FROM Post p  WHERE (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> findAllPostsByKeyword(String keyword, Pageable pageable);

    // 新增：根据标签获取所有状态的帖子（管理员专用）
    @Query("SELECT p FROM Post p  WHERE :tag MEMBER OF p.tags")
    Page<Post> findAllPostsByTag(String tag, Pageable pageable);
}