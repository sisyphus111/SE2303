package com.se2303.userservice.service;

import com.se2303.userservice.dto.response.JaccountUserInfo;
import org.springframework.stereotype.Service;

@Service
public interface JaccountService {
    
    /**
     * 用授权码换取access_token
     */
    public String exchangeCodeForToken(String code, String redirectUri);

    /**
     * 用access_token获取用户信息
     */
    public JaccountUserInfo getUserInfo(String accessToken);
    
    /**
     * 向jAccount发送登出请求
     * @param postLogoutRedirectUri 登出后的重定向地址
     * @param state 可选的状态参数
     */
    public void logout(String postLogoutRedirectUri, String state);
    
} 