package com.se2303.userservice.dto;

import com.se2303.userservice.dto.response.JaccountUserInfoResponse;
import com.se2303.userservice.dto.response.JaccountUserInfo;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class JaccountUserInfoResponseTest {
    @Test
    void testSettersAndGetters() {
        JaccountUserInfoResponse resp = new JaccountUserInfoResponse();
        resp.setErrno(0);
        resp.setError("ok");
        resp.setTotal(2);
        JaccountUserInfo info1 = new JaccountUserInfo();
        info1.setId("id1");
        resp.setEntities(Arrays.asList(info1));
        assertEquals(0, resp.getErrno());
        assertEquals("ok", resp.getError());
        assertEquals(2, resp.getTotal());
        assertEquals(1, resp.getEntities().size());
        assertEquals("id1", resp.getEntities().get(0).getId());
    }

    @Test
    void testToString() {
        JaccountUserInfoResponse resp = new JaccountUserInfoResponse();
        resp.setErrno(1);
        resp.setError("err");
        resp.setTotal(0);
        resp.setEntities(null);
        String str = resp.toString();
        assertTrue(str.contains("errno=1"));
        assertTrue(str.contains("error='err'"));
        assertTrue(str.contains("total=0"));
    }
} 