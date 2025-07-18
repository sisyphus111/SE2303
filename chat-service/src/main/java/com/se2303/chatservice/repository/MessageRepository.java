package com.se2303.chatservice.repository;

import com.se2303.chatservice.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /**
     * 按创建时间倒序分页查询消息
     */
    Page<Message> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 按创建时间倒序查询所有消息（不分页）
     */
    List<Message> findAllByOrderByCreatedAtDesc();
    
    /**
     * 按ID正序分页查询消息
     */
    Page<Message> findAllByOrderByIdAsc(Pageable pageable);
    
    /**
     * 根据用户ID查询消息
     */
    Page<Message> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 查询所有有效消息（不分页）
     */
    List<Message> findByValidTrueOrderByCreatedAtDesc();
    
    /**
     * 分页查询有效消息
     */
    Page<Message> findByValidTrueOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 根据用户ID查询有效消息
     */
    Page<Message> findByUserIdAndValidTrueOrderByCreatedAtDesc(Long userId, Pageable pageable);
} 