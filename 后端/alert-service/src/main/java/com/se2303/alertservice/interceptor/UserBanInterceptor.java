package com.se2303.alertservice.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se2303.alertservice.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 用户封禁拦截器
 * 检查请求头中的X-Valid字段，如果为false则表示用户已被封禁
 */
@Component
public class UserBanInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取X-Valid请求头
        String validHeader = request.getHeader("X-Valid");
        
        // 如果X-Valid为false，表示用户已被封禁
        if (validHeader != null && "false".equalsIgnoreCase(validHeader)) {
            // 设置响应状态码
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            
            // 创建封禁响应
            ApiResponse banResponse = ApiResponse.error("已封禁");
            
            // 将响应写入输出流
            try (PrintWriter writer = response.getWriter()) {
                writer.write(objectMapper.writeValueAsString(banResponse));
                writer.flush();
            }
            
            // 返回false，阻止请求继续处理
            return false;
        }
        
        // 如果X-Valid为true或不存在，允许请求继续处理
        return true;
    }
} 