package com.se2303.communityservice.service;

import com.se2303.communityservice.dto.response.PostBriefInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ILikeService {
    void likePost(Long postId, String userId);

    void unlikePost(Long postId, String userId);

    List<PostBriefInfo> getLikedPosts(String userId);

    Boolean isLiked(Long postId, String userId);

    Long getLikedCountByUserId(Long userId);
}
