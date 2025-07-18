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

    /**
     * 获取管理员评论列表
     * @param keyword 搜索关键词
     * @param sortBy 排序方式
     * @param page 页码
     * @param size 每页数量
     * @return 评论列表
     */
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse> getAdminComments(@RequestParam(required = false) String keyword,
                                                        @RequestParam(required = false) String sortBy,
                                                        @RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "15") Integer size,
                                                        @RequestHeader("X-User-Id") String userId,
                                                        @RequestHeader("X-Valid") String valid,
                                                        @RequestHeader("X-Identity") String identity) {
        logger.info("=== GET /comments/admin 请求开始 ===");
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
                logger.warn("非管理员尝试获取评论列表: userId={}", userId);
                return ResponseEntity.status(403).body(new ApiResponse(false, null, "仅管理员可获取评论列表"));
            }

            // 4. 调用服务层获取评论列表
            Map<String, Object> result = commentService.getAdminComments(keyword, sortBy, page, size);
            logger.info("✅ 获取管理员评论列表成功: keyword={}, page={}, size={}", keyword, page, size);
            return ResponseEntity.ok(new ApiResponse(true, result, "获取评论列表成功"));
        } catch (Exception e) {
            logger.error("❌ 获取管理员评论列表时发生错误: error={}", e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 删除评论（软删除）
     * @param comment_id 评论ID
     * @param userId 管理员ID，通过请求头传递
     * @return 删除结果
     */
    @PutMapping("/delete/{comment_id}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable Long comment_id, @RequestHeader("X-User-Id") String userId,
                                                     @RequestHeader("X-Valid") String valid, @RequestHeader("X-Identity") String identity) {
        logger.info("=== PUT /comments/delete/{} 请求开始 ===", comment_id);
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
                logger.warn("非管理员尝试删除评论: userId={}", userId);
                return ResponseEntity.status(403).body(new ApiResponse(false, null, "仅管理员可删除评论"));
            }

            // 4. 调用服务层删除评论
            Comment comment = commentService.deleteComment(comment_id, userId);
            logger.info("✅ 评论已删除: commentId={}", comment_id);
            return ResponseEntity.ok(new ApiResponse(true, comment, "评论已删除"));
        } catch (Exception e) {
            logger.error("❌ 删除评论时发生错误: commentId={}, error={}", comment_id, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    @GetMapping("/today_count")
    public ResponseEntity<ApiResponse> getTodayCount(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Valid") String valid,
            @RequestHeader("X-Identity") String identity) {
        Integer todayCount = commentService.getTodayApprovedCommentCount();
        return ResponseEntity.ok(new ApiResponse(true, todayCount, "今日评论数量查询成功"));
    }

    /**
     * 根据多个标签筛选评论
     * @param tags 标签列表，用逗号分隔
     * @param status 评论状态（APPROVED, PENDING, BANNED）
     * @param page 页码
     * @param size 每页数量
     * @return 评论列表
     */
    @GetMapping("/by-tags")
    public ResponseEntity<ApiResponse> getCommentsByTags(
            @RequestParam String tags,
            @RequestParam(defaultValue = "APPROVED") String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "15") Integer size) {
        logger.info("=== GET /comments/by-tags 请求开始 ===");
        try {
            // 解析标签列表
            java.util.List<String> tagList = java.util.Arrays.asList(tags.split(","));
            
            // 构建分页请求
            org.springframework.data.domain.Pageable pageable = 
                org.springframework.data.domain.PageRequest.of(page - 1, size);
            
            // 调用服务层获取评论
            com.se2303.communityservice.dto.PageDTO<Comment> result = 
                commentService.getCommentsByTags(tagList, status, pageable);
            
            logger.info("✅ 根据标签筛选评论成功: tags={}, status={}, page={}, size={}", tags, status, page, size);
            return ResponseEntity.ok(new ApiResponse(true, result, "根据标签筛选评论成功"));
        } catch (Exception e) {
            logger.error("❌ 根据标签筛选评论时发生错误: tags={}, error={}", tags, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 根据多个标签筛选评论并按时间排序
     * @param tags 标签列表，用逗号分隔
     * @param status 评论状态（APPROVED, PENDING, BANNED）
     * @param page 页码
     * @param size 每页数量
     * @return 评论列表
     */
    @GetMapping("/by-tags/sorted")
    public ResponseEntity<ApiResponse> getCommentsByTagsSorted(
            @RequestParam String tags,
            @RequestParam(defaultValue = "APPROVED") String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "15") Integer size) {
        logger.info("=== GET /comments/by-tags/sorted 请求开始 ===");
        try {
            // 解析标签列表
            java.util.List<String> tagList = java.util.Arrays.asList(tags.split(","));
            
            // 构建分页请求
            org.springframework.data.domain.Pageable pageable = 
                org.springframework.data.domain.PageRequest.of(page - 1, size);
            
            // 调用服务层获取评论
            com.se2303.communityservice.dto.PageDTO<Comment> result = 
                commentService.getCommentsByTagsOrderByCreatedAt(tagList, status, pageable);
            
            logger.info("✅ 根据标签筛选评论并按时间排序成功: tags={}, status={}, page={}, size={}", tags, status, page, size);
            return ResponseEntity.ok(new ApiResponse(true, result, "根据标签筛选评论并按时间排序成功"));
        } catch (Exception e) {
            logger.error("❌ 根据标签筛选评论并按时间排序时发生错误: tags={}, error={}", tags, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 管理员专用：根据多个标签筛选所有状态的评论
     * @param tags 标签列表，用逗号分隔
     * @param page 页码
     * @param size 每页数量
     * @param userId 管理员ID
     * @param valid 用户状态
     * @param identity 用户身份
     * @return 评论列表
     */
    @GetMapping("/admin/by-tags")
    public ResponseEntity<ApiResponse> getAllCommentsByTags(
            @RequestParam String tags,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "15") Integer size,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Valid") String valid,
            @RequestHeader("X-Identity") String identity) {
        logger.info("=== GET /comments/admin/by-tags 请求开始 ===");
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
                logger.warn("非管理员尝试获取评论列表: userId={}", userId);
                return ResponseEntity.status(403).body(new ApiResponse(false, null, "仅管理员可获取评论列表"));
            }

            // 4. 解析标签列表
            java.util.List<String> tagList = java.util.Arrays.asList(tags.split(","));
            
            // 5. 构建分页请求
            org.springframework.data.domain.Pageable pageable = 
                org.springframework.data.domain.PageRequest.of(page - 1, size);
            
            // 6. 调用服务层获取评论
            com.se2303.communityservice.dto.PageDTO<Comment> result = 
                commentService.getAllCommentsByTags(tagList, pageable);
            
            logger.info("✅ 管理员根据标签筛选评论成功: tags={}, page={}, size={}", tags, page, size);
            return ResponseEntity.ok(new ApiResponse(true, result, "管理员根据标签筛选评论成功"));
        } catch (Exception e) {
            logger.error("❌ 管理员根据标签筛选评论时发生错误: tags={}, error={}", tags, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }
}