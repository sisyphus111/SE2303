package com.se2303.inspectionservice.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InspectionRequestTest {
    @Test
    void testGetterSetter() {
        InspectionRequest req = new InspectionRequest();
        req.setContent("test");
        assertEquals("test", req.getContent());
    }
} 