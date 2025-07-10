package com.se2303.communityservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders; // Use Spring's HttpHeaders
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class InspectionServiceClient {
    @Autowired
    private RestTemplate restTemplate;

    private static final String INSPECTION_URL = "http://localhost:8088/inspect";

    public boolean isContentSafe(String content) {
        try {
            HttpHeaders headers = new HttpHeaders(); // Spring's HttpHeaders
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> request = Map.of("content", content);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(INSPECTION_URL, entity, Map.class);
            Map<String, Object> result = response.getBody();
            return (Boolean) result.get("isSafe");
        } catch (Exception e) {
            // Return true on exception to avoid impacting main business logic
            return true;
        }
    }
}