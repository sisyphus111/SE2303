package com.se2303.moodservice.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 数据库配置类
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.se2303.moodservice.repository")
@EntityScan(basePackages = "com.se2303.moodservice.model")
public class DatabaseConfig {
    
    // 数据库配置已经通过application.yml文件配置
    // 这里主要是启用JPA和事务管理
} 