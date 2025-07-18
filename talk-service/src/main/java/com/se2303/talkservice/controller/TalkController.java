package com.se2303.talkservice.controller;

import com.se2303.talkservice.dto.ApiResponse;
import com.se2303.talkservice.dto.PastMessage;
import com.se2303.talkservice.dto.SentMessage;
import com.se2303.talkservice.dto.WsContent;
import com.se2303.talkservice.handler.TalkWebSocketHandler;
import com.se2303.talkservice.model.Message;
import com.se2303.talkservice.service.MessageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 聊天控制器
 * 提供聊天相关的REST API接口
 */
@RestController
@RequestMapping("/api/talk")
public class TalkController {
    
    @Autowired
    private MessageService messageService;

    private static final Logger logger = LoggerFactory.getLogger(TalkController.class);

    @Autowired
    private TalkWebSocketHandler webSocketHandler;

    
    /**
     * 获取历史消息记录（分页）
     * GET /api/talk/messages?page=0&size=20
     */
    @GetMapping("/messages")
    public ResponseEntity<ApiResponse> getPastMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        
        try {
            // 从请求头获取用户ID
            String userIdStr = request.getHeader("X-User-Id");
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("缺少用户ID"));
            }
            
            Long userId = Long.parseLong(userIdStr);
            System.out.println("获取历史消息 - 用户ID: " + userId);
            
            List<Message> messages = messageService.getMessagesByPage(page - 1, size);
            
            // 转换为PastMessage DTO
            List<PastMessage> pastMessages = messages.stream()
                    .map(msg -> new PastMessage(
                            msg.getId(),
                            msg.getUserId(),
                            msg.getContent(),
                            msg.getCreatedAt()
                    ))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(pastMessages, "获取历史消息记录成功"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("用户ID格式错误"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取历史消息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 发送消息
     * POST /api/talk/messages
     */
    @PostMapping("/messages")
    public ResponseEntity<ApiResponse> sendMessage(
            @RequestBody SentMessage sentMessage,
            HttpServletRequest request) {
        
        try {
            // 从请求头获取用户ID
            String userIdStr = request.getHeader("X-User-Id");
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("缺少用户ID"));
            }
            
            Long userId = Long.parseLong(userIdStr);
            System.out.println("发送消息 - 用户ID: " + userId);
            
            // 保存消息到数据库
            Message savedMessage = messageService.saveMessage(userId, sentMessage.getContent());
            
            // 构建WebSocket推送内容
            WsContent wsContent = new WsContent();
            WsContent.ContentData contentData = new WsContent.ContentData();
            contentData.setId(savedMessage.getId());
            contentData.setFrom_userId(userId);
            contentData.setContent(sentMessage.getContent());
            contentData.setCreatedAt(savedMessage.getCreatedAt());
            wsContent.setContent(contentData);
            
            logger.info("已经根据用户id构建了wsContent，现在开始推送给所有在线用户");
            // 通过WebSocket推送给所有在线用户
            webSocketHandler.broadcastToAllUsers(wsContent);
            
            Map<String, Object> result = Map.of(
                "messageId", savedMessage.getId(),
                "success", true
            );
            
            return ResponseEntity.ok(ApiResponse.success(result, "消息发送成功"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("用户ID格式错误"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("消息发送失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户的消息历史
     * GET /api/talk/user/messages?page=0&size=20
     */
    @GetMapping("/user/messages")
    public ResponseEntity<ApiResponse> getUserMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        
        try {
            // 从请求头获取用户ID
            String userIdStr = request.getHeader("X-User-Id");
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("缺少用户ID"));
            }
            
            Long userId = Long.parseLong(userIdStr);
            System.out.println("获取用户消息 - 用户ID: " + userId);
            
            List<Message> messages = messageService.getMessagesByUserId(userId, page, size);
            
            // 转换为PastMessage DTO
            List<PastMessage> pastMessages = messages.stream()
                    .map(msg -> new PastMessage(
                            msg.getId(),
                            msg.getUserId(),
                            msg.getContent(),
                            msg.getCreatedAt()
                    ))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(pastMessages, "获取用户消息记录成功"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("用户ID格式错误"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取用户消息失败: " + e.getMessage()));
        }
    }
} 