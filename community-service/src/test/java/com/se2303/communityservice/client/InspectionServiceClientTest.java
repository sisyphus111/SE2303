package com.se2303.communityservice.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InspectionServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private InspectionServiceClient inspectionServiceClient;

    @Test
    void isContentSafe_ShouldReturnTrueWhenContentIsSafe() {
        // Arrange
        ResponseEntity<Map> responseEntity = mock(ResponseEntity.class);
        Map<String, Object> responseBody = Map.of("isSafe", true);

        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(Map.class))
        ).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(responseBody);

        // Act
        boolean result = inspectionServiceClient.isContentSafe("safe content");

        // Assert
        assertTrue(result);
    }

    @Test
    void isContentSafe_ShouldReturnFalseWhenContentIsUnsafe() {
        // Arrange
        ResponseEntity<Map> responseEntity = mock(ResponseEntity.class);
        Map<String, Object> responseBody = Map.of("isSafe", false);

        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(Map.class))
        ).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(responseBody);

        // Act
        boolean result = inspectionServiceClient.isContentSafe("unsafe content");

        // Assert
        assertFalse(result);
    }

    @Test
    void isContentSafe_ShouldReturnTrueOnException() {
        // Arrange
        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(Map.class))
        ).thenThrow(new RuntimeException("Network error"));

        // Act
        boolean result = inspectionServiceClient.isContentSafe("any content");

        // Assert
        assertTrue(result);
    }

    @Test
    void isContentSafe_ShouldSetCorrectHeaders() {
        // Arrange
        ResponseEntity<Map> responseEntity = mock(ResponseEntity.class);
        Map<String, Object> responseBody = Map.of("isSafe", true);

        when(restTemplate.postForEntity(
                anyString(),
                argThat((HttpEntity<String> entity) -> {
                    HttpHeaders headers = entity.getHeaders();
                    return headers.getContentType().equals(MediaType.APPLICATION_JSON);
                }),
                eq(Map.class))
        ).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(responseBody);

        // Act
        boolean result = inspectionServiceClient.isContentSafe("content");

        // Assert
        assertTrue(result);
    }
}