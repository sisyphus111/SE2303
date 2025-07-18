package com.se2303.communityservice.service;


import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.dto.PostDTO;
import com.se2303.communityservice.dto.response.PostBriefInfo;
import com.se2303.communityservice.entity.Post;
import com.se2303.communityservice.entity.Report;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IPostService {
    PageDTO<PostBriefInfo> getPosts(String keyword, String category, String reviewStatus, String sortBy, Pageable pageable);
    PostBriefInfo getPostById(Long postId);
    List<PostBriefInfo> getMyPosts(String userId);
    List<String> getAllTags();
//    List<Post> getTopPosts();
    Post createPost(PostDTO postDTO, String userId);
    Post updatePost(Long postId, PostDTO postDTO, String userId);
    Post deletePost(Long postId, String userId, Boolean identity);
    void clearCache();
    PostBriefInfo approvePost(Long postId, String adminId);
    Post banPost(Long postId, String adminId);
    Report reportPost(Long postId, String userId, String reason, String detail);
    
    // 新增：获取审核队列专用方法
    PageDTO<PostBriefInfo> getReviewQueue(String keyword, String category, String status, Pageable pageable);

    // 新增：获取审核通过的帖子数量
    Integer getApprovedPostCount();

    Integer getTodayApprovedPostCount();
}
