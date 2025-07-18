package com.se2303.communityservice.service.Impl;

import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.dto.PostDTO;
import com.se2303.communityservice.dto.response.PostBriefInfo;
import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.entity.Report;
import com.se2303.communityservice.repository.CommentRepository;
import com.se2303.communityservice.repository.LikeRepository;
import com.se2303.communityservice.repository.PostRepository;
import com.se2303.communityservice.repository.ReportRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private RedisTemplate<String, Post> postRedisTemplate;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    private Post post;
    private PostDTO postDTO;
    private PostBriefInfo postBriefInfo;
    private Pageable pageable;
    private String userId = "user1";
    private String adminId = "admin1";
    private Long postId = 1L;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(postId);
        post.setUserId(userId);
        post.setTitle("Test Post");
        post.setContent("Test Content");
        post.setTags(Set.of("tag1"));
        post.setReviewStatus("APPROVED");
        post.setCreatedAt(LocalDateTime.now());

        postDTO = new PostDTO();
        postDTO.setTitle("Test Post");
        postDTO.setContent("Test Content");
        postDTO.setTags(Set.of("tag1"));

        postBriefInfo = new PostBriefInfo();
        postBriefInfo.setId(postId);
        postBriefInfo.setTitle("Test Post");
        postBriefInfo.setContent("Test Content");
        postBriefInfo.setTags(Set.of("tag1"));
        postBriefInfo.setUserId(userId);
        postBriefInfo.setCreatedAt(post.getCreatedAt());
        postBriefInfo.setReviewStatus("APPROVED");
        postBriefInfo.setLikeCount(5L);
        postBriefInfo.setCommentCount(3L);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getPosts_ByKeyword_LatestSort_Success() {
        List<Post> posts = Collections.singletonList(post);
        Page<Post> postPage = new PageImpl<>(posts, pageable, 1);
        when(postRepository.findByKeywordAndReviewStatusOrderByCreatedAtDesc("test", "APPROVED", pageable)).thenReturn(postPage);
        when(likeRepository.countByPostId(postId)).thenReturn(5L);
        when(commentRepository.countApprovedCommentsByPostId(postId)).thenReturn(3L);

        PageDTO<PostBriefInfo> result = postService.getPosts("test", null, "APPROVED", "latest", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(post.getTitle(), result.getContent().get(0).getTitle());
        assertEquals(5L, result.getContent().get(0).getLikeCount());
        assertEquals(3L, result.getContent().get(0).getCommentCount());
    }

    @Test
    void getPosts_ByCategory_MostLikedSort_Success() {
        List<Post> posts = Collections.singletonList(post);
        Page<Post> postPage = new PageImpl<>(posts, pageable, 1);
        when(postRepository.findByTagAndReviewStatusOrderByLikeCountDesc("tag1", "APPROVED", pageable)).thenReturn(postPage);
        when(likeRepository.countByPostId(postId)).thenReturn(5L);
        when(commentRepository.countApprovedCommentsByPostId(postId)).thenReturn(3L);

        PageDTO<PostBriefInfo> result = postService.getPosts(null, "tag1", "APPROVED", "mostLiked", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(post.getTitle(), result.getContent().get(0).getTitle());
    }

    @Test
    void getPosts_MultipleTags_MostCommentedSort_Success() {
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<Post> posts = Collections.singletonList(post);
        Page<Post> postPage = new PageImpl<>(posts, pageable, 1);
        when(postRepository.findByTagsInAndReviewStatusOrderByCommentCountDesc(tags, "APPROVED", pageable)).thenReturn(postPage);
        when(likeRepository.countByPostId(postId)).thenReturn(5L);
        when(commentRepository.countApprovedCommentsByPostId(postId)).thenReturn(3L);

        PageDTO<PostBriefInfo> result = postService.getPosts(null, "tag1,tag2", "APPROVED", "mostCommented", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(post.getTitle(), result.getContent().get(0).getTitle());
    }

    @Test
    void getPosts_AllPosts_AllStatus_Success() {
        List<Post> posts = Collections.singletonList(post);
        Page<Post> postPage = new PageImpl<>(posts, pageable, 1);
        when(postRepository.findAllPosts(pageable)).thenReturn(postPage);
        when(likeRepository.countByPostId(postId)).thenReturn(5L);
        when(commentRepository.countApprovedCommentsByPostId(postId)).thenReturn(3L);

        PageDTO<PostBriefInfo> result = postService.getPosts(null, null, "ALL", "latest", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(post.getTitle(), result.getContent().get(0).getTitle());
    }

    @Test
    void getPostById_Success() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.countByPostId(postId)).thenReturn(5L);
        when(commentRepository.countApprovedCommentsByPostId(postId)).thenReturn(3L);

        PostBriefInfo result = postService.getPostById(postId);

        assertNotNull(result);
        assertEquals(post.getTitle(), result.getTitle());
        assertEquals(5L, result.getLikeCount());
        assertEquals(3L, result.getCommentCount());
    }

    @Test
    void getPostById_NotFound_ThrowsEntityNotFoundException() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            postService.getPostById(postId);
        });

        assertEquals("Post not found with id: " + postId, exception.getMessage());
    }

    @Test
    void getPostById_InvalidPostId_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.getPostById(0L);
        });

        assertEquals("Invalid postId: 0", exception.getMessage());
    }

    @Test
    void getMyPosts_Success() {
        List<Post> posts = Collections.singletonList(post);
        when(postRepository.findByUserIdAndReviewStatus(userId, "APPROVED")).thenReturn(posts);
        when(likeRepository.countByPostId(postId)).thenReturn(5L);
        when(commentRepository.countApprovedCommentsByPostId(postId)).thenReturn(3L);

        List<PostBriefInfo> result = postService.getMyPosts(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(post.getTitle(), result.get(0).getTitle());
        assertEquals(5L, result.get(0).getLikeCount());
        assertEquals(3L, result.get(0).getCommentCount());
    }

    @Test
    void getMyPosts_InvalidUserId_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.getMyPosts("");
        });

        assertEquals("Invalid userId: ", exception.getMessage());
    }

    @Test
    void getAllTags_Success() {
        List<Post> posts = Collections.singletonList(post);
        when(postRepository.findAllByReviewStatus("APPROVED")).thenReturn(posts);

        // 模拟 redisTemplate.opsForValue() 返回 ValueOperations
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("all_tags")).thenReturn(null);

        List<String> result = postService.getAllTags();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("tag1", result.get(0));
        verify(valueOps).set(eq("all_tags"), anyList(), eq(1L), eq(TimeUnit.HOURS));
    }

    @Test
    void getAllTags_CacheHit_Success() {
        List<String> cachedTags = Collections.singletonList("tag1");

        // 模拟 redisTemplate.opsForValue() 返回 ValueOperations
        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("all_tags")).thenReturn(cachedTags);

        List<String> result = postService.getAllTags();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("tag1", result.get(0));
        verify(postRepository, never()).findAllByReviewStatus(anyString());
    }

    @Test
    void createPost_Success() {
        // 创建一个新的 Post 对象，状态为 PENDING
        Post savedPost = new Post();
        savedPost.setId(postId);
        savedPost.setUserId(userId);
        savedPost.setTitle(postDTO.getTitle());
        savedPost.setContent(postDTO.getContent());
        savedPost.setTags(postDTO.getTags());
        savedPost.setReviewStatus("PENDING");
        savedPost.setCreatedAt(LocalDateTime.now());

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        Post result = postService.createPost(postDTO, userId);

        assertNotNull(result);
        assertEquals("PENDING", result.getReviewStatus());
        assertEquals(postDTO.getTitle(), result.getTitle());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void createPost_InvalidPostDTO_ThrowsIllegalArgumentException() {
        postDTO.setTitle("");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.createPost(postDTO, userId);
        });

        assertEquals("Post title cannot be empty", exception.getMessage());
    }

    @Test
    void updatePost_Success() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post result = postService.updatePost(postId, postDTO, userId);

        assertNotNull(result);
        assertEquals(postDTO.getTitle(), result.getTitle());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void updatePost_Unauthorized_ThrowsSecurityException() {
        Post differentUserPost = new Post();
        differentUserPost.setId(postId);
        differentUserPost.setUserId("otherUser");
        when(postRepository.findById(postId)).thenReturn(Optional.of(differentUserPost));

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            postService.updatePost(postId, postDTO, userId);
        });

        assertEquals("Unauthorized to update this post", exception.getMessage());
    }

    @Test
    void deletePost_Success() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.deletePost(postId, userId, true);

        assertNotNull(result);
        verify(postRepository).delete(post);
    }

    @Test
    void deletePost_Unauthorized_ThrowsSecurityException() {
        Post differentUserPost = new Post();
        differentUserPost.setId(postId);
        differentUserPost.setUserId("otherUser");
        when(postRepository.findById(postId)).thenReturn(Optional.of(differentUserPost));

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            postService.deletePost(postId, userId, true);
        });

        assertEquals("Unauthorized to delete this post", exception.getMessage());
    }

    @Test
    void approvePost_Success() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(likeRepository.countByPostId(postId)).thenReturn(5L);
        when(commentRepository.countApprovedCommentsByPostId(postId)).thenReturn(3L);

        PostBriefInfo result = postService.approvePost(postId, adminId);

        assertNotNull(result);
        assertEquals("APPROVED", result.getReviewStatus());
        assertEquals(5L, result.getLikeCount());
        assertEquals(3L, result.getCommentCount());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void getReviewQueue_ByKeyword_Success() {
        List<Post> posts = Collections.singletonList(post);
        Page<Post> postPage = new PageImpl<>(posts, pageable, 1);
        when(postRepository.findByKeywordAndReviewStatus("test", "PENDING", pageable)).thenReturn(postPage);
        when(likeRepository.countByPostId(postId)).thenReturn(5L);
        when(commentRepository.countApprovedCommentsByPostId(postId)).thenReturn(3L);

        PageDTO<PostBriefInfo> result = postService.getReviewQueue("test", null, "PENDING", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(post.getTitle(), result.getContent().get(0).getTitle());
    }

    @Test
    void getReviewQueue_ByMultipleTags_Success() {
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<Post> posts = Collections.singletonList(post);
        Page<Post> postPage = new PageImpl<>(posts, pageable, 1);
        when(postRepository.findByTagsInAndReviewStatus(tags, "PENDING", pageable)).thenReturn(postPage);
        when(likeRepository.countByPostId(postId)).thenReturn(5L);
        when(commentRepository.countApprovedCommentsByPostId(postId)).thenReturn(3L);

        PageDTO<PostBriefInfo> result = postService.getReviewQueue(null, "tag1,tag2", "PENDING", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(post.getTitle(), result.getContent().get(0).getTitle());
    }

    @Test
    void clearCache_Success() {
        when(redisTemplate.keys("posts:*")).thenReturn(Set.of("posts:1"));
        when(postRedisTemplate.keys("post:*")).thenReturn(Set.of("post:1"));
        when(redisTemplate.keys("userCollections:*")).thenReturn(Set.of("userCollections:1"));
        when(redisTemplate.keys("userLikes:*")).thenReturn(Set.of("userLikes:1"));

        postService.clearCache();

        verify(redisTemplate).delete(Set.of("posts:1"));
        verify(postRedisTemplate).delete(Set.of("post:1"));
        verify(redisTemplate).delete(Set.of("userCollections:1"));
        verify(redisTemplate).delete(Set.of("userLikes:1"));
    }

    @Test
    void banPost_Success() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post result = postService.banPost(postId, adminId);

        assertNotNull(result);
        assertEquals("BANNED", result.getReviewStatus());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void reportPost_Success() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(reportRepository.save(any(Report.class))).thenReturn(new Report());

        Report result = postService.reportPost(postId, userId, "Inappropriate", "Details");

        assertNotNull(result);
        assertEquals(Report.ReportStatus.PENDING, result.getStatus());
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void reportPost_NonApprovedPost_ThrowsIllegalStateException() {
        post.setReviewStatus("PENDING");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            postService.reportPost(postId, userId, "Inappropriate", "Details");
        });

        assertEquals("Cannot report non-approved post", exception.getMessage());
    }

    @Test
    void getApprovedPostCount_Success() {
        when(postRepository.getApprovedPostCount()).thenReturn(10);

        Integer result = postService.getApprovedPostCount();

        assertEquals(10, result);
        verify(postRepository).getApprovedPostCount();
    }

    @Test
    void getTodayApprovedPostCount_Success() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        when(postRepository.getTodayApprovedPostCount(startOfDay, endOfDay)).thenReturn(5);

        Integer result = postService.getTodayApprovedPostCount();

        assertEquals(5, result);
        verify(postRepository).getTodayApprovedPostCount(startOfDay, endOfDay);
    }
}