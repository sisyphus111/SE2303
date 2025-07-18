package com.se2303.communityservice.service.Impl;

import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.entity.Comment;
import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.entity.Report;
import com.se2303.communityservice.repository.CommentRepository;
import com.se2303.communityservice.repository.PostRepository;
import com.se2303.communityservice.repository.ReportRepository;
import com.se2303.communityservice.service.Impl.ReportService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ReportService reportService;

    private Report testReport;
    private Post testPost;
    private Comment testComment;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testReport = new Report();
        testReport.setId(1L);
        testReport.setPostId(1L);
        testReport.setCommentId(1L);
        testReport.setType("post");
        testReport.setUserId("user1");
        testReport.setReason("Inappropriate content");
        testReport.setDetail("Detailed description");
        testReport.setStatus(Report.ReportStatus.PENDING);
        testReport.setCreatedAt(LocalDateTime.now());

        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setContent("Test Content");
        testPost.setUserId("user1");
        testPost.setReviewStatus("PENDING");
        testPost.setCreatedAt(LocalDateTime.now());

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setPostId(1L);
        testComment.setUserId("user1");
        testComment.setContent("Test Comment");
        testComment.setReviewStatus("PENDING");
        testComment.setCreatedAt(LocalDateTime.now());

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getAllReports_WithStatus_Success() {
        // 准备
        List<Report> reports = Arrays.asList(testReport);
        Page<Report> reportPage = new PageImpl<>(reports, pageable, reports.size());
        when(reportRepository.findByStatus(eq(Report.ReportStatus.PENDING), eq(pageable)))
                .thenReturn(reportPage);

        // 执行
        PageDTO<Report> result = reportService.getAllReports(Report.ReportStatus.PENDING, pageable);

        // 验证
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(testReport, result.getContent().get(0));
        verify(reportRepository).findByStatus(eq(Report.ReportStatus.PENDING), eq(pageable));
    }

    @Test
    void getAllReports_WithoutStatus_Success() {
        // 准备
        List<Report> reports = Arrays.asList(testReport);
        Page<Report> reportPage = new PageImpl<>(reports, pageable, reports.size());
        when(reportRepository.findAll(eq(pageable))).thenReturn(reportPage);

        // 执行
        PageDTO<Report> result = reportService.getAllReports(null, pageable);

        // 验证
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(testReport, result.getContent().get(0));
        verify(reportRepository).findAll(eq(pageable));
    }

    @Test
    void getReportById_Success() {
        // 准备
        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));

        // 执行
        Report result = reportService.getReportById(1L);

        // 验证
        assertNotNull(result);
        assertEquals(testReport, result);
        verify(reportRepository).findById(1L);
    }

    @Test
    void getReportById_NotFound_ThrowsException() {
        // 准备
        when(reportRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行 & 验证
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> reportService.getReportById(1L));
        assertEquals("举报记录未找到: 1", exception.getMessage());
        verify(reportRepository).findById(1L);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void getReportById_InvalidId_ThrowsException(Long reportId) {
        // 执行 & 验证
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reportService.getReportById(reportId));
        assertEquals("无效的举报ID: " + reportId, exception.getMessage());
        verify(reportRepository, never()).findById(any());
    }

    @ParameterizedTest
    @CsvSource({
            "post, resolved, BANNED, APPROVED",
            "post, rejected, APPROVED, REJECTED",
            "comment, resolved, BANNED, APPROVED",
            "comment, rejected, APPROVED, REJECTED"
    })
    void handleReport_Success(String type, String action, String expectedReviewStatus, String expectedReportStatus) {
        // 准备
        testReport.setType(type);
        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));
        if (type.equals("post")) {
            when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
            when(postRepository.save(any(Post.class))).thenReturn(testPost);
        } else {
            when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
            when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        }
        when(reportRepository.save(any(Report.class))).thenReturn(testReport);

        // 执行
        Report result = reportService.handleReport(1L, action, "admin1");

        // 验证
        assertNotNull(result);
        assertEquals(Report.ReportStatus.valueOf(expectedReportStatus), result.getStatus());
        if (type.equals("post")) {
            assertEquals(expectedReviewStatus, testPost.getReviewStatus());
            verify(postRepository).save(testPost);
        } else {
            assertEquals(expectedReviewStatus, testComment.getReviewStatus());
            verify(commentRepository).save(testComment);
        }
        verify(reportRepository).findById(1L);
        verify(reportRepository).save(testReport);
    }

    @ParameterizedTest
    @CsvSource({
            "post, resolved, BANNED, APPROVED",
            "post, rejected, APPROVED, REJECTED",
            "comment, resolved, BANNED, APPROVED",
            "comment, rejected, APPROVED, REJECTED"
    })
    void handleReport_WithReason_Success(String type, String action, String expectedReviewStatus, String expectedReportStatus) {
        // 准备
        testReport.setType(type);
        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));
        if (type.equals("post")) {
            when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
            when(postRepository.save(any(Post.class))).thenReturn(testPost);
        } else {
            when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
            when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        }
        when(reportRepository.save(any(Report.class))).thenReturn(testReport);

        // 执行
        Report result = reportService.handleReport(1L, action, "admin1", "Test reason");

        // 验证
        assertNotNull(result);
        assertEquals(Report.ReportStatus.valueOf(expectedReportStatus), result.getStatus());
        if (type.equals("post")) {
            assertEquals(expectedReviewStatus, testPost.getReviewStatus());
            verify(postRepository).save(testPost);
        } else {
            assertEquals(expectedReviewStatus, testComment.getReviewStatus());
            verify(commentRepository).save(testComment);
        }
        verify(reportRepository).findById(1L);
        verify(reportRepository).save(testReport);
    }

    @Test
    void handleReport_PostNotFound_ThrowsException() {
        // 准备
        testReport.setType("post");
        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行 & 验证
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> reportService.handleReport(1L, "resolved", "admin1"));
        assertEquals("帖子未找到: 1", exception.getMessage());
        verify(reportRepository).findById(1L);
        verify(postRepository).findById(1L);
        verify(postRepository, never()).save(any());
        verify(reportRepository, never()).save(any());
    }

    @Test
    void handleReport_CommentNotFound_ThrowsException() {
        // 准备
        testReport.setType("comment");
        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行 & 验证
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> reportService.handleReport(1L, "resolved", "admin1"));
        assertEquals("评论未找到: 1", exception.getMessage());
        verify(reportRepository).findById(1L);
        verify(commentRepository).findById(1L);
        verify(commentRepository, never()).save(any());
        verify(reportRepository, never()).save(any());
    }

    @Test
    void handleReport_AlreadyProcessed_ThrowsException() {
        // 准备
        testReport.setStatus(Report.ReportStatus.APPROVED);
        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));

        // 执行 & 验证
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> reportService.handleReport(1L, "resolved", "admin1"));
        assertEquals("举报已被处理，无法重复处理", exception.getMessage());
        verify(reportRepository).findById(1L);
        verify(postRepository, never()).findById(any());
        verify(commentRepository, never()).findById(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "", " "})
    void handleReport_InvalidAction_ThrowsException(String action) {
        // 执行 & 验证
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reportService.handleReport(1L, action, "admin1"));
        assertEquals("无效的处理动作，只支持 resolved 或 rejected: " + action, exception.getMessage());
        verify(reportRepository, never()).findById(any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void handleReport_InvalidAdminId_ThrowsException(String adminId) {
        // 执行 & 验证
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reportService.handleReport(1L, "resolved", adminId));
        assertEquals("无效的管理员ID: " + (adminId == null ? "null" : adminId), exception.getMessage());
        verify(reportRepository, never()).findById(any());
    }

    @Test
    void getReportDetail_Post_Success() {
        // 准备
        testReport.setType("post");
        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // 执行
        Map<String, Object> result = reportService.getReportDetail(1L);

        // 验证
        assertNotNull(result);
        assertEquals(testReport, result.get("report"));
        assertEquals(testPost, result.get("post"));
        assertNull(result.get("comment"));
        verify(reportRepository).findById(1L);
        verify(postRepository).findById(1L);
        verify(commentRepository, never()).findById(any());
    }

    @Test
    void getReportDetail_Comment_Success() {
        // 准备
        testReport.setType("comment");
        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        // 执行
        Map<String, Object> result = reportService.getReportDetail(1L);

        // 验证
        assertNotNull(result);
        assertEquals(testReport, result.get("report"));
        assertEquals(testComment, result.get("comment"));
        assertNull(result.get("post"));
        verify(reportRepository).findById(1L);
        verify(commentRepository).findById(1L);
        verify(postRepository, never()).findById(any());
    }

    @Test
    void getReportDetail_NotFound_ThrowsException() {
        // 准备
        when(reportRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行 & 验证
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> reportService.getReportDetail(1L));
        assertEquals("举报记录未找到: 1", exception.getMessage());
        verify(reportRepository).findById(1L);
        verify(postRepository, never()).findById(any());
        verify(commentRepository, never()).findById(any());
    }

    @Test
    void getReportStats_Success() {
        // 准备
        when(reportRepository.count()).thenReturn(10L);
        when(reportRepository.countByStatus(Report.ReportStatus.PENDING)).thenReturn(5L);
        when(reportRepository.countByStatus(Report.ReportStatus.APPROVED)).thenReturn(3L);
        when(reportRepository.countByStatus(Report.ReportStatus.REJECTED)).thenReturn(2L);

        // 执行
        Map<String, Object> result = reportService.getReportStats();

        // 验证
        assertNotNull(result);
        assertEquals(10L, result.get("total"));
        assertEquals(5L, result.get("pending"));
        assertEquals(3L, result.get("approved"));
        assertEquals(2L, result.get("rejected"));
        verify(reportRepository).count();
        verify(reportRepository).countByStatus(Report.ReportStatus.PENDING);
        verify(reportRepository).countByStatus(Report.ReportStatus.APPROVED);
        verify(reportRepository).countByStatus(Report.ReportStatus.REJECTED);
    }

    @Test
    void getReportCountByPostId_Success() {
        // 准备
        when(reportRepository.countByPostId(1L)).thenReturn(5L);

        // 执行
        Long result = reportService.getReportCountByPostId(1L);

        // 验证
        assertEquals(5L, result);
        verify(reportRepository).countByPostId(1L);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void getReportCountByPostId_InvalidId_ThrowsException(Long postId) {
        // 执行 & 验证
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reportService.getReportCountByPostId(postId));
        assertEquals("无效的帖子ID: " + postId, exception.getMessage());
        verify(reportRepository, never()).countByPostId(any());
    }
}