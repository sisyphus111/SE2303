package com.se2303.chatservice.service;

import com.se2303.chatservice.dto.ApiResponse;
import com.se2303.chatservice.dto.CreateMessageRequest;
import com.se2303.chatservice.dto.MessageResponse;
import com.se2303.chatservice.model.Message;
import com.se2303.chatservice.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    /**
     * 创建消息
     */
    public ApiResponse createMessage(CreateMessageRequest request) {
        try {
            if (request.getUserId() == null) {
                return ApiResponse.error("用户ID不能为空");
            }
            
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return ApiResponse.error("消息内容不能为空");
            }
            
            Message message = new Message(
                request.getUserId(),
                request.getContent().trim()
            );
            
            Message savedMessage = messageRepository.save(message);
            MessageResponse response = convertToResponse(savedMessage);
            
            return ApiResponse.success(response, "消息发送成功");
        } catch (Exception e) {
            return ApiResponse.error("发送消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页查询消息列表（只返回有效消息）
     */
    public ApiResponse getMessages(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Message> messages = messageRepository.findByValidTrueOrderByCreatedAtDesc(pageable);

            List<MessageResponse> responses = messages.getContent().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("list", responses);
            result.put("total", messages.getTotalElements());
            result.put("totalPages", messages.getTotalPages());
            result.put("currentPage", page);
            result.put("pageSize", size);

            return ApiResponse.success(result, "获取消息列表成功");
        } catch (Exception e) {
            return ApiResponse.error("获取消息列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据用户ID分页查询消息（只返回有效消息）
     */
    public ApiResponse getMessagesByUserId(Long userId, int page, int size) {
        try {
            if (userId == null) {
                return ApiResponse.error("用户ID不能为空");
            }
            
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Message> messages = messageRepository.findByUserIdAndValidTrueOrderByCreatedAtDesc(userId, pageable);

            List<MessageResponse> responses = messages.getContent().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("list", responses);
            result.put("total", messages.getTotalElements());
            result.put("totalPages", messages.getTotalPages());
            result.put("currentPage", page);
            result.put("pageSize", size);

            return ApiResponse.success(result, "获取用户消息成功");
        } catch (Exception e) {
            return ApiResponse.error("获取用户消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有有效消息（不分页）
     */
    public ApiResponse getAllMessages() {
        try {
            List<Message> messages = messageRepository.findByValidTrueOrderByCreatedAtDesc();
            List<MessageResponse> responses = messages.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(responses, "获取所有消息成功");
        } catch (Exception e) {
            return ApiResponse.error("获取所有消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 软删除消息（将valid设置为false）
     */
    public ApiResponse deleteMessage(Long messageId) {
        try {
            if (messageId == null) {
                return ApiResponse.error("消息ID不能为空");
            }
            
            Message message = messageRepository.findById(messageId).orElse(null);
            if (message == null) {
                return ApiResponse.error("消息不存在");
            }
            
            message.setValid(false);
            messageRepository.save(message);
            return ApiResponse.success(null, "消息删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 将Message实体转换为MessageResponse
     */
    private MessageResponse convertToResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getUserId(),
                message.getContent(),
                message.getCreatedAt(),
                message.getValid()
        );
    }
} 