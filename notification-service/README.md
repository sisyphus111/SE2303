# Notification Service

通知服务，提供文章、音乐和轮播图等内容的API接口。

## 服务信息

- **端口**: 8087
- **基础URL**: `http://localhost:8087/notification-service`
- **API前缀**: `/api/notification`

## 快速开始

### 1. 环境要求

- Java 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置

创建数据库并执行初始化脚本：

```sql
-- 创建数据库
CREATE DATABASE notification_service_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE notification_service_db;

-- 执行初始化脚本
SOURCE database_schema.sql;
```

### 3. 配置文件

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/notification_service_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 4. 启动服务

#### 方式一：使用启动脚本

**Windows:**
```bash
start.bat
```

**Linux/Mac:**
```bash
chmod +x start.sh
./start.sh
```

#### 方式二：使用Maven命令

```bash
# 使用Maven启动
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/notification-service-1.0.0.jar
```

## API接口

### 健康检查

```
GET /api/notification/health
```

**响应示例**:
```json
{
  "success": true,
  "message": "服务健康",
  "data": {
    "status": "UP",
    "service": "Notification Service",
    "timestamp": 1703123456789
  }
}
```

### 文章相关接口

#### 1. 创建文章

```
POST /api/notification/articles/create
```

**请求体**:
```json
{
  "title": "如何保持良好的心理健康",
  "introduction": "分享一些日常保持心理健康的小技巧",
  "description": "心理健康是我们整体健康的重要组成部分...",
  "cover": "https://example.com/image1.jpg",
  "link": "https://example.com/article1",
  "category": "心理健康"
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "文章创建成功",
  "data": {
    "id": 1,
    "title": "如何保持良好的心理健康",
    "introduction": "分享一些日常保持心理健康的小技巧",
    "description": "心理健康是我们整体健康的重要组成部分...",
    "cover": "https://example.com/image1.jpg",
    "link": "https://example.com/article1",
    "category": "心理健康",
    "createdAt": "2024-01-15T10:00:00"
  }
}
```

#### 2. 获取推荐文章列表

```
GET /api/notification/articles/recommended?page=1&size=10&category=mental-health
```

**参数**:
- `page` (可选): 页码，默认1
- `size` (可选): 每页数量，默认10
- `category` (可选): 文章分类

#### 3. 搜索文章

```
GET /api/notification/articles/search?keyword=焦虑
```

**参数**:
- `keyword` (必需): 搜索关键词

#### 4. 根据分类获取文章

```
GET /api/notification/articles/category/心理健康
```

**参数**:
- `category` (路径参数): 文章分类

#### 5. 获取所有文章

```
GET /api/notification/articles/all
```

### 音乐相关接口

#### 1. 创建音乐

```
POST /api/notification/music/create
```

**请求体**:
```json
{
  "title": "森林白噪音",
  "category": "自然音效",
  "cover": "https://example.com/music1.jpg",
  "url": "https://example.com/music1.mp3",
  "duration": 300
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "音乐创建成功",
  "data": {
    "id": 1,
    "title": "森林白噪音",
    "category": "自然音效",
    "cover": "https://example.com/music1.jpg",
    "url": "https://example.com/music1.mp3",
    "duration": 300,
    "createdAt": "2024-01-15T10:00:00"
  }
}
```

#### 2. 获取音乐列表

```
GET /api/notification/music/list?page=1&size=20
```

**参数**:
- `page` (可选): 页码，默认1
- `size` (可选): 每页数量，默认20

#### 3. 搜索音乐

```
GET /api/notification/music/search?keyword=宁静
```

**参数**:
- `keyword` (必需): 搜索关键词

#### 4. 根据分类获取音乐

```
GET /api/notification/music/category/放松音乐
```

**参数**:
- `category` (路径参数): 音乐分类

#### 5. 获取所有音乐

```
GET /api/notification/music/all
```

### 轮播图相关接口

#### 1. 创建轮播图

```
POST /api/notification/carousel/create
```

**请求体**:
```json
{
  "imageUrl": "https://example.com/carousel1.jpg",
  "title": "心理健康宣传",
  "description": "保持积极心态，关注心理健康",
  "category": "心理健康",
  "sortOrder": 1
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "轮播图创建成功",
  "data": {
    "id": 1,
    "imageUrl": "https://example.com/carousel1.jpg",
    "title": "心理健康宣传",
    "description": "保持积极心态，关注心理健康",
    "category": "心理健康",
    "sortOrder": 1,
    "createdAt": "2024-01-15T10:00:00"
  }
}
```

#### 2. 获取轮播图列表

```
GET /api/notification/carousel/list
```

#### 3. 获取所有轮播图

```
GET /api/notification/carousel/all
```

## 请求头要求

所有API请求都需要包含以下请求头：

```
X-User-Id: 用户ID
X-Valid: 用户是否有效 (true/false)
X-Identity: 用户身份标识
```

**示例**:
```bash
curl -H "X-User-Id: user123" \
     -H "X-Valid: true" \
     -H "X-Identity: counselor" \
     -H "Content-Type: application/json" \
     -X POST \
     -d '{"title":"测试文章","introduction":"测试简介"}' \
     http://localhost:8087/notification-service/api/notification/articles/create
```

## 错误处理

服务使用统一的错误响应格式：

```json
{
  "success": false,
  "message": "错误描述",
  "data": null
}
```

常见HTTP状态码：
- `200`: 请求成功
- `201`: 创建成功
- `400`: 请求参数错误
- `403`: 用户被禁用
- `404`: 资源不存在
- `500`: 服务器内部错误

## 数据库表结构

### articles 表
- `id`: 主键
- `title`: 文章标题
- `introduction`: 文章简介
- `description`: 文章描述
- `cover`: 封面图片URL
- `link`: 文章链接
- `category`: 文章分类
- `created_at`: 创建时间
- `updated_at`: 更新时间

### musics 表
- `id`: 主键
- `title`: 音乐标题
- `category`: 音乐分类
- `cover`: 封面图片URL
- `url`: 音乐文件URL
- `duration`: 时长（秒）
- `created_at`: 创建时间
- `updated_at`: 更新时间

### carousel_images 表
- `id`: 主键
- `image_url`: 图片URL
- `title`: 轮播图标题
- `description`: 描述
- `category`: 分类
- `sort_order`: 排序
- `created_at`: 创建时间
- `updated_at`: 更新时间

## 开发说明

### 项目结构
```
src/main/java/com/se2303/notificationservice/
├── controller/          # 控制器层
├── service/            # 服务层
├── repository/         # 数据访问层
├── model/             # 实体类
├── dto/               # 数据传输对象
├── interceptor/       # 拦截器
└── config/            # 配置类
```

### 技术栈
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL 8.0
- Maven

### 开发规范
1. 遵循RESTful API设计规范
2. 使用统一的响应格式 `ApiResponse`
3. 所有接口都需要用户身份验证
4. 异常统一处理
5. 代码注释完整
6. 使用Bean Validation进行参数验证

## 部署

### Docker部署

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/notification-service-1.0.0.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 环境变量配置

```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=notification_service_db
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
```
