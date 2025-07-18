package com.se2303.inspectionservice.service;

import org.springframework.stereotype.Service;

@Service
public class AlertService {
    public void sendAlert(String reason) {
        // In a real application, this would call another service,
        // send an email, or push a notification.
        System.out.println("ALERT! Dangerous content detected: " + reason);
    }
} 