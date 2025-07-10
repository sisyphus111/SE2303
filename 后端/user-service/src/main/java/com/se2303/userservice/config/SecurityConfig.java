package com.se2303.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()                    // 禁用CSRF保护
            .cors().disable()                    // 禁用CORS（由网关处理）
            .authorizeHttpRequests(authz -> 
                authz.anyRequest().permitAll()   // 允许所有请求无需认证
            )
            .headers().frameOptions().disable(); // 允许iframe嵌入（如果需要）
        
        return http.build();
    }
} 