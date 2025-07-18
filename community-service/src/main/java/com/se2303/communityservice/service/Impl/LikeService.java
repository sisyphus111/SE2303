package com.se2303.communityservice.service.Impl;

import com.se2303.communityservice.dto.response.PostBriefInfo;
import com.se2303.communityservice.entity.Like;
import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.repository.LikeRepository;
import com.se2303.communityservice.repository.PostRepository;
import com.se2303.communityservice.repository.CommentRepository;
import com.se2303.communityservice.service.ILikeService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService implements ILikeService {
    private static final Logger logger = LoggerFactory.getLogger(LikeService.class);

    // 使用内存锁替代Redisson分布式锁（适用于单实例部署）
    private final ConcurrentHashMap<String, Object> lockMap = new ConcurrentHashMap<>();

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String LIKE_COUNT_PREFIX = "like_count:";

    // @Override
    // @Transactional(isolation = Isolation.READ_COMMITTED)
    // public void likePost(Long postId, String userId) {
    //     validateInputs(postId, userId);
    //     String lockKey = "post:like:" + postId + ":" + userId;
    //     Object lock = lockMap.computeIfAbsent(lockKey, k -> new Object());
        
    //     synchronized (lock) {
    //         try {
    //             if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
    //                 logger.info("User {} already liked post {}", userId, postId);
    //                 throw new IllegalStateException("User has already liked this post");
    //             }

    //             Like like = new Like();
    //             like.setPostId(postId);
    //             like.setUserId(userId);
    //             likeRepository.save(like);
    //             logger.info("User {} liked post {}", userId, postId);

    //             String redisKey = LIKE_COUNT_PREFIX + postId;
    //             redisTemplate.opsForValue().increment(redisKey, 1L);
              
    //         } finally {
    //             // 清理锁对象，避免内存泄漏
    //             lockMap.remove(lockKey);
    //         }
    //     }
    // }

    // @Override
    // @Transactional(isolation = Isolation.READ_COMMITTED)
    // public void unlikePost(Long postId, String userId) {
    //     validateInputs(postId, userId);
    //     String lockKey = "post:like:" + postId + ":" + userId;
    //     Object lock = lockMap.computeIfAbsent(lockKey, k -> new Object());
        
    //     synchronized (lock) {
    //         try {
    //             Like like = (Like) likeRepository.findByPostIdAndUserId(postId, userId)
    //                     .orElseThrow(() -> {
    //                         logger.info("User {} has not liked post {}", userId, postId);
    //                         return new IllegalStateException("User has not liked this post");
    //                     });

    //             likeRepository.delete(like);
    //             logger.info("User {} unliked post {}", userId, postId);

    //             String redisKey = LIKE_COUNT_PREFIX + postId;
    //             redisTemplate.opsForValue().decrement(redisKey, 1L);
                
    //         } finally {
    //             // 清理锁对象，避免内存泄漏
    //             lockMap.remove(lockKey);
    //         }
    //     }
    // }

    @Override
    @Transactional
    public void likePost(Long postId, String userId) {
        validateInputs(postId, userId);
       
        if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
            logger.info("User {} already liked post {}", userId, postId);
            throw new IllegalStateException("User has already liked this post");
        }

        Like like = new Like();
        like.setPostId(postId);
        like.setUserId(userId);
        likeRepository.save(like);
        logger.info("User {} liked post {}", userId, postId);
        
    }

    @Override
    @Transactional
    public void unlikePost(Long postId, String userId) {
        validateInputs(postId, userId);
        
        Like like = (Like) likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> {
                    logger.info("User {} has not liked post {}", userId, postId);
                    return new IllegalStateException("User has not liked this post");
                });

        likeRepository.delete(like);
        logger.info("User {} unliked post {}", userId, postId);
        
    }


    @Override
    // @Transactional(readOnly = true)
    @Transactional
    public List<PostBriefInfo> getLikedPosts(String userId) {
        validateInputs(null, userId);
        List<Like> likes = likeRepository.findByUserId(userId);
        List<Long> postIds = likes.stream()
                .map(Like::getPostId)
                .collect(Collectors.toList());
        List<Post> posts = postRepository.findAllById(postIds).stream()
                .filter(post -> "APPROVED".equals(post.getReviewStatus()))
                .collect(Collectors.toList());
        logger.info("Fetched {} liked posts for user {}", posts.size(), userId);
        return posts.stream()
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
    }


    private void validateInputs(Long postId, String userId) {
        if (postId != null && (postId <= 0)) {
            throw new IllegalArgumentException("Invalid postId: " + postId);
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid userId: " + userId);
        }
    }

    @Override
    public Boolean isLiked(Long postId, String userId) {
        // validateInputs(postId, userId);
        
        // 输出postId和userId
        logger.info("isLiked的postId: {}", postId);
        logger.info("isLiked的userId: {}", userId);

        return likeRepository.existsByPostIdAndUserId(postId, userId);
    }

    @Override
    public Long getLikedCountByUserId(Long userId) {
        // 在postRepository中遍历该用户的所有帖子，将其点赞数求和
        List<Post> posts = postRepository.findByUserId(String.valueOf(userId));
        Long likedCount = 0L;
        for (Post post : posts) {
            likedCount += likeRepository.countByPostId(post.getId());
        }
        return likedCount;
    }
}