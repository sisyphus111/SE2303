package com.se2303.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * CORS跨域配置类
 * 提供全局的CORS配置，允许前端跨域访问
 */
@Configuration
public class CorsConfig {

    /**
     * 配置CORS过滤器
     * 
     * @return CorsWebFilter CORS过滤器
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // 允许的源
        corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // 允许的HTTP方法
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 允许的请求头
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        
        // 允许发送凭证（cookies等）
        corsConfig.setAllowCredentials(true);
        
        // 预检请求的缓存时间（秒）
        corsConfig.setMaxAge(1728000L); // 20天
        
        // 暴露的响应头
        corsConfig.setExposedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
}
