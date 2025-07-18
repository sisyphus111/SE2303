package com.se2303.moodservice.controller;

import com.se2303.moodservice.dto.ApiResponse;
import com.se2303.moodservice.dto.CheckInRequest;
import com.se2303.moodservice.dto.DiaryRequest;
import com.se2303.moodservice.dto.MoodCheckInRequest;
import com.se2303.moodservice.dto.MoodCheckInResponse;
import com.se2303.moodservice.dto.MoodQueryRequest;
import com.se2303.moodservice.dto.TodayMoodResponse;
import com.se2303.moodservice.model.MoodLabel;
import com.se2303.moodservice.service.DatabaseInitService;
import com.se2303.moodservice.service.MoodService;
import com.se2303.moodservice.service.MoodAnalysisService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 心情打卡控制器
 * 注意：已删除@CrossOrigin注解，因为网关统一处理CORS
 */
@RestController
@RequestMapping("/api/mood")
public class MoodController {
    
    @Autowired
    private MoodService moodService;
    
    @Autowired
    private DatabaseInitService databaseInitService;
    
    @Autowired
    private MoodAnalysisService moodAnalysisService;
    
    /**
     * 用户心情打卡（包含日记）
     * POST /api/mood/check-in
     */
    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse> checkIn(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Identity") boolean identity,
            @Valid @RequestBody MoodCheckInRequest request) {
        MoodCheckInResponse response = moodService.checkIn(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "心情打卡成功"));
    }
    
    /**
     * 只打卡不写日记
     * POST /api/mood/check-in-only
     */
    @PostMapping("/check-in-only")
    public ResponseEntity<ApiResponse> checkInOnly(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Identity") boolean identity,
            @Valid @RequestBody CheckInRequest request) {
        MoodCheckInResponse response = moodService.checkInOnly(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "心情打卡成功"));
    }
    
    /**
     * 只写日记不打卡
     * POST /api/mood/diary-only
     */
    @PostMapping("/diary-only")
    public ResponseEntity<ApiResponse> writeDiaryOnly(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Identity") boolean identity,
            @Valid @RequestBody DiaryRequest request) {
        MoodCheckInResponse response = moodService.writeDiaryOnly(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "日记保存成功"));
    }
    
    /**
     * 根据ID获取心情记录
     * GET /api/mood/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getMoodById(@PathVariable Long id) {
        MoodCheckInResponse response = moodService.findById(id);
        if (response != null) {
            return ResponseEntity.ok(ApiResponse.success(response, "获取心情记录成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("心情记录不存在"));
        }
    }
    
    /**
     * 根据用户ID和日期获取心情记录
     * GET /api/mood/user/{userId}/date/{date}
     */
    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<ApiResponse> getMoodByUserIdAndDate(
            @PathVariable String userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        MoodCheckInResponse response = moodService.findByUserIdAndDate(userId, date);
        if (response != null) {
            return ResponseEntity.ok(ApiResponse.success(response, "获取心情记录成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("该日期的心情记录不存在"));
        }
    }
    
    /**
     * 根据用户ID获取所有心情记录
     * GET /api/mood/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getMoodsByUserId(@PathVariable String userId) {
        List<MoodCheckInResponse> responses = moodService.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(responses, "获取用户心情记录成功"));
    }
    
    /**
     * 根据用户ID和日期范围获取心情记录
     * GET /api/mood/user/{userId}/range?startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/user/{userId}/range")
    public ResponseEntity<ApiResponse> getMoodsByUserIdAndDateRange(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<MoodCheckInResponse> responses = moodService.findByUserIdAndDateBetween(userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(responses, "获取日期范围心情记录成功"));
    }
    
    /**
     * 检查用户是否已打卡
     * GET /api/mood/user/{userId}/check-status/{date}
     */
    @GetMapping("/user/{userId}/check-status/{date}")
    public ResponseEntity<ApiResponse> checkStatus(
            @PathVariable String userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        boolean hasCheckedIn = moodService.hasCheckedIn(userId, date);
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("date", date);
        data.put("hasCheckedIn", hasCheckedIn);
        return ResponseEntity.ok(ApiResponse.success(data, "获取打卡状态成功"));
    }
    
    /**
     * 检查用户是否已写日记
     * GET /api/mood/user/{userId}/diary-status/{date}
     */
    @GetMapping("/user/{userId}/diary-status/{date}")
    public ResponseEntity<ApiResponse> checkDiaryStatus(
            @PathVariable String userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        boolean hasWrittenDiary = moodService.hasWrittenDiary(userId, date);
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("date", date);
        data.put("hasWrittenDiary", hasWrittenDiary);
        return ResponseEntity.ok(ApiResponse.success(data, "获取日记状态成功"));
    }
    
    /**
     * 更新心情记录
     * PUT /api/mood/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateMood(
            @PathVariable Long id,
            @Valid @RequestBody MoodCheckInRequest request) {
        MoodCheckInResponse response = moodService.updateMood(id, request);
        if (response != null) {
            return ResponseEntity.ok(ApiResponse.success(response, "心情记录更新成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("心情记录不存在"));
        }
    }
    
    /**
     * 删除心情记录
     * DELETE /api/mood/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteMood(@PathVariable Long id) {
        moodService.deleteMood(id);
        return ResponseEntity.ok(ApiResponse.success("心情记录删除成功"));
    }
    
    /**
     * 删除某一天的日记内容
     * DELETE /api/mood/user/{userId}/diary/{date}
     */
    @DeleteMapping("/user/{userId}/diary/{date}")
    public ResponseEntity<ApiResponse> deleteDiary(
            @PathVariable String userId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        MoodCheckInResponse response = moodService.deleteDiary(userId, date);
        if (response != null) {
            return ResponseEntity.ok(ApiResponse.success(response, "日记删除成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("日记不存在"));
        }
    }
    
    /**
     * 获取用户打卡统计
     * GET /api/mood/user/{userId}/stats
     */
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<ApiResponse> getUserStats(@PathVariable String userId) {
        long totalCheckIns = moodService.getCheckInCount(userId);
        long totalDiaries = moodService.getDiaryCount(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("totalCheckIns", totalCheckIns);
        data.put("totalDiaries", totalDiaries);
        return ResponseEntity.ok(ApiResponse.success(data, "获取用户统计成功"));
    }
    
    /**
     * 获取用户在指定日期范围内的打卡统计
     * GET /api/mood/user/{userId}/stats/range?startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/user/{userId}/stats/range")
    public ResponseEntity<ApiResponse> getUserStatsByRange(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        long checkInCount = moodService.getCheckInCount(userId, startDate, endDate);
        long diaryCount = moodService.getDiaryCount(userId, startDate, endDate);
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("startDate", startDate);
        data.put("endDate", endDate);
        data.put("checkInCount", checkInCount);
        data.put("diaryCount", diaryCount);
        return ResponseEntity.ok(ApiResponse.success(data, "获取日期范围统计成功"));
    }
    
    /**
     * 获取所有可用的心情标签
     * GET /api/mood/labels
     */
    @GetMapping("/labels")
    public ResponseEntity<ApiResponse> getAvailableLabels() {
        Map<String, Object> data = new HashMap<>();
        data.put("positive", Arrays.stream(MoodLabel.getPositiveMoods()).map(MoodLabel::getLabel).toArray(String[]::new));
        data.put("neutral", Arrays.stream(MoodLabel.getNeutralMoods()).map(MoodLabel::getLabel).toArray(String[]::new));
        data.put("negative", Arrays.stream(MoodLabel.getNegativeMoods()).map(MoodLabel::getLabel).toArray(String[]::new));
        return ResponseEntity.ok(ApiResponse.success(data, "获取心情标签成功"));
    }
    
    /**
     * 健康检查接口
     * GET /api/mood/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> health() {
        Map<String, String> data = new HashMap<>();
        data.put("status", "UP");
        data.put("service", "mood-service");
        return ResponseEntity.ok(ApiResponse.success(data, "服务运行正常"));
    }
    
    /**
     * 获取数据统计
     * GET /api/mood/data-stats
     */
    @GetMapping("/data-stats")
    public ResponseEntity<ApiResponse> getDataStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("stats", databaseInitService.getDataStats());
        return ResponseEntity.ok(ApiResponse.success(data, "获取数据统计成功"));
    }
    
    /**
     * 手动初始化示例数据
     * POST /api/mood/init-data
     */
    @PostMapping("/init-data")
    public ResponseEntity<ApiResponse> initSampleData() {
        databaseInitService.manualInit();
        Map<String, Object> data = new HashMap<>();
        data.put("stats", databaseInitService.getDataStats());
        return ResponseEntity.ok(ApiResponse.success(data, "示例数据初始化成功"));
    }
    
    /**
     * 清空所有数据
     * DELETE /api/mood/clear-data
     */
    @DeleteMapping("/clear-data")
    public ResponseEntity<ApiResponse> clearAllData() {
        databaseInitService.clearAllData();
        return ResponseEntity.ok(ApiResponse.success("所有数据已清空"));
    }
    
    /**
     * 获取今日心情（包含标签权值计算）
     * GET /api/mood/today/{userId}
     */
    @GetMapping("/today/{userId}")
    public ResponseEntity<ApiResponse> getTodayMood(@PathVariable String userId) {
        TodayMoodResponse response = moodService.getTodayMood(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "获取今日心情成功"));
    }
    
    /**
     * 获取指定日期的今日心情（包含标签权值计算）
     * GET /api/mood/today/{userId}/{date}
     */
    @GetMapping("/today/{userId}/{date}")
    public ResponseEntity<ApiResponse> getTodayMoodByDate(
            @PathVariable String userId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        TodayMoodResponse response = moodService.getTodayMood(userId, date);
        return ResponseEntity.ok(ApiResponse.success(response, "获取指定日期心情成功"));
    }
    
    /**
     * 灵活查询心情记录
     * 支持按用户、情绪标签、时间范围进行筛选
     * 如果不提供任何筛选条件，则返回所有数据
     * POST /api/mood/query
     */
    @PostMapping("/query")
    public ResponseEntity<ApiResponse> queryMoods(@RequestBody MoodQueryRequest request) {
        try {
            List<MoodCheckInResponse> responses = moodService.queryMoods(request);
            Map<String, Object> data = new HashMap<>();
            data.put("moods", responses);
            data.put("total", responses.size());
            data.put("page", request.getPage());
            data.put("size", request.getSize());
            return ResponseEntity.ok(ApiResponse.success(data, "查询心情记录成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }
    
    /**
     * 灵活查询心情记录（GET方式）
     * 支持按用户、情绪标签、时间范围进行筛选
     * 如果不提供任何筛选条件，则返回所有数据
     * GET /api/mood/query?userId=xxx&moodLabels=happy,sad&startDate=2024-01-01&endDate=2024-01-31&page=0&size=20
     */
    @GetMapping("/query")
    public ResponseEntity<ApiResponse> queryMoodsGet(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) List<String> moodLabels,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        try {
            MoodQueryRequest request = new MoodQueryRequest();
            request.setUserId(userId);
            request.setMoodLabels(moodLabels);
            request.setStartDate(startDate);
            request.setEndDate(endDate);
            request.setPage(page);
            request.setSize(size);
            
            List<MoodCheckInResponse> responses = moodService.queryMoods(request);
            Map<String, Object> data = new HashMap<>();
            data.put("moods", responses);
            data.put("total", responses.size());
            data.put("page", page);
            data.put("size", size);
            return ResponseEntity.ok(ApiResponse.success(data, "查询心情记录成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户心情分析报告
     * GET /api/mood/analysis/{userId}
     */
    @GetMapping("/analysis/{userId}")
    public ResponseEntity<ApiResponse> getAnalysisReport(@PathVariable String userId) {
        String analysisReport = moodAnalysisService.generateAnalysisReport(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("analysis", analysisReport);
        return ResponseEntity.ok(ApiResponse.success(data, "获取分析报告成功"));
    }
    
    /**
     * 根据日期范围获取用户心情分析报告
     * GET /api/mood/analysis/{userId}/range?startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/analysis/{userId}/range")
    public ResponseEntity<ApiResponse> getAnalysisReportByRange(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String analysisReport = moodAnalysisService.generateAnalysisReport(userId, startDate.toString(), endDate.toString());
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("startDate", startDate);
        data.put("endDate", endDate);
        data.put("analysis", analysisReport);
        return ResponseEntity.ok(ApiResponse.success(data, "获取分析报告成功"));
    }
} 