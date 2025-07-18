package com.se2303.communityservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;

class JacksonConfigTest {

    private JacksonConfig jacksonConfig;

    @BeforeEach
    void setUp() {
        jacksonConfig = new JacksonConfig();
    }

    @Test
    void testObjectMapper() {
        ObjectMapper objectMapper = jacksonConfig.objectMapper();
        assertNotNull(objectMapper, "ObjectMapper should not be null");
        assertTrue(objectMapper.findModules().stream().anyMatch(module -> module instanceof JavaTimeModule),
                "JavaTimeModule should be registered");
        assertEquals("yyyy-MM-dd HH:mm:ss", ((SimpleDateFormat) objectMapper.getDateFormat()).toPattern(),
                "Date format should be yyyy-MM-dd HH:mm:ss");
    }
}