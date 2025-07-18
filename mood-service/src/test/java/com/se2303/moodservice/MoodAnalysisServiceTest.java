package com.se2303.moodservice;

import com.se2303.moodservice.service.MoodAnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 心情分析服务测试
 */
@SpringBootTest
public class MoodAnalysisServiceTest {
    
    @Autowired
    private MoodAnalysisService moodAnalysisService;
    
    @Test
    public void testGenerateAnalysisReport() {
        try {
            String report = moodAnalysisService.generateAnalysisReport("1");
            System.out.println("分析报告: " + report);
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 