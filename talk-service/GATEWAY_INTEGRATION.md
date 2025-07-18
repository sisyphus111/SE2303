# Talk Service 网关集成配置

## 概述

Talk Service 已经配置为与现有网关系统集成，采用简化的认证机制，与其他服务保持一致。

## 认证机制

### 认证请求头

Talk Service 支持以下认证请求头：

- `X-User-Id`: 用户ID（必需）
- `X-Valid`: 用户状态有效性（可选，false时拒绝访问）

### 认证流程

1. **请求拦截**: 所有 `/api/**` 路径的请求都会被 `GatewayAuthInterceptor` 拦截
2. **状态检查**: 检查用户状态是否有效（`X-Valid` 为 `false` 时拒绝访问）
3. **用户ID获取**: 从 `X-User-Id` 头获取用户ID
4. **请求处理**: 认证通过后，请求继续处理

## API 端点

### 1. 获取历史消息
```
GET /api/talk/messages?page=0&size=20
```

**请求头要求:**
```
X-User-Id: 123
X-Valid: true  (可选，false时拒绝访问)
```

**响应示例:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "userId": 123,
      "content": "Hello World",
      "createdAt": "2024-01-01T12:00:00"
    }
  ],
  "message": "获取历史消息记录成功"
}
```

### 2. 发送消息
```
POST /api/talk/messages
```

**请求头要求:**
```
X-User-Id: 123
X-Valid: true  (可选，false时拒绝访问)
```

**请求体:**
```json
{
  "content": "Hello World"
}
```

**响应示例:**
```json
{
  "success": true,
  "data": {
    "messageId": 1,
    "success": true
  },
  "message": "消息发送成功"
}
```

### 3. 获取用户消息历史
```
GET /api/talk/user/messages?page=0&size=20
```

**请求头要求:**
```
X-User-Id: 123
X-Valid: true  (可选，false时拒绝访问)
```

**响应示例:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "userId": 123,
      "content": "Hello World",
      "createdAt": "2024-01-01T12:00:00"
    }
  ],
  "message": "获取用户消息记录成功"
}
```

## 配置选项

### application.yml 配置

```yaml
# CORS配置 - 支持前端访问
spring:
  mvc:
    cors:
      allowed-origins: 
        - http://localhost:3000
        - http://localhost:8080
      allowed-methods: GET,POST,PUT,DELETE,OPTIONS
      allowed-headers: "*"
      allow-credentials: true
      max-age: 3600
```

## 错误处理

### 认证错误响应

当认证失败时，服务会返回相应的错误响应：

```json
{
  "success": false,
  "data": null,
  "message": "错误描述"
}
```

**常见错误:**
- `400`: 缺少用户ID或用户ID格式错误
- `403`: 用户状态无效（X-Valid: false）
- `500`: 服务器内部错误

## 测试

### 使用 curl 测试

```bash
# 获取历史消息
curl -X GET "http://localhost:8087/api/talk/messages?page=0&size=5" \
  -H "X-User-Id: 123" \
  -H "X-Valid: true"

# 发送消息
curl -X POST "http://localhost:8087/api/talk/messages" \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 123" \
  -H "X-Valid: true" \
  -d '{"content": "测试消息"}'

# 获取用户消息
curl -X GET "http://localhost:8087/api/talk/user/messages?page=0&size=5" \
  -H "X-User-Id: 123" \
  -H "X-Valid: true"

# 测试用户状态无效
curl -X GET "http://localhost:8087/api/talk/messages?page=0&size=5" \
  -H "X-User-Id: 123" \
  -H "X-Valid: false"

# 健康检查（不需要认证）
curl -X GET "http://localhost:8087/actuator/health"
```

## 注意事项

1. **简化认证**: 采用与其他服务一致的简化认证机制
2. **用户状态**: 只有 `X-Valid` 不为 `false` 的用户才能访问服务
3. **CORS**: 配置允许前端访问
4. **日志**: 认证过程会记录调试日志
5. **健康检查**: `/actuator/health` 端点不需要认证

## 集成检查清单

- [ ] 网关配置了正确的请求头转发
- [ ] 用户服务正确设置用户状态
- [ ] CORS 配置允许前端访问
- [ ] 数据库连接正常
- [ ] Redis 连接正常（如果使用）
- [ ] 日志级别配置正确
- [ ] 监控端点可访问

## 与其他服务的对比

Talk Service 的认证机制与 mood-service 保持一致：

- 都使用 `X-Valid` 头检查用户状态
- 都使用 `X-User-Id` 头获取用户ID
- 都有简化的拦截器实现
- 都排除 `/actuator/**` 路径的认证要求 