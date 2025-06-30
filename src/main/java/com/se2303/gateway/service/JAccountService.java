package com.se2303.gateway.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.se2303.gateway.entity.User;
import com.se2303.gateway.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * JAccount服务类
 * 处理JAccount OIDC登录流程
 */
@Service
public class JAccountService {
    
    private static final Logger logger = LoggerFactory.getLogger(JAccountService.class);
    
    @Value("${jaccount.client-id}")
    private String clientId;
    
    @Value("${jaccount.client-secret}")
    private String clientSecret;
    
    @Value("${jaccount.redirect-uri}")
    private String redirectUri;
    
    @Value("${jaccount.profile-api-url}")
    private String profileApiUrl;
    
    private final WebClient webClient;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    public JAccountService(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder().build();
    }
    
    /**
     * 获取JAccount授权URL
     */
    public String getAuthorizationUrl(String state) {
        return String.format(
            "https://jaccount.sjtu.edu.cn/oauth2/authorize?response_type=code&scope=openid&client_id=%s&redirect_uri=%s&state=%s",
            clientId, redirectUri, state
        );
    }
    
    /**
     * 用授权码换取访问令牌
     */
    public Mono<String> exchangeCodeForToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUri);
        
        return webClient.post()
                .uri("https://jaccount.sjtu.edu.cn/oauth2/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(response);
                        return jsonNode.get("access_token").asText();
                    } catch (Exception e) {
                        logger.error("Failed to parse token response", e);
                        throw new RuntimeException("Failed to parse token response", e);
                    }
                });
    }
    
    /**
     * 用访问令牌获取用户信息
     */
    public Mono<JsonNode> getUserProfile(String accessToken) {
        return webClient.get()
                .uri(profileApiUrl + "?access_token=" + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        return objectMapper.readTree(response);
                    } catch (Exception e) {
                        logger.error("Failed to parse user profile response", e);
                        throw new RuntimeException("Failed to parse user profile response", e);
                    }
                });
    }
    
    /**
     * 处理JAccount登录回调
     */
    public Mono<User> handleJAccountCallback(String code) {
        return exchangeCodeForToken(code)
                .flatMap(this::getUserProfile)
                .map(this::createOrUpdateUser);
    }
    
    /**
     * 创建或更新用户
     */
    private User createOrUpdateUser(JsonNode userProfile) {
        try {
            // 从JAccount API响应中提取用户信息
            JsonNode entities = userProfile.get("entities");
            if (entities == null || !entities.isArray() || entities.size() == 0) {
                throw new RuntimeException("Invalid user profile response");
            }
            
            JsonNode userInfo = entities.get(0);
            String jaccountId = userInfo.get("id").asText();
            String account = userInfo.get("account").asText();
            String code = userInfo.has("code") ? userInfo.get("code").asText() : account; // 如果没有code字段，使用account作为默认值
            
            // 查找现有用户或创建新用户
            Optional<User> existingUser = userRepository.findByJaccountId(jaccountId);
            User user;
            
            if (existingUser.isPresent()) {
                // 更新现有用户的学号信息
                user = existingUser.get();
                user.setCode(code);
                logger.info("Updated existing user code: {}", account);
            } else {
                // 创建新用户，只保存学号
                user = new User(jaccountId, account, code);
                logger.info("Created new user with code: {}", account);
            }
            
            return userRepository.save(user);
            
        } catch (Exception e) {
            logger.error("Failed to create or update user", e);
            throw new RuntimeException("Failed to create or update user", e);
        }
    }
    
    /**
     * 根据账号查找用户
     */
    public Optional<User> findUserByAccount(String account) {
        return userRepository.findByAccount(account);
    }
    
    /**
     * 根据JAccount ID查找用户
     */
    public Optional<User> findUserByJaccountId(String jaccountId) {
        return userRepository.findByJaccountId(jaccountId);
    }
} 