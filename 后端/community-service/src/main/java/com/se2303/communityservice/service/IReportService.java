package com.se2303.communityservice.service;

import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.entity.Report;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface IReportService {
    
    /**
     * 获取所有举报列表（管理员用）
     */
    PageDTO<Report> getAllReports(Report.ReportStatus status, Pageable pageable);
    
    /**
     * 根据ID获取举报详情
     */
    Report getReportById(Long reportId);
    
    /**
     * 处理举报（批准或拒绝）
     */
    Report handleReport(Long reportId, String action, String adminId);
    
    /**
     * 处理举报（批准或拒绝，带处理原因）
     */
    Report handleReport(Long reportId, String action, String adminId, String handleReason);
    
    /**
     * 获取指定帖子的举报数量
     */
    Long getReportCountByPostId(Long postId);
    
    /**
     * 获取举报详情（包含帖子信息）
     */
    Map<String, Object> getReportDetail(Long reportId);
    
    /**
     * 获取举报统计信息
     */
    Map<String, Object> getReportStats();
} 