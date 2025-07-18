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

    public void sendCommentInform(Long targetUserId, Long fromUserId, Long postId, String commentContent) {
        try {
            Map<String, Object> informData = new HashMap<>();
            informData.put("id", UUID.randomUUID().toString());
            informData.put("type", "COMMENT");
            informData.put("userId", targetUserId.toString());
            informData.put("fromUserId", fromUserId.toString());
            informData.put("relatedId", postId.toString());
            informData.put("content", commentContent);
            informData.put("createdAt", LocalDateTime.now().toString());
            informData.put("isRead", "false");

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

    public void sendLikeInform(Long targetUserId, Long fromUserId, Long postId) {
        try {
            Map<String, Object> informData = new HashMap<>();
            informData.put("id", UUID.randomUUID().toString());
            informData.put("type", "LIKE");
            informData.put("userId", targetUserId.toString());
            informData.put("fromUserId", fromUserId.toString());
            informData.put("relatedId", postId.toString());
            informData.put("content", "");
            informData.put("createdAt", LocalDateTime.now().toString());
            informData.put("isRead", "false");

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
        if (informStreamKey == null) {
            logger.error("Stream key is not configured for userId={}", userId);
            throw new IllegalStateException("Stream key is not configured");
        }
        return informStreamKey + ":" + userId;
    }

    public String getStreamStats(Long userId) {
        try {
            String streamKey = getUserStreamKey(userId);
            return String.format("Stream: %s 存在", streamKey);
        } catch (Exception e) {
            logger.error("获取Stream信息失败: {}", e.getMessage(), e);
            return "获取Stream信息失败: " + e.getMessage();
        }
    }
}