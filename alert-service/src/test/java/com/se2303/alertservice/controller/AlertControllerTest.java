package com.se2303.alertservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se2303.alertservice.dto.AlertResponse;
import com.se2303.alertservice.dto.ApiResponse;
import com.se2303.alertservice.dto.CreateAlertRequest;
import com.se2303.alertservice.model.AlertLevel;
import com.se2303.alertservice.model.AlertStatus;
import com.se2303.alertservice.model.AlertType;
import com.se2303.alertservice.service.AlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AlertControllerTest {

    @Mock
    private AlertService alertService;

    @InjectMocks
    private AlertController alertController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private AlertResponse testAlertResponse;
    private CreateAlertRequest createRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(alertController).build();
        objectMapper = new ObjectMapper();

        testAlertResponse = new AlertResponse();
        testAlertResponse.setId(1L);
        testAlertResponse.setUserId("user123");
        testAlertResponse.setAlertType(AlertType.EMOTIONAL_CRISIS);
        testAlertResponse.setAlertLevel(AlertLevel.HIGH);
        testAlertResponse.setDescription("Test alert");
        testAlertResponse.setStatus(AlertStatus.PENDING);
        testAlertResponse.setCreatedAt(new Date());

        createRequest = new CreateAlertRequest();
        createRequest.setUserId("user123");
        createRequest.setAlertType(AlertType.EMOTIONAL_CRISIS);
        createRequest.setAlertLevel(AlertLevel.HIGH);
        createRequest.setDescription("Test alert");
        createRequest.setOperationDetails("Test operation");
        createRequest.setAiSessionId("session123");
        createRequest.setAiTimeoutDuration(30);
    }

    @Test
    void testCreateAlert_Success() throws Exception {
        // Arrange
        when(alertService.createAlert(any(CreateAlertRequest.class))).thenReturn(testAlertResponse);

        // Act
        ResponseEntity<ApiResponse> response = alertController.createAlert(createRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("警报创建成功", response.getBody().getMessage());

        verify(alertService, times(1)).createAlert(createRequest);
    }

    @Test
    void testCreateAlert_Exception() throws Exception {
        // Arrange
        when(alertService.createAlert(any(CreateAlertRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<ApiResponse> response = alertController.createAlert(createRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("警报创建失败"));

        verify(alertService, times(1)).createAlert(createRequest);
    }

    @Test
    void testGetPendingAlerts_Success() throws Exception {
        // Arrange
        List<AlertResponse> alerts = Arrays.asList(testAlertResponse);
        when(alertService.getPendingAlerts()).thenReturn(alerts);

        // Act
        ResponseEntity<ApiResponse> response = alertController.getPendingAlerts();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取待处理警报成功", response.getBody().getMessage());

        verify(alertService, times(1)).getPendingAlerts();
    }

    @Test
    void testGetPendingAlerts_Exception() throws Exception {
        // Arrange
        when(alertService.getPendingAlerts()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<ApiResponse> response = alertController.getPendingAlerts();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("获取待处理警报失败"));

        verify(alertService, times(1)).getPendingAlerts();
    }

    @Test
    void testGetAlertById_Exists() throws Exception {
        // Arrange
        when(alertService.findById(1L)).thenReturn(testAlertResponse);

        // Act
        ResponseEntity<ApiResponse> response = alertController.getAlertById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取警报详情成功", response.getBody().getMessage());

        verify(alertService, times(1)).findById(1L);
    }

    @Test
    void testGetAlertById_NotExists() throws Exception {
        // Arrange
        when(alertService.findById(999L)).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> response = alertController.getAlertById(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("警报不存在", response.getBody().getMessage());

        verify(alertService, times(1)).findById(999L);
    }

    @Test
    void testGetAlertsByUserId_Success() throws Exception {
        // Arrange
        List<AlertResponse> alerts = Arrays.asList(testAlertResponse);
        when(alertService.findByUserId("user123")).thenReturn(alerts);

        // Act
        ResponseEntity<ApiResponse> response = alertController.getAlertsByUserId("user123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取用户警报列表成功", response.getBody().getMessage());

        verify(alertService, times(1)).findByUserId("user123");
    }

    @Test
    void testGetAlertsByUserId_Exception() throws Exception {
        // Arrange
        when(alertService.findByUserId("user123")).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<ApiResponse> response = alertController.getAlertsByUserId("user123");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("获取用户警报列表失败"));

        verify(alertService, times(1)).findByUserId("user123");
    }

    @Test
    void testGetAlertsByStatus_Success() throws Exception {
        // Arrange
        List<AlertResponse> alerts = Arrays.asList(testAlertResponse);
        when(alertService.findByStatus(AlertStatus.PENDING)).thenReturn(alerts);

        // Act
        ResponseEntity<ApiResponse> response = alertController.getAlertsByStatus(AlertStatus.PENDING);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取状态警报列表成功", response.getBody().getMessage());

        verify(alertService, times(1)).findByStatus(AlertStatus.PENDING);
    }

    @Test
    void testGetAlertsByStatus_Exception() throws Exception {
        // Arrange
        when(alertService.findByStatus(AlertStatus.PENDING)).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<ApiResponse> response = alertController.getAlertsByStatus(AlertStatus.PENDING);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("获取状态警报列表失败"));

        verify(alertService, times(1)).findByStatus(AlertStatus.PENDING);
    }

    @Test
    void testAssignAlert_Success() throws Exception {
        // Arrange
        when(alertService.assignAlert(1L, "counselor123")).thenReturn(testAlertResponse);

        // Act
        ResponseEntity<ApiResponse> response = alertController.assignAlert(1L, "counselor123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("警报分配成功", response.getBody().getMessage());

        verify(alertService, times(1)).assignAlert(1L, "counselor123");
    }

    @Test
    void testAssignAlert_AlertNotFound() throws Exception {
        // Arrange
        when(alertService.assignAlert(999L, "counselor123")).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> response = alertController.assignAlert(999L, "counselor123");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("警报不存在", response.getBody().getMessage());

        verify(alertService, times(1)).assignAlert(999L, "counselor123");
    }

    @Test
    void testAssignAlert_Exception() throws Exception {
        // Arrange
        when(alertService.assignAlert(1L, "counselor123")).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<ApiResponse> response = alertController.assignAlert(1L, "counselor123");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("警报分配失败"));

        verify(alertService, times(1)).assignAlert(1L, "counselor123");
    }

    @Test
    void testUpdateAlertStatus_Success() throws Exception {
        // Arrange
        when(alertService.updateAlertStatus(1L, AlertStatus.IN_PROGRESS)).thenReturn(testAlertResponse);

        // Act
        ResponseEntity<ApiResponse> response = alertController.updateAlertStatus(1L, AlertStatus.IN_PROGRESS);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("警报状态更新成功", response.getBody().getMessage());

        verify(alertService, times(1)).updateAlertStatus(1L, AlertStatus.IN_PROGRESS);
    }

    @Test
    void testUpdateAlertStatus_AlertNotFound() throws Exception {
        // Arrange
        when(alertService.updateAlertStatus(999L, AlertStatus.IN_PROGRESS)).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> response = alertController.updateAlertStatus(999L, AlertStatus.IN_PROGRESS);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("警报不存在", response.getBody().getMessage());

        verify(alertService, times(1)).updateAlertStatus(999L, AlertStatus.IN_PROGRESS);
    }

    @Test
    void testUpdateAlertStatus_Exception() throws Exception {
        // Arrange
        when(alertService.updateAlertStatus(1L, AlertStatus.IN_PROGRESS)).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<ApiResponse> response = alertController.updateAlertStatus(1L, AlertStatus.IN_PROGRESS);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("警报状态更新失败"));

        verify(alertService, times(1)).updateAlertStatus(1L, AlertStatus.IN_PROGRESS);
    }

    @Test
    void testAddFollowUpNotes_Success() throws Exception {
        // Arrange
        when(alertService.addFollowUpNotes(1L, "Test notes")).thenReturn(testAlertResponse);

        // Act
        ResponseEntity<ApiResponse> response = alertController.addFollowUpNotes(1L, "Test notes");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("回访备注添加成功", response.getBody().getMessage());

        verify(alertService, times(1)).addFollowUpNotes(1L, "Test notes");
    }

    @Test
    void testAddFollowUpNotes_AlertNotFound() throws Exception {
        // Arrange
        when(alertService.addFollowUpNotes(999L, "Test notes")).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse> response = alertController.addFollowUpNotes(999L, "Test notes");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("警报不存在", response.getBody().getMessage());

        verify(alertService, times(1)).addFollowUpNotes(999L, "Test notes");
    }

    @Test
    void testAddFollowUpNotes_Exception() throws Exception {
        // Arrange
        when(alertService.addFollowUpNotes(1L, "Test notes")).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<ApiResponse> response = alertController.addFollowUpNotes(1L, "Test notes");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("回访备注添加失败"));

        verify(alertService, times(1)).addFollowUpNotes(1L, "Test notes");
    }

    @Test
    void testGetAlertsByCounselorId_Success() throws Exception {
        // Arrange
        List<AlertResponse> alerts = Arrays.asList(testAlertResponse);
        when(alertService.findByCounselorId("counselor123")).thenReturn(alerts);

        // Act
        ResponseEntity<ApiResponse> response = alertController.getAlertsByCounselorId("counselor123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取心理咨询师警报列表成功", response.getBody().getMessage());

        verify(alertService, times(1)).findByCounselorId("counselor123");
    }

    @Test
    void testGetAlertsByCounselorId_Exception() throws Exception {
        // Arrange
        when(alertService.findByCounselorId("counselor123")).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<ApiResponse> response = alertController.getAlertsByCounselorId("counselor123");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("获取心理咨询师警报列表失败"));

        verify(alertService, times(1)).findByCounselorId("counselor123");
    }

    @Test
    void testSearchAlerts_Success() throws Exception {
        // Arrange
        Page<AlertResponse> alertPage = new PageImpl<>(Arrays.asList(testAlertResponse), PageRequest.of(0, 10), 1);
        when(alertService.findAlertsByConditions(anyString(), any(), any(), any(), any())).thenReturn(alertPage);

        // Act
        ResponseEntity<ApiResponse> response = alertController.searchAlerts(
                "user123", AlertStatus.PENDING, AlertType.EMOTIONAL_CRISIS, AlertLevel.HIGH, 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("查询警报成功", response.getBody().getMessage());

        verify(alertService, times(1)).findAlertsByConditions(
                "user123", AlertStatus.PENDING, AlertType.EMOTIONAL_CRISIS, AlertLevel.HIGH, PageRequest.of(0, 10));
    }

    @Test
    void testSearchAlerts_Exception() throws Exception {
        // Arrange
        when(alertService.findAlertsByConditions(anyString(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<ApiResponse> response = alertController.searchAlerts(
                "user123", AlertStatus.PENDING, AlertType.EMOTIONAL_CRISIS, AlertLevel.HIGH, 0, 10);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("查询警报失败"));

        verify(alertService, times(1)).findAlertsByConditions(
                "user123", AlertStatus.PENDING, AlertType.EMOTIONAL_CRISIS, AlertLevel.HIGH, PageRequest.of(0, 10));
    }

    @Test
    void testGetAlertStatistics_Success() throws Exception {
        // Arrange
        AlertService.AlertStatistics stats = new AlertService.AlertStatistics();
        stats.setTotalAlerts(100L);
        stats.setPendingAlerts(20L);
        when(alertService.getAlertStatistics()).thenReturn(stats);

        // Act
        ResponseEntity<ApiResponse> response = alertController.getAlertStatistics();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取警报统计成功", response.getBody().getMessage());

        verify(alertService, times(1)).getAlertStatistics();
    }

    @Test
    void testGetAlertStatistics_Exception() throws Exception {
        // Arrange
        when(alertService.getAlertStatistics()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<ApiResponse> response = alertController.getAlertStatistics();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("获取警报统计失败"));

        verify(alertService, times(1)).getAlertStatistics();
    }

    @Test
    void testDeleteAlert_Success() throws Exception {
        // Arrange
        when(alertService.deleteAlert(1L)).thenReturn(true);

        // Act
        ResponseEntity<ApiResponse> response = alertController.deleteAlert(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("警报删除成功", response.getBody().getMessage());

        verify(alertService, times(1)).deleteAlert(1L);
    }

    @Test
    void testDeleteAlert_NotExists() throws Exception {
        // Arrange
        when(alertService.deleteAlert(999L)).thenReturn(false);

        // Act
        ResponseEntity<ApiResponse> response = alertController.deleteAlert(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("警报不存在", response.getBody().getMessage());

        verify(alertService, times(1)).deleteAlert(999L);
    }

    @Test
    void testDeleteAlert_Exception() throws Exception {
        // Arrange
        when(alertService.deleteAlert(1L)).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<ApiResponse> response = alertController.deleteAlert(1L);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("警报删除失败"));

        verify(alertService, times(1)).deleteAlert(1L);
    }

    @Test
    void testGetAlertTypes_Success() throws Exception {
        // Act
        ResponseEntity<ApiResponse> response = alertController.getAlertTypes();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取警报类型成功", response.getBody().getMessage());
        assertNotNull(response.getBody().getObject());
    }

    @Test
    void testGetAlertLevels_Success() throws Exception {
        // Act
        ResponseEntity<ApiResponse> response = alertController.getAlertLevels();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取警报级别成功", response.getBody().getMessage());
        assertNotNull(response.getBody().getObject());
    }

    @Test
    void testGetAlertStatuses_Success() throws Exception {
        // Act
        ResponseEntity<ApiResponse> response = alertController.getAlertStatuses();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取警报状态成功", response.getBody().getMessage());
        assertNotNull(response.getBody().getObject());
    }

    @Test
    void testHealth_Success() throws Exception {
        // Act
        ResponseEntity<ApiResponse> response = alertController.health();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("服务健康", response.getBody().getMessage());
        assertNotNull(response.getBody().getObject());
    }
} 