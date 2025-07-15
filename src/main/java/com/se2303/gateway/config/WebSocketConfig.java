package com.se2303.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WebSocket网关配置类
 * 
 * 功能说明：
 * - 配置WebSocket路由转发规则
 * - 处理WebSocket连接的身份验证
 * - 支持跨域WebSocket连接
 * 
 * 支持的WebSocket服务：
 * - 聊天室服务 (localhost:8090)：社区聊天室功能
 * - AI聊天服务 (localhost:8081)：AI智能对话功能
 */
@Configuration
public class WebSocketConfig {

    /**
     * 配置WebSocket路由
     * 
     * @param builder 路由构建器
     * @return RouteLocator 路由定位器
     */
    @Bean
    public RouteLocator webSocketRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 聊天室WebSocket路由
                .route("talk-websocket", r -> r
                        .path("/ws/talk/**")
                        .and()
                        .header("Upgrade", "websocket")
                        .filters(f -> f.rewritePath("/ws/talk/(?<segment>.*)", "/ws/${segment}"))
                        .uri("ws://localhost:8090")
                )
                // AI聊天WebSocket路由
                .route("ai-chat-websocket", r -> r
                        .path("/ws/chat/**")
                        .and()
                        .header("Upgrade", "websocket") 
                        .filters(f -> f.rewritePath("/ws/chat/(?<segment>.*)", "/ws/chat/${segment}"))
                        .uri("ws://localhost:8081")
                )
                .build();
    }
}