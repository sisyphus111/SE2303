package com.se2303.inspectionservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;

@Service
public class LLMService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Dify API 配置
    private static final String DIFY_CHAT_URL = "http://127.0.0.1/chat/bC1S72xe6NMOVKL4";
    private static final String DIFY_API_URL = "http://127.0.0.1/v1";
    private static final String API_KEY = "REDACTED";

    /**
     * 调用 Dify API 进行内容安全检查
     * @param content 待检查的内容
     * @return true 如果内容安全，false 如果不安全
     */
    public boolean isContentSafe(String content) {
        if (content == null || content.trim().isEmpty()) {
            return true;
        }

        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + API_KEY);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", new HashMap<>());
            requestBody.put("query", "请检查以下内容是否包含自杀倾向或危险信息，只需回答'安全'或'有人想死'：" + content);
            requestBody.put("response_mode", "blocking");
            requestBody.put("user", "inspection-service");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // 发送请求到 Dify API
            ResponseEntity<String> response = restTemplate.exchange(
                DIFY_API_URL + "/chat-messages",
                HttpMethod.POST,
                request,
                String.class
            );

            // 解析响应
            JsonNode responseJson = objectMapper.readTree(response.getBody());
            String answer = responseJson.path("answer").asText("");

            // 检查是否包含"有人想死"
            if (answer.contains("有人想死")) {
                return false;
            }

            return true;

        } catch (Exception e) {
            // 如果 API 调用失败，记录错误并返回 true（安全）
            System.err.println("Dify API 调用失败: " + e.getMessage());
            return true;
        }
    }
} 