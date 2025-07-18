package com.se2303.talkservice.config;

import com.se2303.talkservice.handler.TalkWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket配置类
 * 
 * 注意：使用原生WebSocket，不使用SockJS
 * 这样与前端的原生WebSocket连接兼容，也与bot-service保持一致
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Autowired
    private TalkWebSocketHandler talkWebSocketHandler;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册聊天WebSocket处理器 - 主要路径（原生WebSocket）
        registry.addHandler(talkWebSocketHandler, "/ws")
                .setAllowedOriginPatterns("http://localhost:3000", "https://*.cpolar.cn");
        
        // 注册聊天WebSocket处理器 - 前端连接路径
        registry.addHandler(talkWebSocketHandler, "/ws/conn")
                .setAllowedOriginPatterns("http://localhost:3000", "https://*.cpolar.cn");
        
        // 注册聊天WebSocket处理器 - 备用路径（原生WebSocket）
        registry.addHandler(talkWebSocketHandler, "/ws/talk/conn")
                .setAllowedOriginPatterns("http://localhost:3000", "https://*.cpolar.cn");
    }
} 