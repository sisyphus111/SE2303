package com.se2303.communityservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class InformProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(InformProducer.class);
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Value("${inform.stream.key:user_inform_stream}")
    private String informStreamKey;
    
    /**
     * 发送评论通知
     * @param targetUserId 目标用户ID
     * @param fromUserId 评论者ID
     * @param postId 帖子ID
     * @param commentContent 评论内容
     */
    public void sendCommentInform(Long targetUserId, Long fromUserId, Long postId, String commentContent) {
        try {
            Map<String, Object> informData = new HashMap<>();
            informData.put("id", UUID.randomUUID().toString());
            informData.put("type", "COMMENT");
            informData.put("userId", targetUserId.toString());
            informData.put("fromUserId", fromUserId.toString());
            informData.put("relatedId", postId.toString());
            informData.put("content", commentContent); // 只存储评论内容
            informData.put("createdAt", LocalDateTime.now().toString());
            informData.put("isRead", "false");
            
            // 添加到Redis Stream
            String recordId = redisTemplate.opsForStream().add(
                StreamRecords.newRecord()
                    .in(getUserStreamKey(targetUserId))
                    .ofMap(informData)
            ).toString();
            
            logger.info("✅ 评论通知已发送到Stream: targetUserId={}, fromUserId={}, postId={}, recordId={}", 
                       targetUserId, fromUserId, postId, recordId);
        } catch (Exception e) {
            logger.error("❌ 发送评论通知失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 发送点赞通知
     * @param targetUserId 目标用户ID
     * @param fromUserId 点赞者ID
     * @param postId 帖子ID
     */
    public void sendLikeInform(Long targetUserId, Long fromUserId, Long postId) {
        try {
            Map<String, Object> informData = new HashMap<>();
            informData.put("id", UUID.randomUUID().toString());
            informData.put("type", "LIKE");
            informData.put("userId", targetUserId.toString());
            informData.put("fromUserId", fromUserId.toString());
            informData.put("relatedId", postId.toString());
            informData.put("content", ""); // 点赞通知content为空
            informData.put("createdAt", LocalDateTime.now().toString());
            informData.put("isRead", "false");
            
            // 添加到Redis Stream
            String recordId = redisTemplate.opsForStream().add(
                StreamRecords.newRecord()
                    .in(getUserStreamKey(targetUserId))
                    .ofMap(informData)
            ).toString();
            
            logger.info("✅ 点赞通知已发送到Stream: targetUserId={}, fromUserId={}, postId={}, recordId={}", 
                       targetUserId, fromUserId, postId, recordId);
        } catch (Exception e) {
            logger.error("❌ 发送点赞通知失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 统一的通知发送方法
     * @param targetUserId 目标用户ID（字符串类型）
     * @param fromUserId 发送者用户ID（字符串类型）
     * @param type 通知类型（COMMENT/LIKE）
     * @param content 通知内容（评论内容或空字符串）
     * @param postId 帖子ID（长整型）
     */
    public void sendNotification(String targetUserId, String fromUserId, String type, String content, Long postId) {
        try {
            Long targetUserIdLong = Long.parseLong(targetUserId);
            Long fromUserIdLong = Long.parseLong(fromUserId);
            
            if ("COMMENT".equals(type)) {
                sendCommentInform(targetUserIdLong, fromUserIdLong, postId, content);
            } else if ("LIKE".equals(type)) {
                sendLikeInform(targetUserIdLong, fromUserIdLong, postId);
            } else {
                logger.warn("不支持的通知类型: {}", type);
            }
        } catch (NumberFormatException e) {
            logger.error("用户ID格式错误: targetUserId={}, fromUserId={}", targetUserId, fromUserId);
        } catch (Exception e) {
            logger.error("发送通知失败: {}", e.getMessage(), e);
        }
    }
    
    private String getUserStreamKey(Long userId) {
        return informStreamKey + ":" + userId;
    }
    
    /**
     * 获取Stream统计信息（用于监控）
     */
    public String getStreamStats(Long userId) {
        try {
            String streamKey = getUserStreamKey(userId);
            return String.format("Stream: %s 存在", streamKey);
        } catch (Exception e) {
            return "获取Stream信息失败: " + e.getMessage();
        }
    }
}
