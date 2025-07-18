package com.se2303.moodservice.service;

import com.se2303.moodservice.dto.MoodCheckInResponse;
import com.se2303.moodservice.service.impl.MoodAnalysisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoodAnalysisServiceTest {

    @Mock
    private com.se2303.moodservice.service.MoodService moodService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MoodAnalysisServiceImpl moodAnalysisService;

    @BeforeEach
    void setUp() {
        // 设置私有字段的值
        ReflectionTestUtils.setField(moodAnalysisService, "aiApiUrl", "http://test-api.com");
        ReflectionTestUtils.setField(moodAnalysisService, "aiApiKey", "test-key");
    }

    @Test
    void testGenerateAnalysisReport_WithUserId() {
        // Given
        String userId = "testUser";
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        
        MoodCheckInResponse mockResponse = new MoodCheckInResponse();
        mockResponse.setUserId(userId);
        mockResponse.setScore(4);
        mockResponse.setDate(today);
        mockResponse.setDiaryTitle("测试日记");
        mockResponse.setDiaryDescription("今天心情不错");
        mockResponse.setMoodLabels(Arrays.asList("开心", "满足"));
        
        when(moodService.findByUserIdAndDateBetween(eq(userId), eq(startDate), eq(today)))
                .thenReturn(Arrays.asList(mockResponse));
        
        // Mock AI API response
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok("{\"answer\":\"心情分析报告\"}"));

        // When
        String report = moodAnalysisService.generateAnalysisReport(userId);

        // Then
        assertNotNull(report);
        verify(moodService).findByUserIdAndDateBetween(eq(userId), eq(startDate), eq(today));
    }

    @Test
    void testGenerateAnalysisReport_WithUserIdAndDateRange() {
        // Given
        String userId = "testUser";
        String startDate = "2024-01-01";
        String endDate = "2024-01-31";
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        MoodCheckInResponse mockResponse = new MoodCheckInResponse();
        mockResponse.setUserId(userId);
        mockResponse.setScore(4);
        mockResponse.setDate(start);
        mockResponse.setDiaryTitle("测试日记");
        mockResponse.setDiaryDescription("今天心情不错");
        
        when(moodService.findByUserIdAndDateBetween(eq(userId), eq(start), eq(end)))
                .thenReturn(Arrays.asList(mockResponse));
        
        // Mock AI API response
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok("{\"answer\":\"心情分析报告\"}"));

        // When
        String report = moodAnalysisService.generateAnalysisReport(userId, startDate, endDate);

        // Then
        assertNotNull(report);
        verify(moodService).findByUserIdAndDateBetween(eq(userId), eq(start), eq(end));
    }

    @Test
    void testGenerateAnalysisReport_EmptyData() {
        // Given
        String userId = "testUser";
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        
        when(moodService.findByUserIdAndDateBetween(eq(userId), eq(startDate), eq(today)))
                .thenReturn(Collections.emptyList());

        // When
        String report = moodAnalysisService.generateAnalysisReport(userId);

        // Then
        assertNotNull(report);
        assertTrue(report.contains("暂无足够的数据进行分析"));
        verify(moodService).findByUserIdAndDateBetween(eq(userId), eq(startDate), eq(today));
    }

    @Test
    void testGenerateAnalysisReport_WithDateRange_EmptyData() {
        // Given
        String userId = "testUser";
        String startDate = "2024-01-01";
        String endDate = "2024-01-31";
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        when(moodService.findByUserIdAndDateBetween(eq(userId), eq(start), eq(end)))
                .thenReturn(Collections.emptyList());

        // When
        String report = moodAnalysisService.generateAnalysisReport(userId, startDate, endDate);

        // Then
        assertNotNull(report);
        assertTrue(report.contains("暂无足够的数据进行分析"));
        verify(moodService).findByUserIdAndDateBetween(eq(userId), eq(start), eq(end));
    }

    @Test
    void testGenerateAnalysisReport_ApiError() {
        // Given
        String userId = "testUser";
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        
        MoodCheckInResponse mockResponse = new MoodCheckInResponse();
        mockResponse.setUserId(userId);
        mockResponse.setScore(4);
        mockResponse.setDate(today);
        
        when(moodService.findByUserIdAndDateBetween(eq(userId), eq(startDate), eq(today)))
                .thenReturn(Arrays.asList(mockResponse));
        
        // Mock AI API error
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("API Error"));

        // When
        String report = moodAnalysisService.generateAnalysisReport(userId);

        // Then
        assertNotNull(report);
        assertTrue(report.contains("分析报告生成失败"));
        verify(moodService).findByUserIdAndDateBetween(eq(userId), eq(startDate), eq(today));
    }

    @Test
    void testGenerateAnalysisReport_InvalidDateRange() {
        // Given
        String userId = "testUser";
        String startDate = "invalid-date";
        String endDate = "2024-01-31";

        // When
        String report = moodAnalysisService.generateAnalysisReport(userId, startDate, endDate);

        // Then
        assertNotNull(report);
        assertTrue(report.contains("分析报告生成失败"));
    }
} 