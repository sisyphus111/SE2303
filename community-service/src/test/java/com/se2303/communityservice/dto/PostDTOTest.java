package com.se2303.communityservice.dto;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

class PostDTOTest {

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        PostDTO dto = new PostDTO();
        String title = "Test Title";
        Set<String> tags = Set.of("tag1", "tag2");
        String content = "Test content";

        // Act
        dto.setTitle(title);
        dto.setTags(tags);
        dto.setContent(content);

        // Assert
        assertThat(dto.getTitle()).isEqualTo(title);
        assertThat(dto.getTags()).isEqualTo(tags);
        assertThat(dto.getContent()).isEqualTo(content);
    }

    @Test
    void setTags_ShouldHandleNull() {
        // Arrange
        PostDTO dto = new PostDTO();

        // Act
        dto.setTags(null);

        // Assert
        assertThat(dto.getTags()).isNull();
    }
}