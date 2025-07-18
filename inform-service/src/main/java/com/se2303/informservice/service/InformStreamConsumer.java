package com.se2303.informservice.service;

import com.se2303.informservice.entity.Inform;
import com.se2303.informservice.entity.InformType;
import com.se2303.informservice.repository.InformRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class InformStreamConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(InformStreamConsumer.class);
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private InformRepository informRepository;
    
    @Value("${inform.stream.key:user_inform_stream}")
    private String informStreamKey;
    
    @Value("${inform.consumer.group:inform_consumer_group}")
    private String consumerGroup;
    
    @Value("${inform.consumer.name:inform_consumer}")
    private String consumerName;
    
    /**
     * 定时轮询Redis Stream消费通知消息
     * 每30秒执行一次
     */
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void consumeInforms() {
        try {
            // 获取所有用户的Stream keys
            String pattern = informStreamKey + ":*";
            Set<String> keys = redisTemplate.keys(pattern);
            
            if (keys == null || keys.isEmpty()) {
                logger.debug("没有找到用户Stream队列");
                return;
            }
            
            logger.debug("找到{}个用户Stream队列", keys.size());
            for (String streamKey : keys) {
                processUserStream(streamKey);
            }
            
        } catch (Exception e) {
            logger.error("❌ 消费通知消息时发生错误: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理单个用户Stream
     */
    private void processUserStream(String streamKey) {
        try {
            // 创建消费者组（如果不存在）
            createConsumerGroupIfNotExists(streamKey);
            
            // 从Stream中读取消息
            List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                .read(Consumer.from(consumerGroup, consumerName),
                      StreamReadOptions.empty().count(10).block(Duration.ofSeconds(1)),
                      StreamOffset.create(streamKey, ReadOffset.lastConsumed()));
            
            if (records != null && !records.isEmpty()) {
                logger.debug("从Stream {}读取到{}条消息", streamKey, records.size());
                for (MapRecord<String, Object, Object> record : records) {
                    processInformMessage(record, streamKey);
                    // 确认消息已处理
                    redisTemplate.opsForStream().acknowledge(streamKey, consumerGroup, record.getId());
                }
            }
            
        } catch (Exception e) {
            logger.error("❌ 处理用户Stream失败: streamKey={}, error={}", streamKey, e.getMessage(), e);
        }
    }
    
    /**
     * 创建消费者组
     */
    public void createConsumerGroupIfNotExists(String streamKey) {
        try {
            // 尝试创建消费者组，从最新消息开始消费
            redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.latest(), consumerGroup);
            logger.debug("创建消费者组成功: {}", consumerGroup);
        } catch (Exception e) {
            // 消费者组可能已存在，忽略错误
            logger.debug("消费者组可能已存在: {}", e.getMessage());
        }
    }
    
    /**
     * 处理单条通知消息
     */
    public void processInformMessage(MapRecord<String, Object, Object> record, String streamKey) {
        try {
            Map<Object, Object> messageMap = record.getValue();
            
            String type = messageMap.get("type").toString();
            
            // 只处理评论和点赞类型
            if (!"COMMENT".equals(type) && !"LIKE".equals(type)) {
                logger.warn("忽略不支持的通知类型: {}", type);
                return;
            }
            
            // 创建Inform实体
            Inform inform = new Inform();
            inform.setUserId(Long.valueOf(messageMap.get("userId").toString()));
            inform.setType(InformType.valueOf(type));
            inform.setRelatedId(messageMap.get("relatedId").toString());
            inform.setFromUserId(Long.valueOf(messageMap.get("fromUserId").toString()));
            
            // 根据类型设置内容
            if ("COMMENT".equals(type)) {
                inform.setContent(messageMap.get("content").toString()); // 存储评论内容
            } else if ("LIKE".equals(type)) {
                inform.setContent(""); // 点赞通知content为空
            }
            
            inform.setIsRead(false);
            
            // 解析创建时间
            try {
                String createdAtStr = messageMap.get("createdAt").toString();
                inform.setCreatedAt(LocalDateTime.parse(createdAtStr));
            } catch (DateTimeParseException e) {
                inform.setCreatedAt(LocalDateTime.now());
            }
            
            // 保存到数据库
            informRepository.save(inform);
            
            logger.info("✅ 通知消息已保存: userId={}, type={}, fromUserId={}, recordId={}", 
                       inform.getUserId(), inform.getType(), inform.getFromUserId(), record.getId());
            
        } catch (Exception e) {
            logger.error("❌ 处理通知消息失败: recordId={}, streamKey={}, error={}", 
                        record.getId(), streamKey, e.getMessage(), e);
        }
    }
    
    /**
     * 手动触发消费指定用户的通知（用于立即处理）
     */
    public void consumeUserInforms(Long userId) {
        String streamKey = informStreamKey + ":" + userId;
        processUserStream(streamKey);
    }
    
    /**
     * 获取待处理的消息数量
     */
    public Long getPendingMessageCount(String streamKey) {
        try {
            PendingMessagesSummary summary = redisTemplate.opsForStream()
                .pending(streamKey, consumerGroup);
            return summary.getTotalPendingMessages();
        } catch (Exception e) {
            logger.error("❌ 获取待处理消息数量失败: {}", e.getMessage());
            return 0L;
        }
    }
    
    /**
     * 获取Stream信息（用于监控）
     */
    public String getStreamInfo(Long userId) {
        String streamKey = informStreamKey + ":" + userId;
        try {
            StreamInfo.XInfoStream info = redisTemplate.opsForStream().info(streamKey);
            return String.format("Stream: %s, Length: %d, Groups: %d", 
                                streamKey, info.streamLength(), info.groupCount());
        } catch (Exception e) {
            return "Stream不存在或获取信息失败: " + e.getMessage();
        }
    }
}
