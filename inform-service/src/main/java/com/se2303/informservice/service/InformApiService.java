package com.se2303.informservice.service;

import com.se2303.informservice.dto.request.CreateInformRequest;
import com.se2303.informservice.entity.Inform;
import com.se2303.informservice.entity.InformType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 外部服务调用接口
 * 提供给其他微服务调用的通知服务
 */
@Service
public class InformApiService {

    @Autowired
    private InformService informService;

    /**
     * 发送评论通知
     * @param userId 接收通知的用户ID
     * @param content 评论内容
     * @param relatedId 相关帖子ID
     * @param fromUserId 评论者ID
     */
    public void sendCommentNotification(Long userId, String content, String relatedId, Long fromUserId) {
        CreateInformRequest request = new CreateInformRequest();
        request.setUserId(userId);
        request.setType(InformType.COMMENT);
        request.setContent(content);
        request.setRelatedId(relatedId);
        request.setFromUserId(fromUserId);
        
        informService.createInform(request);
    }

    /**
     * 发送点赞通知
     * @param userId 接收通知的用户ID
     * @param relatedId 相关帖子ID
     * @param fromUserId 点赞者ID
     */
    public void sendLikeNotification(Long userId, String relatedId, Long fromUserId) {
        CreateInformRequest request = new CreateInformRequest();
        request.setUserId(userId);
        request.setType(InformType.LIKE);
        request.setContent(""); // 点赞通知content为空
        request.setRelatedId(relatedId);
        request.setFromUserId(fromUserId);
        
        informService.createInform(request);
    }

    /**
     * 通用通知发送方法
     * @param userId 接收通知的用户ID
     * @param type 通知类型
     * @param content 通知内容
     * @param relatedId 相关ID
     * @param fromUserId 发起者ID
     */
    public Inform sendInform(Long userId, InformType type, String content, String relatedId, Long fromUserId) {
        CreateInformRequest request = new CreateInformRequest();
        request.setUserId(userId);
        request.setType(type);
        request.setContent(content);
        request.setRelatedId(relatedId);
        request.setFromUserId(fromUserId);
        
        return informService.createInform(request);
    }

    /**
     * 发送评论通知（简化方法名）
     */
    public void sendCommentInform(Long userId, String content, String relatedId, Long fromUserId) {
        sendCommentNotification(userId, content, relatedId, fromUserId);
    }

    /**
     * 发送点赞通知（简化方法名）
     */
    public void sendLikeInform(Long userId, String relatedId, Long fromUserId) {
        sendLikeNotification(userId, relatedId, fromUserId);
    }
}
