package com.se2303.botservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "ai.prompt")
public class AIPromptConfig {
    
    private String system;
    private Map<String, String> scenarios = new HashMap<>();
    private Map<String, String> personalities = new HashMap<>();
    
    public AIPromptConfig() {
        // 初始化默认场景提示词
        scenarios.put("general", "你是一个专业的心理健康陪伴助手，名叫'小暖'。你的主要职责是提供温暖、理解和支持的对话。");
        scenarios.put("crisis", "你是危机干预助手，当用户表达自杀倾向或严重心理危机时，请立即提供专业帮助建议和热线电话。");
        scenarios.put("academic", "你是学习压力疏导助手，专门帮助学生处理学习压力、考试焦虑等问题。");
        scenarios.put("relationship", "你是人际关系咨询助手，帮助用户处理朋友、家人、恋人之间的关系问题。");
        
        // 初始化不同性格的提示词
        personalities.put("warm", "你是一个温暖、善解人意的陪伴者，总是用温柔的语气和用户交流。");
        personalities.put("professional", "你是一个专业、理性的心理咨询助手，提供客观的建议和指导。");
        personalities.put("encouraging", "你是一个积极、鼓励性的助手，总是给用户正面的支持和鼓励。");
        personalities.put("empathetic", "你是一个富有同理心的助手，能够深刻理解用户的感受和情绪。");
    }
    
    public String getSystem() {
        return system;
    }
    
    public void setSystem(String system) {
        this.system = system;
    }
    
    public Map<String, String> getScenarios() {
        return scenarios;
    }
    
    public void setScenarios(Map<String, String> scenarios) {
        this.scenarios = scenarios;
    }
    
    public Map<String, String> getPersonalities() {
        return personalities;
    }
    
    public void setPersonalities(Map<String, String> personalities) {
        this.personalities = personalities;
    }
    
    /**
     * 获取完整的系统提示词
     * @param scenario 场景类型
     * @param personality 性格类型
     * @return 完整的提示词
     */
    public String getFullPrompt(String scenario, String personality) {
        StringBuilder prompt = new StringBuilder();
        
        // 添加基础系统提示词
        if (system != null && !system.trim().isEmpty()) {
            prompt.append(system).append("\n\n");
        }
        
        // 添加场景特定提示词
        if (scenario != null && scenarios.containsKey(scenario)) {
            prompt.append(scenarios.get(scenario)).append("\n\n");
        }
        
        // 添加性格特定提示词
        if (personality != null && personalities.containsKey(personality)) {
            prompt.append(personalities.get(personality)).append("\n\n");
        }
        
        // 添加通用指导原则
        prompt.append("请记住：你是一个陪伴者，不是医生。对于严重的心理健康问题，请建议用户寻求专业帮助。");
        
        return prompt.toString();
    }
    
    /**
     * 获取默认提示词
     * @return 默认提示词
     */
    public String getDefaultPrompt() {
        return getFullPrompt("general", "warm");
    }
} 