package com.se2303.botservice.controller;

import com.se2303.botservice.config.AIPromptConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/prompt")
@CrossOrigin(origins = "*")
public class PromptController {

    @Autowired
    public AIPromptConfig promptConfig;

    /**
     * 获取所有可用的场景类型
     */
    @GetMapping("/scenarios")
    public ResponseEntity<Map<String, String>> getScenarios() {
        return ResponseEntity.ok(promptConfig.getScenarios());
    }

    /**
     * 获取所有可用的性格类型
     */
    @GetMapping("/personalities")
    public ResponseEntity<Map<String, String>> getPersonalities() {
        return ResponseEntity.ok(promptConfig.getPersonalities());
    }

    /**
     * 获取完整的提示词配置信息
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getPromptConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("system", promptConfig.getSystem());
        config.put("scenarios", promptConfig.getScenarios());
        config.put("personalities", promptConfig.getPersonalities());
        config.put("default", promptConfig.getDefaultPrompt());
        return ResponseEntity.ok(config);
    }

    /**
     * 根据场景和性格获取完整的提示词
     */
    @GetMapping("/full")
    public ResponseEntity<Map<String, String>> getFullPrompt(
            @RequestParam(required = false) String scenario,
            @RequestParam(required = false) String personality) {
        
        String fullPrompt = promptConfig.getFullPrompt(scenario, personality);
        Map<String, String> response = new HashMap<>();
        response.put("prompt", fullPrompt);
        response.put("scenario", scenario != null ? scenario : "默认");
        response.put("personality", personality != null ? personality : "默认");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取默认提示词
     */
    @GetMapping("/default")
    public ResponseEntity<Map<String, String>> getDefaultPrompt() {
        String defaultPrompt = promptConfig.getDefaultPrompt();
        Map<String, String> response = new HashMap<>();
        response.put("prompt", defaultPrompt);
        return ResponseEntity.ok(response);
    }

    /**
     * 更新系统提示词
     */
    @PutMapping("/system")
    public ResponseEntity<Map<String, String>> updateSystemPrompt(@RequestBody Map<String, String> request) {
        String newSystemPrompt = request.get("prompt");
        if (newSystemPrompt != null && !newSystemPrompt.trim().isEmpty()) {
            promptConfig.setSystem(newSystemPrompt);
            Map<String, String> response = new HashMap<>();
            response.put("message", "系统提示词更新成功");
            response.put("prompt", newSystemPrompt);
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "提示词不能为空");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 添加新的场景提示词
     */
    @PostMapping("/scenarios")
    public ResponseEntity<Map<String, String>> addScenario(
            @RequestBody Map<String, String> request) {
        
        String name = request.get("name");
        String prompt = request.get("prompt");
        
        if (name != null && prompt != null && !name.trim().isEmpty() && !prompt.trim().isEmpty()) {
            promptConfig.getScenarios().put(name, prompt);
            Map<String, String> response = new HashMap<>();
            response.put("message", "场景提示词添加成功");
            response.put("name", name);
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "场景名称和提示词不能为空");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 添加新的性格提示词
     */
    @PostMapping("/personalities")
    public ResponseEntity<Map<String, String>> addPersonality(
            @RequestBody Map<String, String> request) {
        
        String name = request.get("name");
        String prompt = request.get("prompt");
        
        if (name != null && prompt != null && !name.trim().isEmpty() && !prompt.trim().isEmpty()) {
            promptConfig.getPersonalities().put(name, prompt);
            Map<String, String> response = new HashMap<>();
            response.put("message", "性格提示词添加成功");
            response.put("name", name);
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "性格名称和提示词不能为空");
            return ResponseEntity.badRequest().body(response);
        }
    }
} 