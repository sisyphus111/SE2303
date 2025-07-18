package com.se2303.botservice.controller;

import com.se2303.botservice.config.AIPromptConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 测试 PromptController 类
 */
class PromptControllerTest {

    private PromptController promptController;
    private AIPromptConfig promptConfig;

    @BeforeEach
    void setUp() {
        promptConfig = mock(AIPromptConfig.class);
        promptController = new PromptController();
        promptController.promptConfig = promptConfig;
    }

    @Test
    void testGetScenarios() {
        // 测试获取所有场景
        Map<String, String> scenarios = new HashMap<>();
        scenarios.put("general", "通用场景");
        when(promptConfig.getScenarios()).thenReturn(scenarios);

        ResponseEntity<Map<String, String>> response = promptController.getScenarios();
        assertEquals(200, response.getStatusCodeValue(), "状态码应为 200");
        assertEquals(scenarios, response.getBody(), "场景映射应匹配");
    }

    @Test
    void testGetPersonalities() {
        // 测试获取所有性格
        Map<String, String> personalities = new HashMap<>();
        personalities.put("warm", "温暖性格");
        when(promptConfig.getPersonalities()).thenReturn(personalities);

        ResponseEntity<Map<String, String>> response = promptController.getPersonalities();
        assertEquals(200, response.getStatusCodeValue(), "状态码应为 200");
        assertEquals(personalities, response.getBody(), "性格映射应匹配");
    }

    @Test
    void testGetPromptConfig() {
        // 测试获取完整配置
        Map<String, String> scenarios = new HashMap<>();
        Map<String, String> personalities = new HashMap<>();
        String systemPrompt = "系统提示词";
        String defaultPrompt = "默认提示词";
        when(promptConfig.getSystem()).thenReturn(systemPrompt);
        when(promptConfig.getScenarios()).thenReturn(scenarios);
        when(promptConfig.getPersonalities()).thenReturn(personalities);
        when(promptConfig.getDefaultPrompt()).thenReturn(defaultPrompt);

        ResponseEntity<Map<String, Object>> response = promptController.getPromptConfig();
        assertEquals(200, response.getStatusCodeValue(), "状态码应为 200");
        Map<String, Object> body = response.getBody();
        assertEquals(systemPrompt, body.get("system"), "系统提示词应匹配");
        assertEquals(scenarios, body.get("scenarios"), "场景应匹配");
        assertEquals(personalities, body.get("personalities"), "性格应匹配");
        assertEquals(defaultPrompt, body.get("default"), "默认提示词应匹配");
    }

    @Test
    void testGetFullPrompt() {
        // 测试获取完整提示词
        String fullPrompt = "完整提示词";
        when(promptConfig.getFullPrompt("general", "warm")).thenReturn(fullPrompt);

        ResponseEntity<Map<String, String>> response = promptController.getFullPrompt("general", "warm");
        assertEquals(200, response.getStatusCodeValue(), "状态码应为 200");
        Map<String, String> body = response.getBody();
        assertEquals(fullPrompt, body.get("prompt"), "提示词应匹配");
        assertEquals("general", body.get("scenario"), "场景应匹配");
        assertEquals("warm", body.get("personality"), "性格应匹配");
    }

    @Test
    void testGetFullPromptWithNullParams() {
        // 测试 null 参数的完整提示词
        String fullPrompt = "默认提示词";
        when(promptConfig.getFullPrompt(null, null)).thenReturn(fullPrompt);

        ResponseEntity<Map<String, String>> response = promptController.getFullPrompt(null, null);
        assertEquals(200, response.getStatusCodeValue(), "状态码应为 200");
        Map<String, String> body = response.getBody();
        assertEquals(fullPrompt, body.get("prompt"), "提示词应匹配");
        assertEquals("默认", body.get("scenario"), "场景应为默认");
        assertEquals("默认", body.get("personality"), "性格应为默认");
    }

    @Test
    void testGetDefaultPrompt() {
        // 测试默认提示词
        String defaultPrompt = "默认提示词";
        when(promptConfig.getDefaultPrompt()).thenReturn(defaultPrompt);

        ResponseEntity<Map<String, String>> response = promptController.getDefaultPrompt();
        assertEquals(200, response.getStatusCodeValue(), "状态码应为 200");
        assertEquals(defaultPrompt, response.getBody().get("prompt"), "默认提示词应匹配");
    }

    @Test
    void testUpdateSystemPromptSuccess() {
        // 测试成功更新系统提示词
        Map<String, String> request = new HashMap<>();
        request.put("prompt", "新系统提示词");

        ResponseEntity<Map<String, String>> response = promptController.updateSystemPrompt(request);
        assertEquals(200, response.getStatusCodeValue(), "状态码应为 200");
        Map<String, String> body = response.getBody();
        assertEquals("系统提示词更新成功", body.get("message"), "消息应匹配");
        assertEquals("新系统提示词", body.get("prompt"), "提示词应匹配");
        verify(promptConfig).setSystem("新系统提示词");
    }

    @Test
    void testUpdateSystemPromptEmpty() {
        // 测试空提示词更新
        Map<String, String> request = new HashMap<>();
        request.put("prompt", "");

        ResponseEntity<Map<String, String>> response = promptController.updateSystemPrompt(request);
        assertEquals(400, response.getStatusCodeValue(), "状态码应为 400");
        assertEquals("提示词不能为空", response.getBody().get("error"), "错误消息应匹配");
    }

    @Test
    void testAddScenarioSuccess() {
        // 测试成功添加场景
        Map<String, String> request = new HashMap<>();
        request.put("name", "newScenario");
        request.put("prompt", "新场景提示词");
        when(promptConfig.getScenarios()).thenReturn(new HashMap<>());

        ResponseEntity<Map<String, String>> response = promptController.addScenario(request);
        assertEquals(200, response.getStatusCodeValue(), "状态码应为 200");
        Map<String, String> body = response.getBody();
        assertEquals("场景提示词添加成功", body.get("message"), "消息应匹配");
        assertEquals("newScenario", body.get("name"), "场景名称应匹配");
    }

    @Test
    void testAddScenarioEmpty() {
        // 测试空场景添加
        Map<String, String> request = new HashMap<>();
        request.put("name", "");
        request.put("prompt", "");

        ResponseEntity<Map<String, String>> response = promptController.addScenario(request);
        assertEquals(400, response.getStatusCodeValue(), "状态码应为 400");
        assertEquals("场景名称和提示词不能为空", response.getBody().get("error"), "错误消息应匹配");
    }

    @Test
    void testAddPersonalitySuccess() {
        // 测试成功添加性格
        Map<String, String> request = new HashMap<>();
        request.put("name", "newPersonality");
        request.put("prompt", "新性格提示词");
        when(promptConfig.getPersonalities()).thenReturn(new HashMap<>());

        ResponseEntity<Map<String, String>> response = promptController.addPersonality(request);
        assertEquals(200, response.getStatusCodeValue(), "状态码应为 200");
        Map<String, String> body = response.getBody();
        assertEquals("性格提示词添加成功", body.get("message"), "消息应匹配");
        assertEquals("newPersonality", body.get("name"), "性格名称应匹配");
    }

    @Test
    void testAddPersonalityEmpty() {
        // 测试空性格添加
        Map<String, String> request = new HashMap<>();
        request.put("name", "");
        request.put("prompt", "");

        ResponseEntity<Map<String, String>> response = promptController.addPersonality(request);
        assertEquals(400, response.getStatusCodeValue(), "状态码应为 400");
        assertEquals("性格名称和提示词不能为空", response.getBody().get("error"), "错误消息应匹配");
    }
}