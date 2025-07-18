package com.se2303.notificationservice.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ApiResponseTest {
    @Test
    public void testSuccessFactory() {
        ApiResponse resp = ApiResponse.success("ok");
        assertTrue(resp.isSuccess());
        assertEquals("ok", resp.getMessage());
    }

    @Test
    public void testErrorFactory() {
        ApiResponse resp = ApiResponse.error("fail");
        assertFalse(resp.isSuccess());
        assertEquals("fail", resp.getMessage());
    }

    @Test
    public void testGetterSetter() {
        ApiResponse resp = new ApiResponse();
        resp.setSuccess(true);
        resp.setObject("obj");
        resp.setMessage("msg");
        assertTrue(resp.isSuccess());
        assertEquals("obj", resp.getObject());
        assertEquals("msg", resp.getMessage());
    }
} 