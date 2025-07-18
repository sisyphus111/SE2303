package com.se2303.userservice.dto.response;

import com.se2303.userservice.entity.User;

public class UserStatusResponse {
    private Long userId;
    private boolean identity;
    private boolean valid;

    public UserStatusResponse(Long userId, boolean identity, boolean valid) {
        this.userId = userId;
        this.identity = identity;
        this.valid = valid;
    }

    public UserStatusResponse() {}

    public Long getUserId() {
        return userId;
    }

    public boolean getIdentity() {
        return identity;
    }

    public boolean getValid() {
        return valid;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setIdentity(boolean identity) {
        this.identity = identity;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public static UserStatusResponse convertToUserStatusResponse(User user) {
        UserStatusResponse userStatusResponse = new UserStatusResponse();
        userStatusResponse.setUserId(user.getId());
        userStatusResponse.setIdentity(user.getAuth().getIsUser());
        userStatusResponse.setValid(user.getAuth().getValid()); // 使用Auth表的valid字段
        return userStatusResponse;
    }
}
