package com.se2303.moodservice.service;

import com.se2303.moodservice.dao.MoodDAO;
import com.se2303.moodservice.dao.MoodItemDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseHealthServiceTest {

    @Mock
    private MoodDAO moodDAO;

    @Mock
    private MoodItemDAO moodItemDAO;

    @InjectMocks
    private DatabaseHealthService databaseHealthService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testIsDatabaseConnected_Success() {
        // Given
        when(moodDAO.findAll()).thenReturn(Arrays.asList());

        // When
        boolean isConnected = databaseHealthService.isDatabaseConnected();

        // Then
        assertTrue(isConnected);
        verify(moodDAO).findAll();
    }

    @Test
    void testIsDatabaseConnected_WithException() {
        // Given
        when(moodDAO.findAll()).thenThrow(new RuntimeException("数据库连接失败"));

        // When
        boolean isConnected = databaseHealthService.isDatabaseConnected();

        // Then
        assertFalse(isConnected);
        verify(moodDAO).findAll();
    }

    @Test
    void testGetDatabaseStats_Success() {
        // Given
        when(moodDAO.countByUserId("1")).thenReturn(5L);
        when(moodDAO.countByUserId("2")).thenReturn(3L);
        when(moodItemDAO.countByLabel("开心")).thenReturn(10L);
        when(moodItemDAO.countByLabel("焦虑")).thenReturn(5L);

        // When
        Map<String, Object> stats = databaseHealthService.getDatabaseStats();

        // Then
        assertNotNull(stats);
        assertTrue((Boolean) stats.get("connected"));
        assertEquals("healthy", stats.get("status"));
        assertEquals(8L, stats.get("totalMoods"));
        assertEquals(15L, stats.get("totalMoodItems"));
    }

    @Test
    void testGetDatabaseStats_WithException() {
        // Given
        when(moodDAO.countByUserId("1")).thenThrow(new RuntimeException("查询失败"));

        // When
        Map<String, Object> stats = databaseHealthService.getDatabaseStats();

        // Then
        assertNotNull(stats);
        assertFalse((Boolean) stats.get("connected"));
        assertEquals("error", stats.get("status"));
        assertNotNull(stats.get("error"));
    }

    @Test
    void testCheckTablesExist_Success() {
        // Given
        when(moodDAO.findAll()).thenReturn(Arrays.asList());
        when(moodItemDAO.findAll()).thenReturn(Arrays.asList());

        // When
        boolean tablesExist = databaseHealthService.checkTablesExist();

        // Then
        assertTrue(tablesExist);
        verify(moodDAO).findAll();
        verify(moodItemDAO).findAll();
    }

    @Test
    void testCheckTablesExist_WithMoodDAOException() {
        // Given
        when(moodDAO.findAll()).thenThrow(new RuntimeException("心情记录表不存在"));

        // When
        boolean tablesExist = databaseHealthService.checkTablesExist();

        // Then
        assertFalse(tablesExist);
        verify(moodDAO).findAll();
        verify(moodItemDAO, never()).findAll();
    }

    @Test
    void testCheckTablesExist_WithMoodItemDAOException() {
        // Given
        when(moodDAO.findAll()).thenReturn(Arrays.asList());
        when(moodItemDAO.findAll()).thenThrow(new RuntimeException("心情标签表不存在"));

        // When
        boolean tablesExist = databaseHealthService.checkTablesExist();

        // Then
        assertFalse(tablesExist);
        verify(moodDAO).findAll();
        verify(moodItemDAO).findAll();
    }

    @Test
    void testGetHealthReport_Success() {
        // Given
        when(moodDAO.findAll()).thenReturn(Arrays.asList());
        when(moodItemDAO.findAll()).thenReturn(Arrays.asList());
        when(moodDAO.countByUserId("1")).thenReturn(5L);
        when(moodDAO.countByUserId("2")).thenReturn(3L);
        when(moodItemDAO.countByLabel("开心")).thenReturn(10L);
        when(moodItemDAO.countByLabel("焦虑")).thenReturn(5L);

        // When
        Map<String, Object> report = databaseHealthService.getHealthReport();

        // Then
        assertNotNull(report);
        assertTrue((Boolean) report.get("databaseConnected"));
        assertTrue((Boolean) report.get("tablesExist"));
        assertEquals("healthy", report.get("overallStatus"));
        assertNotNull(report.get("statistics"));
    }

    @Test
    void testGetHealthReport_WithDatabaseConnectionFailure() {
        // Given
        when(moodDAO.findAll()).thenThrow(new RuntimeException("数据库连接失败"));

        // When
        Map<String, Object> report = databaseHealthService.getHealthReport();

        // Then
        assertNotNull(report);
        assertFalse((Boolean) report.get("databaseConnected"));
        assertFalse((Boolean) report.get("tablesExist"));
        assertEquals("unhealthy", report.get("overallStatus"));
        assertNull(report.get("statistics"));
    }

    @Test
    void testGetHealthReport_WithTablesNotExist() {
        // Given
        when(moodDAO.findAll()).thenReturn(Arrays.asList());
        when(moodItemDAO.findAll()).thenThrow(new RuntimeException("表不存在"));

        // When
        Map<String, Object> report = databaseHealthService.getHealthReport();

        // Then
        assertNotNull(report);
        assertTrue((Boolean) report.get("databaseConnected"));
        assertFalse((Boolean) report.get("tablesExist"));
        assertEquals("unhealthy", report.get("overallStatus"));
        assertNull(report.get("statistics"));
    }

    @Test
    void testIsDatabaseConnected_WithNullPointerException() {
        // Given
        when(moodDAO.findAll()).thenThrow(new NullPointerException("空指针异常"));

        // When
        boolean isConnected = databaseHealthService.isDatabaseConnected();

        // Then
        assertFalse(isConnected);
        verify(moodDAO).findAll();
    }

    @Test
    void testIsDatabaseConnected_WithIllegalStateException() {
        // Given
        when(moodDAO.findAll()).thenThrow(new IllegalStateException("数据库状态异常"));

        // When
        boolean isConnected = databaseHealthService.isDatabaseConnected();

        // Then
        assertFalse(isConnected);
        verify(moodDAO).findAll();
    }

    @Test
    void testGetDatabaseStats_WithZeroCounts() {
        // Given
        when(moodDAO.countByUserId("1")).thenReturn(0L);
        when(moodDAO.countByUserId("2")).thenReturn(0L);
        when(moodItemDAO.countByLabel("开心")).thenReturn(0L);
        when(moodItemDAO.countByLabel("焦虑")).thenReturn(0L);

        // When
        Map<String, Object> stats = databaseHealthService.getDatabaseStats();

        // Then
        assertNotNull(stats);
        assertTrue((Boolean) stats.get("connected"));
        assertEquals("healthy", stats.get("status"));
        assertEquals(0L, stats.get("totalMoods"));
        assertEquals(0L, stats.get("totalMoodItems"));
    }
} 