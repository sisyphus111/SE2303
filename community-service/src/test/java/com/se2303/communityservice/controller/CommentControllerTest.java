package com.se2303.communityservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.entity.Comment;
import com.se2303.communityservice.entity.Report;
import com.se2303.communityservice.response.ApiResponse;
import com.se2303.communityservice.service.ICommentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Comment comment;
    private Report report;
    private Map<String, Object> adminCommentsResult;
    private PageDTO<Comment> pageDTO;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        comment = new Comment();
        comment.setId(1L);
        comment.setPostId(1L);
        comment.setUserId("user123");
        comment.setContent("测试评论");
        comment.setReviewStatus("PENDING");
        comment.setCreatedAt(LocalDateTime.now());

        report = new Report();
        report.setId(1L);
        report.setCommentId(1L);
        report.setUserId("user123");
        report.setReason("不当内容");
        report.setDetail("包含冒犯性语言");
        report.setStatus(Report.ReportStatus.PENDING);
        report.setCreatedAt(LocalDateTime.now());

        adminCommentsResult = new HashMap<>();
        adminCommentsResult.put("content", Arrays.asList(comment));
        adminCommentsResult.put("page", 1);
        adminCommentsResult.put("size", 15);
        adminCommentsResult.put("totalElements", 1L);
        adminCommentsResult.put("totalPages", 1);

        pageDTO = new PageDTO<>(
                Arrays.asList(comment),
                0,
                15,
                1L,
                1
        );
    }

    // 测试 reportComment 端点
    @Test
    void reportComment_success() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("reason", "不当内容");
        requestBody.put("detail", "包含冒犯性语言");

        when(commentService.reportComment(eq(1L), eq("user123"), eq("不当内容"), eq("包含冒犯性语言")))
                .thenReturn(report);

        mockMvc.perform(post("/api/community/comments/report/1")
                        .header("X-User-Id", "user123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("评论举报成功"))
                .andExpect(jsonPath("$.object.id").value(1L))
                .andExpect(jsonPath("$.object.reason").value("不当内容"));

        verify(commentService).reportComment(1L, "user123", "不当内容", "包含冒犯性语言");
    }

    @Test
    void reportComment_invalidHeader() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("reason", "不当内容");

        mockMvc.perform(post("/api/community/comments/report/1")
                        .header("X-User-Id", "")
                        .header("X-Valid", "true")
                        .header("X-Identity", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("未登录或请求头无效"));
    }

    @Test
    void reportComment_emptyReason() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("reason", "");
        requestBody.put("detail", "包含冒犯性语言");

        mockMvc.perform(post("/api/community/comments/report/1")
                        .header("X-User-Id", "user123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("举报原因不能为空"));
    }

    @Test
    void reportComment_notFound() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("reason", "不当内容");
        requestBody.put("detail", "包含冒犯性语言");

        when(commentService.reportComment(anyLong(), anyString(), anyString(), anyString()))
                .thenThrow(new EntityNotFoundException("评论记录未找到: 1"));

        mockMvc.perform(post("/api/community/comments/report/1")
                        .header("X-User-Id", "user123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("服务器内部错误: 评论记录未找到: 1"));
    }

    @Test
    void reportComment_optimisticLockFailure() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("reason", "不当内容");
        requestBody.put("detail", "包含冒犯性语言");

        when(commentService.reportComment(anyLong(), anyString(), anyString(), anyString()))
                .thenThrow(new JpaOptimisticLockingFailureException(new OptimisticLockException("并发更新失败")));

        mockMvc.perform(post("/api/community/comments/report/1")
                        .header("X-User-Id", "user123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("服务器内部错误: 并发更新失败"));
    }

    // 测试 approveComment 端点
    @Test
    void approveComment_success() throws Exception {
        comment.setReviewStatus("APPROVED");
        when(commentService.approveComment(eq(1L), eq("admin123")))
                .thenReturn(comment);

        mockMvc.perform(put("/api/community/comments/approve/1")
                        .header("X-User-Id", "admin123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("评论审核通过"))
                .andExpect(jsonPath("$.object.id").value(1L))
                .andExpect(jsonPath("$.object.reviewStatus").value("APPROVED"));

        verify(commentService).approveComment(1L, "admin123");
    }

    @Test
    void approveComment_nonAdmin() throws Exception {
        mockMvc.perform(put("/api/community/comments/approve/1")
                        .header("X-User-Id", "user123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可审核评论"));
    }

    @Test
    void approveComment_invalidUserId() throws Exception {
        mockMvc.perform(put("/api/community/comments/approve/1")
                        .header("X-User-Id", "")
                        .header("X-Valid", "true")
                        .header("X-Identity", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("未登录或请求头无效"));
    }

    @Test
    void approveComment_notFound() throws Exception {
        when(commentService.approveComment(anyLong(), anyString()))
                .thenThrow(new EntityNotFoundException("评论记录未找到: 1"));

        mockMvc.perform(put("/api/community/comments/approve/1")
                        .header("X-User-Id", "admin123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("服务器内部错误: 评论记录未找到: 1"));
    }

    // 测试 banComment 端点
    @Test
    void banComment_success() throws Exception {
        comment.setReviewStatus("BANNED");
        when(commentService.banComment(eq(1L), eq("admin123")))
                .thenReturn(comment);

        mockMvc.perform(put("/api/community/comments/ban/1")
                        .header("X-User-Id", "admin123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("评论已封禁"))
                .andExpect(jsonPath("$.object.id").value(1L))
                        .andExpect(jsonPath("$.object.reviewStatus").value("BANNED"));

        verify(commentService).banComment(1L, "admin123");
    }

    @Test
    void banComment_nonAdmin() throws Exception {
        mockMvc.perform(put("/api/community/comments/ban/1")
                        .header("X-User-Id", "user123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可封禁评论"));
    }

    @Test
    void banComment_notFound() throws Exception {
        when(commentService.banComment(anyLong(), anyString()))
                .thenThrow(new EntityNotFoundException("评论记录未找到: 1"));

        mockMvc.perform(put("/api/community/comments/ban/1")
                        .header("X-User-Id", "admin123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("服务器内部错误: 评论记录未找到: 1"));
    }

    // 测试 getAdminComments 端点
    @Test
    void getAdminComments_success() throws Exception {
        when(commentService.getAdminComments(eq("test"), eq(null), eq(1), eq(15)))
                .thenReturn(adminCommentsResult);

        mockMvc.perform(get("/api/community/comments/admin")
                        .param("keyword", "test")
                        .param("page", "1")
                        .param("size", "15")
                        .header("X-User-Id", "admin123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取评论列表成功"))
                .andExpect(jsonPath("$.object.totalElements").value(1))
                .andExpect(jsonPath("$.object.content[0].id").value(1L));

        verify(commentService).getAdminComments("test", null, 1, 15);
    }

    @Test
    void getAdminComments_invalidPage() throws Exception {
        when(commentService.getAdminComments(anyString(), anyString(), eq(0), eq(15)))
                .thenReturn(adminCommentsResult);

        mockMvc.perform(get("/api/community/comments/admin")
                        .param("keyword", "test")
                        .param("page", "0")
                        .param("size", "15")
                        .header("X-User-Id", "admin123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取评论列表成功"));

        verify(commentService).getAdminComments("test", null, 0, 15);
    }

    @Test
    void getAdminComments_nonAdmin() throws Exception {
        mockMvc.perform(get("/api/community/comments/admin")
                        .param("keyword", "test")
                        .param("page", "1")
                        .param("size", "15")
                        .header("X-User-Id", "user123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "true"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可获取评论列表"));
    }

    // 测试 deleteComment 端点
    @Test
    void deleteComment_success() throws Exception {
        comment.setReviewStatus("DELETED");
        when(commentService.deleteComment(eq(1L), eq("admin123")))
                .thenReturn(comment);

        mockMvc.perform(put("/api/community/comments/delete/1")
                        .header("X-User-Id", "admin123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("评论已删除"))
                .andExpect(jsonPath("$.object.id").value(1L))
                .andExpect(jsonPath("$.object.reviewStatus").value("DELETED"));

        verify(commentService).deleteComment(1L, "admin123");
    }

    @Test
    void deleteComment_nonAdmin() throws Exception {
        mockMvc.perform(put("/api/community/comments/delete/1")
                        .header("X-User-Id", "user123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可删除评论"));
    }

    @Test
    void deleteComment_notFound() throws Exception {
        when(commentService.deleteComment(anyLong(), anyString()))
                .thenThrow(new EntityNotFoundException("评论记录未找到: 1"));

        mockMvc.perform(put("/api/community/comments/delete/1")
                        .header("X-User-Id", "admin123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("服务器内部错误: 评论记录未找到: 1"));
    }

    // 测试 getTodayCount 端点
    @Test
    void getTodayCount_success() throws Exception {
        when(commentService.getTodayApprovedCommentCount())
                .thenReturn(10);

        mockMvc.perform(get("/api/community/comments/today_count")
                        .header("X-User-Id", "user123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("今日评论数量查询成功"))
                .andExpect(jsonPath("$.object").value(10));

        verify(commentService).getTodayApprovedCommentCount();
    }

    @Test
    void getTodayCount_invalidHeader() throws Exception {
        mockMvc.perform(get("/api/community/comments/today_count")
                        .header("X-User-Id", "")
                        .header("X-Valid", "true")
                        .header("X-Identity", "true"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("未登录或请求头无效"));
    }

    // 测试 getCommentsByTags 端点
    @Test
    void getCommentsByTags_success() throws Exception {
        when(commentService.getCommentsByTags(eq(Arrays.asList("tag1", "tag2")), eq("APPROVED"), any()))
                .thenReturn(pageDTO);

        mockMvc.perform(get("/api/community/comments/by-tags")
                        .param("tags", "tag1,tag2")
                        .param("status", "APPROVED")
                        .param("page", "1")
                        .param("size", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("根据标签筛选评论成功"))
                .andExpect(jsonPath("$.object.totalElements").value(1))
                .andExpect(jsonPath("$.object.content[0].id").value(1L))
                .andExpect(jsonPath("$.object.pageNumber").value(0))
                .andExpect(jsonPath("$.object.pageSize").value(15));

        verify(commentService).getCommentsByTags(eq(Arrays.asList("tag1", "tag2")), eq("APPROVED"), any());
    }

    @Test
    void getCommentsByTags_emptyTags() throws Exception {
        when(commentService.getCommentsByTags(anyList(), anyString(), any()))
                .thenThrow(new IllegalArgumentException("标签列表不能为空"));

        mockMvc.perform(get("/api/community/comments/by-tags")
                        .param("tags", "")
                        .param("status", "APPROVED")
                        .param("page", "1")
                        .param("size", "15"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("服务器内部错误: 标签列表不能为空"));
    }

    @Test
    void getCommentsByTags_invalidStatus() throws Exception {
        when(commentService.getCommentsByTags(eq(Arrays.asList("tag1", "tag2")), eq("INVALID"), any()))
                .thenThrow(new IllegalArgumentException("无效的评论状态: INVALID"));

        mockMvc.perform(get("/api/community/comments/by-tags")
                        .param("tags", "tag1,tag2")
                        .param("status", "INVALID")
                        .param("page", "1")
                        .param("size", "15"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("服务器内部错误: 无效的评论状态: INVALID"));
    }

    // 测试 getCommentsByTagsSorted 端点
    @Test
    void getCommentsByTagsSorted_success() throws Exception {
        when(commentService.getCommentsByTagsOrderByCreatedAt(eq(Arrays.asList("tag1", "tag2")), eq("APPROVED"), any()))
                .thenReturn(pageDTO);

        mockMvc.perform(get("/api/community/comments/by-tags/sorted")
                        .param("tags", "tag1,tag2")
                        .param("status", "APPROVED")
                        .param("page", "1")
                        .param("size", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("根据标签筛选评论并按时间排序成功"))
                .andExpect(jsonPath("$.object.totalElements").value(1))
                .andExpect(jsonPath("$.object.content[0].id").value(1L))
                .andExpect(jsonPath("$.object.pageNumber").value(0))
                .andExpect(jsonPath("$.object.pageSize").value(15));

        verify(commentService).getCommentsByTagsOrderByCreatedAt(eq(Arrays.asList("tag1", "tag2")), eq("APPROVED"), any());
    }

    @Test
    void getCommentsByTagsSorted_emptyTags() throws Exception {
        when(commentService.getCommentsByTagsOrderByCreatedAt(anyList(), anyString(), any()))
                .thenThrow(new IllegalArgumentException("标签列表不能为空"));

        mockMvc.perform(get("/api/community/comments/by-tags/sorted")
                        .param("tags", "")
                        .param("status", "APPROVED")
                        .param("page", "1")
                        .param("size", "15"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("服务器内部错误: 标签列表不能为空"));
    }

    // 测试 getAllCommentsByTags 端点
    @Test
    void getAllCommentsByTags_success() throws Exception {
        when(commentService.getAllCommentsByTags(eq(Arrays.asList("tag1", "tag2")), any()))
                .thenReturn(pageDTO);

        mockMvc.perform(get("/api/community/comments/admin/by-tags")
                        .param("tags", "tag1,tag2")
                        .param("page", "1")
                        .param("size", "15")
                        .header("X-User-Id", "admin123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("管理员根据标签筛选评论成功"))
                .andExpect(jsonPath("$.object.totalElements").value(1))
                .andExpect(jsonPath("$.object.content[0].id").value(1L))
                .andExpect(jsonPath("$.object.pageNumber").value(0))
                .andExpect(jsonPath("$.object.pageSize").value(15));

        verify(commentService).getAllCommentsByTags(eq(Arrays.asList("tag1", "tag2")), any());
    }

    @Test
    void getAllCommentsByTags_nonAdmin() throws Exception {
        mockMvc.perform(get("/api/community/comments/admin/by-tags")
                        .param("tags", "tag1,tag2")
                        .param("page", "1")
                        .param("size", "15")
                        .header("X-User-Id", "user123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "true"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅管理员可获取评论列表"));
    }

    @Test
    void getAllCommentsByTags_emptyTags() throws Exception {
        when(commentService.getAllCommentsByTags(anyList(), any()))
                .thenThrow(new IllegalArgumentException("标签列表不能为空"));

        mockMvc.perform(get("/api/community/comments/admin/by-tags")
                        .param("tags", "")
                        .param("page", "1")
                        .param("size", "15")
                        .header("X-User-Id", "admin123")
                        .header("X-Valid", "true")
                        .header("X-Identity", "false"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("服务器内部错误: 标签列表不能为空"));
    }
}