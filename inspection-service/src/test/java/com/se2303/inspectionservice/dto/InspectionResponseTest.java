package com.se2303.inspectionservice.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InspectionResponseTest {
    @Test
    void testConstructorAndGetterSetter() {
        InspectionResponse resp = new InspectionResponse(true, "ok");
        assertTrue(resp.isSafe());
        assertEquals("ok", resp.getReason());

        resp.setSafe(false);
        resp.setReason("bad");
        assertFalse(resp.isSafe());
        assertEquals("bad", resp.getReason());
    }
} 