package com.se2303.informservice.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InformTypeTest {

    @Test
    void testEnumValues() {
        assertEquals(2, InformType.values().length, "InformType 应包含 2 个枚举值");
        assertEquals(InformType.COMMENT, InformType.valueOf("COMMENT"), "应能通过名称获取 COMMENT");
        assertEquals(InformType.LIKE, InformType.valueOf("LIKE"), "应能通过名称获取 LIKE");
    }

    @Test
    void testGetDescription() {
        assertEquals("评论", InformType.COMMENT.getDescription(), "COMMENT 的描述应为 '评论'");
        assertEquals("点赞", InformType.LIKE.getDescription(), "LIKE 的描述应为 '点赞'");
    }
}