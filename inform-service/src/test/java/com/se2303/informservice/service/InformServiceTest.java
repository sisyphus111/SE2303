package com.se2303.informservice.service;

import com.se2303.informservice.dto.request.CreateInformRequest;
import com.se2303.informservice.entity.Inform;
import com.se2303.informservice.entity.InformType;
import com.se2303.informservice.repository.InformRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InformServiceTest {

    @Mock
    private InformRepository informRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private InformStreamConsumer streamConsumer;

    @InjectMocks
    private InformService informService;

    private Inform inform;
    private CreateInformRequest request;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        inform = new Inform();
        inform.setId(1L);
        inform.setUserId(1L);
        inform.setType(InformType.COMMENT);
        inform.setContent("测试评论");
        inform.setRelatedId("post_123");
        inform.setFromUserId(2L);
        inform.setRead(false);
        inform.setCreatedAt(LocalDateTime.now());

        request = new CreateInformRequest(1L, InformType.COMMENT, "测试评论", "post_123", 2L);

        // 使用反射设置 informQueueKey，模拟 @Value 注入
        try {
            java.lang.reflect.Field field = InformService.class.getDeclaredField("informQueueKey");
            field.setAccessible(true);
            field.set(informService, "user_inform_queue");
        } catch (Exception e) {
            throw new RuntimeException("Failed to set informQueueKey", e);
        }
    }

    @Test
    void testCreateInform_Success() {
        // 模拟 RedisTemplate 的 ListOperations
        ListOperations<String, Object> listOperations = mock(ListOperations.class);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        // 模拟数据库保存
        when(informRepository.save(any(Inform.class))).thenReturn(inform);
        // 模拟 Redis 队列推送
        when(listOperations.rightPush(eq("user_inform_queue:1"), any(Inform.class))).thenReturn(1L);

        Inform result = informService.createInform(request);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(InformType.COMMENT, result.getType());
        assertEquals("测试评论", result.getContent());
        verify(informRepository, times(1)).save(any(Inform.class));
        verify(listOperations, times(1)).rightPush(eq("user_inform_queue:1"), any(Inform.class));
    }

    @Test
    void testGetUserInforms_Success() {
        // 模拟分页查询
        Page<Inform> page = new PageImpl<>(Arrays.asList(inform));
        when(informRepository.findByUserId(eq(1L), any(PageRequest.class))).thenReturn(page);

        Page<Inform> result = informService.getUserInforms(1L, 0, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(inform, result.getContent().get(0));
        verify(informRepository, times(1)).findByUserId(eq(1L), any(PageRequest.class));
    }

    @Test
    void testGetUnreadCount_Success() {
        // 模拟未读通知数量
        when(informRepository.countByUserIdAndIsReadFalse(1L)).thenReturn(5L);

        long count = informService.getUnreadCount(1L);

        // 验证结果
        assertEquals(5L, count);
        verify(informRepository, times(1)).countByUserIdAndIsReadFalse(1L);
    }

    @Test
    void testMarkAsRead_Success() {
        // 模拟查询通知
        when(informRepository.findById(1L)).thenReturn(Optional.of(inform));
        when(informRepository.save(any(Inform.class))).thenReturn(inform);

        boolean result = informService.markAsRead(1L, 1L);

        // 验证结果
        assertTrue(result);
        assertTrue(inform.isRead());
        assertNotNull(inform.getReadAt());
        verify(informRepository, times(1)).save(any(Inform.class));
    }

    @Test
    void testMarkAsRead_InvalidUserId() {
        // 模拟通知属于其他用户
        inform.setUserId(2L);
        when(informRepository.findById(1L)).thenReturn(Optional.of(inform));

        boolean result = informService.markAsRead(1L, 1L);

        // 验证结果
        assertFalse(result);
        verify(informRepository, never()).save(any(Inform.class));
    }

    @Test
    void testDeleteInform_Success() {
        // 模拟查询通知
        when(informRepository.findById(1L)).thenReturn(Optional.of(inform));

        boolean result = informService.deleteInform(1L, 1L);

        // 验证结果
        assertTrue(result);
        verify(informRepository, times(1)).delete(inform);
    }

    @Test
    void testPullFromRedisQueue_Success() {
        // 模拟 RedisTemplate 的 ListOperations
        ListOperations<String, Object> listOperations = mock(ListOperations.class);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        // 模拟 Redis 队列拉取
        when(listOperations.range(eq("user_inform_queue:1"), eq(0L), eq(-1L)))
                .thenReturn(Arrays.asList(inform));

        List<Inform> result = informService.pullFromRedisQueue(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(inform, result.get(0));
        verify(redisTemplate, times(1)).delete(eq("user_inform_queue:1"));
    }

    @Test
    void testCleanupExpiredInforms() {
        // 模拟清理过期通知
        informService.cleanupExpiredInforms();

        // 验证清理方法被调用
        verify(informRepository, times(1))
                .deleteByCreatedAtBefore(any(LocalDateTime.class));
    }
}