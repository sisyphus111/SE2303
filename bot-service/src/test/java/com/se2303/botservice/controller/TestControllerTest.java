package com.se2303.botservice.controller;

import com.se2303.botservice.service.AIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 测试 TestController 类
 */
class TestControllerTest {

    private TestController testController;
    private AIService aiService;

    @BeforeEach
    void setUp() {
        aiService = mock(AIService.class);
        testController = new TestController();
        testController.aiService = aiService;
    }

    @Test
    void testAlertServiceHealthSuccess() {
        when(aiService.checkAlertServiceHealth()).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = testController.testAlertServiceHealth();
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertTrue((Boolean) body.get("alertServiceHealthy"));
        assertEquals("Alert Service 连接正常", body.get("message"));
    }

    @Test
    void testAlertServiceHealthFailure() {
        when(aiService.checkAlertServiceHealth()).thenThrow(new RuntimeException("连接失败"));

        ResponseEntity<Map<String, Object>> response = testController.testAlertServiceHealth();
        assertEquals(500, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertFalse((Boolean) body.get("alertServiceHealthy"));
        assertTrue(body.get("message").toString().contains("测试 Alert Service 连接时出错"));
    }

    @Test
    void testSetTestUserSuccess() {
        String userId = "testUser";
        String sessionId = "testSession";
        doNothing().when(aiService).setCurrentUser(userId, sessionId);

        ResponseEntity<Map<String, Object>> response = testController.setTestUser(userId, sessionId);
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals("用户信息设置成功", body.get("message"));
        assertEquals(userId, body.get("userId"));
        assertEquals(sessionId, body.get("sessionId"));
    }

    @Test
    void testSetTestUserFailure() {
        String userId = "testUser";
        String sessionId = "testSession";
        doThrow(new RuntimeException("设置失败")).when(aiService).setCurrentUser(userId, sessionId);

        ResponseEntity<Map<String, Object>> response = testController.setTestUser(userId, sessionId);
        assertEquals(500, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertTrue(body.get("message").toString().contains("设置用户信息时出错"));
    }

    @Test
    void testGetConversationTimerSuccess() {
        when(aiService.getCurrentConversationDurationMinutes()).thenReturn(10L);

        ResponseEntity<Map<String, Object>> response = testController.getConversationTimer();
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals(10L, body.get("durationMinutes"));
        assertEquals("当前对话持续时间: 10 分钟", body.get("message"));
    }

    @Test
    void testGetConversationTimerFailure() {
        when(aiService.getCurrentConversationDurationMinutes()).thenThrow(new RuntimeException("计时器错误"));

        ResponseEntity<Map<String, Object>> response = testController.getConversationTimer();
        assertEquals(500, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertTrue(body.get("message").toString().contains("获取对话计时器信息时出错"));
    }

    @Test
    void testResetConversationTimerSuccess() {
        doNothing().when(aiService).resetConversationTimer();

        ResponseEntity<Map<String, Object>> response = testController.resetConversationTimer();
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals("对话计时器已重置", body.get("message"));
    }

    @Test
    void testResetConversationTimerFailure() {
        doThrow(new RuntimeException("重置失败")).when(aiService).resetConversationTimer();

        ResponseEntity<Map<String, Object>> response = testController.resetConversationTimer();
        assertEquals(500, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertTrue(body.get("message").toString().contains("重置对话计时器时出错"));
    }

    @Test
    void testDangerousContentSuccess() {
        String message = "测试消息";
        String userId = "testUser";
        String sessionId = "testSession";
        String aiResponse = "模拟响应";
        doNothing().when(aiService).setCurrentUser(userId, sessionId);
        when(aiService.generateMockResponse(message)).thenReturn(aiResponse);

        ResponseEntity<Map<String, Object>> response = testController.testDangerousContent(message, userId, sessionId);
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals(message, body.get("userMessage"));
        assertEquals(aiResponse, body.get("aiResponse"));
        assertEquals("危险内容检测测试完成", body.get("message"));
    }

    @Test
    void testDangerousContentFailure() {
        String message = "测试消息";
        String userId = "testUser";
        String sessionId = "testSession";
        doThrow(new RuntimeException("检测失败")).when(aiService).setCurrentUser(userId, sessionId);

        ResponseEntity<Map<String, Object>> response = testController.testDangerousContent(message, userId, sessionId);
        assertEquals(500, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertTrue(body.get("message").toString().contains("测试危险内容检测时出错"));
    }

    @Test
    void testTimeoutAlertSuccess() {
        String userId = "testUser";
        String sessionId = "testSession";
        long durationMinutes = 55L;
        doNothing().when(aiService).setCurrentUser(userId, sessionId);

        ResponseEntity<Map<String, Object>> response = testController.testTimeoutAlert(userId, sessionId, durationMinutes);
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals("AI超时警报测试完成", body.get("message"));
        assertEquals(userId, body.get("userId"));
        assertEquals(sessionId, body.get("sessionId"));
        assertEquals(durationMinutes, body.get("durationMinutes"));
    }

    @Test
    void testTimeoutAlertFailure() {
        String userId = "testUser";
        String sessionId = "testSession";
        long durationMinutes = 55L;
        doThrow(new RuntimeException("警报失败")).when(aiService).setCurrentUser(userId, sessionId);

        ResponseEntity<Map<String, Object>> response = testController.testTimeoutAlert(userId, sessionId, durationMinutes);
        assertEquals(500, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertTrue(body.get("message").toString().contains("测试AI超时警报时出错"));
    }

    @Test
    void testJsonFormatSuccess() {
        String json = "{\"test\": \"value\"}";
        when(aiService.testJsonFormat()).thenReturn(json);

        ResponseEntity<Map<String, Object>> response = testController.testJsonFormat();
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals(json, body.get("json"));
        assertEquals("JSON格式测试完成", body.get("message"));
    }

    @Test
    void testJsonFormatFailure() {
        when(aiService.testJsonFormat()).thenThrow(new RuntimeException("JSON 失败"));

        ResponseEntity<Map<String, Object>> response = testController.testJsonFormat();
        assertEquals(500, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertTrue(body.get("message").toString().contains("JSON格式测试失败"));
    }

    @Test
    void testHealth() {
        ResponseEntity<Map<String, Object>> response = testController.health();
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals("Bot Service 运行正常", body.get("message"));
        assertEquals("bot-service", body.get("service"));
    }
}