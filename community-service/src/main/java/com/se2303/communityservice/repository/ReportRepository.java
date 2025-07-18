package com.se2303.communityservice.repository;

import com.se2303.communityservice.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    /**
     * 根据状态查找举报
     */
    Page<Report> findByStatus(Report.ReportStatus status, Pageable pageable);
    
    /**
     * 查找所有举报（分页）
     */
    Page<Report> findAll(Pageable pageable);
    
    /**
     * 根据帖子ID查找举报
     */
    List<Report> findByPostId(Long postId);
    
    /**
     * 统计指定帖子的举报数量
     */
    @Query("SELECT COUNT(r) FROM Report r WHERE r.postId = :postId")
    Long countByPostId(@Param("postId") Long postId);
    
    /**
     * 根据用户ID查找举报（该用户提交的举报）
     */
    Page<Report> findByUserId(String userId, Pageable pageable);
    
    /**
     * 根据状态统计举报数量
     */
    @Query("SELECT COUNT(r) FROM Report r WHERE r.status = :status")
    Long countByStatus(@Param("status") Report.ReportStatus status);
}
