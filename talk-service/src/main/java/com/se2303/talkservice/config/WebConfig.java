package com.se2303.talkservice.config;

import com.se2303.talkservice.interceptor.GatewayAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 配置拦截器和跨域等Web相关设置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private GatewayAuthInterceptor gatewayAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册用户状态检查拦截器，应用到所有API路径
        registry.addInterceptor(gatewayAuthInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/actuator/**"); // 排除监控端点
    }
} 