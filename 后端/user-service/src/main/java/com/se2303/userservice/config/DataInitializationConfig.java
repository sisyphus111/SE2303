package com.se2303.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 数据初始化配置类
 * 配置数据库初始化相关的Bean和设置
 */
@Configuration
public class DataInitializationConfig {

    /**
     * 密码编码器Bean
     * 用于加密用户密码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 