package com.se2303.notificationservice.service;

import com.se2303.notificationservice.dto.ApiResponse;
import com.se2303.notificationservice.dto.CarouselImageResponse;
import com.se2303.notificationservice.dto.CreateCarouselImageRequest;
import com.se2303.notificationservice.dto.UpdateCarouselImageRequest;
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
     * 按id正序分页获取轮播图
     */
    public ApiResponse getCarouselListByIdAsc(int page, int size) {
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page - 1, size);
            org.springframework.data.domain.Page<CarouselImage> carouselPage = carouselImageRepository.findAllByOrderByIdAsc(pageable);
            List<CarouselImageResponse> responses = carouselPage.getContent().stream()
                    .map(this::convertToResponse)
                    .collect(java.util.stream.Collectors.toList());
            long total = carouselPage.getTotalElements();
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("list", responses);
            result.put("total", total);
            return ApiResponse.success(result, "按id正序分页获取轮播图成功");
        } catch (Exception e) {
            return ApiResponse.error("按id正序分页获取轮播图失败: " + e.getMessage());
        }
    }

    /**
     * 删除轮播图
     */
    public ApiResponse deleteCarouselImage(Long id) {
        try {
            if (id == null) {
                return ApiResponse.error("轮播图ID不能为空");
            }
            if (!carouselImageRepository.existsById(id)) {
                return ApiResponse.error("轮播图不存在");
            }
            carouselImageRepository.deleteById(id);
            return ApiResponse.success(null, "轮播图删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除轮播图失败: " + e.getMessage());
        }
    }

    /**
     * 更新轮播图
     */
    public ApiResponse updateCarouselImage(Long id, com.se2303.notificationservice.dto.UpdateCarouselImageRequest request) {
        try {
            if (id == null) {
                return ApiResponse.error("轮播图ID不能为空");
            }
            if (!carouselImageRepository.existsById(id)) {
                return ApiResponse.error("轮播图不存在");
            }
            CarouselImage image = carouselImageRepository.findById(id).orElse(null);
            if (image == null) {
                return ApiResponse.error("轮播图不存在");
            }
            if (request.getImageUrl() != null) {
                image.setImageUrl(request.getImageUrl());
            }
            if (request.getTitle() != null) {
                image.setTitle(request.getTitle());
            }
            if (request.getDescription() != null) {
                image.setDescription(request.getDescription());
            }
            if (request.getCategory() != null) {
                image.setCategory(request.getCategory());
            }
            if (request.getSortOrder() != null) {
                image.setSortOrder(request.getSortOrder());
            }
            CarouselImage updated = carouselImageRepository.save(image);
            CarouselImageResponse response = convertToResponse(updated);
            return ApiResponse.success(response, "轮播图更新成功");
        } catch (Exception e) {
            return ApiResponse.error("更新轮播图失败: " + e.getMessage());
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