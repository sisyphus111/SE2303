package com.se2303.communityservice.controller;

import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.entity.Report;
import com.se2303.communityservice.response.ApiResponse;
import com.se2303.communityservice.service.IReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/community/reports")
public class ReportController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private IReportService reportService;

    /**
     * 获取举报列表（管理员专用）
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getReports(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") @Min(1) int page,
            @RequestParam(required = false, defaultValue = "10") @Min(1) int size,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Valid") String valid,
            @RequestHeader("X-Identity") String identity,
            HttpServletRequest request) {
        logger.info("=== GET /reports 获取举报列表 ===");
        try {
            // 1. 验证请求头
            if (userId == null || userId.trim().isEmpty() || valid == null || identity == null) {
                logger.warn("无效的请求头: userId={}, valid={}, identity={}", userId, valid, identity);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "未登录或请求头无效"));
            }

            // 2. 验证用户状态
            boolean isValid = Boolean.parseBoolean(valid);
            boolean isIdentity = Boolean.parseBoolean(identity);
            logger.debug("UserId: {}, Valid: {}, Identity: {}", userId, isValid, isIdentity);

            if (!isValid) {
                logger.warn("用户状态无效: userId={}", userId);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "用户状态无效"));
            }

            // 3. 验证管理员权限
            if (isIdentity) {
                logger.warn("非管理员尝试访问举报列表: userId={}", userId);
                return ResponseEntity.status(403).body(new ApiResponse(false, null, "仅管理员可访问举报列表"));
            }

            // 4. 验证分页参数
            validatePageParams(page, size);
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            // 5. 解析状态参数
            Report.ReportStatus reportStatus = null;
            if (status != null && !status.trim().isEmpty() && !status.equals("all")) {
                try {
                    reportStatus = Report.ReportStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.warn("无效的状态参数: {}", status);
                    return ResponseEntity.badRequest().body(new ApiResponse(false, null, "无效的状态参数"));
                }
            }

            // 6. 获取举报列表
            PageDTO<Report> reports = reportService.getAllReports(reportStatus, pageable);
            logger.info("✅ 举报列表获取成功: page={}, size={}, status={}", page, size, status);
            return ResponseEntity.ok(new ApiResponse(true, reports, "举报列表获取成功"));

        } catch (IllegalArgumentException e) {
            logger.error("❌ 参数验证失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, null, e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ 获取举报列表时发生错误: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 获取举报详情（管理员专用）
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse> getReportDetail(
            @PathVariable Long reportId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Valid") String valid,
            @RequestHeader("X-Identity") String identity) {
        logger.info("=== GET /reports/{} 获取举报详情 ===", reportId);
        try {
            // 验证请求头
            if (userId == null || userId.trim().isEmpty() || valid == null || identity == null) {
                logger.warn("无效的请求头: userId={}, valid={}, identity={}", userId, valid, identity);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "未登录或请求头无效"));
            }

            boolean isValid = Boolean.parseBoolean(valid);
            boolean isIdentity = Boolean.parseBoolean(identity);

            if (!isValid) {
                logger.warn("用户状态无效: userId={}", userId);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "用户状态无效"));
            }

            // 验证管理员权限
            if (isIdentity) {
                logger.warn("非管理员尝试获取举报详情: userId={}", userId);
                return ResponseEntity.status(403).body(new ApiResponse(false, null, "仅管理员可获取举报详情"));
            }

            Map<String, Object> reportDetail = reportService.getReportDetail(reportId);
            logger.info("✅ 举报详情获取成功: reportId={}", reportId);
            return ResponseEntity.ok(new ApiResponse(true, reportDetail, "举报详情获取成功"));

        } catch (Exception e) {
            logger.error("❌ 获取举报详情时发生错误: reportId={}, error={}", reportId, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 获取举报统计信息（管理员专用）
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getReportStats(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Valid") String valid,
            @RequestHeader("X-Identity") String identity) {
        logger.info("=== GET /reports/stats 获取举报统计 ===");
        try {
            // 验证请求头
            if (userId == null || userId.trim().isEmpty() || valid == null || identity == null) {
                logger.warn("无效的请求头: userId={}, valid={}, identity={}", userId, valid, identity);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "未登录或请求头无效"));
            }

            boolean isValid = Boolean.parseBoolean(valid);
            boolean isIdentity = Boolean.parseBoolean(identity);

            if (!isValid) {
                logger.warn("用户状态无效: userId={}", userId);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "用户状态无效"));
            }

            // 验证管理员权限
            if (isIdentity) {
                logger.warn("非管理员尝试获取举报统计: userId={}", userId);
                return ResponseEntity.status(403).body(new ApiResponse(false, null, "仅管理员可获取举报统计"));
            }

            Map<String, Object> stats = reportService.getReportStats();
            logger.info("✅ 举报统计获取成功");
            return ResponseEntity.ok(new ApiResponse(true, stats, "举报统计获取成功"));

        } catch (Exception e) {
            logger.error("❌ 获取举报统计时发生错误: error={}", e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 处理举报（管理员专用）
     */
    @PutMapping("/{reportId}/handle")
    public ResponseEntity<ApiResponse> handleReport(
            @PathVariable Long reportId,
            @RequestBody Map<String, String> request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Valid") String valid,
            @RequestHeader("X-Identity") String identity) {
        logger.info("=== PUT /reports/{}/handle 处理举报 ===", reportId);
        try {
            // 验证请求头
            if (userId == null || userId.trim().isEmpty() || valid == null || identity == null) {
                logger.warn("无效的请求头: userId={}, valid={}, identity={}", userId, valid, identity);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "未登录或请求头无效"));
            }

            boolean isValid = Boolean.parseBoolean(valid);
            boolean isIdentity = Boolean.parseBoolean(identity);

            if (!isValid) {
                logger.warn("用户状态无效: userId={}", userId);
                return ResponseEntity.status(401).body(new ApiResponse(false, null, "用户状态无效"));
            }

            // 验证管理员权限
            if (isIdentity) {
                logger.warn("非管理员尝试处理举报: userId={}", userId);
                return ResponseEntity.status(403).body(new ApiResponse(false, null, "仅管理员可处理举报"));
            }

            // 获取请求参数
            String action = request.get("action");
            String handleReason = request.get("reason");

            if (action == null || action.trim().isEmpty()) {
                logger.warn("处理动作为空: reportId={}", reportId);
                return ResponseEntity.badRequest().body(new ApiResponse(false, null, "处理动作不能为空"));
            }

            Report handledReport = reportService.handleReport(reportId, action, userId, handleReason);
            logger.info("✅ 举报处理成功: reportId={}, action={}, adminId={}", reportId, action, userId);
            return ResponseEntity.ok(new ApiResponse(true, handledReport, "举报处理成功"));

        } catch (IllegalStateException e) {
            logger.warn("❌ 举报处理失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, null, e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ 处理举报时发生错误: reportId={}, error={}", reportId, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(false, null, "服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 验证分页参数
     */
    private void validatePageParams(int page, int size) {
        if (page < 1 || size < 1 || size > 100) {
            throw new IllegalArgumentException("无效的分页参数: page=" + page + ", size=" + size);
        }
    }
} 