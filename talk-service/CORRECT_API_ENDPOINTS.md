# 聊天服务API文档 - 简化版

## 概述
本文档描述了简化后的聊天服务的API接口和WebSocket通信协议。

## 实现流程

### 前端流程
1. **WebSocket建立连接**: 通过 `ws://localhost:8080/ws?x-userId={userId}` 建立连接
2. **发送心跳包**: 每9.9秒发送 `'heart'` 字符串保持连接
3. **获取历史消息**: 刚连接上或向上翻页时调用API获取历史消息记录（支持分页）

### 后端流程
1. **连接WebSocket**: 接收前端连接，提取userId令牌
2. **将消息存入数据库**: 收到消息后保存到数据库
3. **向所有在线用户发送通知**: 有新消息时推送给所有在线用户

## WebSocket连接

### 建立连接
```
ws://localhost:8080/ws?x-userId={userId}
```

**参数说明：**
- `x-userId`: 用户ID，从前端UserContext.js获取

**示例：**
```javascript
const userId = getUserFromContext(); // 从UserContext.js获取
const ws = new WebSocket(`ws://localhost:8080/ws?x-userId=${userId}`);
```

### 心跳包
客户端需要定时发送心跳包以保持连接：
```javascript
function send(msg) {
  if (ws && ws.readyState === WebSocket.OPEN) ws.send(msg)
}

const sendHeartPack = () => {
  heartTimer = setInterval(() => {
    send('heart')
  }, 9900)
}
```

### 发送消息
```javascript
// 发送文本消息
ws.send(JSON.stringify({
  type: 'text',
  content: '消息内容'
}));
```

## HTTP API接口

### 1. 获取历史消息记录

**接口地址：** `GET /api/talk/messages`

**请求参数：**
- `page` (可选): 页码，默认0
- `size` (可选): 每页大小，默认20

**响应格式：**
```json
{
    "success": true,
    "message": "获取历史消息记录成功",
    "data": [
        {
            "id": 1,
            "userId": 123,
            "content": "消息内容",
            "createdAt": "2024-01-01T12:00:00"
        }
    ]
}
```

**示例：**
```bash
curl -X GET "http://localhost:8080/api/talk/messages?page=0&size=10"
```

### 2. 发送消息

**接口地址：** `POST /api/talk/messages`

**请求参数：**
- `userId` (必需): 发送者用户ID

**请求体：**
```json
{
    "type": "text",
    "content": "消息内容"
}
```

**响应格式：**
```json
{
    "success": true,
    "message": "消息发送成功",
    "data": {
        "messageId": 1,
        "success": true
    }
}
```

**示例：**
```bash
curl -X POST "http://localhost:8080/api/talk/messages?userId=123" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "text",
    "content": "这是一条测试消息"
  }'
```

## WebSocket消息推送

### 消息推送格式
当有新消息时，服务器会通过WebSocket推送给所有在线用户：

```json
{
    "type": "text",
    "content": {
        "id": 1,
        "from_userId": 123,
        "content": "消息内容",
        "createdAt": "2024-01-01T12:00:00"
    }
}
```

### 消息类型
- `text`: 文本消息

## 数据库表结构

### Message表
```sql
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    valid BOOLEAN NOT NULL DEFAULT TRUE,
    content TEXT,
    created_at DATETIME NOT NULL
);
```

## 错误处理

### 常见错误码
- `400`: 请求参数错误
- `404`: 资源不存在
- `500`: 服务器内部错误

### 错误响应格式
```json
{
    "success": false,
    "message": "错误描述",
    "data": null
}
```

## 测试工具

### API测试脚本
```bash
# 运行测试脚本
chmod +x test_scripts/test_simple_api.sh
./test_scripts/test_simple_api.sh
```

### WebSocket测试页面
打开 `test_scripts/websocket_test_simple.html` 在浏览器中进行WebSocket测试。

## 注意事项

1. WebSocket连接需要传递`x-userId`参数
2. 客户端需要定时发送心跳包（每9.9秒）
3. 消息发送后会自动推送给所有在线用户
4. 用户名、昵称、头像由前端在显示时获取
5. 所有时间格式为ISO 8601标准
6. 简化版本只支持文本消息类型 