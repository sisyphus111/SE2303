package com.se2303.communityservice.service.Impl;

import com.se2303.communityservice.dto.response.PostBriefInfo;
import com.se2303.communityservice.entity.Like;
import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.repository.CommentRepository;
import com.se2303.communityservice.repository.LikeRepository;
import com.se2303.communityservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    private Like like;
    private Post post;
    private String userId = "user1";
    private Long postId = 1L;

    @BeforeEach
    void setUp() {
        like = new Like();
        like.setId(1L);
        like.setPostId(postId);
        like.setUserId(userId);
        like.setCreatedAt(LocalDateTime.now());

        post = new Post();
        post.setId(postId);
        post.setUserId(userId);
        post.setTitle("Test Post");
        post.setContent("Test Content");
        post.setReviewStatus("APPROVED");
        post.setTags(Set.of("tag1"));
        post.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void likePost_Success() {
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        likeService.likePost(postId, userId);

        verify(likeRepository).save(any(Like.class));
        verify(likeRepository).existsByPostIdAndUserId(postId, userId);
    }

    @Test
    void likePost_AlreadyLiked_ThrowsIllegalStateException() {
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            likeService.likePost(postId, userId);
        });

        assertEquals("User has already liked this post", exception.getMessage());
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void likePost_InvalidPostId_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            likeService.likePost(0L, userId);
        });

        assertEquals("Invalid postId: 0", exception.getMessage());
    }

    @Test
    void likePost_InvalidUserId_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            likeService.likePost(postId, "");
        });

        assertEquals("Invalid userId: ", exception.getMessage());
    }

    @Test
    void unlikePost_Success() {
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(like));

        likeService.unlikePost(postId, userId);

        verify(likeRepository).delete(like);
        verify(likeRepository).findByPostIdAndUserId(postId, userId);
    }

    @Test
    void unlikePost_NotLiked_ThrowsIllegalStateException() {
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            likeService.unlikePost(postId, userId);
        });

        assertEquals("User has not liked this post", exception.getMessage());
        verify(likeRepository, never()).delete(any(Like.class));
    }

    @Test
    void unlikePost_InvalidPostId_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            likeService.unlikePost(0L, userId);
        });

        assertEquals("Invalid postId: 0", exception.getMessage());
    }

    @Test
    void getLikedPosts_Success() {
        List<Like> likes = Collections.singletonList(like);
        List<Post> posts = Collections.singletonList(post);
        when(likeRepository.findByUserId(userId)).thenReturn(likes);
        when(postRepository.findAllById(anyList())).thenReturn(posts);
        when(likeRepository.countByPostId(postId)).thenReturn(5L);
        when(commentRepository.countApprovedCommentsByPostId(postId)).thenReturn(3L);

        List<PostBriefInfo> result = likeService.getLikedPosts(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        PostBriefInfo briefInfo = result.get(0);
        assertEquals(post.getId(), briefInfo.getId());
        assertEquals(post.getTitle(), briefInfo.getTitle());
        assertEquals(post.getTags(), briefInfo.getTags());
        assertEquals(post.getContent(), briefInfo.getContent());
        assertEquals(post.getUserId(), briefInfo.getUserId());
        assertEquals(post.getCreatedAt(), briefInfo.getCreatedAt());
        assertEquals(post.getReviewStatus(), briefInfo.getReviewStatus());
        assertEquals(5L, briefInfo.getLikeCount());
        assertEquals(3L, briefInfo.getCommentCount());
        assertTrue(briefInfo.getLiked(), "The liked field should be true for liked posts");
    }

    @Test
    void getLikedPosts_NoApprovedPosts_ReturnsEmptyList() {
        Post unapprovedPost = new Post();
        unapprovedPost.setId(postId);
        unapprovedPost.setReviewStatus("PENDING");
        List<Like> likes = Collections.singletonList(like);
        when(likeRepository.findByUserId(userId)).thenReturn(likes);
        when(postRepository.findAllById(anyList())).thenReturn(Collections.singletonList(unapprovedPost));

        List<PostBriefInfo> result = likeService.getLikedPosts(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getLikedPosts_InvalidUserId_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            likeService.getLikedPosts("");
        });

        assertEquals("Invalid userId: ", exception.getMessage());
    }

    @Test
    void isLiked_True() {
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(true);

        Boolean result = likeService.isLiked(postId, userId);

        assertTrue(result);
        verify(likeRepository).existsByPostIdAndUserId(postId, userId);
    }

    @Test
    void isLiked_False() {
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(false);

        Boolean result = likeService.isLiked(postId, userId);

        assertFalse(result);
        verify(likeRepository).existsByPostIdAndUserId(postId, userId);
    }

    @Test
    void getLikedCountByUserId_Success() {
        // 创建两个帖子，确保每个帖子都有唯一的 ID
        Post post1 = new Post();
        post1.setId(1L);
        post1.setUserId("1");
        Post post2 = new Post();
        post2.setId(2L);
        post2.setUserId("1");
        List<Post> posts = Arrays.asList(post1, post2);

        // 模拟 postRepository 返回帖子列表
        when(postRepository.findByUserId(String.valueOf(1L))).thenReturn(posts);

        // 模拟 likeRepository.countByPostId 对每个帖子 ID 返回 2L
        when(likeRepository.countByPostId(1L)).thenReturn(2L);
        when(likeRepository.countByPostId(2L)).thenReturn(2L);

        Long result = likeService.getLikedCountByUserId(1L);

        assertEquals(4L, result); // 2 posts * 2 likes each
        verify(postRepository).findByUserId(String.valueOf(1L));
        verify(likeRepository).countByPostId(1L);
        verify(likeRepository).countByPostId(2L);
    }

    @Test
    void getLikedCountByUserId_NoPosts_ReturnsZero() {
        when(postRepository.findByUserId(String.valueOf(1L))).thenReturn(Collections.emptyList());

        Long result = likeService.getLikedCountByUserId(1L);

        assertEquals(0L, result);
        verify(postRepository).findByUserId(String.valueOf(1L));
        verify(likeRepository, never()).countByPostId(anyLong());
    }
}