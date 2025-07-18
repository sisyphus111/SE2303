package com.se2303.userservice.dto;

import com.se2303.userservice.dto.response.JaccountTokenResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JaccountTokenResponseTest {
    @Test
    void testSettersAndGetters() {
        JaccountTokenResponse resp = new JaccountTokenResponse();
        resp.setAccessToken("tokenREDACTED");
        resp.setTokenType("bearer");
        resp.setExpiresIn(3600);
        resp.setRefreshToken("refresh123");
        assertEquals("tokenREDACTED", resp.getAccessToken());
        assertEquals("bearer", resp.getTokenType());
        assertEquals(3600, resp.getExpiresIn());
        assertEquals("refresh123", resp.getRefreshToken());
    }

    @Test
    void testToString() {
        JaccountTokenResponse resp = new JaccountTokenResponse();
        resp.setAccessToken("tokenREDACTED");
        resp.setTokenType("bearer");
        resp.setExpiresIn(3600);
        resp.setRefreshToken("refresh123");
        String str = resp.toString();
        assertTrue(str.contains("token12345...")); // 修正断言
        assertTrue(str.contains("bearer"));
        assertTrue(str.contains("3600"));
        assertTrue(str.contains("refresh123"));
    }
} 