package com.se2303.gateway.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 只存储用户学号，不存储其他个人信息
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "jaccount_id", unique = true, nullable = false)
    private String jaccountId;  // JAccount的唯一ID
    
    @Column(unique = true, nullable = false)
    private String account;     // JAccount账号名
    
    @Column(nullable = false)
    private String code;        // 学工号（唯一保留的个人信息）
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public User() {}
    
    public User(String jaccountId, String account, String code) {
        this.jaccountId = jaccountId;
        this.account = account;
        this.code = code;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getJaccountId() {
        return jaccountId;
    }
    
    public void setJaccountId(String jaccountId) {
        this.jaccountId = jaccountId;
    }
    
    public String getAccount() {
        return account;
    }
    
    public void setAccount(String account) {
        this.account = account;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 