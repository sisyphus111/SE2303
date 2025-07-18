package com.se2303.talkservice.service;

import com.se2303.talkservice.model.Message;
import com.se2303.talkservice.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消息服务
 * 处理消息的存储和查询
 */
@Service
public class MessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    
    @Autowired
    private MessageRepository messageRepository;
    
    /**
     * 保存消息到数据库
     * @param userId 用户ID
     * @param content 消息内容
     * @return 保存的消息实体
     */
    public Message saveMessage(Long userId, String content) {
        try {
            Message message = new Message(userId, content);
            Message savedMessage = messageRepository.save(message);
            logger.info("消息保存成功: id={}, userId={}, content={}", 
                       savedMessage.getId(), savedMessage.getUserId(), savedMessage.getContent());
            
            return savedMessage;
            
        } catch (Exception e) {
            logger.error("保存消息失败: {}", e.getMessage());
            throw new RuntimeException("保存消息失败", e);
        }
    }
    
    /**
     * 分页获取消息
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表
     */
    public List<Message> getMessagesByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // 降序排列，第一页显示最新的消息
        Page<Message> messagePage = messageRepository.findAllByValidTrueOrderByCreatedAtDesc(pageable);

        // 页内消息按降序排列（从新到旧）
        List<Message> messages = messagePage.getContent();
        return messages;
    }
    
    /**
     * 根据用户ID分页获取消息
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表
     */
    public List<Message> getMessagesByUserId(Long userId, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            // 升序排列，第一页显示最早的消息
            Page<Message> messagePage = messageRepository.findByUserIdAndValidTrueOrderByCreatedAtAsc(userId, pageable);
            logger.info("获取用户消息成功: userId={}, page={}, size={}, total={}", 
                       userId, page, size, messagePage.getTotalElements());
            // 页内消息按降序排列（从新到旧）
            return messagePage.getContent();
            
        } catch (Exception e) {
            logger.error("获取用户消息失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("获取用户消息失败", e);
        }
    }
} 