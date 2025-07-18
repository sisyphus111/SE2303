package com.se2303.userservice.dto.response;

import com.se2303.userservice.entity.User;
import com.se2303.userservice.entity.UserPreferences;
import com.se2303.userservice.repository.UserPreferencesRepository;

public class UserInfoForCommunityResponse {
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private Boolean valid;

    public UserInfoForCommunityResponse(Long userId, String username, String nickname, String avatar, Boolean valid) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.valid = valid;
    }

    public UserInfoForCommunityResponse() {

    }



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

    public Boolean getValid() {
        return valid;
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

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public static UserInfoForCommunityResponse convertToUserInfoForCommunityResponse(User user, UserPreferences userPreferences) {
        boolean valid = userPreferences.getAnonymousPosting();
        return new UserInfoForCommunityResponse(user.getId(), user.getUsername(), user.getNickname(), user.getAvatar(), valid);
    }
    
}
