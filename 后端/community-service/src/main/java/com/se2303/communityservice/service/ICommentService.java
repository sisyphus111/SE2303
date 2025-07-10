package com.se2303.communityservice.service;

import com.se2303.communityservice.dto.CommentDTO;
import com.se2303.communityservice.dto.PageDTO;
import com.se2303.communityservice.entity.Comment;
import com.se2303.communityservice.entity.Report;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ICommentService {
    PageDTO<Comment> getCommentsByPostId(Long postId, Pageable pageable);

    Comment addComment(Long postId, CommentDTO commentDTO, String userId);

    Comment approveComment(Long commentId, String adminId);

    Comment banComment(Long commentId, String adminId);
    
    // 新增：获取审核队列专用方法
    PageDTO<Comment> getReviewQueue(String keyword, String status, Pageable pageable);
    
    // 新增：获取评论详情（包含帖子信息）
    Map<String, Object> getCommentDetail(Long commentId);
    
    // 新增：获取评论统计信息
    Map<String, Object> getCommentStats();

    // 新增：举报评论
    Report reportComment(Long commentId, String userId, String reason, String detail);
}
