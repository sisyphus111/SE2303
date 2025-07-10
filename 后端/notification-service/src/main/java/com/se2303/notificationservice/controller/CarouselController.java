package com.se2303.notificationservice.controller;

import com.se2303.notificationservice.dto.ApiResponse;
import com.se2303.notificationservice.dto.CreateCarouselImageRequest;
import com.se2303.notificationservice.service.CarouselService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 轮播图控制器
 */
@RestController
@RequestMapping("/api/notification")
public class CarouselController {
    
    @Autowired
    private CarouselService carouselService;
    
    /**
     * 创建轮播图
     * POST /api/notification/carousel/create
     */
    @PostMapping("/carousel/create")
    public ResponseEntity<ApiResponse> createCarouselImage(@Valid @RequestBody CreateCarouselImageRequest request) {
        try {
            ApiResponse response = carouselService.createCarouselImage(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("创建轮播图失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取轮播图列表
     * GET /api/notification/carousel/list
     */
    @GetMapping("/carousel/list")
    public ResponseEntity<ApiResponse> getCarouselList(@RequestParam(required = false) String category) {
        return ResponseEntity.ok(carouselService.getCarouselImages(category));
    }
    
    /**
     * 获取所有轮播图
     * GET /api/notification/carousel/all
     */
    @GetMapping("/carousel/all")
    public ResponseEntity<ApiResponse> getAllCarouselImages() {
        return ResponseEntity.ok(carouselService.getAllCarouselImages());
    }
} 