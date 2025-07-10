package com.se2303.alertservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * CORS配置类
 * 
 * 注意：此配置类已被禁用，因为网关已经统一处理了CORS配置
 * 避免重复配置导致CORS头部冲突问题
 * 
 * 在微服务架构中，通常由网关统一处理CORS，各个微服务不需要单独配置
 * 这样可以避免CORS头部重复设置的问题
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    /**
     * 配置CORS映射
     * 
     * 注意：此方法已被注释，因为网关已经统一处理了CORS
     * 如果同时使用网关和微服务的CORS配置，会导致Access-Control-Allow-Origin头部重复
     */
    /*
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // 允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的HTTP方法
                .allowedHeaders("*")  // 允许所有请求头
                .allowCredentials(true)  // 允许发送cookie
                .maxAge(3600);  // 预检请求的有效期，单位为秒
    }
    */
    
    /**
     * 配置CORS配置源
     * 
     * 注意：此方法已被注释，因为网关已经统一处理了CORS
     */
    /*
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    */
} 