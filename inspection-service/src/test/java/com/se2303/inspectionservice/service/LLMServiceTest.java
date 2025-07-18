package com.se2303.inspectionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LLMServiceTest {
    private LLMService service;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        service = new LLMService();
        restTemplate = mock(RestTemplate.class);
        objectMapper = mock(ObjectMapper.class);
        Field restTemplateField = LLMService.class.getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);
        restTemplateField.set(service, restTemplate);
        Field objectMapperField = LLMService.class.getDeclaredField("objectMapper");
        objectMapperField.setAccessible(true);
        objectMapperField.set(service, objectMapper);
    }

    @Test
    void testIsContentSafe_NullOrEmpty() {
        assertTrue(service.isContentSafe(null));
        assertTrue(service.isContentSafe("   "));
    }

    @Test
    void testIsContentSafe_Safe() throws Exception {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"answer\":\"安全\"}", org.springframework.http.HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);
        ObjectNode jsonNode = mock(ObjectNode.class);
        when(objectMapper.readTree(anyString())).thenReturn(jsonNode);
        when(jsonNode.path("answer")).thenReturn(jsonNode);
        when(jsonNode.asText("")).thenReturn("安全");
        assertTrue(service.isContentSafe("正常内容"));
    }

    @Test
    void testIsContentSafe_Unsafe() throws Exception {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"answer\":\"有⼈想死\"}", org.springframework.http.HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);
        ObjectNode jsonNode = mock(ObjectNode.class);
        when(objectMapper.readTree(anyString())).thenReturn(jsonNode);
        when(jsonNode.path("answer")).thenReturn(jsonNode);
        when(jsonNode.asText("")).thenReturn("有⼈想死");
        assertTrue(service.isContentSafe("危险内容"));
    }

    @Test
    void testIsContentSafe_Exception() throws Exception {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("error"));
        assertTrue(service.isContentSafe("异常内容"));
    }
} 