package com.se2303.informservice.controller;

import com.se2303.informservice.dto.response.ApiResponse;
import com.se2303.informservice.service.InformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Stream监控Controller
 * 用于调试和监控Redis Stream状态
 */
@RestController
@RequestMapping("/api/informs")
public class StreamMonitorController {

    @Autowired
    private InformService informService;

    /**
     * 获取用户Stream信息
     */
    @GetMapping("/user/{userId}/stream-info")
    public ApiResponse<String> getStreamInfo(@PathVariable Long userId) {
        try {
            String info = informService.getStreamInfo(userId);
            return ApiResponse.success("获取Stream信息成功", info);
        } catch (Exception e) {
            return ApiResponse.error("获取Stream信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取待处理消息数量
     */
    @GetMapping("/user/{userId}/pending-count")
    public ApiResponse<Long> getPendingCount(@PathVariable Long userId) {
        try {
            Long count = informService.getPendingMessageCount(userId);
            return ApiResponse.success("获取待处理消息数量成功", count);
        } catch (Exception e) {
            return ApiResponse.error("获取待处理消息数量失败: " + e.getMessage());
        }
    }

    /**
     * 手动触发消费用户消息
     */
    @PostMapping("/user/{userId}/consume")
    public ApiResponse<String> manualConsume(@PathVariable Long userId) {
        try {
            informService.pollUserInforms(userId, 0, 1); // 触发消费
            return ApiResponse.success("手动消费触发成功");
        } catch (Exception e) {
            return ApiResponse.error("手动消费失败: " + e.getMessage());
        }
    }
}
