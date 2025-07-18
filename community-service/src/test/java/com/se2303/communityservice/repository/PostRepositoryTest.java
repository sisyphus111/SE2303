package com.se2303.communityservice.repository;

import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.entity.Like;
import com.se2303.communityservice.entity.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Post post1;
    private Post post2;

    @BeforeEach
    void setUp() {
        post1 = new Post();
        post1.setUserId("user1");
        post1.setTitle("Test Post 1");
        post1.setContent("Content 1");
        post1.setReviewStatus("APPROVED");
        post1.setCreatedAt(LocalDateTime.now());
        post1.setTags(Set.of("tag1", "tag2")); // Changed from Arrays.asList to Set.of

        post2 = new Post();
        post2.setUserId("user2");
        post2.setTitle("Test Post 2");
        post2.setContent("Content 2");
        post2.setReviewStatus("PENDING");
        post2.setCreatedAt(LocalDateTime.now());
        post2.setTags(Set.of("tag2", "tag3")); // Changed from Arrays.asList to Set.of

        postRepository.saveAll(Set.of(post1, post2));

        Like like = new Like();
        like.setPostId(post1.getId());
        like.setUserId("user1");
        likeRepository.save(like);

        Comment comment = new Comment();
        comment.setPostId(post1.getId());
        comment.setUserId("user1");
        comment.setContent("Test comment");
        comment.setReviewStatus("APPROVED");
        commentRepository.save(comment);
    }

    @Test
    void testFindByReviewStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> result = postRepository.findByReviewStatus("APPROVED", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Test Post 1", result.getContent().get(0).getTitle());
    }

    @Test
    void testFindByKeywordAndReviewStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> result = postRepository.findByKeywordAndReviewStatus("Test", "APPROVED", pageable);

        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getTitle().contains("Test"));
    }

    @Test
    void testFindByTagAndReviewStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> result = postRepository.findByTagAndReviewStatus("tag1", "APPROVED", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Test Post 1", result.getContent().get(0).getTitle());
    }

    @Test
    void testFindByUserIdAndReviewStatus() {
        List<Post> result = postRepository.findByUserIdAndReviewStatus("user1", "APPROVED");

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUserId());
    }

    @Test
    void testFindAllPosts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> result = postRepository.findAllPosts(pageable);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testFindAllPostsOrderByLikeCountDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> result = postRepository.findAllPostsOrderByLikeCountDesc(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("Test Post 1", result.getContent().get(0).getTitle());
    }

    @Test
    void testFindAllPostsOrderByCommentCountDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> result = postRepository.findAllPostsOrderByCommentCountDesc(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("Test Post 1", result.getContent().get(0).getTitle());
    }

    @Test
    void testGetApprovedPostCount() {
        Integer count = postRepository.getApprovedPostCount();

        assertEquals(1, count);
    }

    @Test
    void testGetTodayApprovedPostCount() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        Integer count = postRepository.getTodayApprovedPostCount(startOfDay, endOfDay);

        assertEquals(1, count);
    }

    @Test
    void testFindByUserId() {
        List<Post> result = postRepository.findByUserId("user1");

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUserId());
    }
}