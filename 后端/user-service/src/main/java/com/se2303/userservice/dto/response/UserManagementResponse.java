package com.se2303.userservice.dto.response;

import com.se2303.userservice.entity.User;

public class UserManagementResponse {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String sjtuId;
    private String jaccount;
    private String registrationDate;
    private Boolean valid;
    private Boolean isUser;

    public UserManagementResponse() {}

    public UserManagementResponse(Long id, String username, String nickname, String avatar, 
                                 String sjtuId, String jaccount, String registrationDate, 
                                 Boolean valid, Boolean isUser) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.sjtuId = sjtuId;
        this.jaccount = jaccount;
        this.registrationDate = registrationDate;
        this.valid = valid;
        this.isUser = isUser;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSjtuId() {
        return sjtuId;
    }

    public void setSjtuId(String sjtuId) {
        this.sjtuId = sjtuId;
    }

    public String getJaccount() {
        return jaccount;
    }

    public void setJaccount(String jaccount) {
        this.jaccount = jaccount;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Boolean getIsUser() {
        return isUser;
    }

    public void setIsUser(Boolean isUser) {
        this.isUser = isUser;
    }

    public static UserManagementResponse convertToUserManagementResponse(User user) {
        UserManagementResponse response = new UserManagementResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setSjtuId(user.getAuth().getSjtuId());
        response.setJaccount(user.getAuth().getJaccount());
        response.setValid(user.getAuth().getValid());
        response.setIsUser(user.getAuth().getIsUser());
        
        if (user.getRegistrationDate() != null) {
            response.setRegistrationDate(user.getRegistrationDate().toString());
        }
        
        return response;
    }
} 