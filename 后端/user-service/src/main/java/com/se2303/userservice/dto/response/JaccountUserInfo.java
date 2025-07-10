package com.se2303.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jaccount用户信息响应DTO
 * 对应SJTU开发者平台的用户信息API响应格式
 */
public class JaccountUserInfo {
    
    /**
     * jAccount 自身的唯一 id，用于关联用户当前和曾经拥有的全部身份
     */
    @JsonProperty("id")
    private String id;
    
    /**
     * jaccount 账号名
     */
    @JsonProperty("account")
    private String account;
    
    /**
     * 真实姓名
     */
    @JsonProperty("name")
    private String name;
    
    /**
     * 数据对象类型，无业务意义
     */
    @JsonProperty("kind")
    private String kind;
    
    /**
     * 学工号
     */
    @JsonProperty("code")
    private String code;
    
    /**
     * 用户类型
     */
    @JsonProperty("userType")
    private String userType;
    
    /**
     * 组织信息
     */
    @JsonProperty("organize")
    private OrganizeInfo organize;
    
    /**
     * 顶级组织信息
     */
    @JsonProperty("topOrganize")
    private OrganizeInfo topOrganize;
    
    /**
     * 班级号
     */
    @JsonProperty("classNo")
    private String classNo;
    
    /**
     * 性别
     */
    @JsonProperty("gender")
    private String gender;
    
    /**
     * 邮箱
     */
    @JsonProperty("email")
    private String email;
    
    /**
     * 时区
     */
    @JsonProperty("timeZone")
    private Integer timeZone;
    
    /**
     * 手机号
     */
    @JsonProperty("mobile")
    private String mobile;
    
    /**
     * 微信unionId
     */
    @JsonProperty("unionId")
    private String unionId;
    
    /**
     * 账号过期日期
     */
    @JsonProperty("accountExpireDate")
    private String accountExpireDate;
    
    // 无参构造函数
    public JaccountUserInfo() {}
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getAccount() {
        return account;
    }
    
    public void setAccount(String account) {
        this.account = account;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getKind() {
        return kind;
    }
    
    public void setKind(String kind) {
        this.kind = kind;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public OrganizeInfo getOrganize() {
        return organize;
    }
    
    public void setOrganize(OrganizeInfo organize) {
        this.organize = organize;
    }
    
    public OrganizeInfo getTopOrganize() {
        return topOrganize;
    }
    
    public void setTopOrganize(OrganizeInfo topOrganize) {
        this.topOrganize = topOrganize;
    }
    
    public String getClassNo() {
        return classNo;
    }
    
    public void setClassNo(String classNo) {
        this.classNo = classNo;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Integer getTimeZone() {
        return timeZone;
    }
    
    public void setTimeZone(Integer timeZone) {
        this.timeZone = timeZone;
    }
    
    public String getMobile() {
        return mobile;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public String getUnionId() {
        return unionId;
    }
    
    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }
    
    public String getAccountExpireDate() {
        return accountExpireDate;
    }
    
    public void setAccountExpireDate(String accountExpireDate) {
        this.accountExpireDate = accountExpireDate;
    }
    
    @Override
    public String toString() {
        return "JaccountUserInfo{" +
                "id='" + id + '\'' +
                ", account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", kind='" + kind + '\'' +
                ", code='" + code + '\'' +
                ", userType='" + userType + '\'' +
                ", organize=" + organize +
                ", topOrganize=" + topOrganize +
                ", classNo='" + classNo + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", timeZone=" + timeZone +
                ", mobile='" + mobile + '\'' +
                ", unionId='" + unionId + '\'' +
                ", accountExpireDate='" + accountExpireDate + '\'' +
                '}';
    }
    
    /**
     * 组织信息内部类
     */
    public static class OrganizeInfo {
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("id")
        private String id;
        
        public OrganizeInfo() {}
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        @Override
        public String toString() {
            return "OrganizeInfo{" +
                    "name='" + name + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }
} 