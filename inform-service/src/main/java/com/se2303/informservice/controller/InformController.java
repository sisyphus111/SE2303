package com.se2303.informservice.controller;

import com.se2303.informservice.dto.request.CreateInformRequest;
import com.se2303.informservice.dto.response.ApiResponse;
import com.se2303.informservice.dto.response.InformResponse;
import com.se2303.informservice.dto.response.PageResponse;
import com.se2303.informservice.entity.Inform;
import com.se2303.informservice.service.InformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/informs")
public class InformController {

    private static final Logger logger = LoggerFactory.getLogger(InformController.class);

    @Autowired
    private InformService informService;

    /**
     * 创建通知
     */
    @PostMapping
    public ApiResponse<InformResponse> createInform(@RequestBody CreateInformRequest request) {
        logger.info("📝 收到创建通知请求: {}", request);
        try {
            Inform inform = informService.createInform(request);
            InformResponse response = new InformResponse(inform);
            logger.info("✅ 通知创建成功, ID: {}", inform.getId());
            return ApiResponse.success("通知创建成功", response);
        } catch (Exception e) {
            logger.error("❌ 创建通知失败: {}", e.getMessage(), e);
            return ApiResponse.error("创建通知失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的通知列表（分页）
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<PageResponse<InformResponse>> getUserInforms(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        logger.info("📥 收到获取用户通知列表请求: userId={}, page={}, size={}", userId, page, size);
        try {
            Page<Inform> informs = informService.getUserInforms(userId, page, size);
            Page<InformResponse> responseInforms = informs.map(InformResponse::new);
            logger.info("✅ 成功获取用户 {} 的通知列表，总数: {}, 当前页数据: {}", 
                       userId, informs.getTotalElements(), informs.getNumberOfElements());
            return ApiResponse.success(PageResponse.of(responseInforms));
        } catch (Exception e) {
            logger.error("❌ 获取用户 {} 通知列表失败: {}", userId, e.getMessage(), e);
            return ApiResponse.error("获取通知列表失败: " + e.getMessage());
        }
    }

    /**
     * 轮询获取用户的最新通知
     */
    @GetMapping("/user/{userId}/poll")
    public ApiResponse<PageResponse<InformResponse>> pollUserInforms(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<Inform> informs = informService.pollUserInforms(userId, page, size);
            Page<InformResponse> responseInforms = informs.map(InformResponse::new);
            return ApiResponse.success(PageResponse.of(responseInforms));
        } catch (Exception e) {
            return ApiResponse.error("轮询通知失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户未读通知数量
     */
    @GetMapping("/user/{userId}/unread-count")
    public ApiResponse<Map<String, Object>> getUnreadCount(@PathVariable Long userId) {
        logger.info("📊 收到获取未读通知数量请求: userId={}", userId);
        try {
            long unreadCount = informService.getUnreadCount(userId);
            logger.info("✅ 用户 {} 的未读通知数量: {}", userId, unreadCount);
            return ApiResponse.success(Map.of("unreadCount", unreadCount));
        } catch (Exception e) {
            logger.error("❌ 获取用户 {} 未读通知数量失败: {}", userId, e.getMessage(), e);
            return ApiResponse.error("获取未读通知数量失败: " + e.getMessage());
        }
    }

    /**
     * 标记通知为已读
     */
    @PutMapping("/{informId}/read")
    public ApiResponse<String> markAsRead(
            @PathVariable Long informId,
            @RequestParam Long userId) {
        try {
            boolean success = informService.markAsRead(informId, userId);
            if (success) {
                return ApiResponse.success("通知已标记为已读");
            } else {
                return ApiResponse.error("通知不存在或无权操作", 404);
            }
        } catch (Exception e) {
            return ApiResponse.error("标记通知失败: " + e.getMessage());
        }
    }

    /**
     * 批量标记所有通知为已读
     */
    @PutMapping("/user/{userId}/read-all")
    public ApiResponse<String> markAllAsRead(@PathVariable Long userId) {
        try {
            informService.markAllAsRead(userId);
            return ApiResponse.success("所有通知已标记为已读");
        } catch (Exception e) {
            return ApiResponse.error("批量标记通知失败: " + e.getMessage());
        }
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{informId}")
    public ApiResponse<String> deleteInform(
            @PathVariable Long informId,
            @RequestParam Long userId) {
        try {
            boolean success = informService.deleteInform(informId, userId);
            if (success) {
                return ApiResponse.success("通知删除成功");
            } else {
                return ApiResponse.error("通知不存在或无权删除", 404);
            }
        } catch (Exception e) {
            return ApiResponse.error("删除通知失败: " + e.getMessage());
        }
    }

    /**
     * 从Redis队列拉取通知（调试用）
     */
    @GetMapping("/user/{userId}/pull-queue")
    public ApiResponse<List<InformResponse>> pullFromQueue(@PathVariable Long userId) {
        try {
            List<Inform> informs = informService.pullFromRedisQueue(userId);
            List<InformResponse> responses = informs.stream()
                    .map(InformResponse::new)
                    .collect(Collectors.toList());
            return ApiResponse.success("从队列拉取通知成功", responses);
        } catch (Exception e) {
            return ApiResponse.error("从队列拉取通知失败: " + e.getMessage());
        }
    }

    /**
     * 清理过期通知（管理员接口）
     */
    @DeleteMapping("/cleanup")
    public ApiResponse<String> cleanupExpiredInforms() {
        try {
            informService.cleanupExpiredInforms();
            return ApiResponse.success("过期通知清理完成");
        } catch (Exception e) {
            return ApiResponse.error("清理过期通知失败: " + e.getMessage());
        }
    }
}
