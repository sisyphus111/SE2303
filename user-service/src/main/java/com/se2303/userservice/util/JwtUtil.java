package com.se2303.userservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    private static String SECRET_KEY_STRING;
    private static long EXPIRATION_TIME;
    private static long REFRESH_EXPIRATION_TIME;
    
    @Value("${jwt.secret}")
    public void setSecretKey(String secretKey) {
        SECRET_KEY_STRING = secretKey;
    }
    
    @Value("${jwt.expiration}")
    public void setExpirationTime(long expirationTime) {
        EXPIRATION_TIME = expirationTime;
    }
    
    @Value("${jwt.refresh-expiration}")
    public void setRefreshExpirationTime(long refreshExpirationTime) {
        REFRESH_EXPIRATION_TIME = refreshExpirationTime;
    }
    
    private static SecretKey getSecretKey() {
        if (SECRET_KEY_STRING == null) {
            SECRET_KEY_STRING = "REDACTED";
        }
        return Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());
    }

    // 生成access token
    public static String generateToken(Long userId, boolean valid, boolean identity) {
        if (EXPIRATION_TIME == 0) EXPIRATION_TIME = 86400000; // 默认1天
        return Jwts.builder()
                .claim("X-User-Id", userId)
                .claim("X-Valid", valid)
                .claim("X-Identity", identity)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSecretKey())
                .compact();
    }

    // 生成refresh token
    public static String generateRefreshToken(Long userId, boolean valid, boolean identity) {
        if (REFRESH_EXPIRATION_TIME == 0) REFRESH_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 默认7天
        return Jwts.builder()
                .claim("X-User-Id", userId)
                .claim("X-Valid", valid)
                .claim("X-Identity", identity)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(getSecretKey())
                .compact();
    }

    // 解析token
    public static Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 校验token是否有效
    public static boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }
    
    // 调试方法：打印token中的所有claims
    public static void printTokenClaims(String token) {
        try {
            Claims claims = getClaims(token);
            System.out.println("=== JWT Token Claims ===");
            System.out.println("X-User-Id: " + claims.get("X-User-Id"));
            System.out.println("X-Valid: " + claims.get("X-Valid"));
            System.out.println("X-Identity: " + claims.get("X-Identity"));
            System.out.println("Issued At: " + claims.getIssuedAt());
            System.out.println("Expiration: " + claims.getExpiration());
        } catch (Exception e) {
            System.out.println("Error printing token claims: " + e.getMessage());
        }
    }

    // 从请求头中获取用户ID
    public static Long getUserId(String userIdHeader) {
        if (userIdHeader == null || userIdHeader.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(userIdHeader.trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid userId format in header: " + userIdHeader);
            return null;
        }
    }

    // 从请求头中获取用户有效性状态
    public static boolean getValid(String validHeader) {
        System.out.println("getValid called with header: '" + validHeader + "'");
        if (validHeader == null || validHeader.trim().isEmpty()) {
            System.out.println("validHeader is null or empty, returning false");
            return false;
        }
        boolean result = Boolean.parseBoolean(validHeader.trim());
        System.out.println("Parsed valid value: " + result);
        return result;
    }

    // 从请求头中获取用户身份标识
    public static boolean getIdentity(String identityHeader) {
        System.out.println("getIdentity called with header: '" + identityHeader + "'");
        if (identityHeader == null || identityHeader.trim().isEmpty()) {
            System.out.println("identityHeader is null or empty, returning false");
            return false;
        }
        boolean result = Boolean.parseBoolean(identityHeader.trim());
        System.out.println("Parsed identity value: " + result);
        return result;
    }
}