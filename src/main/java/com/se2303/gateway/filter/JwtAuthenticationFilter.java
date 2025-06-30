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
 * 简化版本：只负责token验证和转发，不处理token生成
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
        
        // 跳过不需要认证的路径（注册、登录等）
        if (isPublicPath(path)) {
            logger.debug("Skipping authentication for public path: {}", path);
            return chain.filter(exchange);
        }
        
        // 获取token
        String token = getTokenFromRequest(exchange);
        
        if (token == null) {
            logger.warn("No token found for protected path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        // 验证token
        if (!jwtUtil.validateToken(token)) {
            logger.warn("Invalid token for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        try {
            // 从token中提取用户ID
            String userId = jwtUtil.getUserIdFromToken(token);
            
            if (userId == null || userId.isEmpty()) {
                logger.warn("No userId found in token for path: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            
            // 将用户ID添加到请求头中，供后端服务使用
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-Gateway-Auth", "jwt")
                    .build();
            
            logger.debug("Authenticated user ID: {} for path: {}", userId, path);
            
            return chain.filter(exchange.mutate().request(request).build());
            
        } catch (Exception e) {
            logger.error("Failed to process JWT token for path: " + path, e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
    
    /**
     * 检查是否为公开路径（不需要认证）
     */
    private boolean isPublicPath(String path) {
        return path.startsWith("/api/user/") ||          // 认证相关路径（注册、登录）
               path.startsWith("/api/test/") ||          // 测试路径
               path.startsWith("/actuator/") ||          // 监控端点
               path.equals("/") ||                       // 根路径
               path.startsWith("/static/") ||            // 静态资源
               path.startsWith("/public/");              // 公共资源
    }
    
    /**
     * 从请求中获取token（支持Authorization header和Cookie）
     */
    private String getTokenFromRequest(ServerWebExchange exchange) {
        // 首先尝试从Authorization header获取
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        // 然后尝试从Cookie获取
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("access_token");
        if (cookie != null) {
            return cookie.getValue();
        }
        
        return null;
    }
    
    @Override
    public int getOrder() {
        return -100; // 高优先级，在其他业务过滤器之前执行
    }
} 