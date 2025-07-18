package com.se2303.notificationservice.repository;

import com.se2303.notificationservice.model.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {
    
    /**
     * 分页查询音乐
     */
    Page<Music> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 根据分类查询音乐
     */
    List<Music> findByCategoryOrderByCreatedAtDesc(String category);
    
    /**
     * 根据标题模糊查询
     */
    @Query("SELECT m FROM Music m WHERE m.title LIKE %:keyword% OR m.category LIKE %:keyword%")
    List<Music> findByKeyword(@Param("keyword") String keyword);

    /**
     * 按id正序分页查询音乐
     */
    Page<Music> findAllByOrderByIdAsc(Pageable pageable);
} 