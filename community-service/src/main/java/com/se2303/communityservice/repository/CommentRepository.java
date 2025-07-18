package com.se2303.communityservice.repository;

import com.se2303.communityservice.entity.Comment;
import com.se2303.communityservice.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.postId = :postId AND c.reviewStatus = 'APPROVED'")
    Page<Comment> findByPostId(Long postId, Pageable pageable);

    // 根据审核状态查询评论
    @Query("SELECT c FROM Comment c WHERE c.reviewStatus = :status")
    Page<Comment> findByReviewStatus(String status, Pageable pageable);
    
    // 根据关键词和审核状态查询评论
    @Query("SELECT c FROM Comment c WHERE c.reviewStatus = :status AND c.content LIKE %:keyword%")
    Page<Comment> findByKeywordAndReviewStatus(String keyword, String status, Pageable pageable);
    
    // 根据用户ID和审核状态查询评论
    @Query("SELECT c FROM Comment c WHERE c.userId = :userId AND c.reviewStatus = :status")
    List<Comment> findByUserIdAndReviewStatus(String userId, String status);
    
    // 统计指定审核状态的评论数量
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.reviewStatus = :status")
    Long countByReviewStatus(String status);
    
    // 获取所有评论（管理员用）
    @Query("SELECT c FROM Comment c")
    Page<Comment> findAllComments(Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.postId = :postId")
    Long countByPostId(Long postId);
    
    // 统计指定帖子已审核通过的评论数量
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.postId = :postId AND c.reviewStatus = 'APPROVED'")
    Long countApprovedCommentsByPostId(Long postId);
    
    // 按内容搜索评论（管理员用）
    @Query("SELECT c FROM Comment c WHERE LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Comment> findByContentContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.reviewStatus = 'APPROVED' AND c.createdAt >= :startOfDay AND c.createdAt <= :endOfDay")
    Integer getTodayApprovedCommentCount(LocalDateTime startOfDay, LocalDateTime endOfDay);

    
    // 根据多个标签筛选评论 - 通过关联的帖子标签来筛选
    @Query("SELECT c FROM Comment c JOIN Post p ON c.postId = p.id WHERE c.reviewStatus = :status AND EXISTS (SELECT t FROM p.tags t WHERE t IN :tags)")
    Page<Comment> findByTagsInAndReviewStatus(@Param("tags") List<String> tags, String status, Pageable pageable);
    
    // 根据多个标签筛选评论并按创建时间排序
    @Query("SELECT c FROM Comment c JOIN Post p ON c.postId = p.id WHERE c.reviewStatus = :status AND EXISTS (SELECT t FROM p.tags t WHERE t IN :tags) ORDER BY c.createdAt DESC")
    Page<Comment> findByTagsInAndReviewStatusOrderByCreatedAtDesc(@Param("tags") List<String> tags, String status, Pageable pageable);
    
    // 管理员专用：根据多个标签筛选所有状态的评论
    @Query("SELECT c FROM Comment c JOIN Post p ON c.postId = p.id WHERE EXISTS (SELECT t FROM p.tags t WHERE t IN :tags)")
    Page<Comment> findAllCommentsByTagsIn(@Param("tags") List<String> tags, Pageable pageable);


    Long countByUserId(String userId);

}
