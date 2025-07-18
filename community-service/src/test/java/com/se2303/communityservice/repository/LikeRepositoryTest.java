package com.se2303.communityservice.repository;

import com.se2303.communityservice.entity.Like;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    private Like like1;
    private Like like2;

    @BeforeEach
    void setUp() {
        like1 = new Like();
        like1.setPostId(1L);
        like1.setUserId("user1");

        like2 = new Like();
        like2.setPostId(2L);
        like2.setUserId("user2");

        likeRepository.save(like1);
        likeRepository.save(like2);
    }

    @Test
    void testFindByPostId() {
        List<Like> result = likeRepository.findByPostId(1L);
        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUserId());
    }

    @Test
    void testExistsByPostIdAndUserId() {
        boolean exists = likeRepository.existsByPostIdAndUserId(1L, "user1");
        assertTrue(exists);
    }

    @Test
    void testFindByPostIdAndUserId() {
        Optional<Like> result = likeRepository.findByPostIdAndUserId(1L, "user1");
        assertTrue(result.isPresent());
        assertEquals("user1", result.get().getUserId());
    }

    @Test
    void testCountByPostId() {
        long count = likeRepository.countByPostId(1L);
        assertEquals(1, count);
    }

    @Test
    void testFindDistinctPostIds() {
        List<Long> result = likeRepository.findDistinctPostIds();
        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
    }

    @Test
    void testFindByUserId() {
        List<Like> result = likeRepository.findByUserId("user1");
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getPostId());
    }
}