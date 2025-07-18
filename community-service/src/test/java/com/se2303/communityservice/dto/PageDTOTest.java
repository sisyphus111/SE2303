package com.se2303.communityservice.dto;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class PageDTOTest {

    @Test
    void constructor_ShouldSetAllFields() {
        // Arrange
        List<String> content = List.of("item1", "item2");
        int pageNumber = 2;
        int pageSize = 10;
        long totalElements = 100;
        int totalPages = 10;

        // Act
        PageDTO<String> page = new PageDTO<>(
                content, pageNumber, pageSize, totalElements, totalPages
        );

        // Assert
        assertThat(page.getContent()).isEqualTo(content);
        assertThat(page.getPageNumber()).isEqualTo(pageNumber);
        assertThat(page.getPageSize()).isEqualTo(pageSize);
        assertThat(page.getTotalElements()).isEqualTo(totalElements);
        assertThat(page.getTotalPages()).isEqualTo(totalPages);
    }

    @Test
    void setters_ShouldUpdateFields() {
        // Arrange
        PageDTO<String> page = new PageDTO<>(null, 0, 0, 0, 0);
        List<String> newContent = List.of("new");
        int newPageNumber = 3;
        int newPageSize = 20;
        long newTotalElements = 200;
        int newTotalPages = 15;

        // Act
        page.setContent(newContent);
        page.setPageNumber(newPageNumber);
        page.setPageSize(newPageSize);
        page.setTotalElements(newTotalElements);
        page.setTotalPages(newTotalPages);

        // Assert
        assertThat(page.getContent()).isEqualTo(newContent);
        assertThat(page.getPageNumber()).isEqualTo(newPageNumber);
        assertThat(page.getPageSize()).isEqualTo(newPageSize);
        assertThat(page.getTotalElements()).isEqualTo(newTotalElements);
        assertThat(page.getTotalPages()).isEqualTo(newTotalPages);
    }
}