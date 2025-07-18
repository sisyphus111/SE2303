package com.se2303.chatservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se2303.chatservice.dto.ApiResponse;
import com.se2303.chatservice.dto.CreateMessageRequest;
import com.se2303.chatservice.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build();
    }

    @Test
    public void testHealth() throws Exception {
        mockMvc.perform(get("/api/chat/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testCreateMessageSuccess() throws Exception {
        CreateMessageRequest req = new CreateMessageRequest();
        req.setUserId(1L);
        req.setContent("hello");
        when(messageService.createMessage(any(CreateMessageRequest.class)))
                .thenReturn(ApiResponse.success(null, "消息发送成功"));
        mockMvc.perform(post("/api/chat/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testCreateMessageFail() throws Exception {
        CreateMessageRequest req = new CreateMessageRequest();
        req.setUserId(1L);
        req.setContent("hello");
        when(messageService.createMessage(any(CreateMessageRequest.class)))
                .thenThrow(new RuntimeException("fail"));
        mockMvc.perform(post("/api/chat/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testGetMessages() throws Exception {
        when(messageService.getMessages(eq(1), eq(20)))
                .thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(get("/api/chat/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testGetMessagesByUserId() throws Exception {
        when(messageService.getMessagesByUserId(eq(1L), eq(1), eq(20)))
                .thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(get("/api/chat/messages/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testGetAllMessages() throws Exception {
        when(messageService.getAllMessages()).thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(get("/api/chat/messages/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testDeleteMessageSuccess() throws Exception {
        when(messageService.deleteMessage(eq(1L))).thenReturn(ApiResponse.success(null, "ok"));
        mockMvc.perform(delete("/api/chat/messages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testDeleteMessageFail() throws Exception {
        when(messageService.deleteMessage(eq(1L))).thenReturn(ApiResponse.error("fail"));
        mockMvc.perform(delete("/api/chat/messages/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testDeleteMessageException() throws Exception {
        when(messageService.deleteMessage(eq(1L))).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(delete("/api/chat/messages/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }
} 