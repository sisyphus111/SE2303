package com.se2303.talkservice.repository;

import com.se2303.talkservice.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 消息数据访问层
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /**
     * 分页获取所有有效消息，按创建时间倒序排列
     * @param pageable 分页参数
     * @return 消息分页结果
     */
    Page<Message> findAllByValidTrueOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 分页获取所有有效消息，按创建时间升序排列
     * @param pageable 分页参数
     * @return 消息分页结果
     */
    Page<Message> findAllByValidTrueOrderByCreatedAtAsc(Pageable pageable);
    
    /**
     * 根据用户ID分页获取有效消息，按创建时间倒序排列
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 消息分页结果
     */
    Page<Message> findByUserIdAndValidTrueOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID分页获取有效消息，按创建时间升序排列
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 消息分页结果
     */
    Page<Message> findByUserIdAndValidTrueOrderByCreatedAtAsc(Long userId, Pageable pageable);
} 