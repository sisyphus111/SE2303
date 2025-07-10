package com.se2303.communityservice.controller;

import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.entity.Comment;
import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.dto.response.PostBriefInfo;
import com.se2303.communityservice.response.ApiResponse;
import com.se2303.communityservice.service.ICommentService;
import com.se2303.communityservice.service.IPostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community/review")
public class ReviewController {
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private IPostService postService;

    @Autowired
    private ICommentService commentService;

    /**
     * 获取审核队列（管理员专用）
     * 返回待审核的帖子和评论
     */
    @GetMapping("/queue")
    public ResponseEntity<ApiResponse> getReviewQueue(
            @RequestParam(required = false) String type, // post, comment, all
            @RequestParam(required = false) String status, // PENDING, APPROVED, BANNED, all
            @RequestParam(required = false, defaultValue = "1") @Min(1) int page,
            @RequestParam(required = false, defaultValue = "10") @Min(1) int size,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Valid") String valid,
            @RequestHeader("X-Identity") String identity,
            HttpServletRequest request) {
        logger.info("=== GET /review/queue 获取审核队列 ===");
        try {
            // 1. 验证请求头
            if (userId == null || userId.trim().isEmpty() || valid == null || identity == null) {
                logger.warn("无效的请求头: userId={}, valid={}, identity={}", userId, valid, identity);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "未登录或请求头无效"));
            }

            // 2. 验证用户状态
            boolean isValid = Boolean.parseBoolean(valid);
            boolean isIdentity = Boolean.parseBoolean(identity);
            logger.debug("UserId: {}, Valid: {}, Identity: {}", userId, isValid, isIdentity);

            if (!isValid) {
                logger.warn("用户状态无效: userId={}", userId);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "用户状态无效"));
            }

            // 3. 验证管理员权限
            if (isIdentity) {
                logger.warn("非管理员尝试访问审核队列: userId={}", userId);
                return ResponseEntity.status(403).body(new ApiResponse(false, null, "仅管理员可访问审核队列"));
            }

            // 4. 验证分页参数
            validatePageParams(page, size);
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            // 5. 获取审核内容
            List<Map<String, Object>> reviewItems = new ArrayList<>();
            
            // 默认获取待审核的内容，如果没有指定状态
            String targetStatus = (status == null || status.equals("all")) ? "PENDING" : status;
            
            if (type == null || type.equals("all") || type.equals("post")) {
                // 获取待审核的帖子
                String postStatus = targetStatus;
                if (postStatus != null) {
                    PageDTO<PostBriefInfo> posts = postService.getReviewQueue(null, null, postStatus, pageable);
                    for (PostBriefInfo post : posts.getContent()) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", post.getId());
                        item.put("type", "post");
                        item.put("title", post.getTitle());
                        item.put("content", post.getContent());
                        item.put("author", post.getUserId());
                        item.put("status", post.getReviewStatus());
                        item.put("tags", post.getTags());
                        item.put("createdAt", post.getCreatedAt());
                        reviewItems.add(item);
                    }
                }
            }

            logger.info("开始获取待审核评论");
            if (type == null || type.equals("all") || type.equals("comment")) {
                // 获取待审核的评论
                String commentStatus = targetStatus;
                if (commentStatus != null) {
                    PageDTO<Comment> comments = commentService.getReviewQueue(null, commentStatus, pageable);
                    for (Comment comment : comments.getContent()) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", comment.getId());
                        item.put("type", "comment");
                        item.put("content", comment.getContent());
                        item.put("author", comment.getUserId());
                        item.put("status", comment.getReviewStatus());
                        item.put("postId", comment.getPostId());
                        item.put("createdAt", comment.getCreatedAt());
                        reviewItems.add(item);
                    }
                }
            }
            logger.info("获取待审核评论为: {}", reviewItems);

            // 6. 构造分页响应
            PageDTO<Map<String, Object>> result = new PageDTO<>(
                    reviewItems,
                    page - 1,
                    size,
                    (long) reviewItems.size(),
                    (int) Math.ceil((double) reviewItems.size() / size)
            );

            logger.info("✅ 审核队列获取成功: page={}, size={}, type={}, status={}", page, size, type, status);
            return ResponseEntity.ok(new ApiResponse(true, result, "审核队列获取成功"));

        } catch (IllegalArgumentException e) {
            logger.error("❌ 参数验证失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, null, e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ 获取审核队列时发生错误: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 获取内容详情（管理员专用）
     */
    @GetMapping("/{contentId}")
    public ResponseEntity<ApiResponse> getContentDetail(
            @PathVariable Long contentId,
            @RequestParam(required = false) String type, // post, comment
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Valid") String valid,
            @RequestHeader("X-Identity") String identity) {
        logger.info("=== GET /review/{} 获取内容详情 ===", contentId);
        try {
            // 验证请求头
            if (userId == null || userId.trim().isEmpty() || valid == null || identity == null) {
                logger.warn("无效的请求头: userId={}, valid={}, identity={}", userId, valid, identity);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "未登录或请求头无效"));
            }

            boolean isValid = Boolean.parseBoolean(valid);
            boolean isIdentity = Boolean.parseBoolean(identity);

            if (!isValid) {
                logger.warn("用户状态无效: userId={}", userId);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "用户状态无效"));
            }

            // 验证管理员权限
            if (isIdentity) {
                logger.warn("非管理员尝试获取内容详情: userId={}", userId);
                return ResponseEntity.status(403).body(new ApiResponse(false, null, "仅管理员可获取内容详情"));
            }

            Map<String, Object> contentDetail = null;
            
            // 尝试获取帖子详情
            try {
                if (type == null || "post".equals(type)) {
                    PostBriefInfo post = postService.getPostById(contentId);
                    contentDetail = new HashMap<>();
                    contentDetail.put("type", "post");
                    contentDetail.put("title", post.getTitle());
                    contentDetail.put("content", post.getContent());
                    contentDetail.put("author", post.getUserId());
                    contentDetail.put("status", post.getReviewStatus());
                    contentDetail.put("tags", post.getTags());
                    contentDetail.put("createdAt", post.getCreatedAt());
                    contentDetail.put("likeCount", post.getLikeCount());
                    contentDetail.put("commentCount", post.getCommentCount());
                }
                else if ("comment".equals(type)) {
                    contentDetail = commentService.getCommentDetail(contentId);
                    contentDetail.put("type", "comment");
                }
            } catch (Exception commentException) {
                logger.error("❌ 内容不存在: contentId={}", contentId);
                return ResponseEntity.status(404).body(new ApiResponse(false, null, "内容不存在"));
            }
            

            logger.info("✅ 内容详情获取成功: contentId={}", contentId);
            return ResponseEntity.ok(new ApiResponse(true, contentDetail, "内容详情获取成功"));

        } catch (Exception e) {
            logger.error("❌ 获取内容详情时发生错误: contentId={}, error={}", contentId, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 审核内容（管理员专用）
     */
    @PutMapping("/{contentId}")
    public ResponseEntity<ApiResponse> reviewContent(
            @PathVariable Long contentId,
            @RequestBody Map<String, String> request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Valid") String valid,
            @RequestHeader("X-Identity") String identity) {
        logger.info("=== PUT /review/{} 审核内容 ===", contentId);
        try {
            // 验证请求头
            if (userId == null || userId.trim().isEmpty() || valid == null || identity == null) {
                logger.warn("无效的请求头: userId={}, valid={}, identity={}", userId, valid, identity);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "未登录或请求头无效"));
            }

            boolean isValid = Boolean.parseBoolean(valid);
            boolean isIdentity = Boolean.parseBoolean(identity);

            if (!isValid) {
                logger.warn("用户状态无效: userId={}", userId);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "用户状态无效"));
            }

            // 验证管理员权限
            if (isIdentity) {
                logger.warn("非管理员尝试审核内容: userId={}", userId);
                return ResponseEntity.status(403).body(new ApiResponse(false, null, "仅管理员可审核内容"));
            }

            // 获取请求参数
            String action = request.get("action"); // APPROVED, BANNED
            String contentType = request.get("type"); // post, comment（如果前端不传，尝试自动识别）

            if (action == null || action.trim().isEmpty()) {
                logger.warn("审核动作为空: contentId={}", contentId);
                return ResponseEntity.badRequest().body(new ApiResponse(false, null, "审核动作不能为空"));
            }

        
            Object reviewedContent = null;
            if("post".equals(contentType)) {
                if ("APPROVED".equals(action)) {
                    reviewedContent = postService.approvePost(contentId, userId);
                } else if ("BANNED".equals(action)) {
                    reviewedContent = postService.banPost(contentId, userId);
                }
            } else if("comment".equals(contentType)) {
                
                    if ("APPROVED".equals(action)) {
                        reviewedContent = commentService.approveComment(contentId, userId);
                    } else if ("BANNED".equals(action)) {
                        reviewedContent = commentService.banComment(contentId, userId);
                    }
                
            }

            logger.info("✅ 内容审核成功: contentId={}, action={}, adminId={}", contentId, action, userId);
            return ResponseEntity.ok(new ApiResponse(true, reviewedContent, "内容审核成功"));

        } catch (Exception e) {
            logger.error("❌ 审核内容时发生错误: contentId={}, error={}", contentId, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 验证分页参数
     */
    private void validatePageParams(int page, int size) {
        if (page < 1 || size < 1 || size > 100) {
            throw new IllegalArgumentException("无效的分页参数: page=" + page + ", size=" + size);
        }
    }
} 