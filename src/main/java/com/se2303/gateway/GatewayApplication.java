package com.se2303.gateway;

// 导入Spring Boot核心启动类
import org.springframework.boot.SpringApplication; // Spring Boot应用启动器
import org.springframework.boot.autoconfigure.SpringBootApplication; // Spring Boot自动配置注解

/**
 * SE2303心理健康平台网关服务启动类
 * 
 * 系统架构说明：
 * - 基于Spring Cloud Gateway实现的微服务API网关
 * - 作为前端和后端微服务之间的统一入口
 * - 负责请求路由、负载均衡、跨域处理、身份认证等功能
 * 
 * 主要功能模块：
 * - 统一入口和反向代理：将前端请求路由到相应的后端服务
 * - CORS跨域处理：解决前端应用跨域访问API的问题
 * - JWT身份认证：验证用户token并提取用户信息
 * - 请求头处理：为所有请求添加统一的请求头信息
 * - 请求路径重写和转发：根据配置将请求转发到对应的微服务
 * 
 * 服务端口：8080
 * 
 * 支持的微服务：
 * - 用户服务 (localhost:8082)：处理用户注册、登录、个人信息管理
 * - 社区服务 (localhost:8083)：处理社区帖子、评论、互动功能
 * - 校园端服务 (localhost:8084): 处理校园端管理功能
 * - 心情服务 (localhost:8085)：处理心情记录、分析、统计功能
 * - 预警服务 (localhost:8086)：处理预警信息、通知校园端功能
 * - 通知服务 (localhost:8087)：资源推送功能
 * - 检查服务 (localhost:8088)：内容审查功能
 */
@SpringBootApplication // Spring Boot应用主类注解，启用自动配置、组件扫描等功能
public class GatewayApplication {

    /**
     * 应用程序主入口方法
     * 
     * 功能说明：
     * - 启动Spring Boot应用容器
     * - 初始化所有配置的Bean组件
     * - 启动内嵌的Web服务器（默认Netty）
     * - 开始监听指定端口的HTTP请求
     * 
     * 启动流程：
     * 1. 加载application.yml配置文件
     * 2. 初始化Spring容器和所有Bean
     * 3. 配置网关路由规则
     * 4. 启动全局过滤器（CORS、JWT认证、请求头处理）
     * 5. 启动Netty服务器监听8080端口
     * 6. 打印启动成功日志信息
     * 
     * @param args 命令行参数数组，可用于传递启动参数
     */
    public static void main(String[] args) {
        // SpringApplication.run()是Spring Boot的标准启动方法
        // 第一个参数：主配置类的Class对象，告诉Spring Boot从哪个类开始扫描配置
        // 第二个参数：命令行参数，可以用来覆盖配置文件中的属性
        SpringApplication.run(GatewayApplication.class, args);
        
        // 应用启动成功后，可以通过以下方式访问：
        // - 健康检查：http://localhost:8080/actuator/health
        // - 网关信息：http://localhost:8080/actuator/gateway
        // - 路由到用户服务：http://localhost:8080/api/user/*
        // - 路由到社区服务：http://localhost:8080/api/community/*
        // - 路由到校园端服务：http://localhost:8080/api/admin/*
        // - 路由到心情服务：http://localhost:8080/api/mood/*
        // - 路由到预警服务：http://localhost:8080/api/alert/*
        // - 路由到通知服务：http://localhost:8080/api/notification/*
        // - 路由到检查服务：http://localhost:8080/api/inspection/*
    }
} 