package com.se2303.notificationservice.service;

import com.se2303.notificationservice.dto.ApiResponse;
import com.se2303.notificationservice.dto.CarouselImageResponse;
import com.se2303.notificationservice.dto.CreateCarouselImageRequest;
import com.se2303.notificationservice.model.CarouselImage;
import com.se2303.notificationservice.repository.CarouselImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarouselService {
    
    @Autowired
    private CarouselImageRepository carouselImageRepository;
    
    /**
     * 创建轮播图
     */
    public ApiResponse createCarouselImage(CreateCarouselImageRequest request) {
        try {
            CarouselImage carouselImage = new CarouselImage(
                request.getImageUrl(),
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                request.getSortOrder()
            );
            
            CarouselImage savedImage = carouselImageRepository.save(carouselImage);
            CarouselImageResponse response = convertToResponse(savedImage);
            
            return ApiResponse.success(response, "轮播图创建成功");
        } catch (Exception e) {
            return ApiResponse.error("创建轮播图失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取轮播图列表
     */
    public ApiResponse getCarouselList() {
        try {
            List<CarouselImage> images = carouselImageRepository.findAllByOrderBySortOrderAsc();
            List<CarouselImageResponse> responses = images.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(responses, "获取轮播图成功");
        } catch (Exception e) {
            return ApiResponse.error("获取轮播图失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取轮播图列表（按分类）
     */
    public ApiResponse getCarouselImages(String category) {
        try {
            List<CarouselImage> images;
            
            if (category != null && !category.trim().isEmpty()) {
                images = carouselImageRepository.findByCategoryOrderBySortOrderAsc(category.trim());
            } else {
                images = carouselImageRepository.findAllByOrderBySortOrderAsc();
            }
            
            List<CarouselImageResponse> responses = images.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(responses, "获取轮播图成功");
        } catch (Exception e) {
            return ApiResponse.error("获取轮播图失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有轮播图
     */
    public ApiResponse getAllCarouselImages() {
        try {
            List<CarouselImage> images = carouselImageRepository.findAllByOrderBySortOrderAsc();
            List<CarouselImageResponse> responses = images.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(responses, "获取所有轮播图成功");
        } catch (Exception e) {
            return ApiResponse.error("获取所有轮播图失败: " + e.getMessage());
        }
    }
    
    /**
     * 将CarouselImage实体转换为CarouselImageResponse
     */
    private CarouselImageResponse convertToResponse(CarouselImage image) {
        return new CarouselImageResponse(
                image.getId(),
                image.getImageUrl(),
                image.getTitle(),
                image.getDescription(),
                image.getCategory(),
                image.getSortOrder(),
                image.getCreatedAt()
        );
    }
} 