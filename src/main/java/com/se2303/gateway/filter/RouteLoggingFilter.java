package com.se2303.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 路由日志过滤器
 * 
 * 功能说明：
 * - 记录请求路由转发的详细信息
 * - 显示路由匹配结果和目标服务
 * - 帮助调试路由配置和负载均衡
 */
@Component
public class RouteLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RouteLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String originalPath = request.getURI().getPath();
        
        return chain.filter(exchange)
            .doOnSuccess(aVoid -> {
                logRouteInfo(exchange, originalPath, true);
            })
            .doOnError(throwable -> {
                logRouteInfo(exchange, originalPath, false);
                logger.error("🔥 路由转发失败: {}", throwable.getMessage());
            });
    }

    /**
     * 记录路由信息
     */
    private void logRouteInfo(ServerWebExchange exchange, String originalPath, boolean success) {
        try {
            // 获取路由信息
            URI routeUri = exchange.getAttribute("org.springframework.cloud.gateway.support.ServerWebExchangeUtils.gatewayRequestUrl");
            String routeId = exchange.getAttribute("org.springframework.cloud.gateway.support.ServerWebExchangeUtils.gatewayPredicateRouteAttr");
            
            logger.info("🚀 ========== 路由转发信息 ==========");
            logger.info("📍 原始路径: {}", originalPath);
            
            if (routeUri != null) {
                logger.info("🎯 目标地址: {}", routeUri);
                logger.info("🏢 目标服务: {}", getServiceName(routeUri));
            }
            
            if (routeId != null) {
                logger.info("🏷️ 路由ID: {}", routeId);
            }
            
            // 记录路径重写信息
            ServerHttpRequest request = exchange.getRequest();
            String finalPath = request.getURI().getPath();
            if (!originalPath.equals(finalPath)) {
                logger.info("✏️ 路径重写: {} -> {}", originalPath, finalPath);
            }
            
            // 记录转发状态
            if (success) {
                logger.info("✅ 路由转发: 成功");
            } else {
                logger.warn("❌ 路由转发: 失败");
            }
            
            logger.info("🔚 ====================================");
            
        } catch (Exception e) {
            logger.debug("记录路由信息时出错: {}", e.getMessage());
        }
    }

    /**
     * 从URI中提取服务名称
     */
    private String getServiceName(URI uri) {
        if (uri == null) {
            return "unknown";
        }
        
        String host = uri.getHost();
        int port = uri.getPort();
        
        if (host != null) {
            if (host.equals("localhost") || host.equals("127.0.0.1")) {
                // 根据端口号推断服务名称
                return getServiceNameByPort(port);
            }
            return host + (port > 0 ? ":" + port : "");
        }
        
        return uri.toString();
    }

    /**
     * 根据端口号推断服务名称
     */
    private String getServiceNameByPort(int port) {
        switch (port) {
            case 8082:
                return "user-service";
            case 8083:
                return "community-service";
            case 8084:
                return "admin-service";
            case 8085:
                return "mood-service";
            case 8086:
                return "alert-service";
            case 8087:
                return "notification-service";
            case 8088:
                return "inspection-service";
            case 8090:
                return "chat-service";
            default:
                return "unknown-service:" + port;
        }
    }

    @Override
    public int getOrder() {
        // 设置在JWT认证过滤器之后执行，但在请求转发之前
        return -50;
    }
}
