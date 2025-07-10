package com.se2303.userservice.dto.response;

import com.se2303.userservice.entity.User;

public class UserProfileResponse {
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String gender;
    private String birthday;
    private String tags;
    private String location;
    private String bio;
    private String profileVisibility;
    private String registrationDate;


    public UserProfileResponse(Long userId, String username, String nickname, String avatar, String gender, String birthday, String tags, String location, String bio, String profileVisibility, String registrationDate) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.gender = gender;
        this.birthday = birthday;
        this.tags = tags;
        this.location = location;
        this.bio = bio;
        this.profileVisibility = profileVisibility;
        this.registrationDate = registrationDate;
    }

    public UserProfileResponse() {}

    public Long getUserId() {
        return userId;
    }

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

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getLocation() {
        return location;
    }

    public String getBio() {
        return bio;
    }

    public String getProfileVisibility() {
        return profileVisibility;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setProfileVisibility(String profileVisibility) {
        this.profileVisibility = profileVisibility;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }


    public static UserProfileResponse convertToUserProfileResponse(User user) {
       UserProfileResponse userProfileResponse = new UserProfileResponse();
       userProfileResponse.setUserId(user.getId());
       userProfileResponse.setUsername(user.getUsername());
       if(user.getNickname() != null) userProfileResponse.setNickname(user.getNickname());
       if(user.getAvatar() != null) userProfileResponse.setAvatar(user.getAvatar());
       if(user.getGender() != null) userProfileResponse.setGender(user.getGender());
       if(user.getBirthday() != null) userProfileResponse.setBirthday(user.getBirthday().toString());
       if(user.getTags() != null) userProfileResponse.setTags(user.getTags());
       if(user.getLocation() != null) userProfileResponse.setLocation(user.getLocation());
       if(user.getBio() != null) userProfileResponse.setBio(user.getBio());
       if(user.getProfileVisibility() != null) userProfileResponse.setProfileVisibility(user.getProfileVisibility());
       if(user.getRegistrationDate() != null) userProfileResponse.setRegistrationDate(user.getRegistrationDate().toString());
       return userProfileResponse;
    }
}
