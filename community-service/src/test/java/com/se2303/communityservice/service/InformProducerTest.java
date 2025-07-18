package com.se2303.communityservice.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InformProducerTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private StreamOperations<String, Object, Object> streamOperations;

    @InjectMocks
    private InformProducer informProducer;

    private final Long targetUserId = 1L;
    private final Long fromUserId = 2L;
    private final Long postId = 100L;
    private final String commentContent = "This is a test comment";
    private final String informStreamKey = "user_inform_stream";

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        // 使用反射设置 informStreamKey
        try {
            var field = InformProducer.class.getDeclaredField("informStreamKey");
            field.setAccessible(true);
            field.set(informProducer, informStreamKey);
        } catch (Exception e) {
            fail("Failed to set informStreamKey: " + e.getMessage());
        }

        // 设置 lenient 模式以避免 UnnecessaryStubbingException
        lenient().when(redisTemplate.opsForStream()).thenReturn(streamOperations);

        // 设置 Logback ListAppender 用于捕获日志
        Logger logger = (Logger) LoggerFactory.getLogger(InformProducer.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void testSendCommentInform_Success() {
        String recordId = "123-0";
        when(streamOperations.add(any())).thenReturn(RecordId.of(recordId));

        informProducer.sendCommentInform(targetUserId, fromUserId, postId, commentContent);

        verify(streamOperations, times(1)).add(argThat(record ->
                record.getStream().equals(informStreamKey + ":" + targetUserId) &&
                        record.getValue().get("type").equals("COMMENT") &&
                        record.getValue().get("userId").equals(targetUserId.toString()) &&
                        record.getValue().get("fromUserId").equals(fromUserId.toString()) &&
                        record.getValue().get("relatedId").equals(postId.toString()) &&
                        record.getValue().get("content").equals(commentContent) &&
                        record.getValue().get("isRead").equals("false") &&
                        record.getValue().containsKey("id") &&
                        record.getValue().containsKey("createdAt")
        ));

        assertFalse(listAppender.list.stream().anyMatch(event ->
                event.getFormattedMessage().contains("发送评论通知失败")));
    }

    @Test
    void testSendCommentInform_RedisException() {
        when(streamOperations.add(any())).thenThrow(new RuntimeException("Redis error"));

        informProducer.sendCommentInform(targetUserId, fromUserId, postId, commentContent);

        verify(streamOperations, times(1)).add(any());

        List<ILoggingEvent> logs = listAppender.list;
        assertTrue(logs.stream().anyMatch(event ->
                event.getFormattedMessage().contains("❌ 发送评论通知失败: Redis error")));
    }

    @Test
    void testSendLikeInform_Success() {
        String recordId = "123-0";
        when(streamOperations.add(any())).thenReturn(RecordId.of(recordId));

        informProducer.sendLikeInform(targetUserId, fromUserId, postId);

        verify(streamOperations, times(1)).add(argThat(record ->
                record.getStream().equals(informStreamKey + ":" + targetUserId) &&
                        record.getValue().get("type").equals("LIKE") &&
                        record.getValue().get("userId").equals(targetUserId.toString()) &&
                        record.getValue().get("fromUserId").equals(fromUserId.toString()) &&
                        record.getValue().get("relatedId").equals(postId.toString()) &&
                        record.getValue().get("content").equals("") &&
                        record.getValue().get("isRead").equals("false") &&
                        record.getValue().containsKey("id") &&
                        record.getValue().containsKey("createdAt")
        ));

        assertFalse(listAppender.list.stream().anyMatch(event ->
                event.getFormattedMessage().contains("发送点赞通知失败")));
    }

    @Test
    void testSendLikeInform_RedisException() {
        when(streamOperations.add(any())).thenThrow(new RuntimeException("Redis error"));

        informProducer.sendLikeInform(targetUserId, fromUserId, postId);

        verify(streamOperations, times(1)).add(any());

        List<ILoggingEvent> logs = listAppender.list;
        assertTrue(logs.stream().anyMatch(event ->
                event.getFormattedMessage().contains("❌ 发送点赞通知失败: Redis error")));
    }

    @Test
    void testSendNotification_Comment_Success() {
        String recordId = "123-0";
        when(streamOperations.add(any())).thenReturn(RecordId.of(recordId));

        informProducer.sendNotification(targetUserId.toString(), fromUserId.toString(), "COMMENT", commentContent, postId);

        verify(streamOperations, times(1)).add(argThat(record ->
                record.getStream().equals(informStreamKey + ":" + targetUserId) &&
                        record.getValue().get("type").equals("COMMENT") &&
                        record.getValue().get("content").equals(commentContent)
        ));

        assertFalse(listAppender.list.stream().anyMatch(event ->
                event.getFormattedMessage().contains("发送评论通知失败")));
    }

    @Test
    void testSendNotification_Like_Success() {
        String recordId = "123-0";
        when(streamOperations.add(any())).thenReturn(RecordId.of(recordId));

        informProducer.sendNotification(targetUserId.toString(), fromUserId.toString(), "LIKE", "", postId);

        verify(streamOperations, times(1)).add(argThat(record ->
                record.getStream().equals(informStreamKey + ":" + targetUserId) &&
                        record.getValue().get("type").equals("LIKE") &&
                        record.getValue().get("content").equals("")
        ));

        assertFalse(listAppender.list.stream().anyMatch(event ->
                event.getFormattedMessage().contains("发送点赞通知失败")));
    }

    @Test
    void testSendNotification_InvalidType() {
        informProducer.sendNotification(targetUserId.toString(), fromUserId.toString(), "INVALID", commentContent, postId);

        verify(streamOperations, never()).add(any());

        List<ILoggingEvent> logs = listAppender.list;
        assertTrue(logs.stream().anyMatch(event ->
                event.getFormattedMessage().contains("不支持的通知类型: INVALID")));
    }

    @Test
    void testSendNotification_InvalidUserIdFormat() {
        informProducer.sendNotification("invalid", fromUserId.toString(), "COMMENT", commentContent, postId);

        verify(streamOperations, never()).add(any());

        List<ILoggingEvent> logs = listAppender.list;
        assertTrue(logs.stream().anyMatch(event ->
                event.getFormattedMessage().contains("用户ID格式错误: targetUserId=invalid, fromUserId=2")));
    }

    @Test
    void testGetStreamStats_Success() {
        String result = informProducer.getStreamStats(targetUserId);

        assertEquals("Stream: " + informStreamKey + ":" + targetUserId + " 存在", result);
    }

    @Test
    void testGetStreamStats_Exception() {
        // 设置 informStreamKey 为 null
        try {
            var field = InformProducer.class.getDeclaredField("informStreamKey");
            field.setAccessible(true);
            field.set(informProducer, null);
            // 验证字段是否正确设置为 null
            assertNull(field.get(informProducer), "informStreamKey should be null");
        } catch (Exception e) {
            fail("Failed to set informStreamKey: " + e.getMessage());
        }

        // 执行测试
        String result = informProducer.getStreamStats(targetUserId);
        System.out.println("Result: " + result); // 调试输出

        // 验证返回结果和日志
        assertTrue(result.startsWith("获取Stream信息失败: Stream key is not configured"),
                "Expected result to start with '获取Stream信息失败: Stream key is not configured', but got: " + result);
        List<ILoggingEvent> logs = listAppender.list;
        assertTrue(logs.stream().anyMatch(event ->
                        event.getFormattedMessage().contains("获取Stream信息失败: Stream key is not configured")),
                "Expected log to contain '获取Stream信息失败: Stream key is not configured'");
    }

    @Test
    void testGetUserStreamKey_Indirect() {
        String recordId = "123-0";
        when(streamOperations.add(any())).thenReturn(RecordId.of(recordId));

        informProducer.sendCommentInform(targetUserId, fromUserId, postId, commentContent);

        verify(streamOperations, times(1)).add(argThat(record ->
                record.getStream().equals(informStreamKey + ":" + targetUserId)
        ));
    }
}