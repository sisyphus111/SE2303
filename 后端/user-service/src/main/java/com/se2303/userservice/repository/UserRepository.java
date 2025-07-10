package com.se2303.userservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.se2303.userservice.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 查找所有普通用户（isUser=true）
     * @return 普通用户列表
     */
    @Query("SELECT u FROM User u WHERE u.auth.isUser = true")
    List<User> findByAuthIsUserTrue();
} 