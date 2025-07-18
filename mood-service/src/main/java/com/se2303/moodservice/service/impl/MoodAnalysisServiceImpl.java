package com.se2303.moodservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.se2303.moodservice.dto.MoodCheckInResponse;
import com.se2303.moodservice.model.MoodAnalysis;
import com.se2303.moodservice.repository.MoodAnalysisRepository;
import com.se2303.moodservice.service.MoodAnalysisService;
import com.se2303.moodservice.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 心情分析服务实现类
 * 集成AI API生成分析报告
 */
@Service
public class MoodAnalysisServiceImpl implements MoodAnalysisService {
    
    @Autowired
    private MoodService moodService;

    @Autowired
    private MoodAnalysisRepository moodAnalysisRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${ai.api.url:http://127.0.0.1}")
    private String aiApiUrl;
    
    @Value("${ai.api.key}")
    private String aiApiKey;
    
    @Override
    public String generateAnalysisReport(String userId) {

        if(moodService.validateAnalysis(userId)) return moodService.getValidAnalysis(userId);
        
        // 获取最近7天的数据
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        String analysisData = generateAnalysisReport(userId, startDate.toString(), endDate.toString());

        // 安全检查：确保分析数据不会超过MEDIUMTEXT限制（16MB = 16,777,215字节）
        // 为了安全起见，我们限制在10MB以内
        final int MAX_ANALYSIS_LENGTH = 10 * 1024 * 1024; // 10MB
        if (analysisData.getBytes(StandardCharsets.UTF_8).length > MAX_ANALYSIS_LENGTH) {
            // 截断过长的文本，并添加提示
            byte[] bytes = analysisData.getBytes(StandardCharsets.UTF_8);
            analysisData = new String(bytes, 0, Math.min(bytes.length, MAX_ANALYSIS_LENGTH - 100), StandardCharsets.UTF_8);
            analysisData += "\n\n[注：分析报告因过长已被截断]";
        }

        // 查找是否已存在该用户的分析记录
        Optional<MoodAnalysis> existingAnalysis = moodAnalysisRepository.findByUserId(userId);
        MoodAnalysis analysis;
        
        if (existingAnalysis.isPresent()) {
            // 更新现有记录
            analysis = existingAnalysis.get();
            analysis.setAnalysis(analysisData);
            analysis.setValid(true);
            analysis.setUpdatedAt(LocalDateTime.now());
        } else {
            // 创建新记录
            analysis = new MoodAnalysis(userId, analysisData, true, LocalDateTime.now());
        }
        
        moodAnalysisRepository.save(analysis);

        return analysisData;
    }
    
    @Override
    public String generateAnalysisReport(String userId, String startDate, String endDate) {
        try {
            // 获取用户的心情数据
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<MoodCheckInResponse> moodRecords = moodService.findByUserIdAndDateBetween(userId, start, end);
            
            if (moodRecords.isEmpty()) {
                return "暂无足够的数据进行分析。请继续记录您的心情和日记，我们将为您提供个性化的分析报告。";
            }
            
            // 构建AI prompt
            String prompt = buildAnalysisPrompt(moodRecords);
            
            // 调用AI API
            return callAiApi(prompt);
            
        } catch (Exception e) {
            return "分析报告生成失败，请稍后重试。";
        }
    }
    
    /**
     * 构建分析prompt
     */
    private String buildAnalysisPrompt(List<MoodCheckInResponse> moodRecords) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下用户的心情记录和日记内容，生成一段温暖、专业且个性化的心理健康分析报告。报告应该包含：\n");
        prompt.append("1. 整体心情状态评估\n");
        prompt.append("2. 情绪变化趋势分析\n");
        prompt.append("3. 基于日记内容的心理状态洞察\n");
        prompt.append("4. 个性化的建议和鼓励\n\n");
        prompt.append("用户数据：\n");
        
        // 按日期排序
        moodRecords.sort((a, b) -> a.getDate().compareTo(b.getDate()));
        
        for (MoodCheckInResponse record : moodRecords) {
            prompt.append("日期：").append(record.getDate()).append("\n");
            prompt.append("心情评分：").append(record.getScore()).append("/5\n");
            
            if (record.getMoodLabels() != null && !record.getMoodLabels().isEmpty()) {
                prompt.append("心情标签：").append(String.join(", ", record.getMoodLabels())).append("\n");
            }
            
            if (record.getDiaryTitle() != null && !record.getDiaryTitle().trim().isEmpty()) {
                prompt.append("日记标题：").append(record.getDiaryTitle()).append("\n");
            }
            
            if (record.getDiaryDescription() != null && !record.getDiaryDescription().trim().isEmpty()) {
                prompt.append("日记内容：").append(record.getDiaryDescription()).append("\n");
            }
            
            // if (record.getWeather() != null && !record.getWeather().trim().isEmpty()) {
            //     prompt.append("天气：").append(record.getWeather()).append("\n");
            // }
            
            prompt.append("---\n");
        }
        
        prompt.append("\n请生成一段温暖、专业且个性化的分析报告，语言要自然流畅，避免过于技术化的表达。");
        
        return prompt.toString();
    }
    
    /**
     * 调用AI API
     */
    private String callAiApi(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + aiApiKey);
            
            // 使用Dify API格式
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", new HashMap<>());
            requestBody.put("query", prompt);
            requestBody.put("response_mode", "blocking");
            requestBody.put("user", "mood-service");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            System.out.println("调用AI API，URL: " + aiApiUrl + "/v1/chat-messages");
            System.out.println("请求体: " + requestBody);
            
            // 使用Dify API端点
            ResponseEntity<String> response = restTemplate.postForEntity(aiApiUrl + "/v1/chat-messages", request, String.class);
            
            System.out.println("AI API响应状态: " + response.getStatusCode());
            System.out.println("AI API响应体: " + response.getBody());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // 解析Dify API响应
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                String answer = responseJson.path("answer").asText("");
                
                // 输出answer
                System.out.println("AI API返回的分析文字: " + answer);


                if (!answer.isEmpty()) {
                    return answer;
                }
            }
            
            return "AI分析服务暂时不可用，请稍后重试。";
            
        } catch (Exception e) {
            System.err.println("AI API调用异常: " + e.getMessage());
            e.printStackTrace();
            return "分析报告生成失败，请稍后重试。";
        }
    }
} 