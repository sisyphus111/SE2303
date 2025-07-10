package com.se2303.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Jaccount用户信息API响应DTO
 * 对应实际的API响应格式
 */
public class JaccountUserInfoResponse {
    
    @JsonProperty("errno")
    private Integer errno;
    
    @JsonProperty("error")
    private String error;
    
    @JsonProperty("total")
    private Integer total;
    
    @JsonProperty("entities")
    private List<JaccountUserInfo> entities;
    
    // 无参构造函数
    public JaccountUserInfoResponse() {}
    
    // Getters and Setters
    public Integer getErrno() {
        return errno;
    }
    
    public void setErrno(Integer errno) {
        this.errno = errno;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public Integer getTotal() {
        return total;
    }
    
    public void setTotal(Integer total) {
        this.total = total;
    }
    
    public List<JaccountUserInfo> getEntities() {
        return entities;
    }
    
    public void setEntities(List<JaccountUserInfo> entities) {
        this.entities = entities;
    }
    
    @Override
    public String toString() {
        return "JaccountUserInfoResponse{" +
                "errno=" + errno +
                ", error='" + error + '\'' +
                ", total=" + total +
                ", entities=" + entities +
                '}';
    }
} 