package com.se2303.communityservice.dto.response;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UserStatisticsResponseTest {

    @Test
    void parameterizedConstructor_ShouldSetFields() {
        // Arrange
        Long likedCount = 150L;
        Long commentCount = 45L;

        // Act
        UserStatisticsResponse response = new UserStatisticsResponse(likedCount, commentCount);

        // Assert
        assertThat(response.getLikedCount()).isEqualTo(likedCount);
        assertThat(response.getCommentCount()).isEqualTo(commentCount);
    }

    @Test
    void defaultConstructor_ShouldInitializeNull() {
        // Act
        UserStatisticsResponse response = new UserStatisticsResponse();

        // Assert
        assertThat(response.getLikedCount()).isNull();
        assertThat(response.getCommentCount()).isNull();
    }

    @Test
    void setters_ShouldUpdateFields() {
        // Arrange
        UserStatisticsResponse response = new UserStatisticsResponse();
        Long newLikedCount = 200L;
        Long newCommentCount = 60L;

        // Act
        response.setLikedCount(newLikedCount);
        response.setCommentCount(newCommentCount);

        // Assert
        assertThat(response.getLikedCount()).isEqualTo(newLikedCount);
        assertThat(response.getCommentCount()).isEqualTo(newCommentCount);
    }
}