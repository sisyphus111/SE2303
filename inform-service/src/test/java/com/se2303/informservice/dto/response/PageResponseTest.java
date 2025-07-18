package com.se2303.informservice.dto.response;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PageResponseTest {

    @Test
    void testDefaultConstructor() {
        PageResponse<String> response = new PageResponse<>();
        assertNull(response.getContent(), "默认构造的 content 应为 null");
        assertEquals(0, response.getPage(), "默认构造的 page 应为 0");
        assertEquals(0, response.getSize(), "默认构造的 size 应为 0");
        assertEquals(0, response.getTotalElements(), "默认构造的 totalElements 应为 0");
        assertEquals(0, response.getTotalPages(), "默认构造的 totalPages 应为 0");
        assertFalse(response.isFirst(), "默认构造的 first 应为 false");
        assertFalse(response.isLast(), "默认构造的 last 应为 false");
        assertFalse(response.isEmpty(), "默认构造的 empty 应为 false");
    }

    @Test
    void testPageConstructor() {
        List<String> content = Arrays.asList("item1", "item2");
        Page<String> page = new PageImpl<>(content, PageRequest.of(1, 10), 25);

        PageResponse<String> response = new PageResponse<>(page);

        assertEquals(content, response.getContent(), "content 应与 page 的内容一致");
        assertEquals(1, response.getPage(), "page 应与 page 的页码一致");
        assertEquals(10, response.getSize(), "size 应与 page 的大小一致");
        assertEquals(25, response.getTotalElements(), "totalElements 应与 page 的总数一致");
        assertEquals(3, response.getTotalPages(), "totalPages 应与 page 的总页数一致");
        assertFalse(response.isFirst(), "first 应与 page 的 isFirst 一致");
        assertFalse(response.isLast(), "last 应与 page 的 isLast 一致");
        assertFalse(response.isEmpty(), "empty 应与 page 的 isEmpty 一致");
    }

    @Test
    void testStaticOfMethod() {
        List<String> content = Arrays.asList("item1", "item2");
        Page<String> page = new PageImpl<>(content, PageRequest.of(0, 5), 10);

        PageResponse<String> response = PageResponse.of(page);

        assertEquals(content, response.getContent(), "of 方法应正确设置 content");
        assertEquals(0, response.getPage(), "of 方法应正确设置 page");
        assertEquals(5, response.getSize(), "of 方法应正确设置 size");
        assertEquals(10, response.getTotalElements(), "of 方法应正确设置 totalElements");
        assertEquals(2, response.getTotalPages(), "of 方法应正确设置 totalPages");
        assertTrue(response.isFirst(), "of 方法应正确设置 first");
        assertFalse(response.isLast(), "of 方法应正确设置 last");
        assertFalse(response.isEmpty(), "of 方法应正确设置 empty");
    }

    @Test
    void testGettersAndSetters() {
        PageResponse<String> response = new PageResponse<>();
        List<String> content = Arrays.asList("item1");
        response.setContent(content);
        response.setPage(1);
        response.setSize(10);
        response.setTotalElements(20);
        response.setTotalPages(2);
        response.setFirst(true);
        response.setLast(false);
        response.setEmpty(false);

        assertEquals(content, response.getContent(), "getContent 应返回设置的 content");
        assertEquals(1, response.getPage(), "getPage 应返回设置的 page");
        assertEquals(10, response.getSize(), "getSize 应返回设置的 size");
        assertEquals(20, response.getTotalElements(), "getTotalElements 应返回设置的 totalElements");
        assertEquals(2, response.getTotalPages(), "getTotalPages 应返回设置的 totalPages");
        assertTrue(response.isFirst(), "getFirst 应返回设置的 first");
        assertFalse(response.isLast(), "getLast 应返回设置的 last");
        assertFalse(response.isEmpty(), "getEmpty 应返回设置的 empty");
    }
}