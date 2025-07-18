package com.se2303.informservice.service;

import com.se2303.informservice.dto.request.CreateInformRequest;
import com.se2303.informservice.entity.Inform;
import com.se2303.informservice.repository.InformRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InformService {

    private static final Logger logger = LoggerFactory.getLogger(InformService.class);

    @Autowired
    private InformRepository informRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private InformStreamConsumer streamConsumer;

    @Value("${inform.queue.key:user_inform_queue}")
    private String informQueueKey;

    @Value("${inform.expiry.days:30}")
    private int expiryDays;
    
    @Value("${user.service.url:http://localhost:8081}")
    private String userServiceUrl;

    /**
     * 创建通知并推送到Redis队列
     */
    @Transactional
    public Inform createInform(CreateInformRequest request) {
        // 创建通知实体
        Inform inform = new Inform();
        inform.setUserId(request.getUserId());
        inform.setType(request.getType());
        inform.setContent(request.getContent());
        inform.setRelatedId(request.getRelatedId());
        inform.setFromUserId(request.getFromUserId());
        inform.setRead(false);
        inform.setCreatedAt(LocalDateTime.now());
        inform.setUpdatedAt(LocalDateTime.now());

        // 保存到数据库
        inform = informRepository.save(inform);

        // 推送到Redis队列
        pushToRedisQueue(inform);

        return inform;
    }

    /**
     * 推送通知到Redis队列
     */
    private void pushToRedisQueue(Inform Inform) {
        String userQueueKey = getUserQueueKey(Inform.getUserId());
        try {
            // 使用Redis列表作为队列，推送到队列尾部
            redisTemplate.opsForList().rightPush(userQueueKey, Inform);
            // 设置队列过期时间�?天）
            redisTemplate.expire(userQueueKey, java.time.Duration.ofDays(7));
        } catch (Exception e) {
            // 记录日志，但不影响主流程
            System.err.println("Failed to push Inform to Redis queue: " + e.getMessage());
        }
    }

    /**
     * 从Redis队列中拉取用户的通知
     */
    public List<Inform> pullFromRedisQueue(Long userId) {
        String userQueueKey = getUserQueueKey(userId);
        try {
            // 从队列头部拉取所有通知
            List<Object> Informs = redisTemplate.opsForList().range(userQueueKey, 0, -1);
            // 清空队列
            redisTemplate.delete(userQueueKey);
            
            if (Informs != null && !Informs.isEmpty()) {
                return Informs.stream()
                        .map(obj -> (Inform) obj)
                        .toList();
            }
        } catch (Exception e) {
            System.err.println("Failed to pull Informs from Redis queue: " + e.getMessage());
        }
        return List.of();
    }

    /**
     * 获取用户的通知列表（分页）
     */
    public Page<Inform> getUserInforms(Long userId, int page, int size) {
        logger.info("🔍 开始查询用户通知列表: userId={}, page={}, size={}", userId, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Inform> informPage = informRepository.findByUserId(userId, pageable);
        
        logger.info("📊 查询结果: userId={}, 总数={}, 当前页数量={}, 总页数={}", 
                   userId, informPage.getTotalElements(), informPage.getNumberOfElements(), 
                   informPage.getTotalPages());
        
        // 为每个inform动态设置用户名（如果需要在服务端处理）
        // informPage.getContent().forEach(this::enrichInformWithUsername);
        
        return informPage;
    }

    /**
     * 获取用户未读通知数量
     */
    public long getUnreadCount(Long userId) {
        logger.info("🔢 开始查询用户未读通知数量: userId={}", userId);
        
        long count = informRepository.countByUserIdAndIsReadFalse(userId);
        
        logger.info("📈 未读通知数量查询结果: userId={}, count={}", userId, count);
        
        return count;
    }

    /**
     * 标记通知为已读
     */
    @Transactional
    public boolean markAsRead(Long informId, Long userId) {
        Optional<Inform> optionalInform = informRepository.findById(informId);
        if (optionalInform.isPresent()) {
            Inform inform = optionalInform.get();
            // 验证通知属于当前用户
            if (inform.getUserId().equals(userId)) {
                inform.setRead(true);
                inform.setUpdatedAt(LocalDateTime.now());
                informRepository.save(inform);
                return true;
            }
        }
        return false;
    }

    /**
     * 批量标记通知为已读
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Inform> unreadInforms = informRepository.findByUserIdAndIsReadFalse(userId);
        for (Inform inform : unreadInforms) {
            inform.setRead(true);
            inform.setUpdatedAt(LocalDateTime.now());
        }
        informRepository.saveAll(unreadInforms);
    }

    /**
     * 删除通知
     */
    @Transactional
    public boolean deleteInform(Long informId, Long userId) {
        Optional<Inform> optionalInform = informRepository.findById(informId);
        if (optionalInform.isPresent()) {
            Inform inform = optionalInform.get();
            // 验证通知属于当前用户
            if (inform.getUserId().equals(userId)) {
                informRepository.delete(inform);
                return true;
            }
        }
        return false;
    }

    /**
     * 清理过期通知
     */
    @Transactional
    public void cleanupExpiredInforms() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(expiryDays);
        informRepository.deleteByCreatedAtBefore(cutoffTime);
    }

    /**
     * 轮询获取用户的最新通知（先从Redis队列拉取，再从数据库查询）
     */
    public Page<Inform> pollUserInforms(Long userId, int page, int size) {
        // 首先手动触发一次Stream消费，确保最新消息被处理
        streamConsumer.consumeUserInforms(userId);
        
        // 从数据库中获取分页结果
        return getUserInforms(userId, page, size);
    }

    /**
     * 获取Stream监控信息
     */
    public String getStreamInfo(Long userId) {
        return streamConsumer.getStreamInfo(userId);
    }

    /**
     * 获取待处理消息数量
     */
    public Long getPendingMessageCount(Long userId) {
        String streamKey = "user_inform_stream:" + userId;
        return streamConsumer.getPendingMessageCount(streamKey);
    }

    /**
     * 获取用户队列Key
     */
    private String getUserQueueKey(Long userId) {
        return informQueueKey + ":" + userId;
    }
    
    /**
     * 从用户服务获取用户名
     * @param userId 用户ID
     * @return 用户名
     */
    public String getUsernameFromUserService(Long userId) {
        try {
            // 调用user-service的API获取用户信息
            String url = userServiceUrl + "/users/" + userId;
            // 这里需要根据实际的user-service API结构来调整
            // UserResponse response = restTemplate.getForObject(url, UserResponse.class);
            // return response.getUsername();
            
            // 临时实现，返回默认用户名格式
            return "用户" + userId;
        } catch (Exception e) {
            // 如果获取失败，返回默认格式
            return "用户" + userId;
        }
    }
    
    /**
     * 为单个通知丰富用户名信息（可选的服务端处理）
     * @param inform 通知对象
     */
    private void enrichInformWithUsername(Inform inform) {
        // 如果需要在服务端预处理，可以在这里调用用户服务
        // 但根据优化原则，建议在前端调用时动态获取
    }
}
