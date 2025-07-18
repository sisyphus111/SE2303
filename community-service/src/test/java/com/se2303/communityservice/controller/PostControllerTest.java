package com.se2303.communityservice.controller;

import com.se2303.communityservice.client.InspectionServiceClient;
import com.se2303.communityservice.dto.CommentDTO;
import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.dto.PostDTO;
import com.se2303.communityservice.dto.response.PostBriefInfo;
import com.se2303.communityservice.dto.response.UserStatisticsResponse;
import com.se2303.communityservice.entity.Comment;
import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.entity.Report;
import com.se2303.communityservice.response.ApiResponse;
import com.se2303.communityservice.service.ICommentService;
import com.se2303.communityservice.service.ILikeService;
import com.se2303.communityservice.service.IPostService;
import com.se2303.communityservice.service.InformProducer;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = PostControllerTest.TestConfig.class)
class PostControllerTest {

    @Configuration
    @EnableAsync
    static class TestConfig {
        @Bean
        public Executor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(1);
            executor.setMaxPoolSize(1);
            executor.setQueueCapacity(1);
            executor.setThreadNamePrefix("TestExecutor-");
            executor.initialize();
            return executor;
        }
    }

    @InjectMocks
    private PostController postController;

    @Mock
    private IPostService postService;

    @Mock
    private ICommentService commentService;

    @Mock
    private ILikeService likeService;

    @Mock
    private InspectionServiceClient inspectionClient;

    @Mock
    private InformProducer informProducer;

    @Mock
    private HttpServletRequest request;

    private PostBriefInfo postBriefInfo;
    private Post post;
    private Comment comment;
    private PostDTO postDTO;
    private CommentDTO commentDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 初始化 PostBriefInfo
        postBriefInfo = new PostBriefInfo();
        postBriefInfo.setId(1L);
        postBriefInfo.setTitle("Test Post");
        postBriefInfo.setContent("Test Content");
        postBriefInfo.setUserId("user123");
        postBriefInfo.setReviewStatus("APPROVED");
        postBriefInfo.setCreatedAt(LocalDateTime.now());
        postBriefInfo.setTags(new HashSet<>(Arrays.asList("test", "example")));
        postBriefInfo.setLikeCount(10L);
        postBriefInfo.setCommentCount(5L);
        postBriefInfo.setLiked(false);

        // 初始化 Post
        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setContent("Test Content");
        post.setUserId("user123");
        post.setReviewStatus("APPROVED");
        post.setCreatedAt(LocalDateTime.now());
        post.setTags(new HashSet<>(Arrays.asList("test", "example")));

        // 初始化 Comment
        comment = new Comment();
        comment.setId(1L);
        comment.setPostId(1L);
        comment.setUserId("user123");
        comment.setContent("Test Comment");
        comment.setReviewStatus("APPROVED");
        comment.setCreatedAt(LocalDateTime.now());

        // 初始化 PostDTO
        postDTO = new PostDTO();
        postDTO.setTitle("Test Post");
        postDTO.setContent("Test Content");
        postDTO.setTags(new HashSet<>(Arrays.asList("test", "example")));

        // 初始化 CommentDTO
        commentDTO = new CommentDTO();
        commentDTO.setContent("Test Comment");

        // 默认模拟内容安全检查
        when(inspectionClient.isContentSafe(anyString())).thenReturn(true);
    }

    @Test
    void testGetPosts_Success() {
        // 准备分页数据
        PageDTO<PostBriefInfo> pageDTO = new PageDTO<>(
                Collections.singletonList(postBriefInfo), 0, 5, 1, 1);
        when(postService.getPosts(eq("test"), eq("test"), eq("APPROVED"), eq("latest"), any(Pageable.class)))
                .thenReturn(pageDTO);
        when(request.getQueryString()).thenReturn("keyword=test&category=test&page=1&size=5");
        when(request.getParameter("category")).thenReturn("test");

        // 执行测试
        ResponseEntity<ApiResponse> response = postController.getPosts(
                "test", "test", "APPROVED", "latest", 1, 5,
                "user123", "true", "true", request);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("帖子分页查询成功", apiResponse.getMessage());
        assertEquals(pageDTO, apiResponse.getObject());
        assertEquals(1, ((PageDTO<?>) apiResponse.getObject()).getContent().size());
        verify(postService).getPosts(eq("test"), eq("test"), eq("APPROVED"), eq("latest"), any(Pageable.class));
    }

    @Test
    void testGetPosts_InvalidHeaders() {
        // 测试无效请求头
        ResponseEntity<ApiResponse> response = postController.getPosts(
                null, null, null, "latest", 1, 5,
                null, null, null, request);

        // 验证结果
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertFalse(apiResponse.isSuccess());
        assertEquals("未登录或请求头无效", apiResponse.getMessage());
        assertNull(apiResponse.getObject());
    }

    @Test
    void testGetPosts_InvalidUser() {
        ResponseEntity<ApiResponse> response = postController.getPosts(
                null, null, null, "latest", 1, 5,
                "user123", "false", "true", request);

        // 验证结果
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertFalse(apiResponse.isSuccess());
        assertEquals("用户状态无效", apiResponse.getMessage());
        assertNull(apiResponse.getObject());
    }

    @Test
    void testGetPosts_InvalidSortBy() {
        ResponseEntity<ApiResponse> response = postController.getPosts(
                null, null, null, "invalidSort", 1, 5,
                "user123", "true", "true", request);

        // 验证结果
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertFalse(apiResponse.isSuccess());
        assertTrue(apiResponse.getMessage().contains("无效的排序方式"));
        assertNull(apiResponse.getObject());
    }

    @Test
    void testGetPosts_InvalidPageParams() {
        ResponseEntity<ApiResponse> response = postController.getPosts(
                null, null, null, "latest", 0, 5,
                "user123", "true", "true", request);

        // 验证结果
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertFalse(apiResponse.isSuccess());
        assertTrue(apiResponse.getMessage().contains("无效的分页参数"));
        assertNull(apiResponse.getObject());
    }

    @Test
    void testGetMyLikes_Success() {
        when(likeService.getLikedPosts("user123")).thenReturn(Collections.singletonList(postBriefInfo));

        ResponseEntity<ApiResponse> response = postController.getMyLikes("user123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("用户点赞的帖子查询成功", apiResponse.getMessage());
        assertEquals(1, ((List<?>) apiResponse.getObject()).size());
        verify(likeService).getLikedPosts("user123");
    }

    @Test
    void testGetPostById_Success() {
        when(postService.getPostById(1L)).thenReturn(postBriefInfo);
        when(likeService.isLiked(1L, "user123")).thenReturn(true);

        ResponseEntity<ApiResponse> response = postController.getPostById(1L, "user123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("帖子详情查询成功", apiResponse.getMessage());
        PostBriefInfo result = (PostBriefInfo) apiResponse.getObject();
        assertTrue(result.getLiked());
        assertEquals("Test Post", result.getTitle());
        verify(postService).getPostById(1L);
        verify(likeService, times(1)).isLiked(1L, "user123"); // 明确指定调用一次
    }

    @Test
    void testGetPostById_NotApproved() {
        postBriefInfo.setReviewStatus("PENDING");
        when(postService.getPostById(1L)).thenReturn(postBriefInfo);

        ResponseEntity<ApiResponse> response = postController.getPostById(1L, "user123", "true", "true");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertFalse(apiResponse.isSuccess());
        assertEquals("无权访问未审核通过的帖子", apiResponse.getMessage());
        assertNull(apiResponse.getObject());
    }

    @Test
    void testGetMyPosts_Success() {
        when(postService.getMyPosts("user123")).thenReturn(Collections.singletonList(postBriefInfo));

        ResponseEntity<ApiResponse> response = postController.getMyPosts("user123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("我的帖子查询成功", apiResponse.getMessage());
        assertEquals(1, ((List<?>) apiResponse.getObject()).size());
        verify(postService).getMyPosts("user123");
    }

    @Test
    void testGetAllTags_Success() {
        when(postService.getAllTags()).thenReturn(Arrays.asList("test", "example"));

        ResponseEntity<ApiResponse> response = postController.getAllTags("user123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("标签查询成功", apiResponse.getMessage());
        assertEquals(2, ((List<?>) apiResponse.getObject()).size());
        verify(postService).getAllTags();
    }

    @Test
    void testGetComments_Success() {
        PageDTO<Comment> pageDTO = new PageDTO<>(
                Collections.singletonList(comment), 0, 10, 1, 1);
        when(postService.getPostById(1L)).thenReturn(postBriefInfo);
        when(commentService.getCommentsByPostId(eq(1L), any(Pageable.class))).thenReturn(pageDTO);

        ResponseEntity<ApiResponse> response = postController.getComments(1L, 1, 10, "user123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("分页评论查询成功", apiResponse.getMessage());
        assertEquals(pageDTO, apiResponse.getObject());
        assertEquals(1, ((PageDTO<?>) apiResponse.getObject()).getContent().size());
        verify(commentService).getCommentsByPostId(eq(1L), any(Pageable.class));
    }

    @Test
    void testGetComments_NotApprovedPost() {
        postBriefInfo.setReviewStatus("PENDING");
        when(postService.getPostById(1L)).thenReturn(postBriefInfo);

        ResponseEntity<ApiResponse> response = postController.getComments(1L, 1, 10, "user123", "true", "true");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertFalse(apiResponse.isSuccess());
        assertEquals("无权访问未审核通过帖子的评论", apiResponse.getMessage());
        assertNull(apiResponse.getObject());
    }

    @Test
    void testSendPost_Success() {
        when(postService.createPost(any(PostDTO.class), eq("user123"))).thenReturn(post);
        when(inspectionClient.isContentSafe(anyString())).thenReturn(true);

        ResponseEntity<ApiResponse> response = postController.sendPost(postDTO, "user123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("发帖成功", apiResponse.getMessage());
        assertEquals(post, apiResponse.getObject());
        verify(postService).createPost(any(PostDTO.class), eq("user123"));
        verify(inspectionClient, times(2)).isContentSafe(anyString()); // 验证两次调用
    }

    @Test
    void testSendPost_InvalidPostDTO() {
        PostDTO invalidPostDTO = new PostDTO();
        invalidPostDTO.setContent("Test Content"); // 缺少标题

        ResponseEntity<ApiResponse> response = postController.sendPost(invalidPostDTO, "user123", "true", "true");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertFalse(apiResponse.isSuccess());
        assertEquals("帖子标题不能为空", apiResponse.getMessage());
        assertNull(apiResponse.getObject());
    }

    @Test
    void testAddComment_Success() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("content", "Test Comment");
        when(postService.getPostById(1L)).thenReturn(postBriefInfo);
        when(commentService.addComment(eq(1L), any(CommentDTO.class), eq("user123"))).thenReturn(comment);
        when(inspectionClient.isContentSafe("Test Comment")).thenReturn(true);

        // 模拟 validateContentAsync
        PostController spyController = spy(postController);
        when(spyController.validateContentAsync("Test Comment"))
                .thenReturn(CompletableFuture.completedFuture(true));

        ResponseEntity<ApiResponse> response = spyController.addComment(1L, requestBody, "user123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("评论发表成功", apiResponse.getMessage());
        assertEquals(comment, apiResponse.getObject());
        verify(commentService).addComment(eq(1L), any(CommentDTO.class), eq("user123"));
        verify(inspectionClient).isContentSafe("Test Comment");
    }

    @Test
    void testAddComment_NestedContent() {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> nestedContent = new HashMap<>();
        nestedContent.put("content", "Test Comment");
        requestBody.put("content", nestedContent);
        when(postService.getPostById(1L)).thenReturn(postBriefInfo);
        when(commentService.addComment(eq(1L), any(CommentDTO.class), eq("user123"))).thenReturn(comment);

        ResponseEntity<ApiResponse> response = postController.addComment(1L, requestBody, "user123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("评论发表成功", apiResponse.getMessage());
        assertEquals(comment, apiResponse.getObject());
        verify(commentService).addComment(eq(1L), any(CommentDTO.class), eq("user123"));
    }

    @Test
    void testAddComment_EmptyContent() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("content", "");

        ResponseEntity<ApiResponse> response = postController.addComment(1L, requestBody, "user123", "true", "true");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertFalse(apiResponse.isSuccess());
        assertEquals("评论内容不能为空", apiResponse.getMessage());
        assertNull(apiResponse.getObject());
    }

    @Test
    void testAddComment_Notification() {
        postBriefInfo.setUserId("otherUser"); // 模拟不同用户
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("content", "Test Comment");
        when(postService.getPostById(1L)).thenReturn(postBriefInfo);
        when(commentService.addComment(eq(1L), any(CommentDTO.class), eq("user123"))).thenReturn(comment);
        doNothing().when(informProducer).sendNotification(anyString(), anyString(), anyString(), anyString(), anyLong());

        ResponseEntity<ApiResponse> response = postController.addComment(1L, requestBody, "user123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("评论发表成功", apiResponse.getMessage());
        verify(informProducer).sendNotification("otherUser", "user123", "COMMENT", "Test Comment", 1L);
    }

    @Test
    void testLikePost_Success() {
        when(postService.getPostById(1L)).thenReturn(postBriefInfo);
        doNothing().when(likeService).likePost(1L, "user123");

        ResponseEntity<ApiResponse> response = postController.likePost(1L, "user123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("点赞成功", apiResponse.getMessage());
        assertEquals(postBriefInfo, apiResponse.getObject());
        verify(likeService).likePost(1L, "user123");
    }

    @Test
    void testLikePost_Notification() {
        postBriefInfo.setUserId("otherUser"); // 模拟不同用户
        when(postService.getPostById(1L)).thenReturn(postBriefInfo);
        doNothing().when(likeService).likePost(1L, "user123");
        doNothing().when(informProducer).sendNotification(anyString(), anyString(), anyString(), anyString(), anyLong());

        ResponseEntity<ApiResponse> response = postController.likePost(1L, "user123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("点赞成功", apiResponse.getMessage());
        verify(informProducer).sendNotification("otherUser", "user123", "LIKE", "", 1L);
    }

    @Test
    void testUnlikePost_Success() {
        when(postService.getPostById(1L)).thenReturn(postBriefInfo);
        doNothing().when(likeService).unlikePost(1L, "user123");

        ResponseEntity<ApiResponse> response = postController.unlikePost(1L, "user123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("取消点赞成功", apiResponse.getMessage());
        assertNull(apiResponse.getObject());
        verify(likeService).unlikePost(1L, "user123");
    }

    @Test
    void testApprovePost_Success() {
        when(postService.approvePost(1L, "admin123")).thenReturn(postBriefInfo);

        ResponseEntity<ApiResponse> response = postController.approvePost(1L, "admin123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("帖子审核通过", apiResponse.getMessage());
        assertEquals(postBriefInfo, apiResponse.getObject());
        verify(postService).approvePost(1L, "admin123");
    }

    @Test
    void testBanPost_Success() {
        when(postService.banPost(1L, "admin123")).thenReturn(post);

        ResponseEntity<ApiResponse> response = postController.banPost(1L, "admin123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("帖子已封禁", apiResponse.getMessage());
        assertEquals(post, apiResponse.getObject());
        verify(postService).banPost(1L, "admin123");
    }

    @Test
    void testReportPost_Success() {
        Map<String, String> request = new HashMap<>();
        request.put("reason", "Inappropriate content");
        request.put("detail", "Detailed reason");
        Report report = new Report();
        when(postService.reportPost(1L, "user123", "Inappropriate content", "Detailed reason")).thenReturn(report);

        ResponseEntity<ApiResponse> response = postController.reportPost(1L, request, "user123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("帖子举报成功", apiResponse.getMessage());
        assertEquals(report, apiResponse.getObject());
        verify(postService).reportPost(1L, "user123", "Inappropriate content", "Detailed reason");
    }

    @Test
    void testReportPost_EmptyReason() {
        Map<String, String> request = new HashMap<>();
        request.put("reason", "");
        request.put("detail", "Detailed reason");

        ResponseEntity<ApiResponse> response = postController.reportPost(1L, request, "user123", "true", "true");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertFalse(apiResponse.isSuccess());
        assertEquals("举报原因不能为空", apiResponse.getMessage());
        assertNull(apiResponse.getObject());
    }

    @Test
    void testGetTotalCount_Success() {
        when(postService.getApprovedPostCount()).thenReturn(100);

        ResponseEntity<ApiResponse> response = postController.getTotalCount("user123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("帖子总数查询成功", apiResponse.getMessage());
        assertEquals(100, apiResponse.getObject());
        verify(postService).getApprovedPostCount();
    }

    @Test
    void testGetTodayCount_Success() {
        when(postService.getTodayApprovedPostCount()).thenReturn(10);

        ResponseEntity<ApiResponse> response = postController.getTodayCount("user123", "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("今日帖子数量查询成功", apiResponse.getMessage());
        assertEquals(10, apiResponse.getObject());
        verify(postService).getTodayApprovedPostCount();
    }

    @Test
    void testGetUserStatistics_Success() {
        when(likeService.getLikedCountByUserId(1L)).thenReturn(5L);
        when(commentService.getCommentCountByUserId(1L)).thenReturn(10L);

        ResponseEntity<ApiResponse> response = postController.getUserStatistics(1L, "true", "true");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("用户统计信息查询成功", apiResponse.getMessage());
        UserStatisticsResponse stats = (UserStatisticsResponse) apiResponse.getObject();
        assertEquals(5L, stats.getLikedCount());
        assertEquals(10L, stats.getCommentCount());
        verify(likeService).getLikedCountByUserId(1L);
        verify(commentService).getCommentCountByUserId(1L);
    }

    @Test
    void testServerErrorHandling() {
        when(postService.getPostById(1L)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<ApiResponse> response = postController.getPostById(1L, "user123", "true", "true");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertFalse(apiResponse.isSuccess());
        assertTrue(apiResponse.getMessage().contains("服务器内部错误"));
        assertNull(apiResponse.getObject());
    }
}