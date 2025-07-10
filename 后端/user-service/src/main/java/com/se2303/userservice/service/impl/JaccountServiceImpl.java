package com.se2303.userservice.service.impl;

import com.se2303.userservice.config.JaccountConfig;
import com.se2303.userservice.dto.response.JaccountTokenResponse;
import com.se2303.userservice.dto.response.JaccountUserInfo;
import com.se2303.userservice.dto.response.JaccountUserInfoResponse;
import com.se2303.userservice.service.JaccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class JaccountServiceImpl implements JaccountService {
    
    private static final Logger logger = LoggerFactory.getLogger(JaccountService.class);
    
    @Autowired
    private JaccountConfig jaccountConfig;
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * 用授权码换取access_token
     */
    public String exchangeCodeForToken(String code, String redirectUri) {
        logger.info("开始用授权码换取access_token: code={}", code.substring(0, Math.min(10, code.length())) + "...");
        logger.debug("完整授权码: {}", code);
        logger.info("重定向URI: {}", redirectUri);
        logger.info("客户端ID: {}", jaccountConfig.getClientId());
        logger.debug("客户端密钥长度: {}", jaccountConfig.getClientSecret() != null ? jaccountConfig.getClientSecret().length() : 0);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("client_id", jaccountConfig.getClientId());
        params.add("client_secret", jaccountConfig.getClientSecret());
        params.add("redirect_uri", redirectUri);
        
        logger.info("Token请求参数: grant_type={}, code={}..., client_id={}, redirect_uri={}", 
            "authorization_code", code.substring(0, Math.min(10, code.length())) + "...", 
            jaccountConfig.getClientId(), redirectUri);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("User-Agent", "SE2303-Frontend/1.0");
        
        logger.info("请求头: Content-Type={}, User-Agent={}", 
            headers.getContentType(), headers.get("User-Agent"));
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        try {
            logger.info("发送token交换请求到: {}", jaccountConfig.getTokenUrl());
            ResponseEntity<JaccountTokenResponse> response = restTemplate.postForEntity(
                jaccountConfig.getTokenUrl(), 
                request, 
                JaccountTokenResponse.class
            );
            
            logger.info("Token响应状态码: {}", response.getStatusCode());
            logger.info("Token响应头: {}", response.getHeaders());
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JaccountTokenResponse tokenResponse = response.getBody();
                if (tokenResponse != null && tokenResponse.getAccessToken() != null) {
                    logger.info("成功获取Jaccount access_token, expires_in: {}", tokenResponse.getExpiresIn());
                    logger.info("完整access_token: {}", tokenResponse.getAccessToken());
                    logger.debug("完整access_token: {}", tokenResponse.getAccessToken());
                    logger.info("Token类型: {}", tokenResponse.getTokenType());
                    return tokenResponse.getAccessToken();
                } else {
                    logger.error("Jaccount返回的token响应为空或不包含access_token");
                    logger.error("Token响应体: {}", tokenResponse);
                    throw new RuntimeException("获取access_token失败：响应为空");
                }
            } else {
                logger.error("Jaccount token请求失败，状态码: {}", response.getStatusCode());
                logger.error("错误响应体: {}", response.getBody());
                throw new RuntimeException("获取access_token失败：HTTP " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("换取access_token时发生异常", e);
            throw new RuntimeException("换取access_token失败: " + e.getMessage(), e);
        }
    }

    /**
     * 用access_token获取用户信息
     */
    public JaccountUserInfo getUserInfo(String accessToken) {
        logger.info("开始获取用户信息");
        logger.debug("使用的access_token: {}", accessToken);
        
        // 根据SJTU API文档，使用query参数传递access_token
        String url = jaccountConfig.getUserInfoUrl() + "?access_token=" + accessToken;
        logger.info("用户信息请求URL: {}", url);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "SE2303-Frontend/1.0");
        headers.setAccept(java.util.Arrays.asList(MediaType.APPLICATION_JSON));
        
        logger.info("用户信息请求头: User-Agent={}, Accept={}", 
            headers.get("User-Agent"), headers.getAccept());
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            logger.info("发送用户信息请求到: {}", url);
            ResponseEntity<JaccountUserInfoResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                JaccountUserInfoResponse.class
            );
            
            logger.info("用户信息响应状态码: {}", response.getStatusCode());
            logger.info("用户信息响应头: {}", response.getHeaders());
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JaccountUserInfoResponse userInfoResponse = response.getBody();
                if (userInfoResponse != null) {
                    logger.info("API响应: errno={}, error={}, total={}", 
                        userInfoResponse.getErrno(), userInfoResponse.getError(), userInfoResponse.getTotal());
                    
                    if (userInfoResponse.getErrno() == 0 && userInfoResponse.getEntities() != null && !userInfoResponse.getEntities().isEmpty()) {
                        JaccountUserInfo userInfo = userInfoResponse.getEntities().get(0);
                        logger.info("成功获取用户信息: id={}, account={}, name={}", 
                            userInfo.getId(), userInfo.getAccount(), userInfo.getName());
                        logger.info("用户详细信息: userType={}, code={}, email={}, mobile={}", 
                            userInfo.getUserType(), userInfo.getCode(), userInfo.getEmail(), userInfo.getMobile());
                        logger.debug("完整用户信息对象: {}", userInfo);
                        return userInfo;
                    } else {
                        logger.error("Jaccount返回的用户信息为空或错误: errno={}, error={}", 
                            userInfoResponse.getErrno(), userInfoResponse.getError());
                        logger.error("响应体: {}", userInfoResponse);
                        throw new RuntimeException("获取用户信息失败：API返回错误或数据为空");
                    }
                } else {
                    logger.error("Jaccount返回的响应为空");
                    throw new RuntimeException("获取用户信息失败：响应为空");
                }
            } else {
                logger.error("Jaccount用户信息请求失败，状态码: {}", response.getStatusCode());
                logger.error("错误响应体: {}", response.getBody());
                throw new RuntimeException("获取用户信息失败：HTTP " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("获取用户信息时发生异常", e);
            logger.error("异常详情: {}", e.getMessage());
            throw new RuntimeException("获取用户信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 向jAccount发送登出请求
     */
    public void logout(String postLogoutRedirectUri, String state) {
        logger.info("开始向jAccount发送登出请求");
        logger.info("登出重定向地址: {}", postLogoutRedirectUri);
        logger.info("状态参数: {}", state);
        
        try {
            // 构建登出URL
            StringBuilder logoutUrlBuilder = new StringBuilder(jaccountConfig.getLogoutUrl());
            logoutUrlBuilder.append("?client_id=").append(URLEncoder.encode(jaccountConfig.getClientId(), StandardCharsets.UTF_8));
            logoutUrlBuilder.append("&post_logout_redirect_uri=").append(URLEncoder.encode(postLogoutRedirectUri, StandardCharsets.UTF_8));
            
            if (state != null && !state.trim().isEmpty()) {
                logoutUrlBuilder.append("&state=").append(URLEncoder.encode(state, StandardCharsets.UTF_8));
            }
            
            String logoutUrl = logoutUrlBuilder.toString();
            logger.info("完整登出URL: {}", logoutUrl);
            
            // 发送GET请求到jAccount登出接口
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", "SE2303-Frontend/1.0");
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            logger.info("发送登出请求到: {}", logoutUrl);
            ResponseEntity<String> response = restTemplate.exchange(
                logoutUrl,
                HttpMethod.GET,
                request,
                String.class
            );
            
            logger.info("jAccount登出响应状态码: {}", response.getStatusCode());
            logger.info("jAccount登出响应头: {}", response.getHeaders());
            
            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.FOUND) {
                logger.info("jAccount登出请求发送成功");
            } else {
                logger.warn("jAccount登出请求返回非预期状态码: {}", response.getStatusCode());
            }
            
        } catch (Exception e) {
            logger.error("向jAccount发送登出请求时发生异常", e);
            // 不抛出异常，因为本地登出仍然应该成功
            logger.warn("jAccount登出请求失败，但本地登出将继续执行: {}", e.getMessage());
        }
    }
} 