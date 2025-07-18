# Talk Service 实时消息推送功能

## 概述

Talk Service 现在支持实时消息推送功能，当用户发送新消息时，消息会立即推送给所有在线用户的对话框，无需刷新页面或重新加载对话记录。

## 功能特性

### 1. 实时消息推送
- **WebSocket连接**: 用户通过WebSocket连接接收实时消息
- **自动推送**: 发送消息后自动推送给所有在线用户
- **多路径支持**: 支持 `/ws`、`/ws/conn`、`/ws/talk/conn` 三种WebSocket连接路径

### 2. 双重发送方式
- **WebSocket发送**: 用户可以直接通过WebSocket发送消息
- **HTTP API发送**: 用户也可以通过HTTP API发送消息，同样会触发实时推送

### 3. 消息格式
推送的消息格式如下：
```json
{
    "content": {
        "id": 123,
        "from_userId": 1,
        "content": "消息内容",
        "createdAt": "2024-01-01T12:00:00"
    }
}
```

## 实现原理

### 1. WebSocket连接管理
```java
// 用户连接时
userSessionManager.addUser(onlineUser);

// 用户断开时
userSessionManager.userOffline(sessionId);
```

### 2. 消息推送流程
```java
// 1. 保存消息到数据库
Message savedMessage = messageService.saveMessage(fromUserId, content);

// 2. 构建推送消息
WsContent wsContent = new WsContent();
contentData.setId(savedMessage.getId());
contentData.setFrom_userId(fromUserId);
contentData.setContent(content);
contentData.setCreatedAt(savedMessage.getCreatedAt());

// 3. 推送给所有在线用户
broadcastToAllUsers(wsContent);
```

### 3. 心跳机制
- 客户端每9秒发送一次心跳包：`"heart"`
- 服务器检测心跳超时自动清理连接
- 支持连接超时配置（默认300秒）

## 使用方法

### 1. 前端WebSocket连接
```javascript
// 建立WebSocket连接
const ws = new WebSocket(`ws://localhost:8090/ws/conn?x-userId=${userId}`);

// 发送心跳包
setInterval(() => {
    if (ws.readyState === WebSocket.OPEN) {
        ws.send('heart');
    }
}, 9000);

// 接收推送消息
ws.onmessage = function(event) {
    const data = JSON.parse(event.data);
    if (data.content) {
        // 处理新消息
        addMessageToChat(data.content);
    }
};

// 发送消息
function sendMessage(content) {
    const message = { content: content };
    ws.send(JSON.stringify(message));
}
```

### 2. HTTP API发送消息
```javascript
// 通过HTTP API发送消息
async function sendMessageViaHttp(content) {
    const response = await fetch('/api/talk/messages', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-User-Id': userId
        },
        body: JSON.stringify({ content: content })
    });
    
    if (response.ok) {
        console.log('消息发送成功');
        // 消息会自动推送给所有在线用户
    }
}
```

## 测试方法

### 1. 使用测试脚本
```bash
# 安装依赖
pip install websockets requests

# 运行测试
python test_realtime_push.py
```

### 2. 手动测试
1. 打开多个浏览器标签页
2. 每个标签页连接WebSocket（不同用户ID）
3. 在一个标签页发送消息
4. 观察其他标签页是否收到实时推送

## 配置选项

### 1. WebSocket配置
```yaml
websocket:
  heartbeat-interval: 30      # 心跳间隔（秒）
  connection-timeout: 300     # 连接超时时间（秒）
  max-message-size: 8192      # 消息最大长度
  max-connections: 10000      # 最大连接数
```

### 2. 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/talk_service
    username: root
    password: Root123!
```

## 监控和日志

### 1. 连接状态监控
```java
// 获取在线用户数量
int onlineCount = userSessionManager.getOnlineUserCount();

// 获取在线用户列表
List<OnlineUser> onlineUsers = userSessionManager.getAllOnlineUsers();
```

### 2. 推送统计
```java
// 推送成功/失败统计
logger.info("消息广播完成: 成功={}, 失败={}, 总在线用户数={}", 
           successCount, failCount, sessions.size());
```

## 故障排除

### 1. 连接问题
- 检查WebSocket路径是否正确
- 确认用户ID参数是否正确传递
- 查看服务器日志中的连接错误

### 2. 推送失败
- 检查客户端WebSocket连接状态
- 确认心跳包是否正常发送
- 查看服务器推送日志

### 3. 数据库问题
- 确认数据库连接正常
- 检查表结构是否正确
- 查看SQL执行日志

## 性能优化

### 1. 连接池管理
- 自动清理断开的连接
- 限制最大连接数
- 心跳超时检测

### 2. 消息推送优化
- 批量推送减少网络开销
- 失败重试机制
- 推送状态监控

## 安全考虑

### 1. 用户认证
- WebSocket连接需要传递用户ID
- HTTP API需要X-User-Id请求头
- 支持用户状态验证

### 2. 消息验证
- 消息内容长度限制
- 特殊字符过滤
- 发送频率限制

## 扩展功能

### 1. 私聊消息
- 支持点对点消息推送
- 消息加密传输
- 离线消息存储

### 2. 群组聊天
- 支持群组消息推送
- 群组成员管理
- 群组权限控制

### 3. 消息类型
- 文本消息
- 图片消息
- 文件消息
- 语音消息 