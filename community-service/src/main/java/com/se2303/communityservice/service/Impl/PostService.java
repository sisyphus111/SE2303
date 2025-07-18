package com.se2303.communityservice.service.Impl;

import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.dto.PostDTO;
import com.se2303.communityservice.dto.response.PostBriefInfo;
import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.entity.Report;
import com.se2303.communityservice.repository.PostRepository;
import com.se2303.communityservice.repository.ReportRepository;
import com.se2303.communityservice.repository.LikeRepository;
import com.se2303.communityservice.repository.CommentRepository;
import com.se2303.communityservice.service.IPostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private RedisTemplate<String, Post> postRedisTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private CommentRepository commentRepository;

    private static final String POST_CACHE_PREFIX = "post:";
    private static final String TAGS_CACHE_KEY = "all_tags";

    @Override
//    @Cacheable(value = "posts", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + (#keyword != null ? #keyword : '') + '-' + (#category != null ? #category : '') + '-' + #sortBy")
    public PageDTO<PostBriefInfo> getPosts(String keyword, String category, String reviewStatus, String sortBy, Pageable pageable) {
        // 构建包含排序信息的缓存键
        // String cacheKey = String.format("posts:%d-%d-%s-%s-%s", 
        //     pageable.getPageNumber(), 
        //     pageable.getPageSize(), 
        //     keyword != null ? keyword : "", 
        //     category != null ? category : "", 
        //     sortBy != null ? sortBy : "latest");
        // logger.info("Generated cache key: {}", cacheKey); // 添加调试日志
        // PageDTO<Post> cachedPosts = (PageDTO<Post>) redisTemplate.opsForValue().get(cacheKey);
        // if (cachedPosts != null) {
        //     logger.info("Cache hit for posts: {}", cacheKey);
        //     return cachedPosts;
        // }

        Page<Post> posts;
        Page<PostBriefInfo> postBriefInfos;
        logger.info("Querying posts with sortBy: {}, pageable: {}", sortBy, pageable);
        
        if(reviewStatus == null || reviewStatus.isEmpty()) reviewStatus = "APPROVED";
        
        // 根据排序方式和reviewStatus选择不同的查询方法,注意reviewStatus可能为"ALL"
        if (keyword != null && !keyword.isEmpty()) {
            if ("mostLiked".equals(sortBy)) {
                if(reviewStatus.equals("ALL")) {
                    posts = postRepository.findByKeywordOrderByLikeCountDesc(keyword, pageable);
                } else {
                    posts = postRepository.findByKeywordAndReviewStatusOrderByLikeCountDesc(keyword, reviewStatus, pageable);
                }
                logger.info("Querying by keyword with mostLiked sort: {}", keyword);
            } else if ("mostCommented".equals(sortBy)) {
                if(reviewStatus.equals("ALL")) {
                    posts = postRepository.findByKeywordOrderByCommentCountDesc(keyword, pageable);
                } else {
                    posts = postRepository.findByKeywordAndReviewStatusOrderByCommentCountDesc(keyword, reviewStatus, pageable);
                }
                logger.info("Querying by keyword with mostCommented sort: {}", keyword);
            } else {
                if(reviewStatus.equals("ALL")) {
                    posts = postRepository.findByKeywordOrderByCreatedAtDesc(keyword, pageable);
                } else {
                    posts = postRepository.findByKeywordAndReviewStatusOrderByCreatedAtDesc(keyword, reviewStatus, pageable);
                }
                logger.info("Querying by keyword with latest sort: {}", keyword);
            }
        } else if (category != null && !category.isEmpty()) {
            // 检查是否为多个标签（用逗号分隔）
            List<String> tags = Arrays.asList(category.split(","));
            logger.info("Processing tags: {}", tags);
            
            if (tags.size() > 1) {
                // 多个标签搜索
                if ("mostLiked".equals(sortBy)) {
                    if(reviewStatus.equals("ALL")) {
                        posts = postRepository.findByTagsInOrderByLikeCountDesc(tags, pageable);
                    } else {
                        posts = postRepository.findByTagsInAndReviewStatusOrderByLikeCountDesc(tags, reviewStatus, pageable);
                    }
                    logger.info("Querying by multiple tags with mostLiked sort: {}", tags);
                } else if ("mostCommented".equals(sortBy)) {
                    if(reviewStatus.equals("ALL")) {
                        posts = postRepository.findByTagsInOrderByCommentCountDesc(tags, pageable);
                    } else {
                        posts = postRepository.findByTagsInAndReviewStatusOrderByCommentCountDesc(tags, reviewStatus, pageable);
                    }
                    logger.info("Querying by multiple tags with mostCommented sort: {}", tags);
                } else {
                    if(reviewStatus.equals("ALL")) {
                        posts = postRepository.findByTagsInOrderByCreatedAtDesc(tags, pageable);
                    } else {
                        posts = postRepository.findByTagsInAndReviewStatusOrderByCreatedAtDesc(tags, reviewStatus, pageable);
                    }
                    logger.info("Querying by multiple tags with latest sort: {}", tags);
                }
            } else {
                // 单个标签搜索（保持原有逻辑）
                if ("mostLiked".equals(sortBy)) {
                    if(reviewStatus.equals("ALL")) {
                        posts = postRepository.findByTagOrderByLikeCountDesc(category, pageable);
                    } else {
                        posts = postRepository.findByTagAndReviewStatusOrderByLikeCountDesc(category, reviewStatus, pageable);
                    }
                    logger.info("Querying by single category with mostLiked sort: {}", category);
                } else if ("mostCommented".equals(sortBy)) {
                    if(reviewStatus.equals("ALL")) {
                        posts = postRepository.findByTagOrderByCommentCountDesc(category, pageable);
                    } else {
                        posts = postRepository.findByTagAndReviewStatusOrderByCommentCountDesc(category, reviewStatus, pageable);
                    }
                    logger.info("Querying by single category with mostCommented sort: {}", category);
                } else {
                    if(reviewStatus.equals("ALL")) {
                        posts = postRepository.findByTagOrderByCreatedAtDesc(category, pageable);
                    } else {
                        posts = postRepository.findByTagAndReviewStatusOrderByCreatedAtDesc(category, reviewStatus, pageable);
                    }
                    logger.info("Querying by single category with latest sort: {}", category);
                }
            }
        } else {
            if ("mostLiked".equals(sortBy)) {
                if(reviewStatus.equals("ALL")) {
                    posts = postRepository.findAllPostsOrderByLikeCountDesc(pageable);
                } else {
                    posts = postRepository.findByReviewStatusOrderByLikeCountDesc(reviewStatus, pageable);
                }
                logger.info("Querying all posts with mostLiked sort");
            } else if ("mostCommented".equals(sortBy)) {
                if(reviewStatus.equals("ALL")) {
                    posts = postRepository.findAllPostsOrderByCommentCountDesc(pageable);
                } else {
                    posts = postRepository.findByReviewStatusOrderByCommentCountDesc(reviewStatus, pageable);
                }
                logger.info("Querying all posts with mostCommented sort");
            } else {
                if(reviewStatus.equals("ALL")) {
                    posts = postRepository.findAllPosts(pageable);
                } else {
                    posts = postRepository.findByReviewStatusOrderByCreatedAtDesc(reviewStatus, pageable);
                }
                logger.info("Querying all posts with latest sort");
            }
        }

        logger.info("Posts found: total={}, content.size={}, sortBy={}", 
                   posts.getTotalElements(), posts.getContent().size(), sortBy);
        postBriefInfos = posts.map(post -> {
            PostBriefInfo postBriefInfo = new PostBriefInfo();
            postBriefInfo.setId(post.getId());
            postBriefInfo.setTitle(post.getTitle());
            postBriefInfo.setTags(post.getTags());
            postBriefInfo.setContent(post.getContent());
            postBriefInfo.setUserId(post.getUserId());
            postBriefInfo.setCreatedAt(post.getCreatedAt());
            postBriefInfo.setReviewStatus(post.getReviewStatus());
            postBriefInfo.setLikeCount( likeRepository.countByPostId(post.getId()));
            postBriefInfo.setCommentCount( commentRepository.countApprovedCommentsByPostId(post.getId()));
            return postBriefInfo;
        });

        PageDTO<PostBriefInfo> pageBriefInfoDTO = new PageDTO<>(
                postBriefInfos.getContent(),
                postBriefInfos.getNumber(),
                postBriefInfos.getSize(),
                postBriefInfos.getTotalElements(),
                postBriefInfos.getTotalPages()
        );
        // redisTemplate.opsForValue().set(cacheKey, pageDTO, 10, TimeUnit.MINUTES);
        // logger.info("Cache miss, stored posts for key: {}", cacheKey);
        return pageBriefInfoDTO;
    }

    @Override
    // @Cacheable(value = "post", key = "#postId")
    public PostBriefInfo getPostById(Long postId) {
        validatePostId(postId);
        // String cacheKey = POST_CACHE_PREFIX + postId;
        // Post cachedPost = postRedisTemplate.opsForValue().get(cacheKey);
        // if (cachedPost != null) {
        //     logger.info("Cache hit for postId: {}", postId);
        //     return cachedPost;
        // }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Post not found with id: {}", postId);
                    return new EntityNotFoundException("Post not found with id: " + postId);
                });


        // 日志输出post.getReviewStatus
        logger.info("Post review status: {}", post.getReviewStatus());


        PostBriefInfo postBriefInfo = new PostBriefInfo();
        postBriefInfo.setId(post.getId());
        postBriefInfo.setTitle(post.getTitle());

        Hibernate.initialize(post.getTags());

        postBriefInfo.setTags(post.getTags());
        postBriefInfo.setContent(post.getContent());
        postBriefInfo.setUserId(post.getUserId());
        postBriefInfo.setCreatedAt(post.getCreatedAt());

        
        // 将getReviewStatus输出为日志
        logger.info("设置了Post review status: {}", post.getReviewStatus());


        postBriefInfo.setReviewStatus(post.getReviewStatus());
        postBriefInfo.setLikeCount(likeRepository.countByPostId(post.getId()));
        postBriefInfo.setCommentCount(commentRepository.countApprovedCommentsByPostId(post.getId()));
        return postBriefInfo;

        
        // postRedisTemplate.opsForValue().set(cacheKey, post, 10, TimeUnit.MINUTES);
        // logger.info("Cache miss, stored post for postId: {}", postId);
        // return post;
    }

    @Override
    public List<PostBriefInfo> getMyPosts(String userId) {
        validateUserId(userId);
        List<Post> posts = postRepository.findByUserIdAndReviewStatus(userId, "APPROVED");
        List<PostBriefInfo> postBriefInfos = posts.stream()
                .map(post -> {
                    PostBriefInfo postBriefInfo = new PostBriefInfo();
                    postBriefInfo.setId(post.getId());
                    postBriefInfo.setTitle(post.getTitle());
                    postBriefInfo.setTags(post.getTags());
                    postBriefInfo.setContent(post.getContent());
                    postBriefInfo.setUserId(post.getUserId());
                    postBriefInfo.setCreatedAt(post.getCreatedAt());
                    postBriefInfo.setReviewStatus(post.getReviewStatus());
                    postBriefInfo.setLikeCount(likeRepository.countByPostId(post.getId()));
                    postBriefInfo.setCommentCount(commentRepository.countApprovedCommentsByPostId(post.getId()));
                    return postBriefInfo;
                })
                .collect(Collectors.toList());
        return postBriefInfos;
    }

    @Override
    public List<String> getAllTags() {
        List<String> cachedTags = (List<String>) redisTemplate.opsForValue().get(TAGS_CACHE_KEY);
        if (cachedTags != null) {
            logger.info("Cache hit for tags");
            return cachedTags;
        }

        List<String> tags = postRepository.findAllByReviewStatus("APPROVED").stream()
                .flatMap(post -> post.getTags().stream())
                .collect(Collectors.toSet())
                .stream().sorted().collect(Collectors.toList());

        redisTemplate.opsForValue().set(TAGS_CACHE_KEY, tags, 1, TimeUnit.HOURS);
        logger.info("Cache miss, stored {} tags", tags.size());
        return tags;
    }

    @Override
    @Transactional
    // @CacheEvict(value = {"posts", "post"}, key = "#postDTO.id != null ? #postDTO.id : ''")
    public Post createPost(PostDTO postDTO, String userId) {
        validateUserId(userId);
        validatePostDTO(postDTO);

        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setTags(postDTO.getTags());
        post.setUserId(userId);
        post.setReviewStatus("PENDING");

        // 先保存 Post 以生成 ID
        Post savedPost = postRepository.save(post);

        logger.info("Created post with id: {} by user: {}", savedPost.getId(), userId);
        return savedPost;
    }


    @Override
    @CacheEvict(value = {"posts", "post"}, key = "#postId")
    public Post updatePost(Long postId, PostDTO postDTO, String userId) {
        validatePostId(postId);
        validateUserId(userId);
        validatePostDTO(postDTO);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Post not found with id: {}", postId);
                    return new EntityNotFoundException("Post not found with id: " + postId);
                });
        if (!post.getUserId().equals(userId)) {
            logger.warn("Unauthorized attempt to update post {} by user {}", postId, userId);
            throw new SecurityException("Unauthorized to update this post");
        }
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setTags(postDTO.getTags());
        Post saved = postRepository.save(post);
        logger.info("Updated post with id: {} by user: {}", postId, userId);
        return saved;
    }

    @Override
    @CacheEvict(value = {"posts", "post"}, key = "#postId")
    public Post deletePost(Long postId, String userId, Boolean identity) {
        validatePostId(postId);
        validateUserId(userId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Post not found with id: {}", postId);
                    return new EntityNotFoundException("Post not found with id: " + postId);
                });
        if (!post.getUserId().equals(userId) && identity == true) {
            logger.warn("Unauthorized attempt to delete post {} by user {}", postId, userId);
            throw new SecurityException("Unauthorized to delete this post");
        }
        postRepository.delete(post);
        logger.info("Deleted post with id: {} by user: {}", postId, userId);
        return post;
    }

    @Override
    @CacheEvict(value = {"posts", "post"}, key = "#postId")
    public PostBriefInfo approvePost(Long postId, String adminId) {
        validatePostId(postId);
        validateUserId(adminId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Post not found with id: {}", postId);
                    return new EntityNotFoundException("Post not found with id: " + postId);
                });
        post.setReviewStatus("APPROVED");
        Post saved = postRepository.save(post);
        logger.info("Approved post with id: {} by admin: {}", postId, adminId);
        PostBriefInfo postBriefInfo = new PostBriefInfo();
        postBriefInfo.setId(saved.getId());
        postBriefInfo.setTitle(saved.getTitle());
        postBriefInfo.setTags(saved.getTags());
        postBriefInfo.setContent(saved.getContent());
        postBriefInfo.setUserId(saved.getUserId());
        postBriefInfo.setCreatedAt(saved.getCreatedAt());
        postBriefInfo.setReviewStatus(saved.getReviewStatus());
        postBriefInfo.setLikeCount(likeRepository.countByPostId(saved.getId()));
        postBriefInfo.setCommentCount(commentRepository.countApprovedCommentsByPostId(saved.getId()));
        return postBriefInfo;
    }

    @Override
    public PageDTO<PostBriefInfo> getReviewQueue(String keyword, String category, String status, Pageable pageable) {
        Page<Post> posts;
        Page<PostBriefInfo> postBriefInfos;
        
        if (keyword != null && !keyword.isEmpty()) {
            posts = postRepository.findByKeywordAndReviewStatus(keyword, status, pageable);
        } else if (category != null && !category.isEmpty()) {
            // 检查是否为多个标签（用逗号分隔）
            List<String> tags = Arrays.asList(category.split(","));
            logger.info("Processing tags for review queue: {}", tags);
            
            if (tags.size() > 1) {
                // 多个标签搜索 - 需要添加对应的Repository方法
                posts = postRepository.findByTagsInAndReviewStatus(tags, status, pageable);
                logger.info("Querying review queue by multiple tags: {}", tags);
            } else {
                // 单个标签搜索（保持原有逻辑）
                posts = postRepository.findByTagAndReviewStatus(category, status, pageable);
                logger.info("Querying review queue by single category: {}", category);
            }
        } else {
            posts = postRepository.findByReviewStatus(status, pageable);
        }
        
        postBriefInfos = posts.map(post -> {
            PostBriefInfo postBriefInfo = new PostBriefInfo();
            postBriefInfo.setId(post.getId());
            postBriefInfo.setTitle(post.getTitle());
            postBriefInfo.setTags(post.getTags());
            postBriefInfo.setContent(post.getContent());
            postBriefInfo.setUserId(post.getUserId());
            postBriefInfo.setCreatedAt(post.getCreatedAt());
            postBriefInfo.setReviewStatus(post.getReviewStatus());
            postBriefInfo.setLikeCount(likeRepository.countByPostId(post.getId()));
            postBriefInfo.setCommentCount(commentRepository.countApprovedCommentsByPostId(post.getId()));
            return postBriefInfo;
        });
        PageDTO<PostBriefInfo> pageBriefInfoDTO = new PageDTO<>(
                postBriefInfos.getContent(),
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages()
        );
        return pageBriefInfoDTO;
    }

    @Scheduled(fixedRate = 300000)
    public void clearCache() {
        Set<String> postKeys = redisTemplate.keys("posts:*");
        if (postKeys != null) {
            redisTemplate.delete(postKeys);
        }
        Set<String> singlePostKeys = postRedisTemplate.keys(POST_CACHE_PREFIX + "*");
        if (singlePostKeys != null) {
            postRedisTemplate.delete(singlePostKeys);
        }
        Set<String> collectionKeys = redisTemplate.keys("userCollections:*");
        if (collectionKeys != null) {
            redisTemplate.delete(collectionKeys);
        }
        Set<String> likeKeys = redisTemplate.keys("userLikes:*");
        if (likeKeys != null) {
            redisTemplate.delete(likeKeys);
        }
        logger.info("Cleared posts, post, userCollections, and userLikes caches");
    }

    @Override
    @CacheEvict(value = {"posts", "post"}, key = "#postId")
    public Post banPost(Long postId, String adminId) {
        validatePostId(postId);
        validateUserId(adminId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Post not found with id: {}", postId);
                    return new EntityNotFoundException("Post not found with id: " + postId);
                });
        post.setReviewStatus("BANNED");
        Post saved = postRepository.save(post);
        logger.info("Banned post with id: {} by admin: {}", postId, adminId);
        return saved;
    }

    @Override
    public Report reportPost(Long postId, String userId, String reason, String detail) {
        validatePostId(postId);
        validateUserId(userId);
        validateReportReason(reason);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Post not found with id: {}", postId);
                    return new EntityNotFoundException("Post not found with id: " + postId);
                });
        if (!"APPROVED".equals(post.getReviewStatus())) {
            logger.warn("Attempt to report non-approved post: {}", postId);
            throw new IllegalStateException("Cannot report non-approved post");
        }
        Report report = new Report();
        report.setPostId(postId);
        report.setType("post");
        report.setCommentId(null);
        report.setUserId(userId);
        report.setReason(reason);
        report.setDetail(detail);
        report.setStatus(Report.ReportStatus.PENDING);
        Report saved = reportRepository.save(report);
        logger.info("Reported post with id: {} by user: {} with reason: {}", postId, userId, reason);
        return saved;
    }

    private void validatePostId(Long postId) {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("Invalid postId: " + postId);
        }
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid userId: " + userId);
        }
    }

    private void validatePostDTO(PostDTO postDTO) {
        if (postDTO == null || postDTO.getTitle() == null || postDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Post title cannot be empty");
        }
        if (postDTO.getContent() == null || postDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }
    }

    private void validateReportReason(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Report reason cannot be empty");
        }
        if (reason.length() > 500) {
            throw new IllegalArgumentException("Report reason cannot exceed 500 characters");
        }
    }

    @Override
    public Integer getApprovedPostCount() {
        return postRepository.getApprovedPostCount();
    }

    @Override
    public Integer getTodayApprovedPostCount() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        return postRepository.getTodayApprovedPostCount(startOfDay, endOfDay);
    }
}