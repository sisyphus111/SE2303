package com.se2303.informservice.service;

import com.se2303.informservice.dto.request.CreateInformRequest;
import com.se2303.informservice.entity.Inform;
import com.se2303.informservice.entity.InformType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InformApiServiceTest {

    @Mock
    private InformService informService;

    @InjectMocks
    private InformApiService informApiService;

    @Test
    void testSendCommentNotification() {
        Long userId = 1L;
        String content = "Test comment";
        String relatedId = "post123";
        Long fromUserId = 2L;
        Inform inform = new Inform(userId, InformType.COMMENT, content);
        when(informService.createInform(any(CreateInformRequest.class))).thenReturn(inform);

        informApiService.sendCommentNotification(userId, content, relatedId, fromUserId);

        verify(informService, times(1)).createInform(argThat(request ->
                request.getUserId().equals(userId) &&
                        request.getType() == InformType.COMMENT &&
                        request.getContent().equals(content) &&
                        request.getRelatedId().equals(relatedId) &&
                        request.getFromUserId().equals(fromUserId)
        ));
    }

    @Test
    void testSendLikeNotification() {
        Long userId = 1L;
        String relatedId = "post123";
        Long fromUserId = 2L;
        Inform inform = new Inform(userId, InformType.LIKE, "");
        when(informService.createInform(any(CreateInformRequest.class))).thenReturn(inform);

        informApiService.sendLikeNotification(userId, relatedId, fromUserId);

        verify(informService, times(1)).createInform(argThat(request ->
                request.getUserId().equals(userId) &&
                        request.getType() == InformType.LIKE &&
                        request.getContent().equals("") &&
                        request.getRelatedId().equals(relatedId) &&
                        request.getFromUserId().equals(fromUserId)
        ));
    }

    @Test
    void testSendInform() {
        Long userId = 1L;
        InformType type = InformType.COMMENT;
        String content = "Test comment";
        String relatedId = "post123";
        Long fromUserId = 2L;
        Inform inform = new Inform(userId, type, content);
        when(informService.createInform(any(CreateInformRequest.class))).thenReturn(inform);

        Inform result = informApiService.sendInform(userId, type, content, relatedId, fromUserId);

        assertEquals(inform, result, "应返回 informService 创建的 Inform 对象");
        verify(informService, times(1)).createInform(argThat(request ->
                request.getUserId().equals(userId) &&
                        request.getType() == type &&
                        request.getContent().equals(content) &&
                        request.getRelatedId().equals(relatedId) &&
                        request.getFromUserId().equals(fromUserId)
        ));
    }

    @Test
    void testSendCommentInform() {
        Long userId = 1L;
        String content = "Test comment";
        String relatedId = "post123";
        Long fromUserId = 2L;
        Inform inform = new Inform(userId, InformType.COMMENT, content);
        when(informService.createInform(any(CreateInformRequest.class))).thenReturn(inform);

        informApiService.sendCommentInform(userId, content, relatedId, fromUserId);

        verify(informService, times(1)).createInform(argThat(request ->
                request.getUserId().equals(userId) &&
                        request.getType() == InformType.COMMENT &&
                        request.getContent().equals(content) &&
                        request.getRelatedId().equals(relatedId) &&
                        request.getFromUserId().equals(fromUserId)
        ));
    }

    @Test
    void testSendLikeInform() {
        Long userId = 1L;
        String relatedId = "post123";
        Long fromUserId = 2L;
        Inform inform = new Inform(userId, InformType.LIKE, "");
        when(informService.createInform(any(CreateInformRequest.class))).thenReturn(inform);

        informApiService.sendLikeInform(userId, relatedId, fromUserId);

        verify(informService, times(1)).createInform(argThat(request ->
                request.getUserId().equals(userId) &&
                        request.getType() == InformType.LIKE &&
                        request.getContent().equals("") &&
                        request.getRelatedId().equals(relatedId) &&
                        request.getFromUserId().equals(fromUserId)
        ));
    }
}