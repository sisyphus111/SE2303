package com.se2303.communityservice.dto;

import lombok.Data;

import java.util.Set;

@Data
public class PostDTO {
//    private Long id;

    private String title;

    private Set<String> tags; // 多个标签

    private String content;

//    private String userId;

//    private LocalDateTime createdAt;
//
//    private Post.PostStatus status=Post.PostStatus.ACTIVE;
//
//    private Post.ReviewStatus reviewStatus = Post.ReviewStatus.PENDING;
}
