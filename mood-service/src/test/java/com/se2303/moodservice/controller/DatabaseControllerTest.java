package com.se2303.moodservice.controller;

import com.se2303.moodservice.dao.MoodDAO;
import com.se2303.moodservice.dao.MoodItemDAO;
import com.se2303.moodservice.dto.ApiResponse;
import com.se2303.moodservice.service.DatabaseHealthService;
import com.se2303.moodservice.service.DatabaseInitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseControllerTest {

    @Mock
    private DatabaseHealthService databaseHealthService;

    @Mock
    private DatabaseInitService databaseInitService;

    @Mock
    private MoodDAO moodDAO;

    @Mock
    private MoodItemDAO moodItemDAO;

    @InjectMocks
    private DatabaseController databaseController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCheckHealth_Success() {
        // Given
        Map<String, Object> healthReport = new HashMap<>();
        healthReport.put("databaseConnected", true);
        healthReport.put("tablesExist", true);
        healthReport.put("overallStatus", "healthy");
        when(databaseHealthService.getHealthReport()).thenReturn(healthReport);

        // When
        ResponseEntity<ApiResponse> response = databaseController.checkHealth();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("数据库健康检查完成", response.getBody().getMessage());
        assertEquals(healthReport, response.getBody().getObject());
    }

    @Test
    void testCheckHealth_WithException() {
        // Given
        when(databaseHealthService.getHealthReport()).thenThrow(new RuntimeException("数据库连接失败"));

        // When & Then
        assertThrows(RuntimeException.class, () -> databaseController.checkHealth());
    }

    @Test
    void testGetStats_Success() {
        // Given
        when(moodDAO.countByUserId("1")).thenReturn(5L);
        when(moodDAO.countByUserId("2")).thenReturn(3L);
        when(moodItemDAO.countByLabel("开心")).thenReturn(10L);
        when(moodItemDAO.countByLabel("焦虑")).thenReturn(5L);

        // When
        ResponseEntity<ApiResponse> response = databaseController.getStats();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取数据库统计成功", response.getBody().getMessage());
        Map<String, Object> data = (Map<String, Object>) response.getBody().getObject();
        assertEquals(8L, data.get("totalMoods"));
        assertEquals(15L, data.get("totalMoodItems"));
    }

    @Test
    void testGetStats_WithException() {
        // Given
        when(moodDAO.countByUserId("1")).thenThrow(new RuntimeException("查询失败"));

        // When
        ResponseEntity<ApiResponse> response = databaseController.getStats();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("获取统计失败: 查询失败", response.getBody().getMessage());
    }

    @Test
    void testInitializeData_Success() {
        // Given
        doNothing().when(databaseInitService).manualInit();

        // When
        ResponseEntity<ApiResponse> response = databaseController.initializeData();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("示例数据初始化成功", response.getBody().getMessage());
    }

    @Test
    void testInitializeData_WithException() {
        // Given
        doThrow(new RuntimeException("初始化失败")).when(databaseInitService).manualInit();

        // When
        ResponseEntity<ApiResponse> response = databaseController.initializeData();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("初始化失败: 初始化失败", response.getBody().getMessage());
    }

    @Test
    void testClearAllData_Success() {
        // Given
        doNothing().when(databaseInitService).clearAllData();

        // When
        ResponseEntity<ApiResponse> response = databaseController.clearAllData();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("所有数据已清空", response.getBody().getMessage());
    }

    @Test
    void testClearAllData_WithException() {
        // Given
        doThrow(new RuntimeException("清空失败")).when(databaseInitService).clearAllData();

        // When
        ResponseEntity<ApiResponse> response = databaseController.clearAllData();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("清空失败: 清空失败", response.getBody().getMessage());
    }

    @Test
    void testGetDataStats_Success() {
        // Given
        String stats = "数据统计: 心情记录 10 条, 心情标签 20 条";
        when(databaseInitService.getDataStats()).thenReturn(stats);

        // When
        ResponseEntity<ApiResponse> response = databaseController.getDataStats();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取数据统计成功", response.getBody().getMessage());
        Map<String, Object> data = (Map<String, Object>) response.getBody().getObject();
        assertEquals(stats, data.get("stats"));
    }

    @Test
    void testGetDataStats_WithException() {
        // Given
        when(databaseInitService.getDataStats()).thenThrow(new RuntimeException("获取统计失败"));

        // When
        ResponseEntity<ApiResponse> response = databaseController.getDataStats();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("获取统计失败: 获取统计失败", response.getBody().getMessage());
    }

    @Test
    void testCheckConnection_Success() {
        // Given
        when(databaseHealthService.isDatabaseConnected()).thenReturn(true);
        when(databaseHealthService.checkTablesExist()).thenReturn(true);

        // When
        ResponseEntity<ApiResponse> response = databaseController.checkConnection();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("数据库连接检查完成", response.getBody().getMessage());
        Map<String, Object> data = (Map<String, Object>) response.getBody().getObject();
        assertTrue((Boolean) data.get("connected"));
        assertTrue((Boolean) data.get("tablesExist"));
        assertEquals("healthy", data.get("status"));
    }

    @Test
    void testCheckConnection_WithConnectionFailure() {
        // Given
        when(databaseHealthService.isDatabaseConnected()).thenReturn(false);
        when(databaseHealthService.checkTablesExist()).thenReturn(false);

        // When
        ResponseEntity<ApiResponse> response = databaseController.checkConnection();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("数据库连接检查完成", response.getBody().getMessage());
        Map<String, Object> data = (Map<String, Object>) response.getBody().getObject();
        assertFalse((Boolean) data.get("connected"));
        assertFalse((Boolean) data.get("tablesExist"));
        assertEquals("unhealthy", data.get("status"));
    }

    @Test
    void testCheckConnection_WithPartialFailure() {
        // Given
        when(databaseHealthService.isDatabaseConnected()).thenReturn(true);
        when(databaseHealthService.checkTablesExist()).thenReturn(false);

        // When
        ResponseEntity<ApiResponse> response = databaseController.checkConnection();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("数据库连接检查完成", response.getBody().getMessage());
        Map<String, Object> data = (Map<String, Object>) response.getBody().getObject();
        assertTrue((Boolean) data.get("connected"));
        assertFalse((Boolean) data.get("tablesExist"));
        assertEquals("unhealthy", data.get("status"));
    }
} 