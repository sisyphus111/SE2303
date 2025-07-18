package com.se2303.moodservice.service;

import com.se2303.moodservice.dao.MoodDAO;
import com.se2303.moodservice.dao.MoodItemDAO;
import com.se2303.moodservice.model.Mood;
import com.se2303.moodservice.model.MoodItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseInitServiceTest {

    @Mock
    private MoodDAO moodDAO;

    @Mock
    private MoodItemDAO moodItemDAO;

    @InjectMocks
    private DatabaseInitService databaseInitService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testManualInit_Success() {
        // Given
        when(moodDAO.save(any(Mood.class))).thenReturn(new Mood());
        when(moodItemDAO.save(any(MoodItem.class))).thenReturn(new MoodItem());

        // When
        databaseInitService.manualInit();

        // Then
        // 验证保存了7个用户1的心情记录和1个用户2的心情记录
        verify(moodDAO, times(8)).save(any(Mood.class));
        // 验证保存了心情标签
        verify(moodItemDAO, atLeastOnce()).save(any(MoodItem.class));
    }

    @Test
    void testManualInit_WithException() {
        // Given
        when(moodDAO.save(any(Mood.class))).thenThrow(new RuntimeException("保存失败"));

        // When & Then
        assertThrows(RuntimeException.class, () -> databaseInitService.manualInit());
    }

    @Test
    void testClearAllData_Success() {
        // Given
        doNothing().when(moodItemDAO).deleteAll();
        doNothing().when(moodDAO).deleteAll();

        // When
        databaseInitService.clearAllData();

        // Then
        verify(moodItemDAO).deleteAll();
        verify(moodDAO).deleteAll();
    }

    @Test
    void testClearAllData_WithException() {
        // Given
        doThrow(new RuntimeException("删除失败")).when(moodItemDAO).deleteAll();

        // When & Then
        assertThrows(RuntimeException.class, () -> databaseInitService.clearAllData());
    }

    @Test
    void testGetDataStats_Success() {
        // Given
        when(moodDAO.countByUserId("1")).thenReturn(7L);
        when(moodItemDAO.countByLabel("开心")).thenReturn(3L);

        // When
        String stats = databaseInitService.getDataStats();

        // Then
        assertNotNull(stats);
        assertTrue(stats.contains("数据统计"));
        assertTrue(stats.contains("心情记录"));
        assertTrue(stats.contains("心情标签"));
        verify(moodDAO).countByUserId("1");
        verify(moodItemDAO).countByLabel("开心");
    }

    @Test
    void testGetDataStats_WithZeroCounts() {
        // Given
        when(moodDAO.countByUserId("1")).thenReturn(0L);
        when(moodItemDAO.countByLabel("开心")).thenReturn(0L);

        // When
        String stats = databaseInitService.getDataStats();

        // Then
        assertNotNull(stats);
        assertTrue(stats.contains("数据统计"));
        assertTrue(stats.contains("心情记录 0 条"));
        assertTrue(stats.contains("心情标签 0 条"));
    }

    @Test
    void testRun_WithExistingData() throws Exception {
        // Given
        when(moodDAO.countByUserId("1")).thenReturn(5L); // 已有数据

        // When
        databaseInitService.run();

        // Then
        verify(moodDAO).countByUserId("1");
        // 不应该调用保存方法，因为已有数据
        verify(moodDAO, never()).save(any(Mood.class));
    }

    @Test
    void testRun_WithNoExistingData() throws Exception {
        // Given
        when(moodDAO.countByUserId("1")).thenReturn(0L); // 没有数据
        when(moodDAO.save(any(Mood.class))).thenReturn(new Mood());
        when(moodItemDAO.save(any(MoodItem.class))).thenReturn(new MoodItem());

        // When
        databaseInitService.run();

        // Then
        verify(moodDAO, times(2)).countByUserId("1"); // 一次在run()中，一次在initializeSampleData()中
        // 应该调用保存方法，因为需要初始化数据
        verify(moodDAO, times(8)).save(any(Mood.class));
    }

    @Test
    void testRun_WithException() throws Exception {
        // Given
        when(moodDAO.countByUserId("1")).thenThrow(new RuntimeException("查询失败"));

        // When & Then
        assertThrows(RuntimeException.class, () -> databaseInitService.run());
    }

    @Test
    void testManualInit_CreatesCorrectMoodRecords() {
        // Given
        when(moodDAO.save(any(Mood.class))).thenAnswer(invocation -> {
            Mood mood = invocation.getArgument(0);
            return mood;
        });
        when(moodItemDAO.save(any(MoodItem.class))).thenAnswer(invocation -> {
            MoodItem moodItem = invocation.getArgument(0);
            return moodItem;
        });

        // When
        databaseInitService.manualInit();

        // Then
        // 验证保存了正确数量的心情记录
        verify(moodDAO, times(8)).save(any(Mood.class));
        
        // 验证保存了心情标签
        verify(moodItemDAO, atLeastOnce()).save(any(MoodItem.class));
    }

    @Test
    void testManualInit_WithMoodItemSaveException() {
        // Given
        when(moodDAO.save(any(Mood.class))).thenReturn(new Mood());
        when(moodItemDAO.save(any(MoodItem.class))).thenThrow(new RuntimeException("标签保存失败"));

        // When & Then
        assertThrows(RuntimeException.class, () -> databaseInitService.manualInit());
    }

    @Test
    void testClearAllData_WithMoodDAOException() {
        // Given
        doNothing().when(moodItemDAO).deleteAll();
        doThrow(new RuntimeException("心情记录删除失败")).when(moodDAO).deleteAll();

        // When & Then
        assertThrows(RuntimeException.class, () -> databaseInitService.clearAllData());
    }

    @Test
    void testGetDataStats_WithMoodDAOException() {
        // Given
        when(moodDAO.countByUserId("1")).thenThrow(new RuntimeException("查询失败"));

        // When & Then
        assertThrows(RuntimeException.class, () -> databaseInitService.getDataStats());
    }

    @Test
    void testGetDataStats_WithMoodItemDAOException() {
        // Given
        when(moodDAO.countByUserId("1")).thenReturn(7L);
        when(moodItemDAO.countByLabel("开心")).thenThrow(new RuntimeException("标签查询失败"));

        // When & Then
        assertThrows(RuntimeException.class, () -> databaseInitService.getDataStats());
    }
} 