package com.se2303.informservice.service;

import com.se2303.informservice.entity.Inform;
import com.se2303.informservice.repository.InformRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Clean version of InformStreamConsumerTest:
 * - No lenient()
 * - No doNothing(); use proper when(...).thenReturn(...)
 * - Use real MapRecord via StreamRecords.newRecord() instead of mocking
 * - Only stub what's actually used in each test
 */
@ExtendWith(MockitoExtension.class)
class InformStreamConsumerTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private InformRepository informRepository;

    @Mock
    private StreamOperations<String, Object, Object> streamOperations;

    @InjectMocks
    private InformStreamConsumer informStreamConsumer;

    private final String informStreamKey = "user_inform_stream";
    private final String consumerGroup = "inform_consumer_group";
    private final String consumerName = "inform_consumer";

    @BeforeEach
    void setUp() throws Exception {
        setField(informStreamConsumer, "informStreamKey", informStreamKey);
        setField(informStreamConsumer, "consumerGroup", consumerGroup);
        setField(informStreamConsumer, "consumerName", consumerName);
    }

    @Test
    void consumeInforms_NoStreams() {
        when(redisTemplate.keys(informStreamKey + ":*")).thenReturn(Collections.emptySet());

        assertDoesNotThrow(() -> informStreamConsumer.consumeInforms());

        verify(redisTemplate).keys(anyString());
        verifyNoInteractions(streamOperations, informRepository);
    }

    @Test
    void consumeInforms_SingleStreamSuccess() {
        String streamKey = informStreamKey + ":1";
        when(redisTemplate.keys(anyString())).thenReturn(Set.of(streamKey));
        when(redisTemplate.opsForStream()).thenReturn(streamOperations);

        MapRecord<String, Object, Object> record = realRecord(streamKey, "1", "COMMENT");
        when(streamOperations.read(any(Consumer.class), any(StreamReadOptions.class), any(StreamOffset.class)))
                .thenReturn(List.of(record));
        when(streamOperations.createGroup(anyString(), any(ReadOffset.class), anyString())).thenReturn("OK");

        assertDoesNotThrow(() -> informStreamConsumer.consumeInforms());

        verify(informRepository).save(any(Inform.class));
    }

    @Test
    void consumeInforms_ExceptionHandling() {
        when(redisTemplate.keys(anyString())).thenThrow(new RuntimeException("Redis error"));

        assertDoesNotThrow(() -> informStreamConsumer.consumeInforms());

        verifyNoInteractions(streamOperations, informRepository);
    }

    @Test
    void processInformMessage_UnsupportedType() {
        MapRecord<String, Object, Object> record = realRecord("stream", "1", "INVALID_TYPE");

        assertDoesNotThrow(() -> informStreamConsumer.processInformMessage(record, "stream"));

        verifyNoInteractions(informRepository);
    }

    @Test
    void processInformMessage_DateParseFailure() {
        MapRecord<String, Object, Object> record = realRecord("stream", "1", "LIKE");

        // corrupt createdAt
        Map<Object, Object> badMap = new HashMap<>(record.getValue());
        badMap.put("createdAt", "invalid_date");
        MapRecord<String, Object, Object> brokenRecord = StreamRecords.newRecord()
                .in(record.getStream())
                .withId(record.getId())
                .ofMap(badMap);

        assertDoesNotThrow(() -> informStreamConsumer.processInformMessage(brokenRecord, "stream"));

        verify(informRepository, atMostOnce()).save(any());
    }

    @Test
    void consumeUserInforms_Success() {
        String streamKey = informStreamKey + ":100";
        when(redisTemplate.opsForStream()).thenReturn(streamOperations);
        when(streamOperations.createGroup(anyString(), any(ReadOffset.class), anyString())).thenReturn("OK");

        MapRecord<String, Object, Object> record = realRecord(streamKey, "100", "COMMENT");
        when(streamOperations.read(any(Consumer.class), any(StreamReadOptions.class), any(StreamOffset.class)))
                .thenReturn(List.of(record));

        assertDoesNotThrow(() -> informStreamConsumer.consumeUserInforms(100L));

        verify(informRepository).save(any(Inform.class));

    }

    // helper to build real MapRecord
    private MapRecord<String, Object, Object> realRecord(String streamKey, String userId, String type) {
        Map<Object, Object> valueMap = new HashMap<>();
        valueMap.put("type", type);
        valueMap.put("userId", userId);
        valueMap.put("relatedId", "post_123");
        valueMap.put("fromUserId", "456");
        valueMap.put("content", "Great post!");
        valueMap.put("createdAt", LocalDateTime.now().toString());

        return StreamRecords.newRecord()
                .in(streamKey)
                .ofMap(valueMap)
                .withId(RecordId.of(UUID.randomUUID().toString()));
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }
}