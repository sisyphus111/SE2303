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

    public UpdateProfileRequest(String username, String nickname, String avatar, String gender, String birthday, String location, String bio, String tags, String profileVisibility) {
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.gender = gender;
        this.birthday = birthday;
        this.location = location;
        this.bio = bio;
        this.tags = tags;
        this.profileVisibility = profileVisibility;
    }

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

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setProfileVisibility(String profileVisibility) {
        this.profileVisibility = profileVisibility;
    }
}