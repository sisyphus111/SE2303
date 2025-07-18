package com.se2303.informservice.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InformTest {

    private Inform inform;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        inform = new Inform();
        now = LocalDateTime.now();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(inform.getCreatedAt(), "默认构造函数应设置 createdAt");
        assertFalse(inform.isRead(), "默认构造函数应设置 isRead 为 false");
        assertNull(inform.getId(), "默认构造函数应将 id 初始化为 null");
        assertNull(inform.getUserId(), "默认构造函数应将 userId 初始化为 null");
        assertNull(inform.getType(), "默认构造函数应将 type 初始化为 null");
        assertNull(inform.getContent(), "默认构造函数应将 content 初始化为 null");
        assertNull(inform.getRelatedId(), "默认构造函数应将 relatedId 初始化为 null");
        assertNull(inform.getFromUserId(), "默认构造函数应将 fromUserId 初始化为 null");
        assertNull(inform.getReadAt(), "默认构造函数应将 readAt 初始化为 null");
    }

    @Test
    void testParameterizedConstructor() {
        Long userId = 1L;
        InformType type = InformType.COMMENT;
        String content = "Test comment";

        Inform inform = new Inform(userId, type, content);

        assertEquals(userId, inform.getUserId(), "userId 应正确设置");
        assertEquals(type, inform.getType(), "type 应正确设置");
        assertEquals(content, inform.getContent(), "content 应正确设置");
        assertNotNull(inform.getCreatedAt(), "createdAt 应自动设置");
        assertFalse(inform.isRead(), "isRead 应为 false");
        assertNull(inform.getRelatedId(), "relatedId 应为 null");
        assertNull(inform.getFromUserId(), "fromUserId 应为 null");
        assertNull(inform.getReadAt(), "readAt 应为 null");
    }

    @Test
    void testGettersAndSetters() {
        Long id = 1L;
        Long userId = 2L;
        InformType type = InformType.LIKE;
        String content = "Test content";
        String relatedId = "post123";
        Long fromUserId = 3L;
        boolean isRead = true;
        LocalDateTime createdAt = now;
        LocalDateTime readAt = now.plusHours(1);

        inform.setId(id);
        inform.setUserId(userId);
        inform.setType(type);
        inform.setContent(content);
        inform.setRelatedId(relatedId);
        inform.setFromUserId(fromUserId);
        inform.setIsRead(isRead);
        inform.setCreatedAt(createdAt);
        inform.setReadAt(readAt);

        assertEquals(id, inform.getId(), "getId 应返回设置的 id");
        assertEquals(userId, inform.getUserId(), "getUserId 应返回设置的 userId");
        assertEquals(type, inform.getType(), "getType 应返回设置的 type");
        assertEquals(content, inform.getContent(), "getContent 应返回设置的 content");
        assertEquals(relatedId, inform.getRelatedId(), "getRelatedId 应返回设置的 relatedId");
        assertEquals(fromUserId, inform.getFromUserId(), "getFromUserId 应返回设置的 fromUserId");
        assertTrue(inform.getIsRead(), "getIsRead 应返回设置的 isRead");
        assertEquals(createdAt, inform.getCreatedAt(), "getCreatedAt 应返回设置的 createdAt");
        assertEquals(readAt, inform.getReadAt(), "getReadAt 应返回设置的 readAt");
    }

    @Test
    void testSetReadAndReadAt() {
        inform.setRead(true);
        assertTrue(inform.isRead(), "setRead(true) 应设置 isRead 为 true");
        assertNotNull(inform.getReadAt(), "setRead(true) 应自动设置 readAt");

        inform.setRead(false);
        assertFalse(inform.isRead(), "setRead(false) 应设置 isRead 为 false");
        assertNotNull(inform.getReadAt(), "setRead(false) 不应改变 readAt");

        inform.setIsRead(true);
        assertTrue(inform.getIsRead(), "setIsRead(true) 应设置 isRead 为 true");
        assertNotNull(inform.getReadAt(), "setIsRead(true) 应自动设置 readAt");

        inform.setIsRead(false);
        assertFalse(inform.getIsRead(), "setIsRead(false) 应设置 isRead 为 false");
        assertNotNull(inform.getReadAt(), "setIsRead(false) 不应改变 readAt");
    }

    @Test
    void testGetTitle() {
        inform.setType(InformType.COMMENT);
        assertEquals("新评论通知", inform.getTitle(), "COMMENT 类型应返回 '新评论通知'");

        inform.setType(InformType.LIKE);
        assertEquals("新点赞通知", inform.getTitle(), "LIKE 类型应返回 '新点赞通知'");

        inform.setType(null);
        assertEquals("通知", inform.getTitle(), "无类型时应返回 '通知'");
    }

    @Test
    void testGetDisplayContent() {
        inform.setType(InformType.COMMENT);
        inform.setContent("Test comment");
        inform.setFromUserId(1L);
        assertEquals("用户1 评论了你的帖子：Test comment", inform.getDisplayContent(null),
                "COMMENT 类型无用户名时应使用默认格式");

        inform.setType(InformType.LIKE);
        assertEquals("用户1 点赞了你的帖子", inform.getDisplayContent(null),
                "LIKE 类型无用户名时应使用默认格式");

        inform.setType(null);
        inform.setContent("System message");
        assertEquals("System message", inform.getDisplayContent(null),
                "其他类型应返回 content");

        assertEquals("System message",
                inform.getDisplayContent("testuser"), "COMMENT 类型应使用提供的用户名");
    }

    @Test
    void testGetRawContent() {
        inform.setContent("Test comment");
        assertEquals("Test comment", inform.getRawContent(), "getRawContent 应返回 content");
    }
}