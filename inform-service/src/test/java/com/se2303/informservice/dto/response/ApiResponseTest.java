package com.se2303.informservice.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApiResponseTest {

    @Test
    void testDefaultConstructor() {
        ApiResponse<String> response = new ApiResponse<>();
        assertFalse(response.isSuccess(), "默认构造的 success 应为 false");
        assertNull(response.getMessage(), "默认构造的 message 应为 null");
        assertNull(response.getData(), "默认构造的 data 应为 null");
        assertEquals(0, response.getCode(), "默认构造的 code 应为 0");
    }

    @Test
    void testParameterizedConstructor() {
        String data = "test data";
        ApiResponse<String> response = new ApiResponse<>(true, "Success message", data, 200);

        assertTrue(response.isSuccess(), "success 应正确设置");
        assertEquals("Success message", response.getMessage(), "message 应正确设置");
        assertEquals(data, response.getData(), "data 应正确设置");
        assertEquals(200, response.getCode(), "code 应正确设置");
    }

    @Test
    void testStaticSuccessMethods() {
        String data = "test data";

        // 测试 success(T data)
        ApiResponse<String> response1 = ApiResponse.success(data);
        assertTrue(response1.isSuccess(), "success(T data) 应设置 success 为 true");
        assertEquals("test data", response1.getMessage(), "success(T data) 应设置默认 message");
        assertEquals(data, response1.getMessage(), "success(T data) 应设置 data");
        assertEquals(200, response1.getCode(), "success(T data) 应设置 code 为 200");

        // 测试 success(String message, T data)
        ApiResponse<String> response2 = ApiResponse.success("Custom message", data);
        assertTrue(response2.isSuccess(), "success(String, T) 应设置 success 为 true");
        assertEquals("Custom message", response2.getMessage(), "success(String, T) 应设置自定义 message");
        assertEquals(data, response2.getData(), "success(String, T) 应设置 data");
        assertEquals(200, response2.getCode(), "success(String, T) 应设置 code 为 200");

        // 测试 success(String message)
        ApiResponse<String> response3 = ApiResponse.success("Message only");
        assertTrue(response3.isSuccess(), "success(String) 应设置 success 为 true");
        assertEquals("Message only", response3.getMessage(), "success(String) 应设置自定义 message");
        assertNull(response3.getData(), "success(String) 应设置 data 为 null");
        assertEquals(200, response3.getCode(), "success(String) 应设置 code 为 200");
    }

    @Test
    void testStaticErrorMethods() {
        // 测试 error(String message)
        ApiResponse<String> response1 = ApiResponse.error("Error message");
        assertFalse(response1.isSuccess(), "error(String) 应设置 success 为 false");
        assertEquals("Error message", response1.getMessage(), "error(String) 应设置 message");
        assertNull(response1.getData(), "error(String) 应设置 data 为 null");
        assertEquals(400, response1.getCode(), "error(String) 应设置 code 为 400");

        // 测试 error(String message, int code)
        ApiResponse<String> response2 = ApiResponse.error("Custom error", 500);
        assertFalse(response2.isSuccess(), "error(String, int) 应设置 success 为 false");
        assertEquals("Custom error", response2.getMessage(), "error(String, int) 应设置 message");
        assertNull(response2.getData(), "error(String, int) 应设置 data 为 null");
        assertEquals(500, response2.getCode(), "error(String, int) 应设置自定义 code");
    }

    @Test
    void testGettersAndSetters() {
        ApiResponse<String> response = new ApiResponse<>();
        String data = "test data";
        response.setSuccess(true);
        response.setMessage("Test message");
        response.setData(data);
        response.setCode(200);

        assertTrue(response.isSuccess(), "getSuccess 应返回设置的 success");
        assertEquals("Test message", response.getMessage(), "getMessage 应返回设置的 message");
        assertEquals(data, response.getData(), "getData 应返回设置的 data");
        assertEquals(200, response.getCode(), "getCode 应返回设置的 code");
    }
}