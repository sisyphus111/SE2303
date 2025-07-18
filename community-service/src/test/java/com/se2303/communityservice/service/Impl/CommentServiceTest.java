package com.se2303.communityservice.service.Impl;

import com.se2303.communityservice.dto.CommentDTO;
import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.entity.Comment;
import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.entity.Report;
import com.se2303.communityservice.repository.CommentRepository;
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
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    private Comment comment;
    private Post post;
    private CommentDTO commentDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        comment.setId(1L);
        comment.setPostId(1L);
        comment.setUserId("user1");
        comment.setContent("Test comment");
        comment.setReviewStatus("PENDING");
        comment.setCreatedAt(LocalDateTime.now());

        post = new Post();
        post.setId(1L);
        post.setUserId("user1");
        post.setTitle("Test Post");
        post.setContent("Test Content");
        post.setReviewStatus("APPROVED");

        commentDTO = new CommentDTO();
        commentDTO.setContent("Test comment");

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void addComment_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.addComment(1L, commentDTO, "user1");

        assertNotNull(result);
        assertEquals("PENDING", result.getReviewStatus());
        assertEquals("user1", result.getUserId());
        verify(redisTemplate, times(2)).delete(anyString());
    }

    @Test
    void addComment_PostNotFound_ThrowsEntityNotFoundException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            commentService.addComment(1L, commentDTO, "user1");
        });

        assertEquals("Post not found with id: 1", exception.getMessage());
    }

    @Test
    void addComment_InvalidPostId_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            commentService.addComment(null, commentDTO, "user1");
        });

        assertEquals("Invalid postId: null", exception.getMessage());
    }

    @Test
    void addComment_EmptyContent_ThrowsIllegalArgumentException() {
        commentDTO.setContent("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            commentService.addComment(1L, commentDTO, "user1");
        });

        assertEquals("Comment content cannot be empty", exception.getMessage());
    }

    @Test
    void addComment_OptimisticLockFailure_ThrowsJpaOptimisticLockingFailureException() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        // 构造 OptimisticLockException 并包装在 JpaOptimisticLockingFailureException 中
        when(commentRepository.save(any(Comment.class)))
                .thenThrow(new JpaOptimisticLockingFailureException(
                        new jakarta.persistence.OptimisticLockException("Optimistic lock failure")));

        assertThrows(JpaOptimisticLockingFailureException.class, () -> {
            commentService.addComment(1L, commentDTO, "user1");
        });
    }

    @Test
    void getCommentsByPostId_Success() {
        List<Comment> comments = Collections.singletonList(comment);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, 1);
        when(commentRepository.findByPostId(1L, pageable)).thenReturn(commentPage);

        PageDTO<Comment> result = commentService.getCommentsByPostId(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(comments, result.getContent());
    }

    @Test
    void getCommentsByPostId_InvalidPostId_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            commentService.getCommentsByPostId(0L, pageable);
        });

        assertEquals("Invalid postId: 0", exception.getMessage());
    }

    @Test
    void approveComment_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.approveComment(1L, "admin1");

        assertEquals("APPROVED", result.getReviewStatus());
        verify(commentRepository).save(comment);
    }

    @Test
    void approveComment_CommentNotFound_ThrowsEntityNotFoundException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            commentService.approveComment(1L, "admin1");
        });

        assertEquals("Comment not found with id: 1", exception.getMessage());
    }

    @Test
    void banComment_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.banComment(1L, "admin1");

        assertEquals("BANNED", result.getReviewStatus());
        verify(commentRepository).save(comment);
    }

    @Test
    void deleteComment_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.deleteComment(1L, "admin1");

        assertEquals("DELETED", result.getReviewStatus());
        verify(commentRepository).save(comment);
    }

    @Test
    void getReviewQueue_WithKeyword_Success() {
        List<Comment> comments = Collections.singletonList(comment);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, 1);
        when(commentRepository.findByKeywordAndReviewStatus("test", "PENDING", pageable)).thenReturn(commentPage);

        PageDTO<Comment> result = commentService.getReviewQueue("test", "PENDING", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(comments, result.getContent());
    }

    @Test
    void getReviewQueue_WithoutKeyword_Success() {
        List<Comment> comments = Collections.singletonList(comment);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, 1);
        when(commentRepository.findByReviewStatus("PENDING", pageable)).thenReturn(commentPage);

        PageDTO<Comment> result = commentService.getReviewQueue(null, "PENDING", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(comments, result.getContent());
    }

    @Test
    void getCommentDetail_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Map<String, Object> result = commentService.getCommentDetail(1L);

        assertNotNull(result);
        assertEquals(comment, result.get("comment"));
        assertEquals(post, result.get("post"));
    }

    @Test
    void getCommentStats_Success() {
        when(commentRepository.count()).thenReturn(100L);
        when(commentRepository.countByReviewStatus("PENDING")).thenReturn(30L);
        when(commentRepository.countByReviewStatus("APPROVED")).thenReturn(50L);
        when(commentRepository.countByReviewStatus("BANNED")).thenReturn(20L);

        Map<String, Object> result = commentService.getCommentStats();

        assertEquals(100L, result.get("total"));
        assertEquals(30L, result.get("pending"));
        assertEquals(50L, result.get("approved"));
        assertEquals(20L, result.get("banned"));
    }

    @Test
    void reportComment_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(reportRepository.save(any(Report.class))).thenReturn(new Report());

        Report result = commentService.reportComment(1L, "user1", "Inappropriate", "Details");

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus().toString());
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void getAdminComments_WithKeyword_Success() {
        List<Comment> comments = Collections.singletonList(comment);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, 1);
        when(commentRepository.findByContentContainingIgnoreCase("test", pageable)).thenReturn(commentPage);

        Map<String, Object> result = commentService.getAdminComments("test", "createdAt", 1, 10);

        assertNotNull(result);
        assertEquals(1L, result.get("totalElements"));
        assertEquals(comments, result.get("content"));
    }

    @Test
    void getAdminComments_WithoutKeyword_Success() {
        List<Comment> comments = Collections.singletonList(comment);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, 1);
        when(commentRepository.findAll(pageable)).thenReturn(commentPage);

        Map<String, Object> result = commentService.getAdminComments(null, "createdAt", 1, 10);

        assertNotNull(result);
        assertEquals(1L, result.get("totalElements"));
        assertEquals(comments, result.get("content"));
    }

    @Test
    void getTodayApprovedCommentCount_Success() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        when(commentRepository.getTodayApprovedCommentCount(startOfDay, endOfDay)).thenReturn(10);

        Integer result = commentService.getTodayApprovedCommentCount();

        assertEquals(10, result);
    }

    @Test
    void getCommentsByTags_Success() {
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<Comment> comments = Collections.singletonList(comment);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, 1);
        when(commentRepository.findByTagsInAndReviewStatus(tags, "APPROVED", pageable)).thenReturn(commentPage);

        PageDTO<Comment> result = commentService.getCommentsByTags(tags, "APPROVED", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(comments, result.getContent());
    }

    @Test
    void getCommentsByTags_EmptyTags_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            commentService.getCommentsByTags(Collections.emptyList(), "APPROVED", pageable);
        });

        assertEquals("标签列表不能为空", exception.getMessage());
    }

    @Test
    void getCommentsByTagsOrderByCreatedAt_Success() {
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<Comment> comments = Collections.singletonList(comment);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, 1);
        when(commentRepository.findByTagsInAndReviewStatusOrderByCreatedAtDesc(tags, "APPROVED", pageable)).thenReturn(commentPage);

        PageDTO<Comment> result = commentService.getCommentsByTagsOrderByCreatedAt(tags, "APPROVED", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(comments, result.getContent());
    }

    @Test
    void getAllCommentsByTags_Success() {
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<Comment> comments = Collections.singletonList(comment);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, 1);
        when(commentRepository.findAllCommentsByTagsIn(tags, pageable)).thenReturn(commentPage);

        PageDTO<Comment> result = commentService.getAllCommentsByTags(tags, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(comments, result.getContent());
    }

    @Test
    void getCommentCountByUserId_Success() {
        when(commentRepository.countByUserId("1")).thenReturn(5L);

        Long result = commentService.getCommentCountByUserId(1L);

        assertEquals(5L, result);
    }
}