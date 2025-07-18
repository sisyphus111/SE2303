package com.se2303.gateway.controller;

// 导入Spring Web相关注解
import org.springframework.web.bind.annotation.GetMapping; // GET请求映射注解
import org.springframework.web.bind.annotation.RequestMapping; // 请求路径映射注解
import org.springframework.web.bind.annotation.RestController; // REST控制器注解
// 导入响应式编程相关类
import reactor.core.publisher.Mono; // 响应式编程Mono类型
// 导入Java标准库
import java.util.HashMap; // 哈希映射类
import java.util.Map; // 映射接口
// 导入日志相关类
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 网关控制器
 * 
 * 功能说明：
 * - 提供网关自身的管理和监控接口
 * - 处理网关级别的通用请求
 * - 提供网关状态信息和基本信息查询
 * - 为前端提供网关健康检查和配置信息接口
 * 
 * 设计理念：
 * - 网关本身也可以提供一些直接的服务接口
 * - 主要用于系统监控、健康检查、配置查询等功能
 * - 不处理具体的业务逻辑，业务逻辑由后端微服务处理
 * - 使用响应式编程模型，与Spring Cloud Gateway保持一致
 * 
 * 访问路径：
 * - 基础路径：/gateway
 * - 所有接口都使用响应式返回类型（Mono/Flux）
 */
@RestController // 标记为REST控制器，自动将返回值序列化为JSON
@RequestMapping("/gateway") // 设置控制器的基础请求路径
public class GatewayController {

    // 创建日志记录器
    private static final Logger logger = LoggerFactory.getLogger(GatewayController.class);

    /**
     * 获取网关基本信息接口
     * 
     * 功能说明：
     * - 返回网关的基本信息和运行状态
     * - 可用于前端显示网关信息或健康检查
     * - 提供网关版本、启动时间、支持的服务等信息
     * 
     * 访问路径：GET /gateway/info
     * 
     * @return Mono<Map<String, Object>> 响应式返回网关信息的映射
     */
    @GetMapping("/info") // 映射GET请求到/gateway/info路径
    public Mono<Map<String, Object>> getGatewayInfo() {
        logger.info("📋 收到网关信息查询请求: /gateway/info");
        
        // 创建信息映射对象，用于存储网关信息
        Map<String, Object> info = new HashMap<>();
        
        // 添加网关基本信息
        info.put("name", "SE2303 Mental Health Gateway"); // 网关名称
        info.put("version", "1.0.0"); // 网关版本
        info.put("description", "SE2303心理健康平台API网关服务"); // 网关描述
        info.put("port", 8080); // 网关端口
        
        // 添加支持的微服务信息
        Map<String, Object> services = new HashMap<>();
        services.put("user-service", "http://localhost:8082"); // 用户服务地址
        services.put("community-service", "http://localhost:8083"); // 社区服务地址
        services.put("admin-service", "http://localhost:8084"); // 校园端服务地址
        services.put("mood-service", "http://localhost:8085"); // 心情服务地址
        services.put("alert-service", "http://localhost:8086"); // 预警服务地址
        services.put("notification-service", "http://localhost:8087"); // 通知服务地址
        services.put("inspection-service", "http://localhost:8088"); // 检查服务地址
        services.put("chat-service", "http://localhost:8090"); // 检查服务地址
        info.put("services", services); // 将服务信息添加到总信息中
        
        // 添加网关功能特性
        Map<String, Object> features = new HashMap<>();
        features.put("cors", true); // 支持跨域
        features.put("jwt-auth", true); // 支持JWT认证
        features.put("request-header-processing", true); // 支持请求头处理
        features.put("path-rewriting", true); // 支持路径重写
        info.put("features", features); // 将功能特性添加到总信息中
        
        // 添加运行时信息
        info.put("timestamp", System.currentTimeMillis()); // 当前时间戳
        info.put("status", "running"); // 运行状态
        
        logger.info("✅ 网关信息查询成功，返回数据: services={}, features={}", 
                   services.size(), features.size());
        
        // 使用Mono.just()包装响应数据，符合响应式编程模型
        // 这样可以与其他异步操作组合，保持系统的响应式特性
        return Mono.just(info);
    }

    /**
     * 健康检查接口
     * 
     * 功能说明：
     * - 提供网关健康状态检查
     * - 可用于负载均衡器或监控系统检查网关状态
     * - 返回简单的健康状态信息
     * 
     * 访问路径：GET /gateway/health
     * 
     * @return Mono<Map<String, Object>> 响应式返回健康状态信息
     */
    @GetMapping("/health") // 映射GET请求到/gateway/health路径
    public Mono<Map<String, Object>> getHealth() {
        logger.info("🔍 收到健康检查请求: /gateway/health");
        
        // 创建健康状态信息映射
        Map<String, Object> health = new HashMap<>();
        
        // 添加基本健康信息
        health.put("status", "UP"); // 健康状态：UP表示正常运行
        health.put("timestamp", System.currentTimeMillis()); // 检查时间戳
        
        // 添加详细健康信息
        Map<String, Object> details = new HashMap<>();
        details.put("gateway", "UP"); // 网关核心服务状态
        details.put("memory", getMemoryInfo()); // 内存使用情况
        details.put("uptime", getUptime()); // 运行时间
        health.put("details", details); // 将详细信息添加到健康状态中
        
        logger.info("✅ 健康检查完成: status=UP, memory={}MB", 
                   Runtime.getRuntime().totalMemory() / (1024 * 1024));
        
        // 返回响应式结果
        return Mono.just(health);
    }

    /**
     * 获取内存使用信息的私有方法
     * 
     * 功能说明：
     * - 获取JVM内存使用情况
     * - 包括总内存、已使用内存、可用内存等信息
     * 
     * @return Map<String, Object> 包含内存信息的映射
     */
    private Map<String, Object> getMemoryInfo() {
        // 获取Java运行时环境实例
        Runtime runtime = Runtime.getRuntime();
        
        // 创建内存信息映射
        Map<String, Object> memory = new HashMap<>();
        
        // 获取内存信息（单位：字节）
        long totalMemory = runtime.totalMemory(); // JVM总内存
        long freeMemory = runtime.freeMemory(); // JVM空闲内存
        long usedMemory = totalMemory - freeMemory; // 已使用内存
        
        // 将内存信息转换为MB并添加到映射中
        memory.put("total", totalMemory / (1024 * 1024) + " MB"); // 总内存
        memory.put("used", usedMemory / (1024 * 1024) + " MB"); // 已使用内存
        memory.put("free", freeMemory / (1024 * 1024) + " MB"); // 空闲内存
        
        return memory; // 返回内存信息映射
    }

    /**
     * 获取系统运行时间的私有方法
     * 
     * 功能说明：
     * - 计算JVM的运行时间
     * - 返回格式化的运行时间字符串
     * 
     * @return String 格式化的运行时间
     */
    private String getUptime() {
        // 获取JVM启动时间到现在的毫秒数
        long uptimeMillis = System.currentTimeMillis() - 
            java.lang.management.ManagementFactory.getRuntimeMXBean().getStartTime();
        
        // 将毫秒转换为秒
        long uptimeSeconds = uptimeMillis / 1000;
        
        // 计算天、小时、分钟、秒
        long days = uptimeSeconds / (24 * 3600); // 天数
        long hours = (uptimeSeconds % (24 * 3600)) / 3600; // 小时数
        long minutes = (uptimeSeconds % 3600) / 60; // 分钟数
        long seconds = uptimeSeconds % 60; // 秒数
        
        // 格式化返回运行时间字符串
        return String.format("%d天 %d小时 %d分钟 %d秒", days, hours, minutes, seconds);
    }
} 