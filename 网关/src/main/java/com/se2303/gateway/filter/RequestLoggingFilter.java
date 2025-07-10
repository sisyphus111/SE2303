package com.se2303.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 请求日志过滤器
 * 
 * 功能说明：
 * - 记录所有通过网关的请求和响应信息
 * - 包括请求时间、响应时间、处理耗时等
 * - 记录请求头、响应状态码等详细信息
 * - 帮助监控和调试网关流量
 */
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();
        String requestTime = LocalDateTime.now().format(formatter);
        
        // 记录请求开始信息
        logRequestStart(request, requestTime);
        
        return chain.filter(exchange)
            .doOnSuccess(aVoid -> {
                // 请求成功完成
                logRequestEnd(exchange, startTime, true, null);
            })
            .doOnError(throwable -> {
                // 请求处理出现错误
                logRequestEnd(exchange, startTime, false, throwable);
            });
    }

    /**
     * 记录请求开始信息
     */
    private void logRequestStart(ServerHttpRequest request, String requestTime) {
        logger.info("🌐 ========== 请求开始 ==========");
        logger.info("⏰ 请求时间: {}", requestTime);
        logger.info("🔗 请求方法: {}", request.getMethod());
        logger.info("📍 请求路径: {}", request.getURI().getPath());
        logger.info("🔍 查询参数: {}", request.getURI().getQuery());
        logger.info("🏠 请求地址: {}", request.getURI());
        
        // 记录重要的请求头
        logger.info("📋 请求头信息:");
        request.getHeaders().forEach((name, values) -> {
            if (isImportantHeader(name)) {
                // 对敏感信息进行脱敏处理
                String value = maskSensitiveValue(name, String.join(", ", values));
                logger.info("  {}: {}", name, value);
            }
        });
        
        // 记录客户端IP
        String clientIp = getClientIp(request);
        logger.info("🌍 客户端IP: {}", clientIp);
    }

    /**
     * 记录请求结束信息
     */
    private void logRequestEnd(ServerWebExchange exchange, long startTime, boolean success, Throwable error) {
        ServerHttpResponse response = exchange.getResponse();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        String endTimeStr = LocalDateTime.now().format(formatter);
        
        if (success) {
            logger.info("✅ ========== 请求完成 ==========");
            logger.info("⏰ 完成时间: {}", endTimeStr);
            logger.info("⚡ 处理耗时: {}ms", duration);
            logger.info("📊 响应状态: {}", response.getStatusCode());
            
            // 记录响应头信息
            logger.info("📋 响应头信息:");
            response.getHeaders().forEach((name, values) -> {
                if (isImportantResponseHeader(name)) {
                    logger.info("  {}: {}", name, String.join(", ", values));
                }
            });
        } else {
            logger.error("❌ ========== 请求失败 ==========");
            logger.error("⏰ 失败时间: {}", endTimeStr);
            logger.error("⚡ 处理耗时: {}ms", duration);
            logger.error("📊 响应状态: {}", response.getStatusCode());
            if (error != null) {
                logger.error("💥 错误信息: {}", error.getMessage());
                logger.error("🔍 错误类型: {}", error.getClass().getSimpleName());
            }
        }
        
        // 性能监控：记录慢请求
        if (duration > 1000) { // 超过1秒的请求
            logger.warn("🐌 慢请求警告: 路径={}, 耗时={}ms", 
                       exchange.getRequest().getURI().getPath(), duration);
        }
        
        logger.info("🏁 ================================");
    }

    /**
     * 判断是否为重要的请求头
     */
    private boolean isImportantHeader(String headerName) {
        String lowerName = headerName.toLowerCase();
        return lowerName.equals("authorization") ||
               lowerName.equals("content-type") ||
               lowerName.equals("user-agent") ||
               lowerName.equals("accept") ||
               lowerName.equals("origin") ||
               lowerName.equals("referer") ||
               lowerName.startsWith("x-");
    }

    /**
     * 判断是否为重要的响应头
     */
    private boolean isImportantResponseHeader(String headerName) {
        String lowerName = headerName.toLowerCase();
        return lowerName.equals("content-type") ||
               lowerName.equals("content-length") ||
               lowerName.equals("cache-control") ||
               lowerName.startsWith("access-control-") ||
               lowerName.startsWith("x-");
    }

    /**
     * 对敏感信息进行脱敏处理
     */
    private String maskSensitiveValue(String headerName, String value) {
        if ("authorization".equalsIgnoreCase(headerName) && value != null) {
            if (value.startsWith("Bearer ")) {
                return "Bearer " + value.substring(7, Math.min(20, value.length())) + "...";
            }
            return "***";
        }
        return value;
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIp(ServerHttpRequest request) {
        // 尝试从各种代理头中获取真实IP
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        
        ip = request.getHeaders().getFirst("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        
        ip = request.getHeaders().getFirst("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        
        ip = request.getHeaders().getFirst("WL-Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        
        // 如果都没有，则使用远程地址
        try {
            var remoteAddress = request.getRemoteAddress();
            if (remoteAddress != null) {
                var address = remoteAddress.getAddress();
                if (address != null) {
                    return address.getHostAddress();
                }
            }
        } catch (Exception e) {
            logger.debug("获取远程地址失败: {}", e.getMessage());
        }
        
        return "unknown";
    }

    @Override
    public int getOrder() {
        // 设置比JWT认证过滤器更高的优先级，确保在认证之前记录请求
        return -200;
    }
}
