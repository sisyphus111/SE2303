package com.se2303.chatservice.service;

import com.se2303.chatservice.dto.ApiResponse;
import com.se2303.chatservice.dto.CreateMessageRequest;
import com.se2303.chatservice.model.Message;
import com.se2303.chatservice.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

public class MessageServiceTest {
    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateMessageSuccess() {
        CreateMessageRequest req = new CreateMessageRequest();
        req.setUserId(1L);
        req.setContent("hello");
        Message msg = new Message(1L, "hello");
        when(messageRepository.save(any(Message.class))).thenReturn(msg);
        ApiResponse resp = messageService.createMessage(req);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testCreateMessageUserIdNull() {
        CreateMessageRequest req = new CreateMessageRequest();
        req.setContent("hello");
        ApiResponse resp = messageService.createMessage(req);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testCreateMessageContentNull() {
        CreateMessageRequest req = new CreateMessageRequest();
        req.setUserId(1L);
        ApiResponse resp = messageService.createMessage(req);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testCreateMessageException() {
        CreateMessageRequest req = new CreateMessageRequest();
        req.setUserId(1L);
        req.setContent("hello");
        when(messageRepository.save(any(Message.class))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = messageService.createMessage(req);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetMessagesSuccess() {
        Message msg = new Message(1L, "hello");
        Page<Message> page = new PageImpl<>(Collections.singletonList(msg));
        when(messageRepository.findByValidTrueOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(page);
        ApiResponse resp = messageService.getMessages(1, 20);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testGetMessagesException() {
        when(messageRepository.findByValidTrueOrderByCreatedAtDesc(any(Pageable.class))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = messageService.getMessages(1, 20);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetMessagesByUserIdSuccess() {
        Message msg = new Message(1L, "hello");
        Page<Message> page = new PageImpl<>(Collections.singletonList(msg));
        when(messageRepository.findByUserIdAndValidTrueOrderByCreatedAtDesc(eq(1L), any(Pageable.class))).thenReturn(page);
        ApiResponse resp = messageService.getMessagesByUserId(1L, 1, 20);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testGetMessagesByUserIdUserIdNull() {
        ApiResponse resp = messageService.getMessagesByUserId(null, 1, 20);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetMessagesByUserIdException() {
        when(messageRepository.findByUserIdAndValidTrueOrderByCreatedAtDesc(eq(1L), any(Pageable.class))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = messageService.getMessagesByUserId(1L, 1, 20);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testGetAllMessagesSuccess() {
        Message msg = new Message(1L, "hello");
        when(messageRepository.findByValidTrueOrderByCreatedAtDesc()).thenReturn(Arrays.asList(msg));
        ApiResponse resp = messageService.getAllMessages();
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testGetAllMessagesException() {
        when(messageRepository.findByValidTrueOrderByCreatedAtDesc()).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = messageService.getAllMessages();
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testDeleteMessageSuccess() {
        Message msg = new Message(1L, "hello");
        msg.setValid(true);
        when(messageRepository.findById(eq(1L))).thenReturn(Optional.of(msg));
        when(messageRepository.save(any(Message.class))).thenReturn(msg);
        ApiResponse resp = messageService.deleteMessage(1L);
        assertTrue(resp.isSuccess());
    }

    @Test
    public void testDeleteMessageIdNull() {
        ApiResponse resp = messageService.deleteMessage(null);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testDeleteMessageNotFound() {
        when(messageRepository.findById(eq(1L))).thenReturn(Optional.empty());
        ApiResponse resp = messageService.deleteMessage(1L);
        assertFalse(resp.isSuccess());
    }

    @Test
    public void testDeleteMessageException() {
        when(messageRepository.findById(eq(1L))).thenThrow(new RuntimeException("fail"));
        ApiResponse resp = messageService.deleteMessage(1L);
        assertFalse(resp.isSuccess());
    }
} 