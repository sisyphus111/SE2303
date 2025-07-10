# Mood Service - 心情打卡服务

## 📖 项目简介

Mood Service 是一个基于Spring Boot的心情打卡服务，支持用户进行心情评分、情绪标签选择、日记记录和天气记录。该服务提供了灵活的打卡和日记功能，用户可以独立进行打卡或写日记，也可以同时进行。

## 🏗️ 项目架构

本项目采用标准的分层架构设计：

```
┌─────────────────────────────────────────┐
│            Controller Layer             │  ← HTTP请求处理
├─────────────────────────────────────────┤
│            Service Layer                │  ← 业务逻辑处理
├─────────────────────────────────────────┤
│              DAO Layer                  │  ← 数据访问抽象
├─────────────────────────────────────────┤
│          Repository Layer               │  ← Spring Data JPA
├─────────────────────────────────────────┤
│            Database Layer               │  ← MySQL数据库
└─────────────────────────────────────────┘
```

### 架构层次说明

- **Controller层**: 处理HTTP请求，参数验证，返回响应
- **Service层**: 业务逻辑处理，事务管理
- **DAO层**: 数据访问对象，抽象数据访问逻辑
- **Repository层**: Spring Data JPA Repository
- **Model层**: 实体类和数据传输对象

## 🚀 功能特性

- ✅ 心情打卡功能（1-10分评分）
- ✅ 情绪标签选择（积极、中性、消极）
- ✅ 日记记录功能（支持标题和内容）
- ✅ 天气记录功能（6种天气类型）
- ✅ 独立打卡和写日记
- ✅ 标签权值计算（积极+1，中性0，消极-1）
- ✅ 今日心情分析
- ✅ 用户统计功能
- ✅ 数据库健康检查
- ✅ 示例数据初始化
- ✅ 用户封禁拦截器（统一管理用户封禁状态）

## 🌤️ 天气类型

系统支持6种天气类型记录：

- **晴天** ☀️ (SUNNY)
- **多云** ☁️ (CLOUDY) 
- **雨天** 🌧️ (RAINY)
- **雪天** ❄️ (SNOWY)
- **风天** 💨 (WINDY)
- **雾天** 🌫️ (FOGGY)

## 📊 心情标签权值系统

系统采用标签权值计算机制来分析用户心情：

- **积极标签权值**: +1
  - 开心、兴奋、满足、平静、感恩、自信、充满活力、乐观

- **中性标签权值**: 0
  - 平静、冷静、专注、放松

- **消极标签权值**: -1
  - 悲伤、愤怒、焦虑、压力、疲惫、孤独、沮丧、担心、失望、烦躁、困惑

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
cd mood-service
```

### 2. 配置数据库
在 `src/main/resources/application.yml` 中修改数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mood_item?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
    username: root
    password: your_password
```

### 3. 启动应用
```bash
# 使用Maven Wrapper
./mvnw spring-boot:run

# 或使用Maven
mvn spring-boot:run
```

### 4. 验证启动
访问健康检查接口：
```
GET http://localhost:8085/mood-service/api/mood/health
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

**字段说明**：
- `success`: 布尔值，表示请求是否成功处理
- `object`: 返回的内容（如果有的话），没有则设为null
- `message`: 成功时，成功消息；失败时，失败信息

**成功响应示例**：
```json
{
  "success": true,
  "object": {
    "id": 1,
    "userId": 1,
    "date": "2024-01-15",
    "score": 8
  },
  "message": "心情打卡成功"
}
```

**失败响应示例**：
```json
{
  "success": false,
  "object": null,
  "message": "用户不存在"
}
```

### 请求头格式

所有需要用户身份验证的接口都需要在请求头中包含以下字段：

```
X-User-Id: String          // 用户ID
X-Valid: true/false        // 用户是否启用（false表示用户被封禁）
X-Identity: true/false     // 身份标识（true=用户，false=校园端）
X-Gateway-Auth: jwt        // 网关认证标识
```

### 用户封禁机制

系统实现了统一的用户封禁拦截器，对所有API请求进行封禁状态检查：

#### 拦截器功能
- **检查字段**: `X-Valid`请求头
- **封禁条件**: `X-Valid: false`
- **拦截范围**: 所有`/api/**`路径
- **排除路径**: `/api/health`（健康检查接口）
- **响应格式**: HTTP 403 + JSON格式封禁消息

#### 封禁响应示例
```json
{
  "success": false,
  "object": null,
  "message": "已封禁"
}
```

#### 使用说明
1. **前端集成**: 在发送请求前检查用户`valid`状态
2. **请求头设置**: 将用户状态通过`X-Valid`传递给后端
3. **响应处理**: 当收到403状态码时，显示封禁提示

详细测试方法请参考 [USER_BAN_INTERCEPTOR_TEST.md](./USER_BAN_INTERCEPTOR_TEST.md)

### 基础信息
- **服务地址**：`http://localhost:8085/mood-service`
- **API前缀**：`/api/mood`
- **完整URL示例**：`http://localhost:8085/mood-service/api/mood/check-in`

### 核心功能接口

#### 1. 完整打卡（包含日记）
```http
POST /api/mood/check-in
Content-Type: application/json
X-User-Id: 1
X-Valid: true
X-Identity: true
X-Gateway-Auth: jwt

{
  "date": "2024-01-15",
  "score": 8,
  "moodLabels": ["开心", "满足", "充满活力"],
  "diaryTitle": "美好的一天",
  "diaryDescription": "今天心情很好，工作顺利",
  "weather": "SUNNY"
}
```

#### 2. 只打卡不写日记
```http
POST /api/mood/check-in-only
Content-Type: application/json
X-User-Id: 1
X-Valid: true
X-Identity: true
X-Gateway-Auth: jwt

{
  "date": "2024-01-15",
  "score": 8,
  "moodLabels": ["开心", "满足"]
}
```

#### 3. 只写日记不打卡
```http
POST /api/mood/diary-only
Content-Type: application/json
X-User-Id: 1
X-Valid: true
X-Identity: true
X-Gateway-Auth: jwt

{
  "date": "2024-01-15",
  "diaryTitle": "雨天思考",
  "diaryDescription": "今天下雨了，待在家里看书，思考了很多人生的问题。雨声很舒缓，让我感到内心的平静。",
  "weather": "RAINY"
}
```

#### 4. 获取今日心情（包含标签权值计算）
```http
GET /api/mood/today/{userId}
```

**响应示例**：
```json
{
  "success": true,
  "object": {
    "id": 1,
    "userId": 1,
    "date": "2024-01-15",
    "score": 4,
    "scoreCommitted": true,
    "diaryTitle": "美好的一天",
    "diaryDescription": "今天心情很好，工作顺利",
    "diaryCommitted": true,
    "weather": "SUNNY",
    "moodLabels": ["开心", "满足"],
    "positiveLabelCount": 2,
    "neutralLabelCount": 0,
    "negativeLabelCount": 0,
    "totalMoodWeight": 2,
    "moodTrend": "positive",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  },
  "message": "获取今日心情成功"
}
```

#### 5. 获取指定日期的今日心情
```http
GET /api/mood/today/{userId}/{date}
```

#### 6. 获取用户在指定日期范围内的打卡统计
```http
GET /api/mood/user/{userId}/stats/range?startDate=2024-01-01&endDate=2024-01-31
```

#### 7. 获取用户心情记录
```http
GET /api/mood/user/{userId}
```

#### 8. 获取指定日期的心情记录
```http
GET /api/mood/user/{userId}/date/{date}
```

#### 9. 检查打卡状态
```http
GET /api/mood/user/{userId}/check-status/{date}
```

#### 10. 检查日记状态
```http
GET /api/mood/user/{userId}/diary-status/{date}
```

#### 11. 获取用户统计
```http
GET /api/mood/user/{userId}/stats
```

#### 12. 获取心情标签
```http
GET /api/mood/labels
```

#### 13. 更新心情记录
```http
PUT /api/mood/{id}
Content-Type: application/json

{
  "userId": 1,
  "date": "2024-01-15",
  "score": 4,
  "moodLabels": ["开心", "满足"],
  "diaryTitle": "美好的一天",
  "diaryDescription": "今天心情很好，工作顺利",
  "weather": "SUNNY"
}
```

#### 14. 删除心情记录
```http
DELETE /api/mood/{id}
```

#### 15. 删除某一天的日记内容
```http
DELETE /api/mood/user/{userId}/diary/{date}
```

**响应示例**：
```json
{
  "id": 1,
  "userId": 1,
  "date": "2024-01-15",
  "score": 4,
  "scoreCommitted": true,
  "diaryTitle": null,
  "diaryDescription": null,
  "diaryCommitted": false,
  "weather": "SUNNY",
  "moodLabels": ["开心", "满足"],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T11:00:00"
}
```

### 数据库管理接口

#### 1. 数据库健康检查
```http
GET /api/database/health
```

#### 2. 检查数据库表状态
```http
GET /api/database/tables
```

#### 3. 获取数据库统计
```http
GET /api/database/stats
```

#### 4. 初始化示例数据
```http
POST /api/mood/init-data
```

#### 5. 清空所有数据
```http
DELETE /api/mood/clear-data
```

## 🏷️ 情绪标签

### 积极情绪
- 开心、兴奋、满足、平静、感恩、自信、充满活力、乐观

### 中性情绪
- 平静、冷静、专注、放松

### 消极情绪
- 悲伤、愤怒、焦虑、压力、疲惫、孤独、沮丧、担心、失望、烦躁、困惑

## 📊 数据模型

### Mood（心情记录）
- `id`: 主键
- `userId`: 用户ID
- `date`: 日期
- `score`: 心情评分（1-10）
- `scoreCommitted`: 是否打卡
- `diaryTitle`: 日记标题
- `diaryDescription`: 日记内容
- `diaryCommitted`: 是否写日记
- `weather`: 天气类型（SUNNY, CLOUDY, RAINY, SNOWY, WINDY, FOGGY）
- `createdAt`: 创建时间
- `updatedAt`: 更新时间

### MoodItem（心情标签）
- `id`: 主键
- `label`: 标签名称
- `moodId`: 关联的心情记录ID
- `createdAt`: 创建时间

## 🔧 配置说明

### 应用配置
```yaml
server:
  port: 8085
  servlet:
    context-path: /mood-service

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mood_item?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect
```

### 环境变量配置
```bash
# 数据库配置
DB_URL=jdbc:mysql://localhost:3306/mood_item
DB_USERNAME=root
DB_PASSWORD=your_password

# 应用配置
SERVER_PORT=8085
CONTEXT_PATH=/mood-service
```

## 🧪 测试示例

### 使用curl测试

#### 1. 健康检查
```bash
curl http://localhost:8085/mood-service/api/mood/health
```

#### 2. 打卡测试
```bash
curl -X POST http://localhost:8085/mood-service/api/mood/check-in \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "date": "2024-01-15",
    "score": 8,
    "moodLabels": ["开心", "满足"],
    "diaryTitle": "美好的一天",
    "diaryDescription": "今天心情很好",
    "weather": "SUNNY"
  }'
```

#### 3. 只写日记测试
```bash
curl -X POST http://localhost:8085/mood-service/api/mood/diary-only \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "date": "2024-01-15",
    "diaryTitle": "雨天思考",
    "diaryDescription": "今天下雨了，待在家里看书，思考了很多人生的问题。雨声很舒缓，让我感到内心的平静。",
    "weather": "RAINY"
  }'
```

### 使用Postman测试

1. 导入Postman集合
2. 设置环境变量：
   - `baseUrl`: `http://localhost:8085/mood-service`
   - `userId`: `1`
3. 运行测试用例

## 🐛 常见问题

### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 验证数据库连接配置
- 确认用户名密码正确

### 2. 端口被占用
- 修改 `application.yml` 中的端口配置
- 或停止占用端口的其他服务

### 3. 依赖下载失败
```bash
# 清理并重新下载依赖
./mvnw clean install
```

**注意**：请确保在生产环境中修改默认的数据库密码和其他敏感配置。
