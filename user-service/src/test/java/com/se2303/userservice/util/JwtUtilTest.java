package com.se2303.userservice.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    @Test
    void testGenerateAndParseToken() {
        String token = JwtUtil.generateToken(123L, true, true);
        assertNotNull(token);
        Claims claims = JwtUtil.getClaims(token);
        assertEquals(123L, ((Number)claims.get("X-User-Id")).longValue());
        assertEquals(true, claims.get("X-Valid"));
        assertEquals(true, claims.get("X-Identity"));
    }
} 