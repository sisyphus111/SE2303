package com.se2303.communityservice.repository;

import com.se2303.communityservice.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByPostId(Long postId);

    boolean existsByPostIdAndUserId(Long postId, String userId);

    Optional<Like> findByPostIdAndUserId(Long postId, String userId);

    long countByPostId(Long postId);

    @Query("SELECT DISTINCT l.postId FROM Like l")
    List<Long> findDistinctPostIds();

    List<Like> findByUserId(String userId);
}