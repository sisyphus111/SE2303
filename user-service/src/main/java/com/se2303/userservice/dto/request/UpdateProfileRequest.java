package com.se2303.userservice.dto.request;

// 更新用户资料请求体
public class UpdateProfileRequest {
    private String username;
    private String nickname;
    private String avatar;
    private String gender;
    private String birthday; // 接收为字符串，后续转换为LocalDate
    private String location;
    private String bio;
    private String tags;
    private String profileVisibility;

    public UpdateProfileRequest() {}

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getLocation() {
        return location;
    }

    public String getBio() {
        return bio;
    }

    public String getTags() {
        return tags;
    }

    public String getProfileVisibility() {
        return profileVisibility;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

}