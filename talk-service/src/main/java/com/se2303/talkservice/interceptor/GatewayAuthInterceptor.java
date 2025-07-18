package com.se2303.talkservice.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se2303.talkservice.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 用户状态检查拦截器
 * 检查请求头中的X-Valid字段，如果为false则表示用户状态无效
 */
@Component
public class GatewayAuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 健康检查端点不需要认证
        if (request.getRequestURI().contains("/actuator/health")) {
            return true;
        }

        // 获取X-Valid请求头
        String validHeader = request.getHeader("X-Valid");
        
        // 如果X-Valid为false，表示用户状态无效
        if (validHeader != null && "false".equalsIgnoreCase(validHeader)) {
            // 设置响应状态码
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            
            // 创建错误响应
            ApiResponse errorResponse = ApiResponse.error("用户状态无效");
            
            // 将响应写入输出流
            try (PrintWriter writer = response.getWriter()) {
                writer.write(objectMapper.writeValueAsString(errorResponse));
                writer.flush();
            }
            
            // 返回false，阻止请求继续处理
            return false;
        }
        
        // 如果X-Valid为true或不存在，允许请求继续处理
        return true;
    }
} 