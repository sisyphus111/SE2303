package com.se2303.communityservice.service.Impl;

import com.se2303.communityservice.dto.CommentDTO;
import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.entity.Comment;
import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.entity.Report;
import com.se2303.communityservice.repository.CommentRepository;
import com.se2303.communityservice.repository.PostRepository;
import com.se2303.communityservice.repository.ReportRepository;
import com.se2303.communityservice.service.ICommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisTemplate<String, Comment> commentRedisTemplate;

    private static final String COMMENT_CACHE_PREFIX = "comments:";
    private static final String POST_CACHE_PREFIX = "post:";

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "comments", allEntries = true)
    public Comment addComment(Long postId, CommentDTO commentDTO, String userId) {
        validateInputs(postId, userId, commentDTO);
        postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Post not found with id: {}", postId);
                    return new EntityNotFoundException("Post not found with id: " + postId);
                });

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(commentDTO.getContent());
        comment.setReviewStatus("PENDING");
        Comment savedComment = commentRepository.save(comment);

        try {
            redisTemplate.delete(COMMENT_CACHE_PREFIX + postId);
            redisTemplate.delete(POST_CACHE_PREFIX + postId);
            logger.info("Added comment to postId: {} by user: {}", postId, userId);
        } catch (JpaOptimisticLockingFailureException e) {
            logger.warn("Optimistic lock failure for postId: {}, retrying", postId);
            throw e;
        }
        return savedComment;
    }

    @Override
    public PageDTO<Comment> getCommentsByPostId(Long postId, Pageable pageable) {
        validatePostId(postId);
        // String cacheKey = COMMENT_CACHE_PREFIX + postId + ":" + pageable.getPageNumber() + ":" + pageable.getPageSize();
        // PageDTO<Comment> cachedComments = (PageDTO<Comment>) redisTemplate.opsForValue().get(cacheKey);
        // if (cachedComments != null) {
        //     logger.info("Cache hit for comments of postId: {}, page: {}, size: {}", postId, pageable.getPageNumber(), pageable.getPageSize());
        //     return cachedComments;
        // }

        logger.info("getCommentsByPostId的postId: {}", postId);
        logger.info("getCommentsByPostId的pageable: {}", pageable);


        Page<Comment> comments = commentRepository.findByPostId(postId, pageable);
        
        logger.info("getCommentsByPostId找到的comments: {}", comments);
        
        
        PageDTO<Comment> pageDTO = new PageDTO<>(
                comments.getContent(),
                comments.getNumber(),
                comments.getSize(),
                comments.getTotalElements(),
                comments.getTotalPages()
        );

        // redisTemplate.opsForValue().set(cacheKey, pageDTO, 10, TimeUnit.MINUTES);
        // logger.info("Cache miss, stored comments for postId: {}, page: {}, size: {}, count: {}",
        //         postId, pageable.getPageNumber(), pageable.getPageSize(), comments.getTotalElements());
        return pageDTO;
    }

    @Override
    // @CacheEvict(value = {"comments", "comment"}, key = "#commentId", allEntries = true)
    public Comment approveComment(Long commentId, String adminId) {
        validateCommentId(commentId);
        validateUserId(adminId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.warn("Comment not found with id: {}", commentId);
                    return new EntityNotFoundException("Comment not found with id: " + commentId);
                });
        comment.setReviewStatus("APPROVED");
        Comment saved = commentRepository.save(comment);
        logger.info("Approved comment with id: {} by admin: {}", commentId, adminId);
        return saved;
    }

    @Override
    // @CacheEvict(value = {"comments", "comment"}, key = "#commentId", allEntries = true)
    public Comment banComment(Long commentId, String adminId) {
        validateCommentId(commentId);
        validateUserId(adminId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.warn("Comment not found with id: {}", commentId);
                    return new EntityNotFoundException("Comment not found with id: " + commentId);
                });
        comment.setReviewStatus("BANNED");
        Comment saved = commentRepository.save(comment);
        logger.info("Banned comment with id: {} by admin: {}", commentId, adminId);
        return saved;
    }

    @Override
    public PageDTO<Comment> getReviewQueue(String keyword, String status, Pageable pageable) {
        logger.info("获取评论审核队列，状态筛选: {}, 关键词: {}", status, keyword);
        
        Page<Comment> comments;
        if (keyword != null && !keyword.isEmpty()) {
            comments = commentRepository.findByKeywordAndReviewStatus(keyword, status, pageable);
        } else {
            comments = commentRepository.findByReviewStatus(status, pageable);
        }
        
        logger.info("✅ 获取到 {} 条评论记录", comments.getTotalElements());
        
        return new PageDTO<>(
                comments.getContent(),
                comments.getNumber(),
                comments.getSize(),
                comments.getTotalElements(),
                comments.getTotalPages()
        );
    }

    @Override
    public Map<String, Object> getCommentDetail(Long commentId) {
        validateCommentId(commentId);
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.warn("评论记录未找到: {}", commentId);
                    return new EntityNotFoundException("评论记录未找到: " + commentId);
                });
        
        // 获取评论所属的帖子信息
        Post post = postRepository.findById(comment.getPostId())
                .orElse(null);
        
        Map<String, Object> result = new HashMap<>();
        result.put("comment", comment);
        result.put("post", post);
        
        return result;
    }

    @Override
    public Map<String, Object> getCommentStats() {
        long totalComments = commentRepository.count();
        long pendingComments = commentRepository.countByReviewStatus("PENDING");
        long approvedComments = commentRepository.countByReviewStatus("APPROVED");
        long bannedComments = commentRepository.countByReviewStatus("BANNED");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", totalComments);
        stats.put("pending", pendingComments);
        stats.put("approved", approvedComments);
        stats.put("banned", bannedComments);
        
        return stats;
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid userId: " + userId);
        }
    }

    private void validateCommentId(Long commentId) {
        if (commentId == null || commentId <= 0) {
            throw new IllegalArgumentException("Invalid commentId: " + commentId);
        }
    }

    private void validateInputs(Long postId, String userId, CommentDTO commentDTO) {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("Invalid postId: " + postId);
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid userId: " + userId);
        }
        if (commentDTO == null || commentDTO.getContent() == null || commentDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }
    }

    public void validatePostId(Long postId) {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("Invalid postId: " + postId);
        }
    }

    @Override
    public Report reportComment(Long commentId, String userId, String reason, String detail) {
        validateCommentId(commentId);
        validateUserId(userId);
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.warn("Comment not found with id: {}", commentId);
                    return new EntityNotFoundException("Comment not found with id: " + commentId);
                });
        Report report = new Report();
        report.setCommentId(commentId);
        report.setType("comment");
        report.setPostId(null);
        report.setUserId(userId);
        report.setReason(reason);
        report.setDetail(detail);
        report.setStatus(Report.ReportStatus.PENDING);
        Report saved = reportRepository.save(report);
        logger.info("Reported comment with id: {} by user: {} with reason: {}", commentId, userId, reason);
        return saved;
    }

    @Override
    public Map<String, Object> getAdminComments(String keyword, String sortBy, Integer page, Integer size) {
        logger.info("获取管理员评论列表，关键词: {}, 排序: {}, 页码: {}, 每页数量: {}", keyword, sortBy, page, size);
        
        // 构建分页请求
        org.springframework.data.domain.Pageable pageable = 
            org.springframework.data.domain.PageRequest.of(page - 1, size);
        
        Page<Comment> comments;
        
        // 根据关键词搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            comments = commentRepository.findByContentContainingIgnoreCase(keyword, pageable);
        } else {
            comments = commentRepository.findAll(pageable);
        }
        
        logger.info("✅ 获取到 {} 条评论记录", comments.getTotalElements());
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("content", comments.getContent());
        result.put("page", page);
        result.put("size", size);
        result.put("totalElements", comments.getTotalElements());
        result.put("totalPages", comments.getTotalPages());
        
        return result;
    }

    @Override
    // @CacheEvict(value = {"comments", "comment"}, key = "#commentId", allEntries = true)
    public Comment deleteComment(Long commentId, String adminId) {
        validateCommentId(commentId);
        validateUserId(adminId);
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.warn("Comment not found with id: {}", commentId);
                    return new EntityNotFoundException("Comment not found with id: " + commentId);
                });
        
        // 软删除：设置状态为已删除
        comment.setReviewStatus("DELETED");
        Comment saved = commentRepository.save(comment);
        
        logger.info("Deleted comment with id: {} by admin: {}", commentId, adminId);
        return saved;
    }

    @Override
    public Integer getTodayApprovedCommentCount() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        return commentRepository.getTodayApprovedCommentCount(startOfDay, endOfDay);
    }

    @Override
    public PageDTO<Comment> getCommentsByTags(java.util.List<String> tags, String status, Pageable pageable) {
        logger.info("根据标签筛选评论，标签: {}, 状态: {}", tags, status);
        
        if (tags == null || tags.isEmpty()) {
            throw new IllegalArgumentException("标签列表不能为空");
        }
        
        Page<Comment> comments = commentRepository.findByTagsInAndReviewStatus(tags, status, pageable);
        
        logger.info("✅ 根据标签筛选到 {} 条评论记录", comments.getTotalElements());
        
        return new PageDTO<>(
                comments.getContent(),
                comments.getNumber(),
                comments.getSize(),
                comments.getTotalElements(),
                comments.getTotalPages()
        );
    }

    @Override
    public PageDTO<Comment> getCommentsByTagsOrderByCreatedAt(java.util.List<String> tags, String status, Pageable pageable) {
        logger.info("根据标签筛选评论并按时间排序，标签: {}, 状态: {}", tags, status);
        
        if (tags == null || tags.isEmpty()) {
            throw new IllegalArgumentException("标签列表不能为空");
        }
        
        Page<Comment> comments = commentRepository.findByTagsInAndReviewStatusOrderByCreatedAtDesc(tags, status, pageable);
        
        logger.info("✅ 根据标签筛选并按时间排序到 {} 条评论记录", comments.getTotalElements());
        
        return new PageDTO<>(
                comments.getContent(),
                comments.getNumber(),
                comments.getSize(),
                comments.getTotalElements(),
                comments.getTotalPages()
        );
    }

    @Override
    public PageDTO<Comment> getAllCommentsByTags(java.util.List<String> tags, Pageable pageable) {
        logger.info("管理员根据标签筛选所有状态的评论，标签: {}", tags);
        
        if (tags == null || tags.isEmpty()) {
            throw new IllegalArgumentException("标签列表不能为空");
        }
        
        Page<Comment> comments = commentRepository.findAllCommentsByTagsIn(tags, pageable);
        
        logger.info("✅ 管理员根据标签筛选到 {} 条评论记录", comments.getTotalElements());
        
        return new PageDTO<>(
                comments.getContent(),
                comments.getNumber(),
                comments.getSize(),
                comments.getTotalElements(),
                comments.getTotalPages()
        );
    }
    public Long getCommentCountByUserId(Long userId) {
        return commentRepository.countByUserId(String.valueOf(userId));

    }
}