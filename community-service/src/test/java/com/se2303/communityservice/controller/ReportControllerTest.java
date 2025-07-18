package com.se2303.communityservice.controller;

import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.entity.Report;
import com.se2303.communityservice.response.ApiResponse;
import com.se2303.communityservice.service.IReportService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ReportControllerTest {

    @Mock
    private IReportService reportService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetReports_Success() {
        // 准备数据
        Report report = new Report();
        report.setId(1L);
        report.setStatus(Report.ReportStatus.PENDING);
        report.setCreatedAt(LocalDateTime.now());

        Page<Report> page = new PageImpl<>(Collections.singletonList(report));
        PageDTO<Report> pageDTO = new PageDTO<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());

        when(reportService.getAllReports(eq(Report.ReportStatus.PENDING), any(Pageable.class))).thenReturn(pageDTO);

        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.getReports(
                "PENDING", 1, 10, "admin123", "true", "false", request);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("举报列表获取成功", response.getBody().getMessage());
        assertEquals(pageDTO, response.getBody().getObject());
        verify(reportService).getAllReports(eq(Report.ReportStatus.PENDING), any(Pageable.class));
    }

    @Test
    void testGetReports_Unauthorized_NullUserId() {
        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.getReports(
                null, 1, 10, null, "true", "false", request);

        // 验证结果
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("未登录或请求头无效", response.getBody().getMessage());
        verify(reportService, never()).getAllReports(any(), any());
    }

    @Test
    void testGetReports_Forbidden_NonAdmin() {
        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.getReports(
                null, 1, 10, "user123", "true", "true", request);

        // 验证结果
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("仅管理员可访问举报列表", response.getBody().getMessage());
        verify(reportService, never()).getAllReports(any(), any());
    }

    @Test
    void testGetReports_InvalidPageParams() {
        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.getReports(
                null, 0, 10, "admin123", "true", "false", request);

        // 验证结果
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("无效的分页参数: page=0, size=10", response.getBody().getMessage());
        verify(reportService, never()).getAllReports(any(), any());
    }

    @Test
    void testGetReports_InvalidStatus() {
        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.getReports(
                "INVALID_STATUS", 1, 10, "admin123", "true", "false", request);

        // 验证结果
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("无效的状态参数", response.getBody().getMessage());
        verify(reportService, never()).getAllReports(any(), any());
    }

    @Test
    void testGetReportDetail_Success() {
        // 准备数据
        Map<String, Object> reportDetail = new HashMap<>();
        reportDetail.put("report", new Report());

        when(reportService.getReportDetail(1L)).thenReturn(reportDetail);

        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.getReportDetail(
                1L, "admin123", "true", "false");

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("举报详情获取成功", response.getBody().getMessage());
        assertEquals(reportDetail, response.getBody().getObject());
        verify(reportService).getReportDetail(1L);
    }

    @Test
    void testGetReportDetail_Unauthorized_NullUserId() {
        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.getReportDetail(
                1L, null, "true", "false");

        // 验证结果
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("未登录或请求头无效", response.getBody().getMessage());
        verify(reportService, never()).getReportDetail(anyLong());
    }

    @Test
    void testGetReportDetail_Forbidden_NonAdmin() {
        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.getReportDetail(
                1L, "user123", "true", "true");

        // 验证结果
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("仅管理员可获取举报详情", response.getBody().getMessage());
        verify(reportService, never()).getReportDetail(anyLong());
    }

    @Test
    void testGetReportStats_Success() {
        // 准备数据
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 10L);
        stats.put("pending", 5L);

        when(reportService.getReportStats()).thenReturn(stats);

        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.getReportStats(
                "admin123", "true", "false");

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("举报统计获取成功", response.getBody().getMessage());
        assertEquals(stats, response.getBody().getObject());
        verify(reportService).getReportStats();
    }

    @Test
    void testGetReportStats_Unauthorized_NullUserId() {
        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.getReportStats(
                null, "true", "false");

        // 验证结果
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("未登录或请求头无效", response.getBody().getMessage());
        verify(reportService, never()).getReportStats();
    }

    @Test
    void testGetReportStats_Forbidden_NonAdmin() {
        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.getReportStats(
                "user123", "true", "true");

        // 验证结果
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("仅管理员可获取举报统计", response.getBody().getMessage());
        verify(reportService, never()).getReportStats();
    }

    @Test
    void testHandleReport_Success() {
        // 准备数据
        Report report = new Report();
        report.setId(1L);
        report.setStatus(Report.ReportStatus.APPROVED);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("action", "resolved");
        requestBody.put("reason", "Inappropriate content");

        when(reportService.handleReport(eq(1L), eq("resolved"), eq("admin123"), eq("Inappropriate content")))
                .thenReturn(report);

        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.handleReport(
                1L, requestBody, "admin123", "true", "false");

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("举报处理成功", response.getBody().getMessage());
        assertEquals(report, response.getBody().getObject());
        verify(reportService).handleReport(eq(1L), eq("resolved"), eq("admin123"), eq("Inappropriate content"));
    }

    @Test
    void testHandleReport_Unauthorized_NullUserId() {
        // 准备数据
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("action", "resolved");

        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.handleReport(
                1L, requestBody, null, "true", "false");

        // 验证结果
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("未登录或请求头无效", response.getBody().getMessage());
        verify(reportService, never()).handleReport(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void testHandleReport_Forbidden_NonAdmin() {
        // 准备数据
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("action", "resolved");

        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.handleReport(
                1L, requestBody, "user123", "true", "true");

        // 验证结果
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("仅管理员可处理举报", response.getBody().getMessage());
        verify(reportService, never()).handleReport(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void testHandleReport_InvalidAction() {
        // 准备数据
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("action", "");

        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.handleReport(
                1L, requestBody, "admin123", "true", "false");

        // 验证结果
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("处理动作不能为空", response.getBody().getMessage());
        verify(reportService, never()).handleReport(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void testHandleReport_IllegalStateException() {
        // 准备数据
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("action", "resolved");
        requestBody.put("reason", "Inappropriate content"); // 添加 reason

        when(reportService.handleReport(eq(1L), eq("resolved"), eq("admin123"), eq("Inappropriate content")))
                .thenThrow(new IllegalStateException("举报已被处理"));

        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.handleReport(
                1L, requestBody, "admin123", "true", "false");

        // 验证结果
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("举报已被处理", response.getBody().getMessage());
        verify(reportService).handleReport(eq(1L), eq("resolved"), eq("admin123"), eq("Inappropriate content"));
    }

    @Test
    void testHandleReport_ServerError() {
        // 准备数据
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("action", "resolved");
        requestBody.put("reason", "Inappropriate content"); // 添加 reason

        when(reportService.handleReport(eq(1L), eq("resolved"), eq("admin123"), eq("Inappropriate content")))
                .thenThrow(new RuntimeException("数据库错误"));

        // 执行测试
        ResponseEntity<ApiResponse> response = reportController.handleReport(
                1L, requestBody, "admin123", "true", "false");

        // 验证结果
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("服务器内部错误"));
        verify(reportService).handleReport(eq(1L), eq("resolved"), eq("admin123"), eq("Inappropriate content"));
    }
}