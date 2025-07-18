package com.se2303.inspectionservice.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {
    @Test
    void testConstructorAndGetterSetter() {
        ApiResponse resp = new ApiResponse(true, "data", "msg");
        assertTrue(resp.getSuccess());
        assertEquals("data", resp.getData());
        assertEquals("msg", resp.getMessage());

        resp.setSuccess(false);
        resp.setData(123);
        resp.setMessage("err");
        assertFalse(resp.getSuccess());
        assertEquals(123, resp.getData());
        assertEquals("err", resp.getMessage());
    }
} 