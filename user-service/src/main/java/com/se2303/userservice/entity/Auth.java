package com.se2303.userservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "auth")
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键，自动递增

    @Column(nullable = false, unique = true)
    private String jaccount;

    @Column(nullable = false, unique = true)
    private String sjtuId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = true, unique = true)
    private String telephone;

    @Column(nullable = false)
    private Boolean isUser;

    @Column(nullable = false)
    private Boolean valid;

    @Column(nullable = true, name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "auth", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @OneToOne(mappedBy = "auth", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserPreferences userPreferences;

    // 默认构造函数
    public Auth() {}

    // 构造函数
    public Auth(String jaccount, String sjtuId, String name, String telephone, Boolean isUser, Boolean valid) {
        this.jaccount = jaccount;
        this.sjtuId = sjtuId;
        this.name = name;
        this.telephone = telephone;
        this.isUser = isUser;
        this.valid = valid;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJaccount() {
        return jaccount;
    }

    public void setJaccount(String jaccount) {
        this.jaccount = jaccount;
    }

    public String getSjtuId() {
        return sjtuId;
    }

    public void setSjtuId(String sjtuId) {
        this.sjtuId = sjtuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Boolean getIsUser() {
        return isUser;
    }

    public void setIsUser(Boolean isUser) {
        this.isUser = isUser;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }   

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserPreferences getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
    }
}
