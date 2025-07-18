# Inform Service API 文档

## 概述
inform-service 是一个基�?Spring Boot 的通知服务，采�?消息队列 + 轮询混合"的方式实现通知功能�?

## 架构特点
- **消息队列**：使�?Redis 作为消息队列，新通知会推送到用户的专属队�?
- **轮询机制**：前端定时轮询获取最新通知
- **持久化存�?*：所有通知都会持久化到 MySQL 数据�?
- **过期清理**：定时清理过期通知，避免数据积�?

## 主要功能
1. 创建通知并推送到 Redis 队列
2. 轮询获取用户最新通知
3. 标记通知为已�?未读
4. 获取未读通知数量
5. 分页查询通知历史
6. 定时清理过期通知

## API 接口

### 1. 用户端接口（/api/informs�?

#### 1.1 获取通知列表
```
GET /api/informs/user/{userId}?page=0&size=20
```

#### 1.2 轮询获取最新通知
```
GET /api/informs/user/{userId}/poll?page=0&size=20
```

#### 1.3 获取未读通知数量
```
GET /api/informs/user/{userId}/unread-count
```

#### 1.4 标记通知为已�?
```
PUT /api/informs/{InformId}/read?userId={userId}
```

#### 1.5 批量标记所有通知为已�?
```
PUT /api/informs/user/{userId}/read-all
```

#### 1.6 删除通知
```
DELETE /api/informs/{InformId}?userId={userId}
```

### 2. 内部服务接口�?api/internal/informs�?

#### 2.1 发送评论通知
```
POST /api/internal/informs/comment
{
  "userId": 1,
  "title": "有人评论了你的帖�?,
  "content": "用户张三评论了你的帖�?心理健康的重要�?",
  "relatedId": "post_123"
}
```

#### 2.2 发送点赞通知
```
POST /api/internal/informs/like
{
  "userId": 1,
  "title": "有人点赞了你的帖�?,
  "content": "用户李四点赞了你的帖�?心理健康的重要�?",
  "relatedId": "post_123"
}
```

#### 2.3 发送关注通知
```
POST /api/internal/informs/follow
{
  "userId": 1,
  "title": "有新的关注�?,
  "content": "用户王五关注了你",
  "relatedId": "user_456"
}
```

#### 2.4 发送系统通知
```
POST /api/internal/informs/system
{
  "userId": 1,
  "title": "系统通知",
  "content": "欢迎加入心理健康社区�?,
  "relatedId": null
}
```

#### 2.5 通用通知发�?
```
POST /api/internal/informs/send
{
  "userId": 1,
  "type": "COMMENT",
  "title": "通知标题",
  "content": "通知内容",
  "relatedId": "相关ID"
}
```

## 通知类型
- `COMMENT`: 评论通知
- `LIKE`: 点赞通知
- `FOLLOW`: 关注通知
- `SYSTEM`: 系统通知

## 使用示例

### 其他服务调用通知服务
```java
// �?community-service 中发送评论通知
@Autowired
private RestTemplate restTemplate;

public void sendCommentInform(Long userId, String commenterName, String postTitle) {
    Map<String, Object> request = new HashMap<>();
    request.put("userId", userId);
    request.put("title", "有人评论了你的帖�?);
    request.put("content", commenterName + "评论了你的帖�?" + postTitle + """);
    request.put("relatedId", "post_" + postId);
    
    restTemplate.postForObject(
        "http://inform-service/api/internal/informs/comment",
        request,
        ApiResponse.class
    );
}
```

### 前端轮询获取通知
```javascript
// 定时轮询获取最新通知
setInterval(() => {
    fetch(`/api/informs/user/${userId}/poll?page=0&size=20`)
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                updateInformList(data.data.content);
            }
        });
}, 5000); // �?秒轮询一�?
```

## 配置说明

### application.properties
```properties
# 服务端口
server.port=8086

# 数据库配�?
spring.datasource.url=jdbc:mysql://localhost:3306/mental_health_db
spring.datasource.username=root
spring.datasource.password=password

# Redis 配置
spring.redis.host=localhost
spring.redis.port=6379

# 通知相关配置
Inform.queue.key=user_Inform_queue
Inform.expiry.days=30
```

## 部署说明

1. 确保 MySQL �?Redis 服务运行
2. 创建数据库和表结构（运行 schema.sql�?
3. 配置 application.properties
4. 启动服务：`mvn spring-boot:run`

## 扩展�?

该服务设计支持后续扩展：
- 可以轻松添加 WebSocket 支持实现实时推�?
- 支持添加更多通知类型
- 可以集成邮件、短信等其他通知渠道
- 支持通知模板化和国际�?
