# SE2303 Gateway

基于 Spring Cloud Gateway 实现的微服务网关，集成 JAccount SSO 登录和 JWT 认证功能，用于替代原有的 nginx 网关。

## 功能特性

- **统一入口**: 所有API请求通过网关统一处理
- **JAccount SSO登录**: 集成上海交通大学 JAccount 单点登录系统
- **JWT认证**: 基于JWT的用户认证和授权机制
- **用户管理**: 自动同步JAccount用户信息到本地数据库
- **路由转发**: 根据请求路径自动转发到对应的后端微服务
- **CORS支持**: 全局跨域配置，支持前端跨域访问
- **请求头处理**: 自动添加用户信息、网关标识等请求头
- **监控支持**: 集成 Spring Boot Actuator，提供健康检查和网关路由信息

## 认证流程

### JAccount SSO 登录流程
1. 前端调用 `/api/auth/jaccount/url` 获取JAccount授权URL
2. 用户跳转到JAccount登录页面完成认证
3. JAccount回调到 `/api/auth/jaccount/callback` 接口
4. 网关处理回调，获取用户信息并生成JWT token
5. 设置HTTP-only cookie，包含access_token和refresh_token
6. 前端后续请求自动携带token进行认证

### JWT Token 机制
- **Access Token**: 用于API访问认证，有效期24小时
- **Refresh Token**: 用于刷新access token，有效期7天
- **Token验证**: 每个需要认证的请求都会验证access token
- **自动刷新**: 可通过refresh token获取新的access token

## API接口

### 认证相关接口

#### 获取JAccount登录URL
```
GET /api/auth/jaccount/url
```
响应：
```json
{
  "authUrl": "https://jaccount.sjtu.edu.cn/oauth2/authorize?...",
  "state": "uuid-string"
}
```

#### JAccount登录回调
```
GET /api/auth/jaccount/callback?code=xxx&state=xxx
```

#### 获取当前用户信息
```
GET /api/auth/user
```
响应：
```json
{
  "success": true,
  "user": {
    "id": "123",
    "account": "username",
    "code": "900000000001"
  }
}
```

#### 用户登出
```
POST /api/auth/logout
```

#### 刷新Token
```
POST /api/auth/refresh
```

### 测试接口

#### Echo测试
```
GET /api/test/get
POST /api/test/post
```

## 数据库配置

### 创建数据库
```sql
CREATE DATABASE se2303_gateway CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 用户表结构
系统会自动创建用户表，包含以下字段：
- `id`: 主键
- `jaccount_id`: JAccount唯一ID
- `account`: JAccount账号名
- `code`: 学工号（唯一保留的个人信息）
- `created_at`: 创建时间
- `updated_at`: 更新时间

## 配置说明

### 环境变量配置
```bash
# JAccount配置
export JACCOUNT_CLIENT_ID=your-jaccount-client-id
export JACCOUNT_CLIENT_SECRET=your-jaccount-client-secret
export JACCOUNT_REDIRECT_URI=http://localhost:8081/api/auth/jaccount/callback

# JWT配置
export JWT_SECRET=your-jwt-secret-key-at-least-32-characters

# 数据库配置
export DB_URL=jdbc:mysql://localhost:3306/se2303_gateway
export DB_USERNAME=root
export DB_PASSWORD=your-password
```

### application.yml 主要配置
- 网关端口: `8081`
- 数据库连接配置
- JAccount OIDC客户端配置
- JWT密钥和过期时间配置
- CORS跨域配置

## 快速开始

### 1. 配置JAccount应用
在 [上海交通大学开发者平台](https://developer.sjtu.edu.cn) 注册应用并获取：
- Client ID
- Client Secret
- 设置回调URL: `http://localhost:8081/api/auth/jaccount/callback`

### 2. 配置数据库
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE se2303_gateway CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 设置环境变量
```bash
export JACCOUNT_CLIENT_ID=your-client-id
export JACCOUNT_CLIENT_SECRET=REDACTED
export JWT_SECRET=REDACTED
```

### 4. 构建和运行
```bash
mvn clean package
java -jar target/se2303-gateway-1.0.0.jar
```

### 5. 验证运行状态
```bash
# 健康检查
curl http://localhost:8081/actuator/health

# 获取JAccount登录URL
curl http://localhost:8081/api/auth/jaccount/url

# 测试接口
curl http://localhost:8081/api/test/get
```

## 安全特性

1. **JWT安全**: 使用HS512算法签名，密钥可配置
2. **HTTP-only Cookie**: Token存储在HTTP-only cookie中，防止XSS攻击
3. **Token过期**: Access token短期有效，refresh token长期有效
4. **路径保护**: 自动识别需要认证的路径，公开路径无需认证
5. **隐私保护**: 只存储和传递用户学号，不保存其他个人敏感信息
6. **用户信息注入**: 认证成功后自动将用户学号添加到请求头

## 开发指南

### 添加新的受保护路由
在 `application.yml` 中添加路由配置，系统会自动进行JWT认证。

### 自定义认证逻辑
可以修改 `JwtAuthenticationFilter` 来自定义认证逻辑。

### 扩展用户信息
可以修改 `User` 实体类和 `JAccountService` 来支持更多用户字段。

## 与原nginx网关的对比

| 功能 | nginx网关 | Spring Cloud Gateway |
|------|-----------|---------------------|
| 路由转发 | nginx location | Gateway Routes |
| CORS处理 | cors.conf | 全局CORS配置 |
| 请求头处理 | proxy_set_header | GlobalFilter |
| 认证授权 | lua脚本 | JWT + Spring Security |
| 用户管理 | 手动SQL | JPA自动管理 |
| 监控 | nginx日志 | Actuator端点 |
| 开发维护 | lua脚本 | Java代码 |

## 故障排除

### 常见问题

1. **JAccount回调失败**: 检查Client ID、Secret和回调URL配置
2. **JWT验证失败**: 检查JWT密钥配置和token有效期
3. **数据库连接失败**: 检查数据库URL、用户名和密码
4. **CORS错误**: 检查前端域名是否在CORS配置中

### 日志级别
开发环境建议设置DEBUG级别：
```yaml
logging:
  level:
    com.se2303.gateway: DEBUG
    org.springframework.security: DEBUG
```

## 监控端点

- 健康检查: `/actuator/health`
- 网关信息: `/actuator/info`
- 路由列表: `/actuator/gateway/routes`
