package com.se2303.botservice.config;

import com.se2303.botservice.handler.ChatWebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 测试 WebSocketConfig 类
 */
class WebSocketConfigTest {

    private WebSocketConfig webSocketConfig;
    private ChatWebSocketHandler chatWebSocketHandler;

    @BeforeEach
    void setUp() {
        webSocketConfig = new WebSocketConfig();
        chatWebSocketHandler = mock(ChatWebSocketHandler.class);
    }

    @Test
    void testRegisterWebSocketHandlers() {
        // 测试 WebSocket 处理器注册
        WebSocketHandlerRegistry registry = mock(WebSocketHandlerRegistry.class);
        WebSocketHandlerRegistration registration = mock(WebSocketHandlerRegistration.class);

        when(registry.addHandler(any(ChatWebSocketHandler.class), eq("/ws/chat")))
                .thenReturn(registration);
        when(registration.setAllowedOrigins(anyString())).thenReturn(registration);

        webSocketConfig.registerWebSocketHandlers(registry);

        verify(registry).addHandler(any(ChatWebSocketHandler.class), eq("/ws/chat"));
        verify(registration).setAllowedOrigins("*");
    }

    @Test
    void testChatWebSocketHandlerBean() {
        // 测试 ChatWebSocketHandler Bean 创建
        ChatWebSocketHandler handler = webSocketConfig.chatWebSocketHandler();
        assertNotNull(handler, "ChatWebSocketHandler 不应为 null");
        assertTrue(handler instanceof ChatWebSocketHandler, "应为 ChatWebSocketHandler 类型");
    }

    @Test
    void testWebSocketConfigurerImplementation() {
        // 测试 WebSocketConfigurer 接口实现
        assertTrue(webSocketConfig instanceof WebSocketConfigurer, "应实现 WebSocketConfigurer 接口");
    }
}