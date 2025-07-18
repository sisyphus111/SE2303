package com.se2303.communityservice.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReportTest {
    private Report report;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        report = new Report();
        now = LocalDateTime.now();
    }

    @Test
    void testDefaultConstructorAndGetters() {
        assertNull(report.getId());
        assertNull(report.getPostId());
        assertNull(report.getCommentId());
        assertNull(report.getType());
        assertNull(report.getUserId());
        assertNull(report.getReason());
        assertNull(report.getDetail());
        assertEquals(Report.ReportStatus.PENDING, report.getStatus());
        assertNull(report.getCreatedAt());
    }

    @Test
    void testSettersAndGetters() {
        report.setId(1L);
        report.setPostId(2L);
        report.setCommentId(3L);
        report.setType("SPAM");
        report.setUserId("user123");
        report.setReason("Inappropriate content");
        report.setDetail("Detailed description");
        report.setStatus(Report.ReportStatus.APPROVED);
        report.setCreatedAt(now);

        assertEquals(1L, report.getId());
        assertEquals(2L, report.getPostId());
        assertEquals(3L, report.getCommentId());
        assertEquals("SPAM", report.getType());
        assertEquals("user123", report.getUserId());
        assertEquals("Inappropriate content", report.getReason());
        assertEquals("Detailed description", report.getDetail());
        assertEquals(Report.ReportStatus.APPROVED, report.getStatus());
        assertEquals(now, report.getCreatedAt());
    }

    @Test
    void testPrePersist() {
        report.onCreate();
        assertNotNull(report.getCreatedAt());
        assertTrue(report.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testToString() {
        report.setId(1L);
        report.setPostId(2L);
        report.setCommentId(3L);
        report.setUserId("user123");
        report.setReason("Inappropriate content");
        report.setStatus(Report.ReportStatus.APPROVED);
        report.setCreatedAt(now);

        String toString = report.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("postId=2"));
        assertTrue(toString.contains("userId='user123'"));
        assertTrue(toString.contains("reason='Inappropriate content'"));
        assertTrue(toString.contains("status=APPROVED"));
    }
}