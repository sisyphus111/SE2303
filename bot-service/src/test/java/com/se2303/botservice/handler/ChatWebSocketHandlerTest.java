package com.se2303.botservice.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se2303.botservice.model.ChatMessage;
import com.se2303.botservice.service.AIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 测试 ChatWebSocketHandler 类
 */
class ChatWebSocketHandlerTest {

    private ChatWebSocketHandler handler;
    private AIService aiService;
    private ObjectMapper objectMapper;
    private WebSocketSession session;

    @BeforeEach
    void setUp() throws Exception {
        aiService = mock(AIService.class);
        objectMapper = new ObjectMapper();
        handler = new ChatWebSocketHandler();
        handler.aiService = aiService;
        session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("testSession");
        when(session.isOpen()).thenReturn(true);
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws/chat?userId=testUser"));

        // 初始化 sessionHistory 以避免 NullPointerException
        handler.sessionHistory.put("testSession", new ArrayList<>());
    }

    @Test
    void testAfterConnectionEstablished() throws Exception {
        // 测试连接建立
        doNothing().when(aiService).setCurrentUser(anyString(), anyString());
        doNothing().when(aiService).resetConversationTimer();
        when(session.isOpen()).thenReturn(true);

        handler.afterConnectionEstablished(session);

        verify(aiService).setCurrentUser("testUser", "testSession");
        verify(session).sendMessage(any(TextMessage.class));
        assertTrue(handler.sessionHistory.containsKey("testSession"), "会话历史应包含 testSession");
        assertEquals(1, handler.sessionHistory.get("testSession").size(), "历史记录应包含欢迎消息");
        assertEquals("ai", handler.sessionHistory.get("testSession").get(0).getType(), "欢迎消息类型应为 ai");
    }

    @Test
    void testHandleTextMessageSuccess() throws Exception {
        // 测试处理文本消息
        ChatMessage userMessage = new ChatMessage("user", "你好", System.currentTimeMillis());
        String jsonMessage = objectMapper.writeValueAsString(userMessage);
        TextMessage textMessage = new TextMessage(jsonMessage);

        doAnswer(invocation -> {
            Consumer<String> onChunk = invocation.getArgument(2, Consumer.class);
            Consumer<String> onComplete = invocation.getArgument(3, Consumer.class);
            Runnable onFinish = invocation.getArgument(4, Runnable.class);
            onChunk.accept("AI 响应");
            onComplete.accept("完整 AI 响应");
            onFinish.run();
            return null;
        }).when(aiService).getStreamingResponse(anyString(), anyList(), any(Consumer.class), any(Consumer.class), any(Runnable.class));

        handler.handleTextMessage(session, textMessage);

        verify(aiService).getStreamingResponse(eq("你好"), anyList(), any(Consumer.class), any(Consumer.class), any(Runnable.class));
        verify(session, atLeastOnce()).sendMessage(any(TextMessage.class));
        assertEquals(2, handler.sessionHistory.get("testSession").size(), "历史记录应包含用户消息、AI完整响应和ai_end消息");
        assertEquals("user", handler.sessionHistory.get("testSession").get(0).getType(), "第一条消息应为用户消息");
        assertEquals("你好", handler.sessionHistory.get("testSession").get(0).getContent(), "用户消息内容应匹配");
        assertEquals("ai", handler.sessionHistory.get("testSession").get(1).getType(), "第二条消息应为 AI 响应");
        assertEquals("完整 AI 响应", handler.sessionHistory.get("testSession").get(1).getContent(), "AI 响应内容应匹配");
    }

    @Test
    void testHandleTextMessageError() throws Exception {
        // 测试处理文本消息错误
        TextMessage textMessage = new TextMessage("invalid json");

        handler.handleTextMessage(session, textMessage);

        verify(session).sendMessage(any(TextMessage.class));
        assertEquals(1, handler.sessionHistory.get("testSession").size(), "历史记录应包含错误消息");
        assertEquals("error", handler.sessionHistory.get("testSession").get(0).getType(), "消息类型应为 error");
        assertEquals("抱歉，处理您的消息时出现了错误，请稍后重试。", handler.sessionHistory.get("testSession").get(0).getContent(), "错误消息内容应匹配");
    }

    @Test
    void testAfterConnectionClosed() throws Exception {
        // 测试连接关闭
        handler.sessionHistory.put("testSession", new ArrayList<>(List.of(new ChatMessage())));
        handler.sessions.put("testSession", session);
        doNothing().when(aiService).resetConversationTimer();

        handler.afterConnectionClosed(session, CloseStatus.NORMAL);

        verify(aiService).resetConversationTimer();
        assertFalse(handler.sessionHistory.containsKey("testSession"), "会话历史应被移除");
        assertFalse(handler.sessions.containsKey("testSession"), "会话应被移除");
    }

    @Test
    void testHandleTransportError() throws Exception {
        // 测试传输错误
        handler.sessionHistory.put("testSession", new ArrayList<>(List.of(new ChatMessage())));
        handler.sessions.put("testSession", session);

        handler.handleTransportError(session, new Exception("传输错误"));

        assertFalse(handler.sessionHistory.containsKey("testSession"), "会话历史应被移除");
        assertFalse(handler.sessions.containsKey("testSession"), "会话应被移除");
    }

    @Test
    void testExtractUserIdFromSessionWithQuery() throws Exception {
        // 测试从 URI 查询参数提取用户 ID
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws/chat?userId=testUser123"));

        String userId = handler.extractUserIdFromSession(session);
        assertEquals("testUser123", userId, "用户 ID 应匹配");
    }

    @Test
    void testExtractUserIdFromSessionNoQuery() throws Exception {
        // 测试没有查询参数时的默认用户 ID
        when(session.getUri()).thenReturn(new URI("ws://localhost/ws/chat"));

        String userId = handler.extractUserIdFromSession(session);
        assertEquals("user_testSession", userId, "默认用户 ID 应匹配");
    }

    @Test
    void testAddToHistoryMaxSize() {
        // 测试历史记录大小限制
        for (int i = 0; i < 25; i++) {
            handler.addToHistory("testSession", new ChatMessage("user", "消息 " + i, System.currentTimeMillis()));
        }
        assertEquals(20, handler.sessionHistory.get("testSession").size(), "历史记录大小应为 20");
        assertEquals("消息 5", handler.sessionHistory.get("testSession").get(0).getContent(), "最早保留的消息应为第5条");
    }
}