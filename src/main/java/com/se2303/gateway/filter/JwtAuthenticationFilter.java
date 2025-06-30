package com.se2303.gateway.filter;

import com.se2303.gateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * JWT认证过滤器
 * 验证请求中的JWT token并将用户信息添加到请求头中
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private final JwtUtil jwtUtil;
    
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        
        // 跳过认证相关的路径和测试路径
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }
        
        // 从cookie中获取access token
        String accessToken = getTokenFromCookie(exchange, "access_token");
        
        if (accessToken == null) {
            // 尝试从Authorization header获取token
            accessToken = getTokenFromHeader(exchange);
        }
        
        if (accessToken == null || !jwtUtil.validateToken(accessToken) || !jwtUtil.isAccessToken(accessToken)) {
            logger.warn("Invalid or missing access token for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        try {
            // 从token中提取用户信息（只包含学号）
            String userId = jwtUtil.getUserIdFromToken(accessToken);
            String account = jwtUtil.getUsernameFromToken(accessToken);
            String code = jwtUtil.getCodeFromToken(accessToken);
            
            // 将用户信息添加到请求头中，供后端服务使用（只传递学号）
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Account", account)
                    .header("X-User-Code", code)  // 只传递学号
                    .header("X-Gateway-Auth", "jwt")
                    .build();
            
            logger.debug("Authenticated user: {} (ID: {}, Code: {})", account, userId, code);
            
            return chain.filter(exchange.mutate().request(request).build());
            
        } catch (Exception e) {
            logger.error("Failed to process JWT token", e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
    
    /**
     * 检查是否为公开路径（不需要认证）
     */
    private boolean isPublicPath(String path) {
        return path.startsWith("/api/auth/") ||
               path.startsWith("/api/test/") ||
               path.startsWith("/actuator/") ||
               path.equals("/") ||
               path.startsWith("/static/") ||
               path.startsWith("/public/");
    }
    
    /**
     * 从cookie中获取token
     */
    private String getTokenFromCookie(ServerWebExchange exchange, String cookieName) {
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst(cookieName);
        return cookie != null ? cookie.getValue() : null;
    }
    
    /**
     * 从Authorization header中获取token
     */
    private String getTokenFromHeader(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
    
    @Override
    public int getOrder() {
        return -100; // 高优先级，在其他业务过滤器之前执行
    }
} 