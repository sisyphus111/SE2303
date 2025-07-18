package com.se2303.informservice.dto.response;

import com.se2303.informservice.entity.Inform;
import com.se2303.informservice.entity.InformType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InformResponseTest {

    @Test
    void testDefaultConstructor() {
        InformResponse response = new InformResponse();
        assertNull(response.getId(), "默认构造的 id 应为 null");
        assertNull(response.getUserId(), "默认构造的 userId 应为 null");
        assertNull(response.getType(), "默认构造的 type 应为 null");
        assertNull(response.getTitle(), "默认构造的 title 应为 null");
        assertNull(response.getContent(), "默认构造的 content 应为 null");
        assertNull(response.getDisplayContent(), "默认构造的 displayContent 应为 null");
        assertNull(response.getRelatedId(), "默认构造的 relatedId 应为 null");
        assertNull(response.getFromUserId(), "默认构造的 fromUserId 应为 null");
        assertNull(response.getFromUsername(), "默认构造的 fromUsername 应为 null");
        assertFalse(response.isRead(), "默认构造的 read 应为 false");
        assertNull(response.getCreatedAt(), "默认构造的 createdAt 应为 null");
        assertNull(response.getReadAt(), "默认构造的 readAt 应为 null");
    }

    @Test
    void testInformConstructor() {
        Inform inform = mock(Inform.class);
        when(inform.getId()).thenReturn(1L);
        when(inform.getUserId()).thenReturn(2L);
        when(inform.getType()).thenReturn(InformType.COMMENT);
        when(inform.getTitle()).thenReturn("Test Title");
        when(inform.getRawContent()).thenReturn("Test comment");
        when(inform.getRelatedId()).thenReturn("post123");
        when(inform.getFromUserId()).thenReturn(3L);
        when(inform.isRead()).thenReturn(true);
        when(inform.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(inform.getReadAt()).thenReturn(LocalDateTime.now());

        InformResponse response = new InformResponse(inform);

        assertEquals(1L, response.getId(), "id 应与 inform 的 id 一致");
        assertEquals(2L, response.getUserId(), "userId 应与 inform 的 userId 一致");
        assertEquals(InformType.COMMENT, response.getType(), "type 应与 inform 的 type 一致");
        assertEquals("Test Title", response.getTitle(), "title 应与 inform 的 title 一致");
        assertEquals("Test comment", response.getContent(), "content 应与 inform 的 rawContent 一致");
        assertEquals("post123", response.getRelatedId(), "relatedId 应与 inform 的 relatedId 一致");
        assertEquals(3L, response.getFromUserId(), "fromUserId 应与 inform 的 fromUserId 一致");
        assertTrue(response.isRead(), "read 应与 inform 的 read 一致");
        assertEquals(inform.getCreatedAt(), response.getCreatedAt(), "createdAt 应与 inform 的 createdAt 一致");
        assertEquals(inform.getReadAt(), response.getReadAt(), "readAt 应与 inform 的 readAt 一致");
        assertNull(response.getFromUsername(), "fromUsername 应为 null");
        assertNull(response.getDisplayContent(), "displayContent 应为 null");
    }

    @Test
    void testSetFromUsernameAndGenerateDisplayForComment() {
        InformResponse response = new InformResponse();
        response.setType(InformType.COMMENT);
        response.setContent("Test comment");
        response.setFromUsernameAndGenerateDisplay("user1");

        assertEquals("user1", response.getFromUsername(), "fromUsername 应正确设置");
        assertEquals("user1 评论了你的帖子：Test comment", response.getDisplayContent(),
                "COMMENT 类型的 displayContent 应正确生成");
    }

    @Test
    void testSetFromUsernameAndGenerateDisplayForLike() {
        InformResponse response = new InformResponse();
        response.setType(InformType.LIKE);
        response.setFromUsernameAndGenerateDisplay("user1");

        assertEquals("user1", response.getFromUsername(), "fromUsername 应正确设置");
        assertEquals("user1 点赞了你的帖子", response.getDisplayContent(),
                "LIKE 类型的 displayContent 应正确生成");
    }

    @Test
    void testSetFromUsernameAndGenerateDisplayForOtherType() {
        InformResponse response = new InformResponse();
        response.setType(InformType.COMMENT); // 假设有 SYSTEM 类型
        response.setContent("System message");
        response.setFromUsernameAndGenerateDisplay("admin");

        assertEquals("admin", response.getFromUsername(), "fromUsername 应正确设置");
        assertEquals("admin 评论了你的帖子：System message", response.getDisplayContent(),
                "其他类型的 displayContent 应等于 content");
    }

    @Test
    void testGettersAndSetters() {
        InformResponse response = new InformResponse();
        Long id = 1L;
        Long userId = 2L;
        InformType type = InformType.COMMENT;
        String title = "Test Title";
        String content = "Test content";
        String displayContent = "Display content";
        String relatedId = "post123";
        Long fromUserId = 3L;
        String fromUsername = "user1";
        boolean read = true;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime readAt = LocalDateTime.now();

        response.setId(id);
        response.setUserId(userId);
        response.setType(type);
        response.setTitle(title);
        response.setContent(content);
        response.setDisplayContent(displayContent);
        response.setRelatedId(relatedId);
        response.setFromUserId(fromUserId);
        response.setFromUsername(fromUsername);
        response.setRead(read);
        response.setCreatedAt(createdAt);
        response.setReadAt(readAt);

        assertEquals(id, response.getId(), "getId 应返回设置的 id");
        assertEquals(userId, response.getUserId(), "getUserId 应返回设置的 userId");
        assertEquals(type, response.getType(), "getType 应返回设置的 type");
        assertEquals(title, response.getTitle(), "getTitle 应返回设置的 title");
        assertEquals(content, response.getContent(), "getContent 应返回设置的 content");
        assertEquals(displayContent, response.getDisplayContent(), "getDisplayContent 应返回设置的 displayContent");
        assertEquals(relatedId, response.getRelatedId(), "getRelatedId 应返回设置的 relatedId");
        assertEquals(fromUserId, response.getFromUserId(), "getFromUserId 应返回设置的 fromUserId");
        assertEquals(fromUsername, response.getFromUsername(), "getFromUsername 应返回设置的 fromUsername");
        assertTrue(response.isRead(), "getRead 应返回设置的 read");
        assertEquals(createdAt, response.getCreatedAt(), "getCreatedAt 应返回设置的 createdAt");
        assertEquals(readAt, response.getReadAt(), "getReadAt 应返回设置的 readAt");
    }
}