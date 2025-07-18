package com.se2303.moodservice.service;

import com.se2303.moodservice.dao.MoodDAO;
import com.se2303.moodservice.dao.MoodItemDAO;
import com.se2303.moodservice.dto.*;
import com.se2303.moodservice.model.Mood;
import com.se2303.moodservice.model.MoodItem;
import com.se2303.moodservice.service.impl.MoodServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoodServiceTest {

    @Mock
    private MoodDAO moodDAO;

    @Mock
    private MoodItemDAO moodItemDAO;

    @InjectMocks
    private MoodServiceImpl moodService;

    private MoodCheckInRequest moodCheckInRequest;
    private CheckInRequest checkInRequest;
    private DiaryRequest diaryRequest;
    private Mood mood;
    private MoodItem moodItem;

    @BeforeEach
    void setUp() {
        // 设置测试数据
        moodCheckInRequest = new MoodCheckInRequest();
        moodCheckInRequest.setScore(4);
        moodCheckInRequest.setDate(LocalDate.now());
        moodCheckInRequest.setDiaryTitle("测试日记");
        moodCheckInRequest.setDiaryDescription("今天心情不错");
        moodCheckInRequest.setMoodLabels(Arrays.asList("开心", "满足"));

        checkInRequest = new CheckInRequest();
        checkInRequest.setScore(3);
        checkInRequest.setDate(LocalDate.now());
        checkInRequest.setMoodLabels(Arrays.asList("平静"));

        diaryRequest = new DiaryRequest();
        diaryRequest.setDate(LocalDate.now());
        diaryRequest.setDiaryTitle("测试日记");
        diaryRequest.setDiaryDescription("今天的心情记录");

        mood = new Mood();
        mood.setId(1L);
        mood.setUserId("testUser");
        mood.setDate(LocalDate.now());
        mood.setScore(4);
        mood.setScoreCommitted(true);
        mood.setDiaryTitle("测试日记");
        mood.setDiaryDescription("今天心情不错");
        mood.setDiaryCommitted(true);

        moodItem = new MoodItem("开心", mood);
    }

    @Test
    void testCheckIn_Success() {
        // Given
        when(moodDAO.findByUserIdAndDate(eq("testUser"), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(moodDAO.save(any(Mood.class))).thenReturn(mood);
        when(moodItemDAO.save(any(MoodItem.class))).thenReturn(moodItem);

        // When
        MoodCheckInResponse response = moodService.checkIn("testUser", moodCheckInRequest);

        // Then
        assertNotNull(response);
        assertEquals("testUser", response.getUserId());
        assertEquals(4, response.getScore());
        assertEquals("测试日记", response.getDiaryTitle());
        verify(moodDAO).save(any(Mood.class));
        verify(moodItemDAO, times(2)).save(any(MoodItem.class));
    }

    @Test
    void testCheckIn_UpdateExisting() {
        // Given
        when(moodDAO.findByUserIdAndDate(eq("testUser"), any(LocalDate.class)))
                .thenReturn(Optional.of(mood));
        when(moodDAO.save(any(Mood.class))).thenReturn(mood);
        when(moodItemDAO.save(any(MoodItem.class))).thenReturn(moodItem);

        // When
        MoodCheckInResponse response = moodService.checkIn("testUser", moodCheckInRequest);

        // Then
        assertNotNull(response);
        verify(moodDAO).save(any(Mood.class));
    }

    @Test
    void testCheckInOnly_Success() {
        // Given
        when(moodDAO.findByUserIdAndDate(eq("testUser"), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(moodItemDAO.save(any(MoodItem.class))).thenReturn(moodItem);
        when(moodItemDAO.findByMoodId(1L)).thenReturn(Arrays.asList(moodItem));
        
        // Mock save to return a mood with ID
        when(moodDAO.save(any(Mood.class))).thenAnswer(invocation -> {
            Mood savedMood = invocation.getArgument(0);
            savedMood.setId(1L); // Set ID after save
            return savedMood;
        });

        // When
        MoodCheckInResponse response = moodService.checkInOnly("testUser", checkInRequest);

        // Then
        assertNotNull(response);
        assertEquals(3, response.getScore());
        assertNull(response.getDiaryTitle());
        
        // 使用 ArgumentCaptor 验证保存的对象
        ArgumentCaptor<Mood> moodCaptor = ArgumentCaptor.forClass(Mood.class);
        verify(moodDAO).save(moodCaptor.capture());
        Mood savedMood = moodCaptor.getValue();
        assertEquals(3, savedMood.getScore());
        assertEquals("testUser", savedMood.getUserId());
        assertTrue(savedMood.getScoreCommitted());
        assertFalse(savedMood.getDiaryCommitted());
        
        verify(moodItemDAO, times(1)).save(any(MoodItem.class));
    }

    @Test
    void testWriteDiaryOnly_Success() {
        // Given
        when(moodDAO.findByUserIdAndDate(eq("testUser"), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(moodDAO.save(any(Mood.class))).thenReturn(mood);

        // When
        MoodCheckInResponse response = moodService.writeDiaryOnly("testUser", diaryRequest);

        // Then
        assertNotNull(response);
        assertEquals("测试日记", response.getDiaryTitle());
        assertEquals("今天心情不错", response.getDiaryDescription());
        verify(moodDAO).save(any(Mood.class));
        verify(moodItemDAO, never()).save(any(MoodItem.class));
    }

    @Test
    void testFindById_Success() {
        // Given
        when(moodDAO.findById(1L)).thenReturn(Optional.of(mood));
        when(moodItemDAO.findByMoodId(1L)).thenReturn(Arrays.asList(moodItem));

        // When
        MoodCheckInResponse response = moodService.findById(1L);

        // Then
        assertNotNull(response);
        assertEquals("testUser", response.getUserId());
        assertEquals(4, response.getScore());
    }

    @Test
    void testFindById_NotFound() {
        // Given
        when(moodDAO.findById(1L)).thenReturn(Optional.empty());

        // When
        MoodCheckInResponse response = moodService.findById(1L);

        // Then
        assertNull(response);
    }

    @Test
    void testFindByUserIdAndDate_Success() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodDAO.findByUserIdAndDate("testUser", date)).thenReturn(Optional.of(mood));
        when(moodItemDAO.findByMoodId(1L)).thenReturn(Arrays.asList(moodItem));

        // When
        MoodCheckInResponse response = moodService.findByUserIdAndDate("testUser", date);

        // Then
        assertNotNull(response);
        assertEquals("testUser", response.getUserId());
    }

    @Test
    void testFindByUserIdAndDate_NotFound() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodDAO.findByUserIdAndDate("testUser", date)).thenReturn(Optional.empty());

        // When
        MoodCheckInResponse response = moodService.findByUserIdAndDate("testUser", date);

        // Then
        assertNull(response);
    }

    @Test
    void testFindByUserId_Success() {
        // Given
        List<Mood> moods = Arrays.asList(mood);
        when(moodDAO.findByUserIdOrderByDateDesc("testUser")).thenReturn(moods);
        when(moodItemDAO.findByMoodId(1L)).thenReturn(Arrays.asList(moodItem));

        // When
        List<MoodCheckInResponse> responses = moodService.findByUserId("testUser");

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("testUser", responses.get(0).getUserId());
    }

    @Test
    void testFindByUserIdAndDateBetween_Success() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<Mood> moods = Arrays.asList(mood);
        when(moodDAO.findByUserIdAndDateBetweenOrderByDateDesc("testUser", startDate, endDate)).thenReturn(moods);
        when(moodItemDAO.findByMoodId(1L)).thenReturn(Arrays.asList(moodItem));

        // When
        List<MoodCheckInResponse> responses = moodService.findByUserIdAndDateBetween("testUser", startDate, endDate);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void testHasCheckedIn_True() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodDAO.hasCheckedIn("testUser", date)).thenReturn(true);

        // When
        boolean result = moodService.hasCheckedIn("testUser", date);

        // Then
        assertTrue(result);
    }

    @Test
    void testHasCheckedIn_False() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodDAO.hasCheckedIn("testUser", date)).thenReturn(false);

        // When
        boolean result = moodService.hasCheckedIn("testUser", date);

        // Then
        assertFalse(result);
    }

    @Test
    void testHasWrittenDiary_True() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodDAO.hasWrittenDiary("testUser", date)).thenReturn(true);

        // When
        boolean result = moodService.hasWrittenDiary("testUser", date);

        // Then
        assertTrue(result);
    }

    @Test
    void testHasWrittenDiary_False() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodDAO.hasWrittenDiary("testUser", date)).thenReturn(false);

        // When
        boolean result = moodService.hasWrittenDiary("testUser", date);

        // Then
        assertFalse(result);
    }

    @Test
    void testUpdateMood_Success() {
        // Given
        when(moodDAO.findById(1L)).thenReturn(Optional.of(mood));
        when(moodDAO.save(any(Mood.class))).thenReturn(mood);
        when(moodItemDAO.save(any(MoodItem.class))).thenReturn(moodItem);

        // When
        MoodCheckInResponse response = moodService.updateMood(1L, moodCheckInRequest);

        // Then
        assertNotNull(response);
        verify(moodDAO).save(any(Mood.class));
    }

    @Test
    void testUpdateMood_NotFound() {
        // Given
        when(moodDAO.findById(1L)).thenReturn(Optional.empty());

        // When
        MoodCheckInResponse response = moodService.updateMood(1L, moodCheckInRequest);

        // Then
        assertNull(response);
    }

    @Test
    void testDeleteMood_Success() {
        // Given
        // 不需要 mock findById，因为实际实现直接调用 deleteById

        // When
        moodService.deleteMood(1L);

        // Then
        verify(moodDAO).deleteById(1L);
    }

    @Test
    void testDeleteMood_NotFound() {
        // Given
        // 不需要 mock findById，因为实际实现直接调用 deleteById

        // When
        moodService.deleteMood(1L);

        // Then
        verify(moodDAO).deleteById(1L);
    }

    @Test
    void testDeleteDiary_Success() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodDAO.findByUserIdAndDate("testUser", date)).thenReturn(Optional.of(mood));
        when(moodDAO.save(any(Mood.class))).thenReturn(mood);

        // When
        MoodCheckInResponse response = moodService.deleteDiary("testUser", date);

        // Then
        assertNotNull(response);
        assertNull(response.getDiaryTitle());
        assertNull(response.getDiaryDescription());
        assertFalse(response.getDiaryCommitted());
    }

    @Test
    void testDeleteDiary_NotFound() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodDAO.findByUserIdAndDate("testUser", date)).thenReturn(Optional.empty());

        // When
        MoodCheckInResponse response = moodService.deleteDiary("testUser", date);

        // Then
        assertNull(response);
    }

    @Test
    void testGetCheckInCount() {
        // Given
        when(moodDAO.countByUserId("testUser")).thenReturn(5L);

        // When
        long count = moodService.getCheckInCount("testUser");

        // Then
        assertEquals(5L, count);
    }

    @Test
    void testGetCheckInCountWithRange() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(moodDAO.countByUserIdAndDateBetween("testUser", startDate, endDate)).thenReturn(3L);

        // When
        long count = moodService.getCheckInCount("testUser", startDate, endDate);

        // Then
        assertEquals(3L, count);
    }

    @Test
    void testGetDiaryCount() {
        // Given
        List<Mood> moods = Arrays.asList(mood, mood, mood, mood);
        when(moodDAO.findByUserIdAndDiaryCommittedTrueOrderByDateDesc("testUser")).thenReturn(moods);

        // When
        long count = moodService.getDiaryCount("testUser");

        // Then
        assertEquals(4L, count);
    }

    @Test
    void testGetDiaryCountWithRange() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<Mood> moods = Arrays.asList(mood, mood);
        when(moodDAO.findByUserIdAndDateBetweenOrderByDateDesc("testUser", startDate, endDate)).thenReturn(moods);

        // When
        long count = moodService.getDiaryCount("testUser", startDate, endDate);

        // Then
        assertEquals(2L, count);
    }

    @Test
    void testGetTodayMood_Success() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodDAO.findByUserIdAndDate("testUser", date)).thenReturn(Optional.of(mood));
        when(moodItemDAO.findByMoodId(1L)).thenReturn(Arrays.asList(moodItem));

        // When
        TodayMoodResponse response = moodService.getTodayMood("testUser", date);

        // Then
        assertNotNull(response);
        assertEquals("testUser", response.getUserId());
        assertEquals(4, response.getScore());
    }

    @Test
    void testGetTodayMood_NotFound() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodDAO.findByUserIdAndDate("testUser", date)).thenReturn(Optional.empty());

        // When
        TodayMoodResponse response = moodService.getTodayMood("testUser", date);

        // Then
        assertNotNull(response);
        assertEquals("testUser", response.getUserId());
        assertNull(response.getScore());
        assertEquals("neutral", response.getMoodTrend());
    }

    @Test
    void testGetTodayMood_CurrentDate() {
        // Given
        LocalDate today = LocalDate.now();
        when(moodDAO.findByUserIdAndDate("testUser", today)).thenReturn(Optional.of(mood));
        when(moodItemDAO.findByMoodId(1L)).thenReturn(Arrays.asList(moodItem));

        // When
        TodayMoodResponse response = moodService.getTodayMood("testUser");

        // Then
        assertNotNull(response);
        assertEquals("testUser", response.getUserId());
    }

    @Test
    void testQueryMoods_Success() {
        // Given
        MoodQueryRequest request = new MoodQueryRequest();
        request.setUserId("testUser");
        request.setMoodLabels(Arrays.asList("开心"));
        request.setStartDate(LocalDate.now().minusDays(7));
        request.setEndDate(LocalDate.now());
        request.setPage(0);
        request.setSize(10);
        
        List<Mood> moods = Arrays.asList(mood);
        when(moodDAO.findAllByOrderByDateDesc()).thenReturn(moods);
        when(moodItemDAO.findByMoodId(1L)).thenReturn(Arrays.asList(moodItem));

        // When
        List<MoodCheckInResponse> responses = moodService.queryMoods(request);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void testQueryMoods_NoFilters() {
        // Given
        MoodQueryRequest request = new MoodQueryRequest();
        request.setPage(0);
        request.setSize(10);
        List<Mood> moods = Arrays.asList(mood);
        when(moodDAO.findAllByOrderByDateDesc()).thenReturn(moods);
        when(moodItemDAO.findByMoodId(1L)).thenReturn(Arrays.asList(moodItem));

        // When
        List<MoodCheckInResponse> responses = moodService.queryMoods(request);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
    }
} 