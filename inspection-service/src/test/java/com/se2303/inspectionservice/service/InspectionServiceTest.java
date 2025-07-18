package com.se2303.inspectionservice.service;

import com.se2303.inspectionservice.dto.ApiResponse;
import com.se2303.inspectionservice.dto.InspectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InspectionServiceTest {
    private LLMService llmService;
    private AlertService alertService;
    private InspectionService inspectionService;

    @BeforeEach
    void setUp() {
        llmService = mock(LLMService.class);
        alertService = mock(AlertService.class);
        inspectionService = new InspectionService(llmService, alertService);
    }

    @Test
    void testInspect_Safe() {
        when(llmService.isContentSafe("ok")).thenReturn(true);
        ApiResponse resp = inspectionService.inspect("ok");
        assertTrue(resp.getSuccess());
        InspectionResponse result = (InspectionResponse) resp.getData();
        assertTrue(result.isSafe());
        assertEquals("Content is safe.", result.getReason());
    }

    @Test
    void testInspect_Unsafe() {
        when(llmService.isContentSafe("bad")).thenReturn(false);
        ApiResponse resp = inspectionService.inspect("bad");
        assertTrue(resp.getSuccess());
        InspectionResponse result = (InspectionResponse) resp.getData();
        assertFalse(result.isSafe());
        assertEquals("Content is not safe.", result.getReason());
        verify(alertService).sendAlert(anyString());
    }
} 