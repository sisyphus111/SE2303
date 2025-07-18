package com.se2303.notificationservice.repository;

import com.se2303.notificationservice.model.CarouselImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarouselImageRepository extends JpaRepository<CarouselImage, Long> {
    
    /**
     * 根据分类查找轮播图，按排序字段排序
     */
    List<CarouselImage> findByCategoryOrderBySortOrderAsc(String category);
    
    /**
     * 查找所有轮播图，按排序字段排序
     */
    List<CarouselImage> findAllByOrderBySortOrderAsc();

    /**
     * 按id正序分页查询轮播图
     */
    org.springframework.data.domain.Page<CarouselImage> findAllByOrderByIdAsc(org.springframework.data.domain.Pageable pageable);
} 