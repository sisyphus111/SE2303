# 心情分析功能说明

## 功能概述

本功能通过AI分析用户的日记和心情评分数据，生成个性化的心理健康分析报告。

## 技术实现

### 后端实现

1. **MoodAnalysisService** - 分析服务接口
   - `generateAnalysisReport(String userId)` - 生成用户分析报告
   - `generateAnalysisReport(String userId, String startDate, String endDate)` - 根据日期范围生成分析报告

2. **MoodAnalysisServiceImpl** - 分析服务实现
   - 获取用户的心情记录和日记数据
   - 构建AI prompt
   - 调用AI API生成分析报告

3. **MoodController** - 控制器接口
   - `GET /api/mood/analysis/{userId}` - 获取用户分析报告
   - `GET /api/mood/analysis/{userId}/range?startDate=2024-01-01&endDate=2024-01-31` - 根据日期范围获取分析报告

### 前端实现

1. **homeService.js** - 前端服务
   - `getAnalysisData(userId)` - 调用后端分析接口

2. **Home.jsx** - 前端页面
   - 集成分析数据展示

## 配置说明

### AI API配置

在 `application.yml` 中配置AI API：

```yaml
ai:
  api:
    url: http://127.0.0.1/v1
    key: REDACTED
```

### 网关配置

确保网关正确路由到mood-service：

```yaml
# 心情服务路由
- id: mood-service
  uri: http://localhost:8085
  predicates:
    - Path=/api/mood/**
  filters:
    - RewritePath=/api/mood/(?<segment>.*), /api/mood/${segment}
```

## API接口

### 获取分析报告

**请求：**
```
GET /api/mood/analysis/{userId}
```

**响应：**
```json
{
  "success": true,
  "message": "获取分析报告成功",
  "object": {
    "userId": "1",
    "analysis": "根据您最近一周的情绪数据分析，您的整体心情状态良好..."
  }
}
```

### 根据日期范围获取分析报告

**请求：**
```
GET /api/mood/analysis/{userId}/range?startDate=2024-01-01&endDate=2024-01-31
```

**响应：**
```json
{
  "success": true,
  "message": "获取分析报告成功",
  "object": {
    "userId": "1",
    "startDate": "2024-01-01",
    "endDate": "2024-01-31",
    "analysis": "根据您指定时间段的情绪数据分析..."
  }
}
```

## 使用流程

1. 用户在前端进行心情打卡和写日记
2. 前端调用 `/api/mood/analysis/{userId}` 获取分析报告
3. 后端获取用户的心情记录和日记数据
4. 构建包含用户数据的AI prompt
5. 调用AI API生成个性化分析报告
6. 返回分析报告给前端展示

## 测试数据

系统会自动初始化测试数据，包括：
- 用户1的最近7天心情记录
- 不同评分和日记内容
- 对应的心情标签

## 注意事项

1. 确保AI API服务可用
2. 检查网络连接和API密钥配置
3. 如果AI服务不可用，会返回友好的错误提示
4. 分析报告基于用户的实际数据生成，数据越丰富分析越准确 