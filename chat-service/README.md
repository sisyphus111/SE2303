# Chat Service

聊天服务，提供消息存储和查询功能。

## 功能特性

- 消息发送和存储
- 分页查询消息列表
- 按用户ID查询消息
- 消息软删除功能（将valid设置为false）
- 只显示有效消息
- 健康检查接口

## 技术栈

- Spring Boot 3.5.3
- Spring Data JPA
- MySQL 8.0
- Maven

## 数据库设计

### messages表
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID |
| content | TEXT | 消息内容 |
| created_at | DATETIME | 创建时间 |
| valid | BOOLEAN | 是否有效（用于软删除） |

## API接口

### 基础信息
- **服务端口**: 8090
- **基础路径**: `/api/chat`

### 接口列表

#### 1. 健康检查
- **路径**: `GET /api/chat/health`
- **说明**: 检查服务健康状态

#### 2. 发送消息
- **路径**: `POST /api/chat/messages`
- **请求体**:
```json
{
    "userId": 123,
    "content": "你好，这是一条测试消息"
}
```
- **响应**:
```json
{
    "success": true,
    "object": {
        "id": 1,
        "userId": 123,
        "content": "你好，这是一条测试消息",
        "createdAt": "2024-01-01T12:00:00"
    },
    "message": "消息发送成功"
}
```

#### 3. 分页查询消息
- **路径**: `GET /api/chat/messages?page=1&size=20`
- **参数**:
  - `page`: 页码（默认1）
  - `size`: 每页大小（默认20）
- **响应**:
```json
{
    "success": true,
    "object": {
        "list": [
            {
                "id": 1,
                "userId": 123,
                "content": "消息内容",
                "createdAt": "2024-01-01T12:00:00"
            }
        ],
        "total": 100,
        "totalPages": 5,
        "currentPage": 1,
        "pageSize": 20
    },
    "message": "获取消息列表成功"
}
```

#### 4. 按用户ID查询消息
- **路径**: `GET /api/chat/messages/user/{userId}?page=1&size=20`
- **参数**:
  - `userId`: 用户ID（路径参数）
  - `page`: 页码（默认1）
  - `size`: 每页大小（默认20）

#### 5. 获取所有消息
- **路径**: `GET /api/chat/messages/all`
- **说明**: 获取所有消息，不分页

#### 6. 软删除消息
- **路径**: `DELETE /api/chat/messages/{id}`
- **参数**:
  - `id`: 消息ID（路径参数）
- **说明**: 将消息的valid字段设置为false，消息不会真正删除

## 启动方式

### 1. 使用Maven
```bash
cd back/chat-service
mvn spring-boot:run
```

### 2. 打包后运行
```bash
cd back/chat-service
mvn clean package
java -jar target/chat-service-0.0.1-SNAPSHOT.jar
```

## 配置说明

### 数据库配置
- **数据库名**: chat_service
- **用户名**: root
- **密码**: REDACTED
- **端口**: 3306

### 服务配置
- **服务端口**: 8090
- **应用名称**: chat-service

## 网关集成

通过网关访问chat-service：
- **HTTP API**: `http://localhost:8080/api/chat/**`
- **直接访问**: `http://localhost:8090/api/chat/**`

## 开发说明

### 项目结构
```
src/main/java/com/se2303/chatservice/
├── ChatServiceApplication.java    # 启动类
├── controller/                    # 控制器层
│   ├── MessageController.java     # 消息控制器
│   └── GlobalExceptionHandler.java # 全局异常处理
├── service/                       # 服务层
│   └── MessageService.java        # 消息服务
├── repository/                    # 数据访问层
│   └── MessageRepository.java     # 消息仓库
├── dto/                          # 数据传输对象
│   ├── ApiResponse.java          # 统一响应格式
│   ├── MessageResponse.java      # 消息响应对象
│   └── CreateMessageRequest.java # 创建消息请求
└── model/                        # 实体类
    └── Message.java              # 消息实体
```

### 扩展功能建议

1. **消息类型支持**: 添加消息类型字段，支持文本、图片、文件等
2. **消息状态**: 添加已读/未读状态
3. **消息搜索**: 实现消息内容搜索功能
4. **消息通知**: 集成WebSocket实现实时消息推送
5. **消息审核**: 添加内容审核功能
6. **消息加密**: 实现端到端加密

## 注意事项

1. 确保MySQL服务已启动
2. 检查数据库连接配置
3. 确保8090端口未被占用
4. 建议在生产环境中修改数据库密码 