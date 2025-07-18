package com.se2303.communityservice.dto.response;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

class PostBriefInfoTest {

    @Test
    void allGettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        PostBriefInfo info = new PostBriefInfo();
        Long id = 123L;
        String title = "Brief Title";
        Set<String> tags = Set.of("news", "tech");
        String content = "Brief content";
        String userId = "user-456";
        LocalDateTime now = LocalDateTime.now();
        String reviewStatus = "APPROVED";
        Long likeCount = 50L;
        Long commentCount = 12L;
        Boolean liked = true;

        // Act
        info.setId(id);
        info.setTitle(title);
        info.setTags(tags);
        info.setContent(content);
        info.setUserId(userId);
        info.setCreatedAt(now);
        info.setReviewStatus(reviewStatus);
        info.setLikeCount(likeCount);
        info.setCommentCount(commentCount);
        info.setLiked(liked);

        // Assert
        assertThat(info.getId()).isEqualTo(id);
        assertThat(info.getTitle()).isEqualTo(title);
        assertThat(info.getTags()).isEqualTo(tags);
        assertThat(info.getContent()).isEqualTo(content);
        assertThat(info.getUserId()).isEqualTo(userId);
        assertThat(info.getCreatedAt()).isEqualTo(now);
        assertThat(info.getReviewStatus()).isEqualTo(reviewStatus);
        assertThat(info.getLikeCount()).isEqualTo(likeCount);
        assertThat(info.getCommentCount()).isEqualTo(commentCount);
        assertThat(info.getLiked()).isEqualTo(liked);
    }

    @Test
    void booleanFields_ShouldHandleNull() {
        // Arrange
        PostBriefInfo info = new PostBriefInfo();

        // Act
        info.setLiked(null);

        // Assert
        assertThat(info.getLiked()).isNull();
    }
}