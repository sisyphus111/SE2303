package com.se2303.informservice.controller;

import com.se2303.informservice.dto.response.ApiResponse;
import com.se2303.informservice.dto.response.InformResponse;
import com.se2303.informservice.entity.Inform;
import com.se2303.informservice.entity.InformType;
import com.se2303.informservice.service.InformApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 内部服务调用的API Controller
 * 用于其他微服务（�?community-service）调�?
 */
@RestController
@RequestMapping("/api/internal/informs")
public class InternalInformController {

    @Autowired
    private InformApiService informApiService;

    /**
     * 发送评论通知
     */
    @PostMapping("/comment")
    public ApiResponse<InformResponse> sendCommentInform(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String content = (String) request.get("content");
            String relatedId = (String) request.get("relatedId");
            Long fromUserId = Long.valueOf(request.get("fromUserId").toString());
            
            informApiService.sendCommentInform(userId, content, relatedId, fromUserId);
            return ApiResponse.success("评论通知发送成功");
        } catch (Exception e) {
            return ApiResponse.error("发送评论通知失败: " + e.getMessage());
        }
    }

    /**
     * 发送点赞通知
     */
    @PostMapping("/like")
    public ApiResponse<InformResponse> sendLikeInform(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String relatedId = (String) request.get("relatedId");
            Long fromUserId = Long.valueOf(request.get("fromUserId").toString());
            
            informApiService.sendLikeInform(userId, relatedId, fromUserId);
            return ApiResponse.success("点赞通知发送成功");
        } catch (Exception e) {
            return ApiResponse.error("发送点赞通知失败: " + e.getMessage());
        }
    }

    /**
     * 发送关注通知
     */
    @PostMapping("/follow")
    public ApiResponse<InformResponse> sendFollowInform(@RequestBody Map<String, Object> request) {
        return ApiResponse.error("关注通知功能已移除，只支持评论和点赞通知");
    }

    /**
     * 发送系统通知
     */
    @PostMapping("/system")
    public ApiResponse<InformResponse> sendSystemInform(@RequestBody Map<String, Object> request) {
        return ApiResponse.error("系统通知功能已移除，只支持评论和点赞通知");
    }

    /**
     * 通用通知发送接�?
     */
    @PostMapping("/send")
    public ApiResponse<InformResponse> sendInform(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            InformType type = InformType.valueOf((String) request.get("type"));
            String content = (String) request.get("content");
            String relatedId = (String) request.get("relatedId");
            Long fromUserId = Long.valueOf(request.get("fromUserId").toString());
            
            Inform inform = informApiService.sendInform(userId, type, content, relatedId, fromUserId);
            InformResponse response = new InformResponse(inform);
            return ApiResponse.success("通知发送成功", response);
        } catch (Exception e) {
            return ApiResponse.error("发送通知失败: " + e.getMessage());
        }
    }
}
