package com.se2303.botservice.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 AIPromptConfig 类
 */
class AIPromptConfigTest {

    private AIPromptConfig promptConfig;

    @BeforeEach
    void setUp() {
        promptConfig = new AIPromptConfig();
    }

    @Test
    void testDefaultConstructor() {
        // 测试默认构造函数是否正确初始化场景和性格
        assertNotNull(promptConfig.getScenarios(), "场景映射不应为 null");
        assertNotNull(promptConfig.getPersonalities(), "性格映射不应为 null");
        assertEquals(4, promptConfig.getScenarios().size(), "默认场景数量应为 4");
        assertEquals(4, promptConfig.getPersonalities().size(), "默认性格数量应为 4");
        assertTrue(promptConfig.getScenarios().containsKey("general"), "应包含 general 场景");
        assertTrue(promptConfig.getPersonalities().containsKey("warm"), "应包含 warm 性格");
    }

    @Test
    void testGetAndSetSystem() {
        // 测试 system 属性的 getter 和 setter
        String systemPrompt = "测试系统提示词";
        promptConfig.setSystem(systemPrompt);
        assertEquals(systemPrompt, promptConfig.getSystem(), "系统提示词应匹配");
    }

    @Test
    void testGetAndSetScenarios() {
        // 测试 scenarios 属性的 getter 和 setter
        Map<String, String> newScenarios = new HashMap<>();
        newScenarios.put("test", "测试场景提示词");
        promptConfig.setScenarios(newScenarios);
        assertEquals(newScenarios, promptConfig.getScenarios(), "场景映射应匹配");
    }

    @Test
    void testGetAndSetPersonalities() {
        // 测试 personalities 属性的 getter 和 setter
        Map<String, String> newPersonalities = new HashMap<>();
        newPersonalities.put("test", "测试性格提示词");
        promptConfig.setPersonalities(newPersonalities);
        assertEquals(newPersonalities, promptConfig.getPersonalities(), "性格映射应匹配");
    }

    @Test
    void testGetFullPromptWithValidInputs() {
        // 测试有效场景和性格的完整提示词
        String systemPrompt = "系统提示词";
        promptConfig.setSystem(systemPrompt);
        String fullPrompt = promptConfig.getFullPrompt("general", "warm");
        assertTrue(fullPrompt.contains(systemPrompt), "完整提示词应包含系统提示词");
        assertTrue(fullPrompt.contains(promptConfig.getScenarios().get("general")), "完整提示词应包含 general 场景");
        assertTrue(fullPrompt.contains(promptConfig.getPersonalities().get("warm")), "完整提示词应包含 warm 性格");
        assertTrue(fullPrompt.contains("请记住：你是一个陪伴者"), "完整提示词应包含通用指导原则");
    }

    @Test
    void testGetFullPromptWithNullInputs() {
        // 测试 null 场景和性格
        String systemPrompt = "系统提示词";
        promptConfig.setSystem(systemPrompt);
        String fullPrompt = promptConfig.getFullPrompt(null, null);
        assertTrue(fullPrompt.contains(systemPrompt), "完整提示词应包含系统提示词");
        assertTrue(fullPrompt.contains("请记住：你是一个陪伴者"), "完整提示词应包含通用指导原则");
        assertFalse(fullPrompt.contains(promptConfig.getScenarios().get("general")), "不应包含场景提示词");
        assertFalse(fullPrompt.contains(promptConfig.getPersonalities().get("warm")), "不应包含性格提示词");
    }

    @Test
    void testGetDefaultPrompt() {
        // 测试默认提示词
        String defaultPrompt = promptConfig.getDefaultPrompt();
        assertTrue(defaultPrompt.contains(promptConfig.getScenarios().get("general")), "默认提示词应包含 general 场景");
        assertTrue(defaultPrompt.contains(promptConfig.getPersonalities().get("warm")), "默认提示词应包含 warm 性格");
        assertTrue(defaultPrompt.contains("请记住：你是一个陪伴者"), "默认提示词应包含通用指导原则");
    }

    @Test
    void testGetFullPromptWithInvalidScenarioAndPersonality() {
        // 测试无效场景和性格
        String systemPrompt = "系统提示词";
        promptConfig.setSystem(systemPrompt);
        String fullPrompt = promptConfig.getFullPrompt("invalid", "invalid");
        assertTrue(fullPrompt.contains(systemPrompt), "完整提示词应包含系统提示词");
        assertTrue(fullPrompt.contains("请记住：你是一个陪伴者"), "完整提示词应包含通用指导原则");
        assertFalse(fullPrompt.contains(promptConfig.getScenarios().get("general")), "不应包含 general 场景");
        assertFalse(fullPrompt.contains(promptConfig.getPersonalities().get("warm")), "不应包含 warm 性格");
    }

    @Test
    void testAnnotations() {
        // 测试类的注解
        assertTrue(AIPromptConfig.class.isAnnotationPresent(Component.class), "应包含 @Component 注解");
        assertTrue(AIPromptConfig.class.isAnnotationPresent(ConfigurationProperties.class), "应包含 @ConfigurationProperties 注解");
        assertEquals("ai.prompt", AIPromptConfig.class.getAnnotation(ConfigurationProperties.class).prefix(), "ConfigurationProperties 前缀应为 ai.prompt");
    }
}