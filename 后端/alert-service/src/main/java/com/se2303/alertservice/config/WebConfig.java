package com.se2303.alertservice.config;

import com.se2303.alertservice.interceptor.UserBanInterceptor;
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
    private UserBanInterceptor userBanInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册用户封禁拦截器，应用到所有API路径
        registry.addInterceptor(userBanInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/alert/health"); // 排除健康检查接口
    }
} 