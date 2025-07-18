package com.se2303.userservice.dto;

import com.se2303.userservice.dto.response.JaccountUserInfo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JaccountUserInfoTest {
    @Test
    void testSettersAndGetters() {
        JaccountUserInfo info = new JaccountUserInfo();
        info.setId("id1");
        info.setAccount("acc");
        info.setName("n");
        info.setKind("k");
        info.setCode("c");
        info.setUserType("ut");
        info.setClassNo("cl");
        info.setGender("g");
        info.setEmail("e");
        info.setTimeZone(8);
        info.setMobile("m");
        info.setUnionId("u");
        info.setAccountExpireDate("2024-01-01");
        assertEquals("id1", info.getId());
        assertEquals("acc", info.getAccount());
        assertEquals("n", info.getName());
        assertEquals("k", info.getKind());
        assertEquals("c", info.getCode());
        assertEquals("ut", info.getUserType());
        assertEquals("cl", info.getClassNo());
        assertEquals("g", info.getGender());
        assertEquals("e", info.getEmail());
        assertEquals(8, info.getTimeZone());
        assertEquals("m", info.getMobile());
        assertEquals("u", info.getUnionId());
        assertEquals("2024-01-01", info.getAccountExpireDate());
    }

    @Test
    void testToString() {
        JaccountUserInfo info = new JaccountUserInfo();
        info.setId("id1");
        info.setAccount("acc");
        info.setName("n");
        info.setKind("k");
        info.setCode("c");
        info.setUserType("ut");
        info.setClassNo("cl");
        info.setGender("g");
        info.setEmail("e");
        info.setTimeZone(8);
        info.setMobile("m");
        info.setUnionId("u");
        info.setAccountExpireDate("2024-01-01");
        String str = info.toString();
        assertTrue(str.contains("id1"));
        assertTrue(str.contains("acc"));
        assertTrue(str.contains("n"));
        assertTrue(str.contains("k"));
        assertTrue(str.contains("c"));
        assertTrue(str.contains("ut"));
        assertTrue(str.contains("cl"));
        assertTrue(str.contains("g"));
        assertTrue(str.contains("e"));
        assertTrue(str.contains("8"));
        assertTrue(str.contains("m"));
        assertTrue(str.contains("u"));
        assertTrue(str.contains("2024-01-01"));
    }

    @Test
    void testOrganizeInfo() {
        JaccountUserInfo.OrganizeInfo org = new JaccountUserInfo.OrganizeInfo();
        org.setName("orgName");
        org.setId("orgId");
        assertEquals("orgName", org.getName());
        assertEquals("orgId", org.getId());
        String str = org.toString();
        assertTrue(str.contains("orgName"));
        assertTrue(str.contains("orgId"));
    }
} 