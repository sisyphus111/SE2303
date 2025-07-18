package com.se2303.communityservice.repository;

import com.se2303.communityservice.entity.Comment;
import com.se2303.communityservice.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    private Comment comment1;
    private Comment comment2;
    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setUserId("user1");
        post.setTitle("Test Post");
        post.setContent("Content");
        post.setReviewStatus("APPROVED");
        post.setCreatedAt(LocalDateTime.now());
        post.setTags(Set.of("tag1", "tag2")); // Changed from Arrays.asList to Set.of
        postRepository.save(post);

        comment1 = new Comment();
        comment1.setPostId(post.getId());
        comment1.setUserId("user1");
        comment1.setContent("Test comment 1");
        comment1.setReviewStatus("APPROVED");
        comment1.setCreatedAt(LocalDateTime.now());

        comment2 = new Comment();
        comment2.setPostId(post.getId());
        comment2.setUserId("user2");
        comment2.setContent("Test comment 2");
        comment2.setReviewStatus("PENDING");
        comment2.setCreatedAt(LocalDateTime.now());

        commentRepository.saveAll(Arrays.asList(comment1, comment2));
    }

    @Test
    void testFindByPostId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> result = commentRepository.findByPostId(post.getId(), pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("APPROVED", result.getContent().get(0).getReviewStatus());
    }

    @Test
    void testFindByReviewStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> result = commentRepository.findByReviewStatus("APPROVED", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Test comment 1", result.getContent().get(0).getContent());
    }

    @Test
    void testFindByKeywordAndReviewStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> result = commentRepository.findByKeywordAndReviewStatus("Test", "APPROVED", pageable);

        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getContent().contains("Test"));
    }

    @Test
    void testFindByUserIdAndReviewStatus() {
        List<Comment> result = commentRepository.findByUserIdAndReviewStatus("user1", "APPROVED");

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUserId());
    }

    @Test
    void testCountByReviewStatus() {
        Long count = commentRepository.countByReviewStatus("APPROVED");

        assertEquals(1L, count);
    }

    @Test
    void testFindAllComments() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> result = commentRepository.findAllComments(pageable);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testCountByPostId() {
        Long count = commentRepository.countByPostId(post.getId());

        assertEquals(2L, count);
    }

    @Test
    void testCountApprovedCommentsByPostId() {
        Long count = commentRepository.countApprovedCommentsByPostId(post.getId());

        assertEquals(1L, count);
    }

    @Test
    void testFindByContentContainingIgnoreCase() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> result = commentRepository.findByContentContainingIgnoreCase("test", pageable);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testGetTodayApprovedCommentCount() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        Integer count = commentRepository.getTodayApprovedCommentCount(startOfDay, endOfDay);

        assertEquals(1, count);
    }

    @Test
    void testFindByTagsInAndReviewStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> result = commentRepository.findByTagsInAndReviewStatus(Arrays.asList("tag1"), "APPROVED", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Test comment 1", result.getContent().get(0).getContent());
    }

    @Test
    void testCountByUserId() {
        Long count = commentRepository.countByUserId("user1");

        assertEquals(1L, count);
    }
}