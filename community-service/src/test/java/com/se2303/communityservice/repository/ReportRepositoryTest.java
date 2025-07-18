package com.se2303.communityservice.repository;

import com.se2303.communityservice.entity.Comment;
import com.se2303.communityservice.entity.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("test")
public class ReportRepositoryTest {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Report report1;
    private Report report2;
    private Comment comment;

    @BeforeEach
    void setUp() {
        // 先创建并保存一个 Comment 实体
        comment = new Comment();
        comment.setPostId(1L);
        comment.setUserId("user1");
        comment.setContent("Test comment");
        comment.setReviewStatus("APPROVED");
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);

        report1 = new Report();
        report1.setCommentId(comment.getId()); // 设置 commentId
        report1.setPostId(1L);
        report1.setUserId("user1");
        report1.setStatus(Report.ReportStatus.PENDING);
        report1.setCreatedAt(LocalDateTime.now());
        report1.setReason("Inappropriate content");
        report1.setType("COMMENT");
        report1.setDetail("Offensive language");

        report2 = new Report();
        report2.setCommentId(comment.getId()); // 设置 commentId
        report2.setPostId(2L);
        report2.setUserId("user2");
        report2.setStatus(Report.ReportStatus.APPROVED);
        report2.setCreatedAt(LocalDateTime.now());
        report2.setReason("Spam");
        report2.setType("COMMENT");
        report2.setDetail("Repeated content");

        reportRepository.saveAll(List.of(report1, report2));
    }

    @Test
    void testFindByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Report> result = reportRepository.findByStatus(Report.ReportStatus.PENDING, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(Report.ReportStatus.PENDING, result.getContent().get(0).getStatus());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Report> result = reportRepository.findAll(pageable);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testFindByPostId() {
        List<Report> result = reportRepository.findByPostId(1L);

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUserId());
    }

    @Test
    void testCountByPostId() {
        Long count = reportRepository.countByPostId(1L);

        assertEquals(1L, count);
    }

    @Test
    void testFindByUserId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Report> result = reportRepository.findByUserId("user1", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("user1", result.getContent().get(0).getUserId());
    }

    @Test
    void testCountByStatus() {
        Long count = reportRepository.countByStatus(Report.ReportStatus.PENDING);

        assertEquals(1L, count);
    }
}