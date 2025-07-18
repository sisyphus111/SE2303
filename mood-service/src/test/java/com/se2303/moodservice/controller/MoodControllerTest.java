package com.se2303.moodservice.controller;

import com.se2303.moodservice.dto.*;
import com.se2303.moodservice.service.DatabaseInitService;
import com.se2303.moodservice.service.MoodAnalysisService;
import com.se2303.moodservice.service.MoodService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
class MoodControllerTest {

    @Mock
    private MoodService moodService;

    @Mock
    private DatabaseInitService databaseInitService;

    @Mock
    private MoodAnalysisService moodAnalysisService;

    @InjectMocks
    private MoodController moodController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private MoodCheckInRequest moodCheckInRequest;
    private CheckInRequest checkInRequest;
    private DiaryRequest diaryRequest;
    private MoodCheckInResponse moodCheckInResponse;
    private TodayMoodResponse todayMoodResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(moodController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

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

        moodCheckInResponse = new MoodCheckInResponse();
        moodCheckInResponse.setId(1L);
        moodCheckInResponse.setUserId("testUser");
        moodCheckInResponse.setScore(4);
        moodCheckInResponse.setDiaryTitle("测试日记");
        moodCheckInResponse.setDiaryDescription("今天心情不错");
        moodCheckInResponse.setMoodLabels(Arrays.asList("开心", "满足"));

        todayMoodResponse = new TodayMoodResponse();
        todayMoodResponse.setUserId("testUser");
        todayMoodResponse.setScore(4);
        todayMoodResponse.setMoodTrend("积极");
    }

    @Test
    void testCheckIn_Success() throws Exception {
        // Given
        when(moodService.checkIn(anyString(), any(MoodCheckInRequest.class)))
                .thenReturn(moodCheckInResponse);

        // When & Then
        mockMvc.perform(post("/api/mood/check-in")
                        .header("X-User-Id", "testUser")
                        .header("X-Identity", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moodCheckInRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("心情打卡成功"))
                .andExpect(jsonPath("$.object.userId").value("testUser"));

        verify(moodService).checkIn(eq("testUser"), any(MoodCheckInRequest.class));
    }

    @Test
    void testCheckInOnly_Success() throws Exception {
        // Given
        when(moodService.checkInOnly(anyString(), any(CheckInRequest.class)))
                .thenReturn(moodCheckInResponse);

        // When & Then
        mockMvc.perform(post("/api/mood/check-in-only")
                        .header("X-User-Id", "testUser")
                        .header("X-Identity", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkInRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("心情打卡成功"));

        verify(moodService).checkInOnly(eq("testUser"), any(CheckInRequest.class));
    }

    @Test
    void testWriteDiaryOnly_Success() throws Exception {
        // Given
        when(moodService.writeDiaryOnly(anyString(), any(DiaryRequest.class)))
                .thenReturn(moodCheckInResponse);

        // When & Then
        mockMvc.perform(post("/api/mood/diary-only")
                        .header("X-User-Id", "testUser")
                        .header("X-Identity", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diaryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("日记保存成功"));

        verify(moodService).writeDiaryOnly(eq("testUser"), any(DiaryRequest.class));
    }

    @Test
    void testGetMoodById_Success() {
        // Given
        when(moodService.findById(1L)).thenReturn(moodCheckInResponse);

        // When
        ResponseEntity<ApiResponse> response = moodController.getMoodById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取心情记录成功", response.getBody().getMessage());
        assertEquals(moodCheckInResponse, response.getBody().getObject());
    }

    @Test
    void testGetMoodById_NotFound() {
        // Given
        when(moodService.findById(1L)).thenReturn(null);

        // When
        ResponseEntity<ApiResponse> response = moodController.getMoodById(1L);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("心情记录不存在", response.getBody().getMessage());
    }

    @Test
    void testGetMoodByUserIdAndDate_Success() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodService.findByUserIdAndDate("testUser", date)).thenReturn(moodCheckInResponse);

        // When
        ResponseEntity<ApiResponse> response = moodController.getMoodByUserIdAndDate("testUser", date);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取心情记录成功", response.getBody().getMessage());
    }

    @Test
    void testGetMoodByUserIdAndDate_NotFound() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodService.findByUserIdAndDate("testUser", date)).thenReturn(null);

        // When
        ResponseEntity<ApiResponse> response = moodController.getMoodByUserIdAndDate("testUser", date);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("该日期的心情记录不存在", response.getBody().getMessage());
    }

    @Test
    void testGetMoodsByUserId_Success() {
        // Given
        List<MoodCheckInResponse> responses = Arrays.asList(moodCheckInResponse);
        when(moodService.findByUserId("testUser")).thenReturn(responses);

        // When
        ResponseEntity<ApiResponse> response = moodController.getMoodsByUserId("testUser");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取用户心情记录成功", response.getBody().getMessage());
        assertEquals(responses, response.getBody().getObject());
    }

    @Test
    void testGetMoodsByUserIdAndDateRange_Success() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<MoodCheckInResponse> responses = Arrays.asList(moodCheckInResponse);
        when(moodService.findByUserIdAndDateBetween("testUser", startDate, endDate)).thenReturn(responses);

        // When
        ResponseEntity<ApiResponse> response = moodController.getMoodsByUserIdAndDateRange("testUser", startDate, endDate);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取日期范围心情记录成功", response.getBody().getMessage());
    }

    @Test
    void testCheckStatus_Success() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodService.hasCheckedIn("testUser", date)).thenReturn(true);

        // When
        ResponseEntity<ApiResponse> response = moodController.checkStatus("testUser", date);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取打卡状态成功", response.getBody().getMessage());
    }

    @Test
    void testCheckDiaryStatus_Success() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodService.hasWrittenDiary("testUser", date)).thenReturn(true);

        // When
        ResponseEntity<ApiResponse> response = moodController.checkDiaryStatus("testUser", date);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取日记状态成功", response.getBody().getMessage());
    }

    @Test
    void testUpdateMood_Success() {
        // Given
        when(moodService.updateMood(1L, moodCheckInRequest)).thenReturn(moodCheckInResponse);

        // When
        ResponseEntity<ApiResponse> response = moodController.updateMood(1L, moodCheckInRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("心情记录更新成功", response.getBody().getMessage());
    }

    @Test
    void testUpdateMood_NotFound() {
        // Given
        when(moodService.updateMood(1L, moodCheckInRequest)).thenReturn(null);

        // When
        ResponseEntity<ApiResponse> response = moodController.updateMood(1L, moodCheckInRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("心情记录不存在", response.getBody().getMessage());
    }

    @Test
    void testDeleteMood_Success() {
        // Given
        doNothing().when(moodService).deleteMood(1L);

        // When
        ResponseEntity<ApiResponse> response = moodController.deleteMood(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("心情记录删除成功", response.getBody().getMessage());
    }

    @Test
    void testDeleteDiary_Success() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodService.deleteDiary("testUser", date)).thenReturn(moodCheckInResponse);

        // When
        ResponseEntity<ApiResponse> response = moodController.deleteDiary("testUser", date);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("日记删除成功", response.getBody().getMessage());
    }

    @Test
    void testDeleteDiary_NotFound() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodService.deleteDiary("testUser", date)).thenReturn(null);

        // When
        ResponseEntity<ApiResponse> response = moodController.deleteDiary("testUser", date);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("日记不存在", response.getBody().getMessage());
    }

    @Test
    void testGetUserStats_Success() {
        // Given
        when(moodService.getCheckInCount("testUser")).thenReturn(5L);
        when(moodService.getDiaryCount("testUser")).thenReturn(3L);

        // When
        ResponseEntity<ApiResponse> response = moodController.getUserStats("testUser");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testGetUserStatsByRange_Success() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(moodService.getCheckInCount("testUser", startDate, endDate)).thenReturn(3L);
        when(moodService.getDiaryCount("testUser", startDate, endDate)).thenReturn(2L);

        // When
        ResponseEntity<ApiResponse> response = moodController.getUserStatsByRange("testUser", startDate, endDate);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void testGetAvailableLabels_Success() {
        // When
        ResponseEntity<ApiResponse> response = moodController.getAvailableLabels();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取心情标签成功", response.getBody().getMessage());
    }

    @Test
    void testHealth_Success() {
        // When
        ResponseEntity<ApiResponse> response = moodController.health();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("服务运行正常", response.getBody().getMessage());
    }

    @Test
    void testGetDataStats_Success() {
        // Given
        when(databaseInitService.getDataStats()).thenReturn("数据统计: 心情记录 10 条, 心情标签 20 条");

        // When
        ResponseEntity<ApiResponse> response = moodController.getDataStats();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取数据统计成功", response.getBody().getMessage());
    }

    @Test
    void testInitSampleData_Success() {
        // Given
        doNothing().when(databaseInitService).manualInit();

        // When
        ResponseEntity<ApiResponse> response = moodController.initSampleData();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("示例数据初始化成功", response.getBody().getMessage());
    }

    @Test
    void testGetTodayMood_Success() {
        // Given
        when(moodService.getTodayMood("testUser")).thenReturn(todayMoodResponse);

        // When
        ResponseEntity<ApiResponse> response = moodController.getTodayMood("testUser");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取今日心情成功", response.getBody().getMessage());
    }

    @Test
    void testGetTodayMoodByDate_Success() {
        // Given
        LocalDate date = LocalDate.now();
        when(moodService.getTodayMood("testUser", date)).thenReturn(todayMoodResponse);

        // When
        ResponseEntity<ApiResponse> response = moodController.getTodayMoodByDate("testUser", date);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取指定日期心情成功", response.getBody().getMessage());
    }

    @Test
    void testQueryMoods_Success() {
        // Given
        MoodQueryRequest request = new MoodQueryRequest();
        request.setUserId("testUser");
        List<MoodCheckInResponse> responses = Arrays.asList(moodCheckInResponse);
        when(moodService.queryMoods(request)).thenReturn(responses);

        // When
        ResponseEntity<ApiResponse> response = moodController.queryMoods(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("查询心情记录成功", response.getBody().getMessage());
    }

    @Test
    void testQueryMoodsGet_Success() {
        // Given
        MoodQueryRequest request = new MoodQueryRequest();
        request.setUserId("testUser");
        List<MoodCheckInResponse> responses = Arrays.asList(moodCheckInResponse);
        when(moodService.queryMoods(any(MoodQueryRequest.class))).thenReturn(responses);

        // When
        ResponseEntity<ApiResponse> response = moodController.queryMoodsGet("testUser", null, null, null, 0, 20);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("查询心情记录成功", response.getBody().getMessage());
    }

    @Test
    void testGetAnalysisReport_Success() {
        // Given
        when(moodAnalysisService.generateAnalysisReport("testUser")).thenReturn("分析报告内容");

        // When
        ResponseEntity<ApiResponse> response = moodController.getAnalysisReport("testUser");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取分析报告成功", response.getBody().getMessage());
    }

    @Test
    void testGetAnalysisReportByRange_Success() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(moodAnalysisService.generateAnalysisReport("testUser", startDate.toString(), endDate.toString()))
                .thenReturn("分析报告内容");

        // When
        ResponseEntity<ApiResponse> response = moodController.getAnalysisReportByRange("testUser", startDate, endDate);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("获取分析报告成功", response.getBody().getMessage());
    }
} 