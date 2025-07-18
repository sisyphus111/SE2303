package com.se2303.communityservice.repository;

import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.entity.Like;
import com.se2303.communityservice.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    Page<Post> findAllPosts(Pageable pageable);
    
    // 新增：获取所有状态的帖子按点赞数排序（管理员专用）
    @Query("SELECT p FROM Post p ORDER BY (SELECT COUNT(l) FROM Like l WHERE l.postId = p.id) DESC, p.createdAt DESC")
    Page<Post> findAllPostsOrderByLikeCountDesc(Pageable pageable);
    
    // 新增：获取所有状态的帖子按评论数排序（管理员专用）
    @Query("SELECT p FROM Post p ORDER BY (SELECT COUNT(c) FROM Comment c WHERE c.postId = p.id AND c.reviewStatus = 'APPROVED') DESC, p.createdAt DESC")
    Page<Post> findAllPostsOrderByCommentCountDesc(Pageable pageable);

    // 新增：根据关键词获取所有状态的帖子（管理员专用）
    @Query("SELECT p FROM Post p  WHERE (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> findAllPostsByKeyword(String keyword, Pageable pageable);

    // 新增：根据标签获取所有状态的帖子（管理员专用）
    @Query("SELECT p FROM Post p  WHERE :tag MEMBER OF p.tags")
    Page<Post> findAllPostsByTag(String tag, Pageable pageable);

    // 新增：支持多个标签搜索的方法
    @Query("SELECT p FROM Post p WHERE p.reviewStatus = :status AND EXISTS (SELECT t FROM p.tags t WHERE t IN :tags)")
    Page<Post> findByTagsInAndReviewStatus(@Param("tags") List<String> tags, String status, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.reviewStatus = :status AND EXISTS (SELECT t FROM p.tags t WHERE t IN :tags) ORDER BY (SELECT COUNT(l) FROM Like l WHERE l.postId = p.id) DESC, p.createdAt DESC")
    Page<Post> findByTagsInAndReviewStatusOrderByLikeCountDesc(@Param("tags") List<String> tags, String status, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.reviewStatus = :status AND EXISTS (SELECT t FROM p.tags t WHERE t IN :tags) ORDER BY (SELECT COUNT(c) FROM Comment c WHERE c.postId = p.id AND c.reviewStatus = 'APPROVED') DESC, p.createdAt DESC")
    Page<Post> findByTagsInAndReviewStatusOrderByCommentCountDesc(@Param("tags") List<String> tags, String status, Pageable pageable);
    
    // 管理员专用：支持多个标签搜索所有状态的帖子
    @Query("SELECT p FROM Post p WHERE EXISTS (SELECT t FROM p.tags t WHERE t IN :tags)")
    Page<Post> findAllPostsByTagsIn(@Param("tags") List<String> tags, Pageable pageable);

    // 按点赞数排序的查询方法
    @Query("SELECT p FROM Post p WHERE p.reviewStatus = :status ORDER BY (SELECT COUNT(l) FROM Like l WHERE l.postId = p.id) DESC, p.createdAt DESC")
    Page<Post> findByReviewStatusOrderByLikeCountDesc(String status, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.reviewStatus = :status AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) ORDER BY (SELECT COUNT(l) FROM Like l WHERE l.postId = p.id) DESC, p.createdAt DESC")
    Page<Post> findByKeywordAndReviewStatusOrderByLikeCountDesc(String keyword, String status, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.reviewStatus = :status AND :tag MEMBER OF p.tags ORDER BY (SELECT COUNT(l) FROM Like l WHERE l.postId = p.id) DESC, p.createdAt DESC")
    Page<Post> findByTagAndReviewStatusOrderByLikeCountDesc(String tag, String status, Pageable pageable);

    // 按评论数排序的查询方法
    @Query("SELECT p FROM Post p WHERE p.reviewStatus = :status ORDER BY (SELECT COUNT(c) FROM Comment c WHERE c.postId = p.id AND c.reviewStatus = 'APPROVED') DESC, p.createdAt DESC")
    Page<Post> findByReviewStatusOrderByCommentCountDesc(String status, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.reviewStatus = :status AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) ORDER BY (SELECT COUNT(c) FROM Comment c WHERE c.postId = p.id AND c.reviewStatus = 'APPROVED') DESC, p.createdAt DESC")
    Page<Post> findByKeywordAndReviewStatusOrderByCommentCountDesc(String keyword, String status, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.reviewStatus = :status AND :tag MEMBER OF p.tags ORDER BY (SELECT COUNT(c) FROM Comment c WHERE c.postId = p.id AND c.reviewStatus = 'APPROVED') DESC, p.createdAt DESC")
    Page<Post> findByTagAndReviewStatusOrderByCommentCountDesc(String tag, String status, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.reviewStatus = 'APPROVED'")
    Integer getApprovedPostCount();

    @Query("SELECT COUNT(p) FROM Post p WHERE p.reviewStatus = 'APPROVED' AND p.createdAt >= :startOfDay AND p.createdAt <= :endOfDay")
    Integer getTodayApprovedPostCount(LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Post> findByUserId(String userId);
    
    // 新增：支持"ALL"状态的查询方法
    @Query("SELECT p FROM Post p WHERE (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) ORDER BY (SELECT COUNT(l) FROM Like l WHERE l.postId = p.id) DESC, p.createdAt DESC")
    Page<Post> findByKeywordOrderByLikeCountDesc(String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) ORDER BY (SELECT COUNT(c) FROM Comment c WHERE c.postId = p.id AND c.reviewStatus = 'APPROVED') DESC, p.createdAt DESC")
    Page<Post> findByKeywordOrderByCommentCountDesc(String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) ORDER BY p.createdAt DESC")
    Page<Post> findByKeywordOrderByCreatedAtDesc(String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.reviewStatus = :status AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) ORDER BY p.createdAt DESC")
    Page<Post> findByKeywordAndReviewStatusOrderByCreatedAtDesc(String keyword, String status, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE EXISTS (SELECT t FROM p.tags t WHERE t IN :tags) ORDER BY (SELECT COUNT(l) FROM Like l WHERE l.postId = p.id) DESC, p.createdAt DESC")
    Page<Post> findByTagsInOrderByLikeCountDesc(@Param("tags") List<String> tags, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE EXISTS (SELECT t FROM p.tags t WHERE t IN :tags) ORDER BY (SELECT COUNT(c) FROM Comment c WHERE c.postId = p.id AND c.reviewStatus = 'APPROVED') DESC, p.createdAt DESC")
    Page<Post> findByTagsInOrderByCommentCountDesc(@Param("tags") List<String> tags, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE :tag MEMBER OF p.tags ORDER BY (SELECT COUNT(l) FROM Like l WHERE l.postId = p.id) DESC, p.createdAt DESC")
    Page<Post> findByTagOrderByLikeCountDesc(String tag, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE :tag MEMBER OF p.tags ORDER BY (SELECT COUNT(c) FROM Comment c WHERE c.postId = p.id AND c.reviewStatus = 'APPROVED') DESC, p.createdAt DESC")
    Page<Post> findByTagOrderByCommentCountDesc(String tag, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE :tag MEMBER OF p.tags ORDER BY p.createdAt DESC")
    Page<Post> findByTagOrderByCreatedAtDesc(String tag, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.reviewStatus = :status AND :tag MEMBER OF p.tags ORDER BY p.createdAt DESC")
    Page<Post> findByTagAndReviewStatusOrderByCreatedAtDesc(String tag, String status, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.reviewStatus = :status ORDER BY p.createdAt DESC")
    Page<Post> findByReviewStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE EXISTS (SELECT t FROM p.tags t WHERE t IN :tags) ORDER BY p.createdAt DESC")
    Page<Post> findByTagsInOrderByCreatedAtDesc(@Param("tags") List<String> tags, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.reviewStatus = :status AND EXISTS (SELECT t FROM p.tags t WHERE t IN :tags) ORDER BY p.createdAt DESC")
    Page<Post> findByTagsInAndReviewStatusOrderByCreatedAtDesc(@Param("tags") List<String> tags, String status, Pageable pageable);
}