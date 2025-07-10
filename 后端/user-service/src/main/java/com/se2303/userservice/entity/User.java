package com.se2303.userservice.entity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id; // 使用Auth的ID作为主键

    @Column(nullable = false)
    private String username;

    @Column(nullable = true)
    private String nickname;

    @Column(nullable = false)
    private String avatar;

    @Column(nullable = true)
    private String gender; // 男、女、保密

    @Column(nullable = true)
    private LocalDate birthday;

    @Column(nullable = true)
    private String location;

    @Column(nullable = true, length = 500)
    private String bio; // 个人简介

    @Column(nullable = true, length = 1000)
    private String tags; // 存储为逗号分隔的字符串或JSON

    @Column(nullable = true, name = "profile_visibility")
    private String profileVisibility; // 个人资料可见性：public/private

    @Column(nullable = true, name = "registration_date")
    private LocalDate registrationDate; // 注册时间

    @Column(nullable = true, name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne
    @MapsId // 使用 Auth 的 id 作为 User 的 id
    @JoinColumn(name = "id")
    private Auth auth;

    

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getNickname() {return nickname;}

    public void setNickname(String nickname) {this.nickname = nickname;}

    public String getAvatar() {return avatar;}
    
    public void setAvatar(String avatar) {this.avatar = avatar;}

    public String getGender() {return gender;}

    public void setGender(String gender) {this.gender = gender;}

    public LocalDate getBirthday() {return birthday;}

    public void setBirthday(LocalDate birthday) {this.birthday = birthday;}

    public String getLocation() {return location;}

    public void setLocation(String location) {this.location = location;}

    public String getBio() {return bio;}

    public void setBio(String bio) {this.bio = bio;}

    public String getTags() {return tags;}

    public void setTags(String tags) {this.tags = tags;}

    public String getProfileVisibility() {return profileVisibility;}

    public void setProfileVisibility(String profileVisibility) {this.profileVisibility = profileVisibility;}

    public LocalDate getRegistrationDate() {return registrationDate;}

    public void setRegistrationDate(LocalDate registrationDate) {this.registrationDate = registrationDate;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}

    public Auth getAuth() {return auth;}

    public void setAuth(Auth auth) {this.auth = auth;}
}
