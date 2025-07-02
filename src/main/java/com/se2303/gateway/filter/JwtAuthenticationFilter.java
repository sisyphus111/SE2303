package com.se2303.gateway.filter;

// 导入自定义的JWT工具类和响应类
import com.se2303.gateway.util.JwtUtil;
import com.se2303.gateway.dto.response.ApiResponse;
// 导入JSON处理相关类
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
// 导入日志相关类
import org.slf4j.Logger; // 日志记录器接口
import org.slf4j.LoggerFactory; // 日志工厂类，用于创建Logger实例
// 导入Spring Cloud Gateway相关类
import org.springframework.cloud.gateway.filter.GatewayFilterChain; // 网关过滤器链接口
import org.springframework.cloud.gateway.filter.GlobalFilter; // 全局过滤器接口
import org.springframework.core.Ordered; // 定义执行顺序的接口
import org.springframework.core.io.buffer.DataBuffer; // 数据缓冲区接口
// 导入HTTP相关类
import org.springframework.http.HttpCookie; // HTTP Cookie类
import org.springframework.http.HttpStatus; // HTTP状态码枚举
import org.springframework.http.MediaType; // 媒体类型类
import org.springframework.http.server.reactive.ServerHttpRequest; // 响应式HTTP请求接口
import org.springframework.http.server.reactive.ServerHttpResponse; // 响应式HTTP响应接口
import org.springframework.stereotype.Component; // Spring组件注解
import org.springframework.web.server.ServerWebExchange; // 服务器Web交换接口
import reactor.core.publisher.Mono; // 响应式编程Mono类型
// 导入标准库
import java.nio.charset.StandardCharsets; // 字符编码

/**
 * JWT认证过滤器
 * 
 * 功能说明：
 * - 对所有通过网关的请求进行JWT令牌验证
 * - 简化版本：只负责token验证和用户信息转发，不处理token生成
 * - 区分公开路径和受保护路径，公开路径无需认证
 * - 验证成功后将用户信息添加到请求头中传递给后端服务
 * - 认证失败时返回统一格式的ApiResponse响应体
 * 
 * 工作流程：
 * 1. 检查请求路径是否需要认证
 * 2. 从请求中提取JWT令牌
 * 3. 验证令牌的有效性
 * 4. 提取用户信息并添加到请求头
 * 5. 继续执行后续过滤器，或返回错误响应
 */
@Component // 标记为Spring组件，自动注册到容器中
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    
    // 创建日志记录器，用于记录过滤器执行过程中的信息
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    // 注入JWT工具类，用于令牌验证和解析
    private final JwtUtil jwtUtil;
    
    // JSON对象映射器，用于将ApiResponse对象序列化为JSON字符串
    private final ObjectMapper objectMapper;
    
    /**
     * 构造函数：通过依赖注入获取JwtUtil实例
     * 
     * @param jwtUtil JWT工具类实例
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil; // 保存JWT工具类引用
        this.objectMapper = new ObjectMapper(); // 创建JSON对象映射器
    }
    
    /**
     * 过滤器核心方法：处理每个请求的JWT认证
     * 
     * @param exchange 服务器Web交换对象，包含请求和响应信息
     * @param chain 网关过滤器链，用于继续执行后续过滤器
     * @return Mono<Void> 异步处理结果
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求的URI路径，用于判断是否需要认证
        String path = exchange.getRequest().getURI().getPath();
        
        // 检查当前路径是否为公开路径（不需要认证的路径）
        if (isPublicPath(path)) {
            // 记录调试日志：跳过公开路径的认证
            logger.debug("Skipping authentication for public path: {}", path);
            // 直接继续执行后续过滤器，无需认证
            return chain.filter(exchange);
        }
        
        // 从请求中提取JWT令牌
        String token = getTokenFromRequest(exchange);
        
        // 检查是否成功获取到令牌
        if (token == null) {
            // 记录警告日志：受保护路径缺少令牌
            logger.warn("No token found for protected path: {}", path);
            // 返回自定义错误响应：缺少认证令牌
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, 
                "认证失败：缺少访问令牌", "MISSING_TOKEN");
        }
        
        // 使用JWT工具类验证令牌的有效性
        if (!jwtUtil.validateToken(token)) {
            // 记录警告日志：令牌验证失败
            logger.warn("Invalid token for path: {}", path);
            // 返回自定义错误响应：令牌无效
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, 
                "认证失败：访问令牌无效或已过期", "INVALID_TOKEN");
        }
        
        try {
            // 从有效的令牌中提取用户ID信息
            String userId = jwtUtil.getUserIdFromToken(token);
            
            // 检查用户ID是否存在且非空
            if (userId == null || userId.isEmpty()) {
                // 记录警告日志：令牌中没有用户ID信息
                logger.warn("No userId found in token for path: {}", path);
                // 返回自定义错误响应：用户ID缺失
                return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, 
                    "认证失败：令牌中缺少用户标识", "MISSING_USER_ID");
            }
            
            // 创建新的请求对象，添加用户认证信息到请求头中
            ServerHttpRequest request = exchange.getRequest().mutate()
                    // 添加用户ID到请求头，供后端服务使用
                    .header("X-User-Id", userId)
                    // 添加认证方式标识，表明使用JWT认证
                    .header("X-Gateway-Auth", "jwt")
                    .build(); // 构建新的请求对象
            
            // 记录调试日志：成功认证的用户信息
            logger.debug("Authenticated user ID: {} for path: {}", userId, path);
            
            // 使用包含用户信息的新请求继续执行过滤器链
            return chain.filter(exchange.mutate().request(request).build());
            
        } catch (Exception e) {
            // 捕获JWT处理过程中的任何异常
            // 记录错误日志：JWT处理失败的详细信息
            logger.error("Failed to process JWT token for path: " + path, e);
            // 返回自定义错误响应：令牌处理异常
            return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, 
                "认证失败：令牌处理异常", "TOKEN_PROCESSING_ERROR");
        }
    }
    
    /**
     * 写入自定义错误响应的方法
     * 
     * 功能说明：
     * - 创建ApiResponse对象（success=false, object=null）
     * - 将响应对象序列化为JSON
     * - 设置正确的Content-Type和状态码
     * - 写入响应体并完成响应
     * 
     * @param exchange 服务器Web交换对象
     * @param status HTTP状态码
     * @param message 错误消息
     * @param errorCode 错误代码（可选，用于前端处理）
     * @return Mono<Void> 异步响应结果
     */
    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, 
                                        String message, String errorCode) {
        // 获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        
        // 设置HTTP状态码
        response.setStatusCode(status);
        
        // 设置响应内容类型为JSON
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        // 注意：不在这里设置CORS头部，因为网关的application.yml已经统一配置了CORS
        // 避免重复设置导致 "Access-Control-Allow-Origin header contains multiple values" 错误
        
        try {
            // 创建ApiResponse对象 - 认证失败时object为null
            ApiResponse apiResponse = new ApiResponse(false, null, message);
            
            // 将ApiResponse对象序列化为JSON字符串
            String jsonResponse = objectMapper.writeValueAsString(apiResponse);
            
            // 将JSON字符串转换为字节数组，使用UTF-8编码
            byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
            
            // 创建数据缓冲区
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            
            // 记录调试日志：返回错误响应
            logger.debug("Returning error response: {} - {} ({})", status.value(), message, errorCode);
            
            // 写入响应体并完成响应
            return response.writeWith(Mono.just(buffer));
            
        } catch (JsonProcessingException e) {
            // JSON序列化失败时的备用处理
            logger.error("Failed to serialize error response to JSON", e);
            
            // 返回简单的文本错误响应
            String fallbackResponse = "{\"success\":false,\"object\":null,\"message\":\"认证失败\"}";
            byte[] bytes = fallbackResponse.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            
            return response.writeWith(Mono.just(buffer));
        }
    }
    
    /**
     * 检查指定路径是否为公开路径（不需要JWT认证）
     * 
     * 公开路径包括：
     * - 用户认证相关接口（注册、登录等）
     * - 测试接口
     * - 系统监控端点
     * - 静态资源和公共资源
     * - 网关自身接口
     * 
     * @param path 请求路径
     * @return boolean true表示公开路径，false表示需要认证
     */
    private boolean isPublicPath(String path) {
        return path.startsWith("/api/auth/") ||          // 用户认证服务相关路径（注册、登录等）
               path.startsWith("/api/test/") ||          // 测试相关路径
               path.startsWith("/actuator/") ||          // Spring Boot监控端点
               path.startsWith("/gateway/") ||           // 网关自身接口
               path.equals("/") ||                       // 根路径
               path.startsWith("/static/") ||            // 静态资源路径
               path.startsWith("/public/");              // 公共资源路径
    }
    
    /**
     * 从HTTP请求中获取JWT令牌
     * 
     * 支持两种方式：
     * 1. Authorization请求头：Bearer {token} 格式（推荐方式）
     * 2. Cookie方式：accessToken名称的Cookie
     * 
     * @param exchange 服务器Web交换对象
     * @return String JWT令牌字符串，如果未找到则返回null
     */
    private String getTokenFromRequest(ServerWebExchange exchange) {
        // 方式1：尝试从Authorization请求头获取令牌
        // 标准的HTTP Authorization头格式：Authorization: Bearer {token}
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // 提取Bearer后面的令牌部分（跳过"Bearer "这7个字符）
            return authHeader.substring(7);
        }
        
        // 方式2：尝试从Cookie中获取令牌
        // 查找名为"accessToken"的Cookie（与后端设置的cookie名称一致）
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("accessToken");
        if (cookie != null) {
            // 返回Cookie的值作为令牌
            return cookie.getValue();
        }
        
        // 两种方式都未找到令牌，返回null
        return null;
    }
    
    /**
     * 定义过滤器在过滤器链中的执行顺序
     * 
     * @return int 顺序值，数值越小优先级越高
     *         返回-100表示极高优先级，确保在其他业务过滤器之前执行认证
     *         这样可以保证后续的过滤器都能获取到认证后的用户信息
     */
    @Override
    public int getOrder() {
        return -100; // 极高优先级，在其他业务过滤器之前执行
    }
} 