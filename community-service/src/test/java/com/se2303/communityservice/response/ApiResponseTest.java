package com.se2303.communityservice.response;

import com.se2303.communityservice.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    private ApiResponse apiResponse;
    private final boolean testSuccess = true;
    private final Object testObject = new Object();
    private final String testMessage = "Test message";

    @BeforeEach
    void setUp() {
        // 在每个测试之前初始化 ApiResponse 实例
        apiResponse = new ApiResponse(testSuccess, testObject, testMessage);
    }

    @Test
    void testConstructorAndGetters() {
        // 测试构造方法和 getter 方法
        assertTrue(apiResponse.isSuccess(), "Success 字段应为 true");
        assertEquals(testObject, apiResponse.getObject(), "Object 字段应与构造方法传入的值相同");
        assertEquals(testMessage, apiResponse.getMessage(), "Message 字段应与构造方法传入的值相同");
    }

    @Test
    void testSetObject() {
        // 测试 setObject 方法
        Object newObject = new Object();
        apiResponse.setObject(newObject);
        assertEquals(newObject, apiResponse.getObject(), "Object 字段应更新为新值");
    }

    @Test
    void testConstructorWithNullValues() {
        // 测试构造方法处理 null 值
        ApiResponse nullResponse = new ApiResponse(false, null, null);
        assertFalse(nullResponse.isSuccess(), "Success 字段应为 false");
        assertNull(nullResponse.getObject(), "Object 字段应为 null");
        assertNull(nullResponse.getMessage(), "Message 字段应为 null");
    }

    @Test
    void testIsSuccessWithFalse() {
        // 测试 success 字段为 false 的情况
        ApiResponse falseResponse = new ApiResponse(false, testObject, testMessage);
        assertFalse(falseResponse.isSuccess(), "Success 字段应为 false");
    }

    @Test
    void testGetMessageWithEmptyString() {
        // 测试 message 字段为空字符串的情况
        ApiResponse emptyMessageResponse = new ApiResponse(true, testObject, "");
        assertEquals("", emptyMessageResponse.getMessage(), "Message 字段应为空字符串");
    }
}