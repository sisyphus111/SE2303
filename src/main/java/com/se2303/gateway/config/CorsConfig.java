package com.se2303.gateway.config;

import org.springframework.context.annotation.Configuration;

/**
 * CORS（跨域资源共享）配置类
 * 
 * 注意：此配置类已被禁用，因为在application.yml中已经配置了Spring Cloud Gateway的globalcors
 * 避免重复配置导致CORS头部冲突问题
 * 
 * 功能说明：
 * - 解决前端应用跨域访问网关API的问题
 * - 提供全局的CORS配置，适用于所有通过网关的请求
 * - 支持预检请求（OPTIONS）的处理
 * - 配置允许的源、方法、头部等跨域参数
 * 
 * CORS基础知识：
 * - 当前端JavaScript运行在一个域（如localhost:3000）上
 * - 而尝试访问另一个域（如localhost:8080）的API时
 * - 浏览器会执行同源策略检查，阻止跨域请求
 * - CORS机制允许服务器明确声明哪些跨域请求是被允许的
 */
@Configuration // 标记为Spring配置类，包含Bean定义
public class CorsConfig {

    /**
     * 配置CORS过滤器Bean
     * 
     * 注意：此方法已被注释，因为在application.yml中已经通过Spring Cloud Gateway的globalcors进行了CORS配置
     * 如果同时使用两种配置方式，会导致Access-Control-Allow-Origin头部出现重复值的问题
     * 
     * 该方法创建并配置一个CORS过滤器，用于处理所有的跨域请求
     * 
     * @return CorsWebFilter 配置好的CORS过滤器实例
     */
    /*
    @Bean // 将返回值注册为Spring Bean，由Spring容器管理
    public CorsWebFilter corsWebFilter() {
        // 创建CORS配置对象，用于定义跨域访问规则
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // 配置允许的请求源（Origin）
        // setAllowedOriginPatterns使用通配符模式，比setAllowedOrigins更灵活
        // "*" 表示允许任何域的请求访问，包括localhost、IP地址等
        // 生产环境中应该具体指定允许的域名，如：https://yourdomain.com
        corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // 配置允许的HTTP请求方法
        // Arrays.asList()创建一个不可变的列表
        // GET: 用于获取数据
        // POST: 用于创建新资源
        // PUT: 用于更新现有资源  
        // DELETE: 用于删除资源
        // OPTIONS: 用于预检请求，浏览器在发送复杂请求前会先发送OPTIONS请求
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 配置允许的请求头
        // "*" 表示允许任何请求头，包括自定义头部
        // 常见的请求头包括：Content-Type, Authorization, X-Requested-With等
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        
        // 配置是否允许发送凭证信息
        // true表示允许前端发送Cookie、Authorization头等凭证信息
        // 这对于需要身份认证的API很重要
        // 注意：当setAllowCredentials(true)时，setAllowedOriginPatterns不能使用"*"
        // 但这里使用了通配符模式，在某些情况下可能需要调整
        corsConfig.setAllowCredentials(true);
        
        // 配置预检请求（OPTIONS）的缓存时间（单位：秒）
        // 1728000秒 = 20天
        // 浏览器会缓存预检请求的结果，在缓存期内不会重复发送OPTIONS请求
        // 这可以减少网络请求，提高性能
        corsConfig.setMaxAge(1728000L); // 20天的缓存期
        
        // 配置暴露给前端的响应头
        // 默认情况下，浏览器只能访问基本的响应头
        // "*" 表示暴露所有响应头给前端JavaScript代码
        // 这样前端就可以读取自定义的响应头信息
        corsConfig.setExposedHeaders(Arrays.asList("*"));

        // 创建基于URL的CORS配置源
        // 这个类用于将CORS配置应用到特定的URL路径上
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // 将CORS配置注册到所有路径（/**）
        // "/**" 是Ant风格的路径模式，表示匹配所有路径和子路径
        // 这意味着通过网关的所有请求都会应用这个CORS配置
        source.registerCorsConfiguration("/**", corsConfig);
        
        // 创建并返回CORS过滤器实例
        // 该过滤器会自动处理所有的跨域请求和预检请求
        return new CorsWebFilter(source);
    }
    */
}
