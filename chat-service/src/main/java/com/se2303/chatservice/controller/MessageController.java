package com.se2303.chatservice.controller;

import com.se2303.chatservice.dto.ApiResponse;
import com.se2303.chatservice.dto.CreateMessageRequest;
import com.se2303.chatservice.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/api/chat")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    /**
     * 健康检查接口
     * GET /api/chat/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Chat Service");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(ApiResponse.success(health, "服务健康"));
    }
    
    /**
     * 发送消息
     * POST /api/chat/messages
     */
    @PostMapping("/messages")
    public ResponseEntity<ApiResponse> createMessage(@Valid @RequestBody CreateMessageRequest request) {
        try {
            ApiResponse response = messageService.createMessage(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("发送消息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 分页获取消息列表
     * GET /api/chat/messages
     */
    @GetMapping("/messages")
    public ResponseEntity<ApiResponse> getMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(messageService.getMessages(page, size));
    }
    
    /**
     * 根据用户ID分页获取消息
     * GET /api/chat/messages/user/{userId}
     */
    @GetMapping("/messages/user/{userId}")
    public ResponseEntity<ApiResponse> getMessagesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(messageService.getMessagesByUserId(userId, page, size));
    }
    
    /**
     * 获取所有消息（不分页）
     * GET /api/chat/messages/all
     */
    @GetMapping("/messages/all")
    public ResponseEntity<ApiResponse> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }
    
    /**
     * 删除消息
     * DELETE /api/chat/messages/{id}
     */
    @DeleteMapping("/messages/{id}")
    public ResponseEntity<ApiResponse> deleteMessage(@PathVariable Long id) {
        try {
            ApiResponse response = messageService.deleteMessage(id);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("删除消息失败: " + e.getMessage()));
        }
    }
} 