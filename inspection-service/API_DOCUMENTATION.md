# InspectionService API 调用文档

## 服务概述

`InspectionService` 是一个内容审查微服务，用于检测文本内容中的危险信息（如自杀倾向等）。当检测到危险内容时，会自动触发警报机制。

## 服务信息

- **服务名称**: inspection-service
- **端口**: 8088
- **基础URL**: `http://localhost:8088`
- **协议**: HTTP/HTTPS

## API 接口

### 1. 内容审查接口

#### 接口地址
```
POST /inspect
```

#### 请求头
```
Content-Type: application/json
```

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| content | String | 是 | 待审查的文本内容 |

#### 请求示例

```json
{
  "content": "我想结束生命，活着没有意义"
}
```

#### 响应参数

| 参数名 | 类型 | 说明 |
|--------|------|------|
| isSafe | Boolean | 内容是否安全（true: 安全, false: 危险） |
| reason | String | 审查结果说明 |

#### 响应示例

**安全内容响应：**
```json
{
  "isSafe": true,
  "reason": "Content is safe."
}
```

**危险内容响应：**
```json
{
  "isSafe": false,
  "reason": "Content is not safe."
}
```

## 调用示例

### Java (Spring Boot)

```java
@Service
public class ContentValidationService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String INSPECTION_SERVICE_URL = "http://localhost:8088/inspect";
    
    public boolean validateContent(String content) {
        try {
            // 构建请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("content", content);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            // 发送请求
            ResponseEntity<Map> response = restTemplate.postForEntity(
                INSPECTION_SERVICE_URL, 
                request, 
                Map.class
            );
            
            // 解析响应
            Map<String, Object> result = response.getBody();
            return (Boolean) result.get("isSafe");
            
        } catch (Exception e) {
            // 异常处理：默认认为内容安全
            logger.error("调用审查服务失败", e);
            return true;
        }
    }
}
```

### Python

```python
import requests
import json

class ContentInspectionClient:
    def __init__(self, base_url="http://localhost:8088"):
        self.base_url = base_url
        self.inspect_url = f"{base_url}/inspect"
    
    def validate_content(self, content):
        """
        验证内容是否安全
        
        Args:
            content (str): 待检查的内容
            
        Returns:
            bool: True表示安全，False表示危险
        """
        try:
            response = requests.post(
                self.inspect_url,
                headers={"Content-Type": "application/json"},
                data=json.dumps({"content": content}),
                timeout=10
            )
            
            if response.status_code == 200:
                result = response.json()
                return result.get("isSafe", True)
            else:
                print(f"审查服务返回错误: {response.status_code}")
                return True
                
        except Exception as e:
            print(f"调用审查服务失败: {e}")
            return True

# 使用示例
client = ContentInspectionClient()
is_safe = client.validate_content("我想结束生命")
```

### Node.js

```javascript
const axios = require('axios');

class ContentInspectionClient {
    constructor(baseUrl = 'http://localhost:8088') {
        this.inspectUrl = `${baseUrl}/inspect`;
    }
    
    async validateContent(content) {
        try {
            const response = await axios.post(this.inspectUrl, {
                content: content
            }, {
                headers: {
                    'Content-Type': 'application/json'
                },
                timeout: 10000
            });
            
            return response.data.isSafe;
            
        } catch (error) {
            console.error('调用审查服务失败:', error.message);
            return true; // 默认认为安全
        }
    }
}

// 使用示例
const client = new ContentInspectionClient();
client.validateContent('我想结束生命').then(isSafe => {
    console.log('内容是否安全:', isSafe);
});
```

### cURL

```bash
# 测试安全内容
curl -X POST http://localhost:8088/inspect \
  -H "Content-Type: application/json" \
  -d '{"content": "今天天气很好"}'

# 测试危险内容
curl -X POST http://localhost:8088/inspect \
  -H "Content-Type: application/json" \
  -d '{"content": "我想结束生命"}'
```

## 集成建议

### 1. 异步调用
对于高并发场景，建议使用异步调用方式：

```java
@Async
public CompletableFuture<Boolean> validateContentAsync(String content) {
    return CompletableFuture.supplyAsync(() -> validateContent(content));
}
```

### 2. 缓存机制
对于重复内容，可以添加缓存机制：

```java
@Cacheable(value = "contentValidation", key = "#content")
public boolean validateContent(String content) {
    // 调用审查服务
}
```

### 3. 熔断机制
建议添加熔断机制，避免审查服务不可用时影响主业务：

```java
@HystrixCommand(fallbackMethod = "validateContentFallback")
public boolean validateContent(String content) {
    // 调用审查服务
}

public boolean validateContentFallback(String content) {
    // 降级处理：默认认为内容安全
    return true;
}
```

## 错误处理

### HTTP 状态码

| 状态码 | 说明 | 处理建议 |
|--------|------|----------|
| 200 | 成功 | 正常处理响应 |
| 400 | 请求参数错误 | 检查请求格式 |
| 500 | 服务器内部错误 | 重试或降级处理 |
| 超时 | 网络超时 | 重试或降级处理 |

### 异常处理建议

1. **网络异常**: 建议重试1-2次，失败后降级处理
2. **服务不可用**: 使用熔断机制，避免级联故障
3. **响应解析异常**: 记录日志，默认认为内容安全

## 性能考虑

- **响应时间**: 正常情况下 < 2秒
- **并发支持**: 支持多并发请求
- **超时设置**: 建议设置10秒超时
- **重试机制**: 建议最多重试2次

## 监控和日志

建议在调用方添加以下监控：

1. **调用成功率监控**
2. **响应时间监控**
3. **异常日志记录**
4. **危险内容检测统计**

## 部署信息

- **开发环境**: `http://localhost:8088`
- **测试环境**: `http://test-inspection-service:8088`
- **生产环境**: `http://prod-inspection-service:8088`

## 联系方式

如有问题，请联系开发团队或查看服务日志。 