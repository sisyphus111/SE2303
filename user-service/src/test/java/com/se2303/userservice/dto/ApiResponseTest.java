package com.se2303.userservice.dto;

import com.se2303.userservice.dto.response.ApiResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {
    @Test
    void testConstructorAndGettersSetters() {
        ApiResponse<String> resp = new ApiResponse<>(true, "data", "msg");
        assertTrue(resp.getSuccess());
        assertEquals("data", resp.getData());
        assertEquals("msg", resp.getMessage());
        resp.setSuccess(false);
        resp.setData("d2");
        resp.setMessage("m2");
        assertFalse(resp.getSuccess());
        assertEquals("d2", resp.getData());
        assertEquals("m2", resp.getMessage());
    }

    @Test
    void testNoArgConstructor() {
        ApiResponse<Integer> resp = new ApiResponse<>();
        resp.setSuccess(true);
        resp.setData(123);
        resp.setMessage("ok");
        assertTrue(resp.getSuccess());
        assertEquals(123, resp.getData());
        assertEquals("ok", resp.getMessage());
    }
} 