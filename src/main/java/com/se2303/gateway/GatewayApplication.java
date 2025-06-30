package com.se2303.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SE2303 Gateway 启动类
 * 基于Spring Cloud Gateway实现的微服务网关
 * 
 * 主要功能：
 * - 统一入口和反向代理
 * - CORS跨域处理
 * - 请求路径重写和转发
 * - 请求头处理
 */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
} 