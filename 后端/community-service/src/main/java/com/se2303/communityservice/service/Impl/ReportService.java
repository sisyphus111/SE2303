package com.se2303.communityservice.service.Impl;

import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.entity.Report;
import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.repository.ReportRepository;
import com.se2303.communityservice.repository.PostRepository;
import com.se2303.communityservice.service.IReportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService implements IReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PostRepository postRepository;

    @Override
    public PageDTO<Report> getAllReports(Report.ReportStatus status, Pageable pageable) {
        logger.info("获取举报列表，状态筛选: {}", status);
        
        Page<Report> reports;
        if (status != null) {
            reports = reportRepository.findByStatus(status, pageable);
        } else {
            reports = reportRepository.findAll(pageable);
        }
        
        logger.info("✅ 获取到 {} 条举报记录", reports.getTotalElements());
        
        return new PageDTO<>(
                reports.getContent(),
                reports.getNumber(),
                reports.getSize(),
                reports.getTotalElements(),
                reports.getTotalPages()
        );
    }

    @Override
    public Report getReportById(Long reportId) {
        validateReportId(reportId);
        
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> {
                    logger.warn("举报记录未找到: {}", reportId);
                    return new EntityNotFoundException("举报记录未找到: " + reportId);
                });
        
        logger.debug("获取举报详情: {}", reportId);
        return report;
    }

    @Override
    @Transactional
    public Report handleReport(Long reportId, String action, String adminId) {
        return handleReport(reportId, action, adminId, null);
    }

    @Override
    @Transactional
    public Report handleReport(Long reportId, String action, String adminId, String handleReason) {
        validateReportId(reportId);
        validateAdminId(adminId);
        validateAction(action);
        
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> {
                    logger.warn("举报记录未找到: {}", reportId);
                    return new EntityNotFoundException("举报记录未找到: " + reportId);
                });
        
        // 检查举报状态
        if (report.getStatus() != Report.ReportStatus.PENDING) {
            throw new IllegalStateException("举报已被处理，无法重复处理");
        }
        
        // 更新举报状态
        if ("resolved".equals(action)) {
            report.setStatus(Report.ReportStatus.APPROVED);
        } else if ("rejected".equals(action)) {
            report.setStatus(Report.ReportStatus.REJECTED);
        }
        
        Report savedReport = reportRepository.save(report);
        logger.info("✅ 管理员 {} 处理举报 {}: {}", adminId, reportId, action);
        
        return savedReport;
    }

    @Override
    public Long getReportCountByPostId(Long postId) {
        validatePostId(postId);
        return reportRepository.countByPostId(postId);
    }

    /**
     * 获取举报详情（包含帖子信息）
     */
    public Map<String, Object> getReportDetail(Long reportId) {
        validateReportId(reportId);
        
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> {
                    logger.warn("举报记录未找到: {}", reportId);
                    return new EntityNotFoundException("举报记录未找到: " + reportId);
                });
        
        // 获取被举报的帖子信息
        Post post = postRepository.findById(report.getPostId())
                .orElse(null);
        
        Map<String, Object> result = new HashMap<>();
        result.put("report", report);
        result.put("post", post);
        
        return result;
    }

    /**
     * 获取举报统计信息
     */
    public Map<String, Object> getReportStats() {
        long totalReports = reportRepository.count();
        long pendingReports = reportRepository.countByStatus(Report.ReportStatus.PENDING);
        long approvedReports = reportRepository.countByStatus(Report.ReportStatus.APPROVED);
        long rejectedReports = reportRepository.countByStatus(Report.ReportStatus.REJECTED);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", totalReports);
        stats.put("pending", pendingReports);
        stats.put("approved", approvedReports);
        stats.put("rejected", rejectedReports);
        
        return stats;
    }

    private void validateReportId(Long reportId) {
        if (reportId == null || reportId <= 0) {
            throw new IllegalArgumentException("无效的举报ID: " + reportId);
        }
    }

    private void validateAdminId(String adminId) {
        if (adminId == null || adminId.trim().isEmpty()) {
            throw new IllegalArgumentException("无效的管理员ID: " + adminId);
        }
    }

    private void validateAction(String action) {
        if (action == null || (!action.equals("resolved") && !action.equals("rejected"))) {
            throw new IllegalArgumentException("无效的处理动作，只支持 resolved 或 rejected: " + action);
        }
    }

    private void validatePostId(Long postId) {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("无效的帖子ID: " + postId);
        }
    }
} 