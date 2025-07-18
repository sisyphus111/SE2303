package com.se2303.communityservice.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CommentDTOTest {

    @Test
    void contentGetterSetter_ShouldWorkCorrectly() {
        // Arrange
        CommentDTO dto = new CommentDTO();
        String expectedContent = "Test comment content";

        // Act
        dto.setContent(expectedContent);

        // Assert
        assertThat(dto.getContent()).isEqualTo(expectedContent);
    }

    @Test
    void setContent_ShouldHandleNull() {
        // Arrange
        CommentDTO dto = new CommentDTO();

        // Act
        dto.setContent(null);

        // Assert
        assertThat(dto.getContent()).isNull();
    }
}