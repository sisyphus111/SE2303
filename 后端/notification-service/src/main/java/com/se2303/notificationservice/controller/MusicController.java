package com.se2303.notificationservice.controller;

import com.se2303.notificationservice.dto.ApiResponse;
import com.se2303.notificationservice.dto.CreateMusicRequest;
import com.se2303.notificationservice.service.MusicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 音乐控制器
 */
@RestController
@RequestMapping("/api/notification")
public class MusicController {
    
    @Autowired
    private MusicService musicService;
    
    /**
     * 创建音乐
     * POST /api/notification/music/create
     */
    @PostMapping("/music/create")
    public ResponseEntity<ApiResponse> createMusic(@Valid @RequestBody CreateMusicRequest request) {
        try {
            ApiResponse response = musicService.createMusic(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("创建音乐失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取音乐列表
     * GET /api/notification/music/list
     */
    @GetMapping("/music/list")
    public ResponseEntity<ApiResponse> getMusicList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(musicService.getMusicList(page, size));
    }
    
    /**
     * 搜索音乐
     * GET /api/notification/music/search
     */
    @GetMapping("/music/search")
    public ResponseEntity<ApiResponse> searchMusic(@RequestParam String keyword) {
        return ResponseEntity.ok(musicService.searchMusic(keyword));
    }
    
    /**
     * 根据分类获取音乐
     * GET /api/notification/music/category/{category}
     */
    @GetMapping("/music/category/{category}")
    public ResponseEntity<ApiResponse> getMusicByCategory(@PathVariable String category) {
        return ResponseEntity.ok(musicService.getMusicByCategory(category));
    }
    
    /**
     * 获取所有音乐
     * GET /api/notification/music/all
     */
    @GetMapping("/music/all")
    public ResponseEntity<ApiResponse> getAllMusic() {
        return ResponseEntity.ok(musicService.getAllMusic());
    }
} 