package com.se2303.communityservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.dto.response.PostBriefInfo;
import com.se2303.communityservice.entity.Comment;
import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.response.ApiResponse;
import com.se2303.communityservice.service.ICommentService;
import com.se2303.communityservice.service.IPostService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPostService postService;

    @MockBean
    private ICommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private PostBriefInfo postBriefInfo;
    private Comment comment;
    private String adminId = "admin123";
    private String userId = "user123";
    private PageDTO<PostBriefInfo> postPageDTO;
    private PageDTO<Comment> commentPageDTO;

    @BeforeEach
    public void setUp() {
        // 初始化测试数据
        postBriefInfo = new PostBriefInfo();
        postBriefInfo.setId(1L);
        postBriefInfo.setTitle("Test Post");
        postBriefInfo.setContent("This is a test post");
        postBriefInfo.setUserId(userId);
        postBriefInfo.setReviewStatus("PENDING");
        postBriefInfo.setCreatedAt(LocalDateTime.now());
        postBriefInfo.setTags(new HashSet<>(Arrays.asList("tag1", "tag2")));
        postBriefInfo.setLikeCount(10L);
        postBriefInfo.setCommentCount(5L);

        comment = new Comment();
        comment.setId(1L);
        comment.setPostId(1L);
        comment.setUserId(userId);
        comment.setContent("This is a test comment");
        comment.setReviewStatus("PENDING");
        comment.setCreatedAt(LocalDateTime.now());

        postPageDTO = new PageDTO<>(Collections.singletonList(postBriefInfo), 0, 10, 1, 1);
        commentPageDTO = new PageDTO<>(Collections.singletonList(comment), 0, 10, 1, 1);
    }

    // 测试获取审核队列
    @Test
    public void testGetReviewQueue_Success_PostsAndComments() throws Exception {
        // 模拟 postService 和 commentService 的行为
        when(postService.getReviewQueue(any(), any(), eq("PENDING"), any())).thenReturn(postPageDTO);
        when(commentService.getReviewQueue(any(), eq("PENDING"), any())).thenReturn(commentPageDTO);

        // 执行请求
        mockMvc.perform(get("/api/community/review/queue")
                        .param("type", "all")
                        .param("status", "PENDING")
                        .param("page", "1")
                        .param("size", "10")
                        .header("X-User-Id", adminId)
                        .header("X-Valid", "true")
                        .header("X-Identity", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("审核队列获取成功"))
                .andExpect(jsonPath("$.object.content").isArray())
                .andExpect(jsonPath("$.object.content[0].type").value("post"))
                .andExpect(jsonPath("$.object.content[1].type").value("comment"));
    }

    @Test
    public void testGetReviewQueue_Unauthorized_NonAdmin() throws Exception {
        // 非管理员身份
        mockMvc.perform(get("/api/community/review/queue")
                        .param("type", "all")
                        .param("status", "PENDING")
                        .param("page", "1")
                        .param("size", "10")
                        .header("X-User-Id", userId)
                        .header("X-Valid", "true")
                        .header("X-Identity", "true"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可访问审核队列"));
    }

    @Test
    public void testGetReviewQueue_InvalidHeaders() throws Exception {
        // 缺少请求头
        mockMvc.perform(get("/api/community/review/queue")
                        .param("type", "all")
                        .param("status", "PENDING")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("未登录或请求头无效"));
    }

    @Test
    public void testGetReviewQueue_InvalidPageParams() throws Exception {
        // 无效分页参数
        mockMvc.perform(get("/api/community/review/queue")
                        .param("type", "all")
                        .param("status", "PENDING")
                        .param("page", "0")
                        .param("size", "0")
                        .header("X-User-Id", adminId)
                        .header("X-Valid", "true")
                        .header("X-Identity", "false"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
//                .andExpect(jsonPath("$.message").value("无效的分页参数: page=0, size=0"));
    }

    // 测试获取内容详情
    @Test
    public void testGetContentDetail_Success_Post() throws Exception {
        // 模拟 postService 的行为
        when(postService.getPostById(1L)).thenReturn(postBriefInfo);

        // 执行请求
        mockMvc.perform(get("/api/community/review/1")
                        .param("type", "post")
                        .header("X-User-Id", adminId)
                        .header("X-Valid", "true")
                        .header("X-Identity", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("内容详情获取成功"))
                .andExpect(jsonPath("$.object.type").value("post"))
                .andExpect(jsonPath("$.object.title").value("Test Post"));
    }

    @Test
    public void testGetContentDetail_Success_Comment() throws Exception {
        // 模拟 commentService 的行为
        Map<String, Object> commentDetail = new HashMap<>();
        commentDetail.put("comment", comment);
        commentDetail.put("post", null);
        when(commentService.getCommentDetail(1L)).thenReturn(commentDetail);

        // 执行请求
        mockMvc.perform(get("/api/community/review/1")
                        .param("type", "comment")
                        .header("X-User-Id", adminId)
                        .header("X-Valid", "true")
                        .header("X-Identity", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("内容详情获取成功"))
                .andExpect(jsonPath("$.object.type").value("comment"))
                .andExpect(jsonPath("$.object.comment.content").value("This is a test comment"));
    }

    @Test
    public void testGetContentDetail_NotFound() throws Exception {
        // 模拟内容不存在
        when(postService.getPostById(1L)).thenThrow(new EntityNotFoundException("Post not found with id: 1"));
        when(commentService.getCommentDetail(1L)).thenThrow(new EntityNotFoundException("Comment not found with id: 1"));

        // 执行请求
        mockMvc.perform(get("/api/community/review/1")
                        .param("type", "post")
                        .header("X-User-Id", adminId)
                        .header("X-Valid", "true")
                        .header("X-Identity", "false"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("内容不存在"));
    }

    // 测试审核内容
    @Test
    public void testReviewContent_Success_ApprovePost() throws Exception {
        // 模拟 postService 的行为
        when(postService.approvePost(1L, adminId)).thenReturn(postBriefInfo);

        // 构造请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("action", "APPROVED");
        requestBody.put("type", "post");

        // 执行请求
        mockMvc.perform(put("/api/community/review/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .header("X-User-Id", adminId)
                        .header("X-Valid", "true")
                        .header("X-Identity", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("内容审核成功"));
    }

    @Test
    public void testReviewContent_Success_BanComment() throws Exception {
        // 模拟 commentService 的行为
        when(commentService.banComment(1L, adminId)).thenReturn(comment);

        // 构造请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("action", "BANNED");
        requestBody.put("type", "comment");

        // 执行请求
        mockMvc.perform(put("/api/community/review/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .header("X-User-Id", adminId)
                        .header("X-Valid", "true")
                        .header("X-Identity", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("内容审核成功"));
    }

    @Test
    public void testReviewContent_InvalidAction() throws Exception {
        // 构造无效的请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("action", "");
        requestBody.put("type", "post");

        // 执行请求
        mockMvc.perform(put("/api/community/review/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .header("X-User-Id", adminId)
                        .header("X-Valid", "true")
                        .header("X-Identity", "false"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("审核动作不能为空"));
    }

    @Test
    public void testReviewContent_Unauthorized_NonAdmin() throws Exception {
        // 构造请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("action", "APPROVED");
        requestBody.put("type", "post");

        // 非管理员身份
        mockMvc.perform(put("/api/community/review/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
                        .header("X-User-Id", userId)
                        .header("X-Valid", "true")
                        .header("X-Identity", "true"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可审核内容"));
    }
}