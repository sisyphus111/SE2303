package com.se2303.inspectionservice.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlertServiceTest {
    @Test
    void testSendAlert() {
        AlertService service = new AlertService();
        service.sendAlert("test reason"); // 只要不抛异常即可
    }
} 