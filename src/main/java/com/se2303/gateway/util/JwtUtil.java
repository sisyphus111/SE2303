package com.se2303.gateway.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT（JSON Web Token）工具类 
 * 
 * 功能说明：
 * - 专门用于JWT令牌的验证和解析操作
 * - 不负责JWT的生成（令牌生成由后端认证服务负责）
 * - 提供令牌有效性验证、用户信息提取等功能
 * - 支持从令牌中提取用户名、用户ID、过期时间等信息
 * 
 * 设计理念：
 * - 网关层只需要验证令牌和提取基本信息
 * - 令牌的生成和更复杂的逻辑由专门的认证服务处理
 * - 保持网关层的轻量化和高性能
 * 
 * 配置管理：
 * - 使用@Value注解从配置文件中注入JWT密钥
 * - 支持环境变量覆盖配置文件中的值
 * - 提供默认值以确保应用能够正常启动
 */
@Component
public class JwtUtil {
    
    /**
     * JWT签名密钥配置
     * 
     * 配置说明：
     * - @Value注解从application.yml中读取jwt.secret配置项
     * - ${jwt.secret} 语法说明：
     *   * jwt.secret: 配置文件中的属性路径
     *   * :REDACTED: 默认值，当配置文件中没有该属性时使用
     * 
     * 安全最佳实践：
     * - 开发环境：可以使用简单的密钥便于调试
     * - 测试环境：建议使用更复杂的密钥
     * - 生产环境：必须使用高强度密钥（至少32位随机字符）
     * 
     * 配置优先级（从高到低）：
     * 1. 系统环境变量：JWT_SECRET
     * 2. JVM系统属性：-Djwt.secret=xxx
     * 3. application.yml配置文件中的值
     * 4. @Value注解中的默认值
     * 
     * 使用示例：
     * - 环境变量：export JWT_SECRET=your-production-secret
     * - 启动参数：java -jar gateway.jar --jwt.secret=your-secret
     * - 配置文件：在application.yml中设置jwt.secret值
     */
    @Value("${jwt.secret}")
    private String secret;
    
    /**
     * 获取用于JWT签名和验证的密钥对象
     * 
     * 工作原理：
     * - 将字符串密钥转换为字节数组
     * - 使用HMAC-SHA算法生成对称加密密钥
     * - 该密钥用于JWT的签名验证
     * 
     * 安全注意事项：
     * - 密钥长度至少32位以确保安全性
     * - 所有微服务必须使用相同的密钥才能互相验证JWT
     * - 密钥泄露将导致整个系统的安全风险
     * 
     * @return SecretKey JWT签名验证用的密钥对象
     */
    private SecretKey getSigningKey() {
        // Keys.hmacShaKeyFor()方法根据提供的字节数组生成HMAC密钥
        // secret.getBytes()将字符串转换为字节数组
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    /**
     * 从JWT令牌中提取过期时间信息
     * 
     * JWT标准中，过期时间存储在Expiration（exp）声明中
     * 
     * @param token JWT令牌字符串
     * @return Date 令牌过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        // 使用Claims::getExpiration方法引用提取过期时间声明
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * 从JWT令牌中提取用户ID信息
     * 
     * 用户ID通常作为自定义声明存储在JWT中
     * 
     * @param token JWT令牌字符串
     * @return String 用户ID的字符串表示，如果没有该声明则返回null
     */
    public String getUserIdFromToken(String token) {
        // 使用Lambda表达式提取名为"X-User-Id"的自定义声明
        // 先获取Long类型的用户ID，然后转换为String用于HTTP头传递
        Long userId = getClaimFromToken(token, claims -> claims.get("X-User-Id", Long.class));
        return userId != null ? userId.toString() : null;
    }

    /**
     * 从JWT令牌中提取用户是否有效信息
     * 
     * @param token JWT令牌字符串
     * @return boolean 用户是否有效，如果没有该声明则返回false
     */
    public boolean getValidFromToken(String token) {
        // 使用Lambda表达式提取名为"X-Valid"的自定义声明
        // claims.get("X-Valid", boolean.class) 获取指定名称和类型的声明值
        return getClaimFromToken(token, claims -> claims.get("X-Valid", boolean.class));
    }

    /**
     * 从JWT令牌中提取用户身份信息
     * 
     * @param token JWT令牌字符串
     * @return boolean 用户是否是普通用户，如果没有该声明则返回false
     */
    public boolean getIdentityFromToken(String token) {
        // 使用Lambda表达式提取名为"X-Identity"的自定义声明
        // claims.get("X-Identity", boolean.class) 获取指定名称和类型的声明值
        return getClaimFromToken(token, claims -> claims.get("X-Identity", boolean.class));
    }
    
    /**
     * 通用的JWT声明提取方法
     * 
     * 使用函数式编程的方式，允许调用者指定如何从Claims中提取特定信息
     * 
     * @param token JWT令牌字符串
     * @param claimsResolver 声明解析函数，定义如何从Claims对象中提取所需信息
     * @param <T> 返回值的泛型类型
     * @return T 根据解析函数提取的值
     */
    public <T> T getClaimFromToken(String token, java.util.function.Function<Claims, T> claimsResolver) {
        // 首先解析令牌获取所有声明
        final Claims claims = getAllClaimsFromToken(token);
        // 然后使用提供的解析函数提取特定信息
        return claimsResolver.apply(claims);
    }
    
    /**
     * 从JWT令牌中解析出所有声明信息
     * 
     * 这是JWT解析的核心方法，会验证令牌签名并提取载荷信息
     * 
     * @param token JWT令牌字符串
     * @return Claims 包含所有声明信息的对象
     * @throws JwtException 如果令牌格式错误、签名无效或已过期
     */
    private Claims getAllClaimsFromToken(String token) {
        // 使用JJWT库的解析器构建器模式
        return Jwts.parserBuilder()
                // 设置用于验证签名的密钥（从配置文件中读取）
                .setSigningKey(getSigningKey())
                // 构建解析器实例
                .build()
                // 解析JWT令牌，验证签名和格式
                .parseClaimsJws(token)
                // 获取载荷部分的声明信息
                .getBody();
    }
    
    /**
     * 检查JWT令牌是否已过期
     * 
     * 通过比较令牌的过期时间与当前时间来判断
     * 
     * @param token JWT令牌字符串
     * @return Boolean true表示已过期，false表示未过期
     */
    public Boolean isTokenExpired(String token) {
        // 从令牌中提取过期时间
        final Date expiration = getExpirationDateFromToken(token);
        // 将过期时间与当前时间比较
        // before()方法：如果过期时间早于当前时间，返回true（已过期）
        return expiration.before(new Date());
    }
    
    /**
     * 验证JWT令牌的整体有效性
     * 
     * 验证项目包括：
     * - 令牌格式是否正确
     * - 签名是否有效（使用配置的密钥验证）
     * - 是否在有效期内
     * - 载荷是否可以正确解析
     * 
     * @param token JWT令牌字符串
     * @return Boolean true表示令牌有效，false表示无效
     */
    public Boolean validateToken(String token) {
        try {
            // 尝试解析令牌，如果成功则说明令牌有效
            Jwts.parserBuilder()
                // 设置签名验证密钥（从配置文件中读取）
                .setSigningKey(getSigningKey())
                // 构建解析器
                .build()
                // 执行解析和验证操作
                .parseClaimsJws(token);
            // 如果没有抛出异常，说明令牌有效
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 捕获所有JWT相关异常和非法参数异常
            // JwtException包括：
            // - MalformedJwtException: 令牌格式错误
            // - ExpiredJwtException: 令牌已过期
            // - SignatureException: 签名验证失败（可能是密钥不匹配）
            // - UnsupportedJwtException: 不支持的令牌类型
            // IllegalArgumentException: 令牌为空或格式不正确
            return false; // 任何异常都表示令牌无效
        }
    }
} 