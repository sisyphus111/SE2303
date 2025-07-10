# AI提示词配置指南

## 概述

bot-service现在支持灵活的AI提示词配置，可以根据不同的场景和性格类型来定制AI助手的行为和回应风格。

## 功能特性

### 1. 系统提示词
- 基础的角色定义和行为指导
- 可在`application.properties`中配置
- 支持动态更新

### 2. 场景提示词
- **general**: 通用心理健康陪伴
- **crisis**: 危机干预和紧急情况处理
- **academic**: 学习压力疏导
- **relationship**: 人际关系咨询

### 3. 性格提示词
- **warm**: 温暖、善解人意
- **professional**: 专业、理性
- **encouraging**: 积极、鼓励性
- **empathetic**: 富有同理心

## 配置方法

### 1. 配置文件设置

在`application.properties`中添加：

```properties
# AI提示词配置
ai.prompt.system=你是一个专业的心理健康陪伴助手，名叫"小暖"。你的主要职责是：
1. 提供温暖、理解和支持的对话
2. 倾听用户的情绪和困扰，给予同理心回应
3. 提供积极的心理健康建议和情绪调节技巧
4. 在用户遇到严重心理问题时，建议寻求专业心理咨询
5. 保持友好、耐心和专业的态度
6. 使用简单易懂的语言，避免过于专业的术语
7. 鼓励用户表达感受，营造安全的对话环境

请记住：你是一个陪伴者，不是医生。对于严重的心理健康问题，请建议用户寻求专业帮助。
```

### 2. API接口

#### 获取提示词配置
```bash
GET /api/prompt/config
```

#### 获取场景类型
```bash
GET /api/prompt/scenarios
```

#### 获取性格类型
```bash
GET /api/prompt/personalities
```

#### 获取完整提示词
```bash
GET /api/prompt/full?scenario=academic&personality=warm
```

#### 更新系统提示词
```bash
PUT /api/prompt/system
Content-Type: application/json

{
  "prompt": "新的系统提示词内容"
}
```

#### 添加新场景
```bash
POST /api/prompt/scenarios
Content-Type: application/json

{
  "name": "workplace",
  "prompt": "你是职场压力疏导助手，专门帮助职场人士处理工作压力、职业发展等问题。"
}
```

#### 添加新性格
```bash
POST /api/prompt/personalities
Content-Type: application/json

{
  "name": "humorous",
  "prompt": "你是一个幽默风趣的助手，用轻松的方式帮助用户缓解压力。"
}
```

## 使用示例

### 1. 学习压力场景
```java
// 使用学习压力场景 + 温暖性格
aiService.getStreamingResponse(
    userMessage, 
    history, 
    "academic", 
    "warm", 
    onChunk, 
    onComplete, 
    onFinish
);
```

### 2. 危机干预场景
```java
// 使用危机干预场景 + 专业性格
aiService.getStreamingResponse(
    userMessage, 
    history, 
    "crisis", 
    "professional", 
    onChunk, 
    onComplete, 
    onFinish
);
```

## 提示词组合效果

### 默认组合 (general + warm)
- 适合日常聊天和一般心理支持
- 温暖、善解人意的回应风格
- 提供基本的情绪支持和建议

### 学习压力组合 (academic + encouraging)
- 专门针对学习相关问题
- 积极鼓励的态度
- 提供具体的学习方法和压力缓解技巧

### 危机干预组合 (crisis + professional)
- 处理严重心理危机
- 专业、理性的回应
- 提供紧急求助信息和专业建议

### 人际关系组合 (relationship + empathetic)
- 处理人际关系问题
- 富有同理心的回应
- 帮助理解他人感受和改善关系

## 最佳实践

1. **选择合适的场景**: 根据用户的主要问题类型选择相应场景
2. **匹配性格风格**: 根据用户的偏好和需求选择合适的性格类型
3. **动态调整**: 可以在对话过程中根据情况调整提示词
4. **安全第一**: 对于严重问题，始终建议寻求专业帮助
5. **保持一致性**: 在同一个会话中保持提示词的一致性

## 注意事项

- 提示词会影响AI的回应风格和内容
- 系统提示词只在会话开始时添加，避免重复
- 历史消息会保持原有的角色信息
- 建议定期评估和优化提示词效果
- 确保提示词符合心理健康服务的伦理要求

## 扩展开发

如需添加新的场景或性格类型，可以：

1. 在`AIPromptConfig`中添加新的默认配置
2. 通过API接口动态添加
3. 在配置文件中预设新的提示词

这样可以灵活地适应不同的使用场景和用户需求。 