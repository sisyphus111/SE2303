package com.se2303.communityservice.controller;

import com.se2303.communityservice.response.ApiResponse;
import com.se2303.communityservice.entity.Comment;
import com.se2303.communityservice.entity.Report;
import com.se2303.communityservice.service.ICommentService;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/comments")
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private ICommentService commentService;

    /**
     * 举报评论
     */
    @PostMapping("/report/{comment_id}")
    public ResponseEntity<ApiResponse> reportComment(@PathVariable Long comment_id,
                                                  @RequestBody Map<String, String> request,
                                                  @RequestHeader("X-User-Id") String userId,
                                                  @RequestHeader("X-Valid") String valid,
                                                  @RequestHeader("X-Identity") String identity) {
        logger.info("=== POST /comments/report/{} 请求开始 ===", comment_id);

        // 获取请求参数
        String reason = request.get("reason");
        String detail = request.get("detail");
        

        try {
            if (userId == null || userId.trim().isEmpty() || valid == null || identity == null) {
                logger.info("无效的请求头: userId={}, valid={}, identity={}", userId, valid, identity);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "未登录或请求头无效"));
            }

            boolean isValid = Boolean.parseBoolean(valid);
            boolean isIdentity = Boolean.parseBoolean(identity);
            logger.info("UserId: {}, Valid: {}, Identity: {}", userId, isValid, isIdentity);

            if (!isValid) {
                logger.info("用户状态无效: userId={}", userId);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "用户状态无效"));
            }

            if (reason == null || reason.trim().isEmpty()) {
                logger.info("举报原因为空: commentId={}", comment_id);
                return ResponseEntity.badRequest().body(new ApiResponse(false, null, "举报原因不能为空"));
            }

            Report report = commentService.reportComment(comment_id, userId, reason, detail);
            logger.info("✅ 评论举报成功: commentId={}, userId={}", comment_id, userId);
            return ResponseEntity.ok(new ApiResponse(true, report, "评论举报成功"));
        } catch (Exception e) {
            logger.error("❌ 举报评论时发生错误: commentId={}, userId={}, error={}", comment_id, userId, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }


    /**
     * 审核通过对应评论
     * @param comment_id 评论ID
     * @param userId 管理员ID，通过请求头传递
     * @return 审核结果
     */
    @PutMapping("/approve/{comment_id}")
    public ResponseEntity<ApiResponse> approveComment(@PathVariable Long comment_id, @RequestHeader("X-User-Id") String userId,
                                                      @RequestHeader("X-Valid") String valid, @RequestHeader("X-Identity") String identity) {
        logger.info("=== PUT /comments/approve/{} 请求开始 ===", comment_id);
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
                logger.warn("非管理员尝试审核评论: userId={}", userId);
                return ResponseEntity.status(403).body(new ApiResponse(false, null, "仅管理员可审核评论"));
            }

            // 4. 调用服务层审核评论
            Comment comment = commentService.approveComment(comment_id, userId);
            logger.info("✅ 评论审核通过: commentId={}", comment_id);
            return ResponseEntity.ok(new ApiResponse(true, comment, "评论审核通过"));
        } catch (Exception e) {
            logger.error("❌ 审核评论时发生错误: commentId={}, error={}", comment_id, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 封禁对应评论
     * @param comment_id 评论ID
     * @param userId 管理员ID，通过请求头传递
     * @return 封禁结果
     */
    @PutMapping("/ban/{comment_id}")
    public ResponseEntity<ApiResponse> banComment(@PathVariable Long comment_id, @RequestHeader("X-User-Id") String userId,
                                                  @RequestHeader("X-Valid") String valid, @RequestHeader("X-Identity") String identity) {
        logger.info("=== PUT /comments/ban/{} 请求开始 ===", comment_id);
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
                logger.warn("非管理员尝试封禁评论: userId={}", userId);
                return ResponseEntity.status(403).body(new ApiResponse(false, null, "仅管理员可封禁评论"));
            }

            // 4. 调用服务层封禁评论
            Comment comment = commentService.banComment(comment_id, userId);
            logger.info("✅ 评论已封禁: commentId={}", comment_id);
            return ResponseEntity.ok(new ApiResponse(true, comment, "评论已封禁"));
        } catch (Exception e) {
            logger.error("❌ 封禁评论时发生错误: commentId={}, error={}", comment_id, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }
}