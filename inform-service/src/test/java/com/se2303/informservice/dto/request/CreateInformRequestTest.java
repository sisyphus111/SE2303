package com.se2303.informservice.dto.request;

import com.se2303.informservice.entity.InformType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateInformRequestTest {

    @Test
    void testDefaultConstructor() {
        CreateInformRequest request = new CreateInformRequest();
        assertNull(request.getUserId(), "默认构造函数应将 userId 初始化为 null");
        assertNull(request.getType(), "默认构造函数应将 type 初始化为 null");
        assertNull(request.getContent(), "默认构造函数应将 content 初始化为 null");
        assertNull(request.getRelatedId(), "默认构造函数应将 relatedId 初始化为 null");
        assertNull(request.getFromUserId(), "默认构造函数应将 fromUserId 初始化为 null");
    }

    @Test
    void testParameterizedConstructor() {
        Long userId = 1L;
        InformType type = InformType.COMMENT;
        String content = "Test content";
        String relatedId = "post123";
        Long fromUserId = 2L;

        CreateInformRequest request = new CreateInformRequest(userId, type, content, relatedId, fromUserId);

        assertEquals(userId, request.getUserId(), "userId 应正确设置");
        assertEquals(type, request.getType(), "type 应正确设置");
        assertEquals(content, request.getContent(), "content 应正确设置");
        assertEquals(relatedId, request.getRelatedId(), "relatedId 应正确设置");
        assertEquals(fromUserId, request.getFromUserId(), "fromUserId 应正确设置");
    }

    @Test
    void testGettersAndSetters() {
        CreateInformRequest request = new CreateInformRequest();
        Long userId = 1L;
        InformType type = InformType.LIKE;
        String content = "Test content";
        String relatedId = "post123";
        Long fromUserId = 2L;

        request.setUserId(userId);
        request.setType(type);
        request.setContent(content);
        request.setRelatedId(relatedId);
        request.setFromUserId(fromUserId);

        assertEquals(userId, request.getUserId(), "getUserId 应返回设置的 userId");
        assertEquals(type, request.getType(), "getType 应返回设置的 type");
        assertEquals(content, request.getContent(), "getContent 应返回设置的 content");
        assertEquals(relatedId, request.getRelatedId(), "getRelatedId 应返回设置的 relatedId");
        assertEquals(fromUserId, request.getFromUserId(), "getFromUserId 应返回设置的 fromUserId");
    }
}