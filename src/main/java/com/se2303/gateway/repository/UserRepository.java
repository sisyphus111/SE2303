package com.se2303.gateway.repository;

import com.se2303.gateway.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据JAccount ID查找用户
     */
    Optional<User> findByJaccountId(String jaccountId);
    
    /**
     * 根据账号名查找用户
     */
    Optional<User> findByAccount(String account);
    
    /**
     * 根据学号查找用户
     */
    Optional<User> findByCode(String code);
    
    /**
     * 检查JAccount ID是否存在
     */
    boolean existsByJaccountId(String jaccountId);
    
    /**
     * 检查账号名是否存在
     */
    boolean existsByAccount(String account);
    
    /**
     * 检查学号是否存在
     */
    boolean existsByCode(String code);
} 