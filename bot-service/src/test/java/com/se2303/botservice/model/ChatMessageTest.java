package com.se2303.botservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 ChatMessage 类
 */
class ChatMessageTest {

    @Test
    void testDefaultConstructor() {
        // 测试默认构造函数
        ChatMessage message = new ChatMessage();
        assertNull(message.getId(), "ID 应为 null");
        assertNull(message.getType(), "类型应为 null");
        assertNull(message.getRole(), "角色应为 null");
        assertNull(message.getContent(), "内容应为 null");
        assertEquals(0, message.getTimestamp(), "时间戳应为 0");
    }

    @Test
    void testPartialConstructor() {
        // 测试部分构造函数
        ChatMessage message = new ChatMessage("user", "测试消息", REDACTEDL);
        assertNull(message.getId(), "ID 应为 null");
        assertEquals("user", message.getType(), "类型应匹配");
        assertNull(message.getRole(), "角色应为 null");
        assertEquals("测试消息", message.getContent(), "内容应匹配");
        assertEquals(REDACTEDL, message.getTimestamp(), "时间戳应匹配");
    }

    @Test
    void testFullConstructor() {
        // 测试完整构造函数
        ChatMessage message = new ChatMessage("msg1", "ai", "assistant", "AI 响应", REDACTEDL);
        assertEquals("msg1", message.getId(), "ID 应匹配");
        assertEquals("ai", message.getType(), "类型应匹配");
        assertEquals("assistant", message.getRole(), "角色应匹配");
        assertEquals("AI 响应", message.getContent(), "内容应匹配");
        assertEquals(REDACTEDL, message.getTimestamp(), "时间戳应匹配");
    }

    @Test
    void testGettersAndSetters() {
        // 测试 getter 和 setter
        ChatMessage message = new ChatMessage();
        message.setId("msg1");
        message.setType("user");
        message.setRole("user");
        message.setContent("测试消息");
        message.setTimestamp(REDACTEDL);

        assertEquals("msg1", message.getId(), "ID 应匹配");
        assertEquals("user", message.getType(), "类型应匹配");
        assertEquals("user", message.getRole(), "角色应匹配");
        assertEquals("测试消息", message.getContent(), "内容应匹配");
        assertEquals(REDACTEDL, message.getTimestamp(), "时间戳应匹配");
    }

    @Test
    void testToString() {
        // 测试 toString 方法
        ChatMessage message = new ChatMessage("msg1", "ai", "assistant", "AI 响应", REDACTEDL);
        String toString = message.toString();
        assertTrue(toString.contains("msg1"), "toString 应包含 ID");
        assertTrue(toString.contains("ai"), "toString 应包含类型");
        assertTrue(toString.contains("assistant"), "toString 应包含角色");
        assertTrue(toString.contains("AI 响应"), "toString 应包含内容");
        assertTrue(toString.contains("REDACTED"), "toString 应包含时间戳");
    }
}