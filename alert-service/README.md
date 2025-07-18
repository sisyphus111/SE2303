# Alert Service - 警报服务

## 📖 项目简介

Alert Service 是一个基于Spring Boot的警报管理服务，用于处理用户危险操作和AI超时等异常情况。该服务提供了完整的警报创建、分配、处理和回访功能，支持心理咨询师进行用户关怀和干预。

## 🏗️ 项目架构

本项目采用标准的分层架构设计：

```
┌─────────────────────────────────────────┐
│            Controller Layer             │  ← HTTP请求处理
├─────────────────────────────────────────┤
│            Service Layer                │  ← 业务逻辑处理
├─────────────────────────────────────────┤
│          Repository Layer               │  ← Spring Data JPA
├─────────────────────────────────────────┤
│            Database Layer               │  ← MySQL数据库
└─────────────────────────────────────────┘
```

## 🚀 功能特性

- ✅ 警报创建（危险操作、AI超时等）
- ✅ 警报分配（给心理咨询师）
- ✅ 警报状态管理（待处理、已分配、处理中等）
- ✅ 回访备注功能
- ✅ 分页查询和条件筛选
- ✅ 统计信息展示
- ✅ 统一响应格式
- ✅ 全局异常处理

## 🚨 警报类型

系统支持多种警报类型：

- **危险操作** (DANGEROUS_OPERATION): 用户进行了可能有害的操作
- **AI超时** (AI_TIMEOUT): 用户使用AI服务超时
- **情绪异常** (EMOTIONAL_CRISIS): 用户情绪状态异常
- **自残倾向** (SELF_HARM): 检测到自残倾向
- **暴力倾向** (VIOLENT_TENDENCY): 检测到暴力倾向
- **其他** (OTHER): 其他需要关注的警报

## 📊 警报级别

- **低** (LOW): 需要关注但紧急程度较低
- **中** (MEDIUM): 需要及时处理
- **高** (HIGH): 需要立即处理
- **紧急** (URGENT): 需要立即干预

## 📋 警报状态

- **待处理** (PENDING): 警报已创建，等待心理咨询师处理
- **已分配** (ASSIGNED): 警报已分配给心理咨询师
- **处理中** (IN_PROGRESS): 心理咨询师正在处理
- **已完成** (COMPLETED): 警报已处理完成
- **已关闭** (CLOSED): 警报已关闭
- **已取消** (CANCELLED): 警报已取消

## 🛠️ 技术栈

- **框架**：Spring Boot 3.5.3
- **数据库**：MySQL 8.0
- **ORM**：Spring Data JPA + Hibernate
- **验证**：Bean Validation
- **构建工具**：Maven

## 📋 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.6+

## 🚀 快速开始

### 1. 克隆项目
```bash
git clone <repository-url>
cd alert-service
```

### 2. 配置数据库
在 `src/main/resources/application.yml` 中修改数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/alert_service?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: your_password
```

### 3. 创建数据库表
执行 `database_schema.sql` 脚本创建数据库表结构。

### 4. 启动应用
```bash
# 使用Maven Wrapper
./mvnw spring-boot:run

# 或使用Maven
mvn spring-boot:run
```

### 5. 验证启动
访问健康检查接口：
```
GET http://localhost:8086/alert-service/api/alert/health
```

## 📡 API 接口文档

### 统一响应格式

所有API接口都使用统一的响应格式 `ApiResponse`：

```json
{
  "success": true,
  "object": {
    // 返回的数据内容
  },
  "message": "操作成功"
}
```

### 基础信息
- **服务地址**：`http://localhost:8086/alert-service`
- **API前缀**：`/api/alert`
- **完整URL示例**：`http://localhost:8086/alert-service/api/alert/create`

### 核心功能接口

#### 1. 创建警报（供其他服务调用）
```http
POST /api/alert/create
Content-Type: application/json

{
  "userId": "user123",
  "alertType": "DANGEROUS_OPERATION",
  "alertLevel": "HIGH",
  "description": "用户进行了危险操作",
  "operationDetails": "用户尝试访问危险网站",
  "aiSessionId": "session_123",
  "aiTimeoutDuration": 30
}
```

#### 2. 获取待回访用户列表（供心理咨询师查看）
```http
GET /api/alert/pending
```

#### 3. 分配警报给心理咨询师
```http
POST /api/alert/{id}/assign?counselorId=1
```

#### 4. 更新警报状态
```http
PUT /api/alert/{id}/status?status=IN_PROGRESS
```

#### 5. 添加回访备注
```http
POST /api/alert/{id}/follow-up?notes=已与用户沟通，情况稳定
```

#### 6. 根据心理咨询师ID获取分配的警报
```http
GET /api/alert/counselor/{counselorId}
```

#### 7. 分页查询警报
```http
GET /api/alert/search?userId=1&status=PENDING&page=0&size=10
```

#### 8. 获取警报统计信息
```http
GET /api/alert/statistics
```

#### 9. 获取警报类型
```http
GET /api/alert/types
```

#### 10. 健康检查
```http
GET /api/alert/health
```

## 🧪 测试示例

### 使用curl测试

#### 1. 健康检查
```bash
curl http://localhost:8086/alert-service/api/alert/health
```

#### 2. 创建警报测试
```bash
curl -X POST http://localhost:8086/alert-service/api/alert/create \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user001",
    "alertType": "DANGEROUS_OPERATION",
    "alertLevel": "HIGH",
    "description": "用户进行了危险操作",
    "operationDetails": "用户尝试访问危险网站"
  }'
```

#### 3. 获取待处理警报
```bash
curl http://localhost:8086/alert-service/api/alert/pending
```

#### 4. 分配警报
```bash
curl -X POST "http://localhost:8086/alert-service/api/alert/1/assign?counselorId=counselor001"
```

### 使用Hoppscotch测试

1. 导入Postman集合
2. 设置环境变量：
   - `baseUrl`: `http://localhost:8086/alert-service`
   - `alertId`: `1`
3. 运行测试用例

## 📊 数据库设计

### 警报表 (alert)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 警报ID（主键） |
| user_id | VARCHAR(50) | 用户ID |

| alert_type | ENUM | 警报类型 |
| alert_level | ENUM | 警报级别 |
| status | ENUM | 警报状态 |
| description | TEXT | 警报描述 |
| operation_details | TEXT | 操作详情 |
| ai_session_id | VARCHAR(100) | AI会话ID |
| ai_timeout_duration | INT | AI超时时长（分钟） |
| assigned_counselor_id | VARCHAR(50) | 分配的心理咨询师ID |

| follow_up_notes | TEXT | 回访备注 |
| follow_up_date | DATETIME | 回访日期 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

## 🔧 配置说明

### 应用配置
```yaml
server:
  port: 8086
  servlet:
    context-path: /alert-service

spring:
  application:
    name: alert-service
  
  datasource:
    url: jdbc:mysql://localhost:3306/alert_service?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
    database-platform: org.hibernate.dialect.MySQL8Dialect
```

## 📝 开发指南

### 添加新的警报类型
1. 在 `AlertType` 枚举中添加新类型
2. 更新数据库表结构（如果需要）
3. 在相关业务逻辑中处理新类型

### 扩展警报功能
1. 在 `Alert` 实体类中添加新字段
2. 更新DTO类
3. 修改Service层业务逻辑
4. 添加相应的Controller接口

### 部署说明
1. 确保MySQL服务运行
2. 执行数据库脚本创建表结构
3. 配置应用参数
4. 启动应用服务

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 📄 许可证

本项目采用 MIT 许可证。 