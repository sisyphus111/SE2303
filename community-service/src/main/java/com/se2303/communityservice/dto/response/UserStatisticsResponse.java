package com.se2303.communityservice.dto.response;

public class UserStatisticsResponse {
    private Long likedCount;
    private Long commentCount;

    public UserStatisticsResponse(Long likedCount, Long commentCount) {
        this.likedCount = likedCount;
        this.commentCount = commentCount;
    }

    public UserStatisticsResponse() {

    }

    public Long getLikedCount() {
        return likedCount;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setLikedCount(Long likedCount) {
        this.likedCount = likedCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }
}
