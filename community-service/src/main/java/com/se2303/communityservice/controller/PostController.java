package com.se2303.communityservice.controller;

import com.se2303.communityservice.client.InspectionServiceClient;
import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.dto.response.PostBriefInfo;
import com.se2303.communityservice.dto.response.UserStatisticsResponse;
import com.se2303.communityservice.response.ApiResponse;
import com.se2303.communityservice.dto.CommentDTO;
import com.se2303.communityservice.dto.PostDTO;
import com.se2303.communityservice.entity.Comment;
import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.entity.Report;
import com.se2303.communityservice.service.ICommentService;
import com.se2303.communityservice.service.ILikeService;
import com.se2303.communityservice.service.IPostService;
// TODO: 导入InformProducer用于发送通知消息到Redis Stream
import com.se2303.communityservice.service.InformProducer;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/community/posts")
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private InspectionServiceClient inspectionClient;
    @Autowired
    private IPostService postService;
    @Autowired
    private ICommentService commentService;
    @Autowired
    private ILikeService likeService;
 
    // TODO: 添加InformProducer注入用于发送通知消息
    @Autowired
    private InformProducer informProducer;
    /**
     * 根据关键词、分类（视为标签）和排序方式分页搜索帖子
     *
     * @param keyword   关键词
     * @param category  分类（作为标签处理，例如：情感、学习、生活、健康、成长）
     *                  支持多个标签，用逗号分隔，例如：情感,学习,生活
     * @param sortBy    排序方式（latest, mostLiked, mostCommented）
     * @param page      页码（从1开始）
     * @param size      每页帖子数
     * @param userId    用户ID（从请求头获取）
     * @param valid     用户验证状态（从请求头获取）
     * @param identity  用户身份（从请求头获取）
     * @return 分页帖子列表
     */
    @GetMapping("/get_posts")
    public ResponseEntity<ApiResponse> getPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String reviewStatus,
            @RequestParam(required = false, defaultValue = "latest") String sortBy,
            @RequestParam(required = false,defaultValue = "1") @Min(1) int page,
            @RequestParam(required = false,defaultValue = "5") @Min(1) int size,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Valid") String valid,
            @RequestHeader("X-Identity") String identity,
            HttpServletRequest request) {
        logger.info("=== GET /posts/get_posts 请求开始 ===");
        logger.info("Raw query string: {}", request.getQueryString());
        logger.info("Raw category parameter: {}", request.getParameter("category"));
        try {
            // 1. 验证请求头
            if (userId == null || userId.trim().isEmpty() || valid == null || identity == null) {
                logger.info("无效的请求头: userId={}, valid={}, identity={}", userId, valid, identity);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "未登录或请求头无效"));
            }

            // 2. 验证用户状态
            boolean isValid = Boolean.parseBoolean(valid);
            boolean isIdentity = Boolean.parseBoolean(identity);
            logger.info("UserId: {}, Valid: {}, Identity: {}", userId, isValid, isIdentity);

            if (!isValid) {
                logger.info("用户状态无效: userId={}", userId);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "用户状态无效"));
            }

            // 3. 验证分页参数和排序方式
            validatePageParams(page, size);
            validateSortBy(sortBy);
            // 前端 page 从 1 开始，JPA Pageable 从 0 开始
            Pageable pageable = PageRequest.of(page - 1, size, getSortByParam(sortBy));
            logger.info("Created pageable with sort: {}", pageable.getSort());

            if(reviewStatus == null || reviewStatus.isEmpty()) reviewStatus = "APPROVED";
            
            // 4. 调用服务层获取帖子
            PageDTO<PostBriefInfo> posts = postService.getPosts(keyword, category, reviewStatus, sortBy, pageable);
            logger.info("✅ 帖子分页查询成功: page={}, size={}, keyword={}, category={}, sortBy={}", page, size, keyword, category, sortBy);
            return ResponseEntity.ok(new ApiResponse(true, posts, "帖子分页查询成功"));
        } catch (IllegalArgumentException e) {
            logger.error("❌ 分页参数验证失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, null, e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ 查询帖子时发生错误: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 获取用户点赞过的帖子
     *
     * @param userId  用户ID（从请求头获取）
     * @param valid   用户验证状态（从请求头获取）
     * @param identity 用户身份（从请求头获取）
     * @return 点赞过的帖子列表
     */
    @GetMapping("/my_likes")
    public ResponseEntity<ApiResponse> getMyLikes(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Valid") String valid,
            @RequestHeader("X-Identity") String identity) {
        logger.info("=== GET /posts/my_likes 请求开始 ===");
        try {
            // 1. 验证请求头
            if (userId == null || userId.trim().isEmpty() || valid == null || identity == null) {
                logger.info("无效的请求头: userId={}, valid={}, identity={}", userId, valid, identity);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "未登录或请求头无效"));
            }

            // 2. 验证用户状态
            boolean isValid = Boolean.parseBoolean(valid);
            boolean isIdentity = Boolean.parseBoolean(identity);
            logger.info("UserId: {}, Valid: {}, Identity: {}", userId, isValid, isIdentity);

            if (!isValid) {
                logger.info("用户状态无效: userId={}", userId);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "用户状态无效"));
            }

            // 3. 获取用户点赞过的帖子
            List<PostBriefInfo> posts = likeService.getLikedPosts(userId);
            logger.info("✅ 用户点赞的帖子查询成功: userId={}, count={}", userId, posts.size());
            return ResponseEntity.ok(new ApiResponse(true, posts, "用户点赞的帖子查询成功"));
        } catch (Exception e) {
            logger.error("❌ 获取用户点赞的帖子时发生错误: userId={}, error={}", userId, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 根据id获取对应post详细信息
     *
     * @param post_id post_id
     * @return 对应的post
     */
    @GetMapping("/get_post/{post_id}")
    public ResponseEntity<ApiResponse> getPostById(@PathVariable Long post_id,
                                                   @RequestHeader("X-User-Id") String userId,
                                                   @RequestHeader("X-Valid") String valid,
                                                   @RequestHeader("X-Identity") String identity) {
        logger.info("=== GET /posts/get_post/{} 请求开始 ===", post_id);
        try {
            // 1. 验证请求头
            if (userId == null || userId.trim().isEmpty() || valid == null || identity == null) {
                logger.info("无效的请求头: userId={}, valid={}, identity={}", userId, valid, identity);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "未登录或请求头无效"));
            }

            // 2. 验证用户状态
            boolean isValid = Boolean.parseBoolean(valid);
            boolean isIdentity = Boolean.parseBoolean(identity);
            logger.info("UserId: {}, Valid: {}, Identity: {}", userId, isValid, isIdentity);

            if (!isValid) {
                logger.info("用户状态无效: userId={}", userId);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "用户状态无效"));
            }

            // 3. 获取帖子
            PostBriefInfo post = postService.getPostById(post_id);

//            logger.info("被获取的帖子的isLiked: {}", likeService.isLiked(post_id, userId));
            post.setLiked(likeService.isLiked(post_id, userId));

            if (!"APPROVED".equals(post.getReviewStatus())) {
                logger.info("普通用户尝试访问未审核通过的帖子: postId={}", post_id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, null, "无权访问未审核通过的帖子"));
            }

            logger.info("✅ 帖子详情查询成功: postId={}", post_id);
            return ResponseEntity.ok(new ApiResponse(true, post, "帖子详情查询成功"));
        } catch (Exception e) {
            logger.error("❌ 获取帖子详情时发生错误: postId={}, error={}", post_id, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 获取“我的帖子”
     * 根据userid获取对应的post
     *
     * @param userId
     * @return
     */
    @GetMapping("/my_post")
    public ResponseEntity<ApiResponse> getMyPosts(@RequestHeader("X-User-Id") String userId,
                                                  @RequestHeader("X-Valid") String valid,
                                                  @RequestHeader("X-Identity") String identity) {
        logger.info("=== GET /posts/my_post 请求开始 ===");
        try {
            // 1. 验证请求头
            if (userId == null || userId.trim().isEmpty() || valid == null || identity == null) {
                logger.info("无效的请求头: userId={}, valid={}, identity={}", userId, valid, identity);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "未登录或请求头无效"));
            }

            // 2. 验证用户状态
            boolean isValid = Boolean.parseBoolean(valid);
            boolean isIdentity = Boolean.parseBoolean(identity);
            logger.info("UserId: {}, Valid: {}, Identity: {}", userId, isValid, isIdentity);

            if (!isValid) {
                logger.info("用户状态无效: userId={}", userId);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "用户状态无效"));
            }
            // 3. 获取用户帖子
            List<PostBriefInfo> posts = postService.getMyPosts(userId);
            logger.info("✅ 我的帖子查询成功: userId={}", userId);
            return ResponseEntity.ok(new ApiResponse(true, posts, "我的帖子查询成功"));
        } catch (Exception e) {
            logger.error("❌ 获取我的帖子时发生错误: userId={}, error={}", userId, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 获取现有的所有tags
     *
     * @return
     */
    @GetMapping("/get_tags")
    public ResponseEntity<ApiResponse> getAllTags(@RequestHeader("X-User-Id") String userId,
                                                  @RequestHeader("X-Valid") String valid,
                                                  @RequestHeader("X-Identity") String identity) {
        logger.info("=== GET /posts/get_tags 请求开始 ===");
        try {
            if (userId == null || userId.trim().isEmpty() || valid == null || identity == null) {
                logger.info("无效的请求头: userId={}, valid={}, identity={}", userId, valid, identity);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "未登录或请求头无效"));
            }

            boolean isValid = Boolean.parseBoolean(valid);
            logger.info("UserId: {}, Valid: {}, Identity: {}", userId, isValid, identity);

            if (!isValid) {
                logger.info("用户状态无效: userId={}", userId);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "用户状态无效"));
            }

            List<String> tags = postService.getAllTags();
            logger.info("✅ 标签查询成功");
            return ResponseEntity.ok(new ApiResponse(true, tags, "标签查询成功"));
        } catch (Exception e) {
            logger.error("❌ 获取标签时发生错误: error={}", e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 获取对应post的所有comment（支持分页）
     *
     * @param post_id 对应的post的id
     * @param page    页码（从1开始）
     * @param size    每页评论数
     * @param userId  用户ID（从请求头获取）
     * @param valid   用户验证状态（从请求头获取）
     * @param identity 用户身份（从请求头获取）
     * @return 分页评论列表
     */
    @GetMapping("/get_comments/{post_id}")
    public ResponseEntity<ApiResponse> getComments(
            @PathVariable Long post_id,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(5) int size,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Valid") String valid,
            @RequestHeader("X-Identity") String identity) {
        logger.info("=== GET /posts/get_comments/{} 请求开始 ===", post_id);
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

            validatePageParams(page, size);
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            PostBriefInfo post = postService.getPostById(post_id);
            if (!"APPROVED".equals(post.getReviewStatus())) {
                logger.info("普通用户尝试访问未审核通过帖子的评论: postId={}", post_id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, null, "无权访问未审核通过帖子的评论"));
            }

            PageDTO<Comment> comments = commentService.getCommentsByPostId(post_id, pageable);

            
            logger.info("✅ 分页评论查询成功: postId={}, page={}, size={}", post_id, page, size);
            return ResponseEntity.ok(new ApiResponse(true, comments, "分页评论查询成功"));
        } catch (IllegalArgumentException e) {
            logger.error("❌ 分页参数验证失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, null, e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ 获取评论时发生错误: postId={}, error={}", post_id, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 发帖
     *
     * @param postDTO
     * @return
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendPost(@RequestBody PostDTO postDTO,
                                                @RequestHeader("X-User-Id") String userId,
                                                @RequestHeader("X-Valid") String valid,
                                                @RequestHeader("X-Identity") String identity) {
        logger.info("=== POST /posts/send 请求开始 ===");
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

            validatePostDTO(postDTO);

            Post post = postService.createPost(postDTO, userId);
            logger.info("✅ 发帖成功: userId={}", userId);
            return ResponseEntity.ok(new ApiResponse(true, post, "发帖成功"));
        } catch (IllegalArgumentException e) {
            logger.error("❌ 帖子数据验证失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, null, e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ 发帖时发生错误: userId={}, error={}", userId, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 给对应的帖子发评论
     *
     * @param post_id
     * @param requestBody 请求体，支持多种格式：{content: "评论内容"} 或 {content: {content: "评论内容"}}
     * @return
     */
    @PostMapping("/add_comment/{post_id}")
    public ResponseEntity<ApiResponse> addComment(@PathVariable Long post_id,
                                                  @RequestBody Map<String, Object> requestBody,
                                                  @RequestHeader("X-User-Id") String userId,
                                                  @RequestHeader("X-Valid") String valid,
                                                  @RequestHeader("X-Identity") String identity) {
        logger.info("=== POST /posts/add_comment/{} 请求开始 ===", post_id);
        logger.info("请求体: {}", requestBody);
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

            // 处理前端发送的不同格式的请求体
            String commentContent = extractCommentContent(requestBody);
            if (commentContent == null || commentContent.trim().isEmpty()) {
                logger.error("❌ 评论内容为空或格式不正确: requestBody={}", requestBody);
                return ResponseEntity.badRequest().body(new ApiResponse(false, null, "评论内容不能为空"));
            }

            // 创建CommentDTO对象
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setContent(commentContent);

            PostBriefInfo post = postService.getPostById(post_id);
            if (!"APPROVED".equals(post.getReviewStatus())) {
                logger.info("尝试对未审核通过的帖子发表评论: postId={}", post_id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, null, "无法对未审核通过的帖子发表评论"));
            }

            Comment comment = commentService.addComment(post_id, commentDTO, userId);
            
            // TODO: 发送评论通知到Redis Stream
            // 当用户对帖子评论时，需要通知帖子作者
            // 注意：只有当评论者不是帖子作者时才发送通知
            if (!userId.equals(post.getUserId())) {
                try {
                    informProducer.sendNotification(
                        post.getUserId(),      // 通知接收者（帖子作者）
                        userId,                // 通知发送者（评论者）
                        "COMMENT",             // 通知类型
                        commentDTO.getContent(), // 评论内容
                        post_id                // 帖子ID
                    );
                    logger.info("✅ 评论通知已发送: postId={}, from={}, to={}", post_id, userId, post.getUserId());
                } catch (Exception e) {
                    logger.error("❌ 发送评论通知失败: postId={}, from={}, to={}, error={}", 
                        post_id, userId, post.getUserId(), e.getMessage());
                }
            }
            
            logger.info("✅ 评论发表成功: postId={}, userId={}", post_id, userId);
            return ResponseEntity.ok(new ApiResponse(true, comment, "评论发表成功"));
        } catch (IllegalArgumentException e) {
            logger.error("❌ 评论数据验证失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, null, e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ 发表评论时发生错误: postId={}, userId={}, error={}", post_id, userId, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 对对应id的帖子点赞
     *
     * @param post_id
     * @param userId
     * @return
     */
    @PostMapping("/like/{post_id}")
    public ResponseEntity<ApiResponse> likePost(@PathVariable Long post_id,
                                                @RequestHeader("X-User-Id") String userId,
                                                @RequestHeader("X-Valid") String valid,
                                                @RequestHeader("X-Identity") String identity) {
        logger.info("=== POST /posts/like/{} 请求开始 ===", post_id);
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

            PostBriefInfo post = postService.getPostById(post_id);

            // 输出post的所有字段作为日志
            logger.info("getPostById的Post review status: {}", post.getReviewStatus());

            if (!"APPROVED".equals(post.getReviewStatus())) {
                logger.info("尝试对未审核通过的帖子点赞: postId={}", post_id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, null, "无法对未审核通过的帖子点赞"));
            }

            likeService.likePost(post_id, userId);
            
            // TODO: 发送点赞通知到Redis Stream
            // 当用户对帖子点赞时，需要通知帖子作者
            // 注意：只有当点赞者不是帖子作者时才发送通知
            if (!userId.equals(post.getUserId())) {
                try {
                    informProducer.sendNotification(
                        post.getUserId(),      // 通知接收者（帖子作者）
                        userId,                // 通知发送者（点赞者）
                        "LIKE",                // 通知类型
                        "",                    // 点赞无内容，传空字符串
                        post_id                // 帖子ID
                    );
                    logger.info("✅ 点赞通知已发送: postId={}, from={}, to={}", post_id, userId, post.getUserId());
                } catch (Exception e) {
                    logger.error("❌ 发送点赞通知失败: postId={}, from={}, to={}, error={}", 
                        post_id, userId, post.getUserId(), e.getMessage());
                }
            }
            
            logger.info("✅ 点赞成功: postId={}, userId={}", post_id, userId);
            return ResponseEntity.ok(new ApiResponse(true, post, "点赞成功"));
        } catch (Exception e) {
            logger.error("❌ 点赞时发生错误: postId={}, userId={}, error={}", post_id, userId, e.getMessage(), e);
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 对对应id的post取消点赞
     */
    @DeleteMapping("/unlike/{post_id}")
    public ResponseEntity<ApiResponse> unlikePost(@PathVariable Long post_id,
                                                  @RequestHeader("X-User-Id") String userId,
                                                  @RequestHeader("X-Valid") String valid,
                                                  @RequestHeader("X-Identity") String identity) {
        logger.info("=== DELETE /posts/unlike/{} 请求开始 ===", post_id);
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

            PostBriefInfo post = postService.getPostById(post_id);
            if (!"APPROVED".equals(post.getReviewStatus())) {
                logger.info("尝试取消未审核通过帖子的点赞: postId={}", post_id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse(false, null, "无法取消未审核通过帖子的点赞"));
            }

            likeService.unlikePost(post_id, userId);
            logger.info("✅ 取消点赞成功: postId={}, userId={}", post_id, userId);
            return ResponseEntity.ok(new ApiResponse(true, null, "取消点赞成功"));
        } catch (Exception e) {
            logger.error("❌ 取消点赞时发生错误: postId={}, userId={}, error={}", post_id, userId, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 审核通过对应帖子
     */
    @PutMapping("/approve/{post_id}")
    public ResponseEntity<ApiResponse> approvePost(@PathVariable Long post_id,
                                                   @RequestHeader("X-User-Id") String userId,
                                                   @RequestHeader("X-Valid") String valid,
                                                   @RequestHeader("X-Identity") String identity) {
        logger.info("=== PUT /posts/approve/{} 请求开始 ===", post_id);
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

            PostBriefInfo post = postService.approvePost(post_id, userId);
            logger.info("✅ 帖子审核通过: postId={}", post_id);
            return ResponseEntity.ok(new ApiResponse(true, post, "帖子审核通过"));
        } catch (Exception e) {
            logger.error("❌ 审核帖子时发生错误: postId={}, error={}", post_id, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 封禁帖子
     */
    @PutMapping("/ban/{post_id}")
    public ResponseEntity<ApiResponse> banPost(@PathVariable Long post_id,
                                               @RequestHeader("X-User-Id") String userId,
                                               @RequestHeader("X-Valid") String valid,
                                               @RequestHeader("X-Identity") String identity) {
        logger.info("=== PUT /posts/ban/{} 请求开始 ===", post_id);
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

            Post post = postService.banPost(post_id, userId);
            logger.info("✅ 帖子已封禁: postId={}", post_id);
            return ResponseEntity.ok(new ApiResponse(true, post, "帖子已封禁"));
        } catch (Exception e) {
            logger.error("❌ 封禁帖子时发生错误: postId={}, error={}", post_id, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 举报帖子
     */
    @PostMapping("/report/{post_id}")
    public ResponseEntity<ApiResponse> reportPost(@PathVariable Long post_id,
                                                  @RequestBody Map<String, String> request,
                                                  @RequestHeader("X-User-Id") String userId,
                                                  @RequestHeader("X-Valid") String valid,
                                                  @RequestHeader("X-Identity") String identity) {
        logger.info("=== POST /posts/report/{} 请求开始 ===", post_id);

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
                logger.info("举报原因为空: postId={}", post_id);
                return ResponseEntity.badRequest().body(new ApiResponse(false, null, "举报原因不能为空"));
            }

            Report report = postService.reportPost(post_id, userId, reason, detail);
            logger.info("✅ 帖子举报成功: postId={}, userId={}", post_id, userId);
            return ResponseEntity.ok(new ApiResponse(true, report, "帖子举报成功"));
        } catch (Exception e) {
            logger.error("❌ 举报帖子时发生错误: postId={}, userId={}, error={}", post_id, userId, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("无效的用户ID: " + userId);
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

    private void validatePostDTO(PostDTO postDTO) {
        if (postDTO == null || postDTO.getTitle() == null || postDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("帖子标题不能为空");
        }
        if (postDTO.getContent() == null || postDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("帖子内容不能为空");
        }
        try {
            CompletableFuture<Boolean> titleCheck = validateContentAsync(postDTO.getTitle());
            CompletableFuture<Boolean> contentCheck = validateContentAsync(postDTO.getContent());
            CompletableFuture.allOf(titleCheck, contentCheck).join();
            if (!titleCheck.get() || !contentCheck.get()) {
                throw new IllegalArgumentException("内容未通过安全检查");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("内容验证失败: " + e.getMessage());
        }
    }

    private void validateCommentDTO(CommentDTO commentDTO) {
        if (commentDTO == null || commentDTO.getContent() == null || commentDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("评论内容不能为空");
        }
        try {
            CompletableFuture<Boolean> contentCheck = validateContentAsync(commentDTO.getContent());
            contentCheck.join();
            if (!contentCheck.get()) {
                throw new IllegalArgumentException("评论内容未通过安全检查");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("评论内容验证失败: " + e.getMessage());
        }
    }
    @Async
    public CompletableFuture<Boolean> validateContentAsync(String content) {
        return CompletableFuture.supplyAsync(() -> inspectionClient.isContentSafe(content));
    }

    /**
     * 验证排序方式
     */
    private void validateSortBy(String sortBy) {
        if (sortBy == null || !Arrays.asList("latest", "mostLiked", "mostCommented").contains(sortBy)) {
            throw new IllegalArgumentException("无效的排序方式: " + sortBy + "，支持：latest, mostLiked, mostCommented");
        }
    }

    /**
     * 根据排序方式获取排序参数
     */
    private Sort getSortByParam(String sortBy) {
        switch (sortBy) {
            case "latest":
                return Sort.by(Sort.Direction.DESC, "createdAt");
            case "mostLiked":
            case "mostCommented":
                // 对于需要关联查询的排序，返回默认排序，实际排序在Service层处理
                return Sort.by(Sort.Direction.DESC, "createdAt");
            default:
                return Sort.by(Sort.Direction.DESC, "createdAt");
        }
    }

    /**
     * 从请求体中提取评论内容，支持多种格式
     * 格式1: {content: "评论内容"}
     * 格式2: {content: {content: "评论内容"}}
     * 
     * @param requestBody 请求体
     * @return 评论内容字符串，如果格式不正确则返回null
     */
    private String extractCommentContent(Map<String, Object> requestBody) {
        if (requestBody == null) {
            return null;
        }

        Object contentObj = requestBody.get("content");
        if (contentObj == null) {
            return null;
        }

        // 如果content是字符串，直接返回
        if (contentObj instanceof String) {
            return (String) contentObj;
        }

        // 如果content是Map，尝试从中提取content字段
        if (contentObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> contentMap = (Map<String, Object>) contentObj;
            Object nestedContent = contentMap.get("content");
            if (nestedContent instanceof String) {
                return (String) nestedContent;
            }
        }

        // 如果都不匹配，返回null
        return null;
    }

    @GetMapping("/total_count")
    public ResponseEntity<ApiResponse> getTotalCount(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Valid") String valid,
            @RequestHeader("X-Identity") String identity) {
        Integer totalCount = postService.getApprovedPostCount();
        return ResponseEntity.ok(new ApiResponse(true, totalCount, "帖子总数查询成功"));
    }

    @GetMapping("/today_count")
    public ResponseEntity<ApiResponse> getTodayCount(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Valid") String valid,
            @RequestHeader("X-Identity") String identity) {
        Integer todayCount = postService.getTodayApprovedPostCount();
        return ResponseEntity.ok(new ApiResponse(true, todayCount, "今日帖子数量查询成功"));
    }

    @GetMapping("/user_statistics/{userId}")
    public ResponseEntity<ApiResponse> getUserStatistics(@PathVariable Long userId,
            @RequestHeader("X-Valid") String valid,
            @RequestHeader("X-Identity") String identity) {

        boolean isValid = Boolean.parseBoolean(valid);
        boolean isIdentity = Boolean.parseBoolean(identity);
        logger.info("UserId: {}, Valid: {}, Identity: {}", userId, isValid, isIdentity);

        Long likedCount = likeService.getLikedCountByUserId(userId);
        Long commentCount = commentService.getCommentCountByUserId(userId);
        

        UserStatisticsResponse userStatistics = new UserStatisticsResponse(likedCount, commentCount);
        return ResponseEntity.ok(new ApiResponse(true, userStatistics, "用户统计信息查询成功"));
    }
}