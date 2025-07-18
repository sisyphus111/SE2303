# InspectionService 项目集成示例

## 1. Community Service 集成

### 在发帖时进行内容审查

```java
// 在 CommunityServiceApplication.java 中添加 RestTemplate Bean
@SpringBootApplication
public class CommunityServiceApplication {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    public static void main(String[] args) {
        SpringApplication.run(CommunityServiceApplication.class, args);
    }
}
```

```java
// 创建 InspectionServiceClient.java
@Component
public class InspectionServiceClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${inspection.service.url:http://localhost:8088}")
    private String inspectionServiceUrl;
    
    public boolean isContentSafe(String content) {
        try {
            String url = inspectionServiceUrl + "/inspect";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> request = Map.of("content", content);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map<String, Object> result = response.getBody();
            
            return (Boolean) result.get("isSafe");
            
        } catch (Exception e) {
            // 记录日志但不影响主业务
            System.err.println("Content inspection failed: " + e.getMessage());
            return true;
        }
    }
}
```

```java
// 在 PostController.java 中使用
@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    @Autowired
    private InspectionServiceClient inspectionClient;
    
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostRequest request) {
        // 内容审查
        if (!inspectionClient.isContentSafe(request.getContent())) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "内容包含不当信息，请修改后重试"));
        }
        
        // 继续创建帖子的逻辑
        // ...
        
        return ResponseEntity.ok(Map.of("message", "发布成功"));
    }
}
```

## 2. User Service 集成

### 在用户注册时审查用户简介

```java
// 在 UserServiceImpl.java 中集成
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private InspectionServiceClient inspectionClient;
    
    @Override
    public User registerUser(UserDTO userDTO) {
        // 审查用户简介
        if (userDTO.getBio() != null && !userDTO.getBio().isEmpty()) {
            if (!inspectionClient.isContentSafe(userDTO.getBio())) {
                throw new IllegalArgumentException("用户简介包含不当内容");
            }
        }
        
        // 审查用户昵称
        if (userDTO.getNickname() != null && !userDTO.getNickname().isEmpty()) {
            if (!inspectionClient.isContentSafe(userDTO.getNickname())) {
                throw new IllegalArgumentException("用户昵称包含不当内容");
            }
        }
        
        // 继续注册逻辑
        // ...
        
        return user;
    }
}
```

## 3. Mood Service 集成

### 在心情记录时进行内容审查

```java
// 在 MoodServiceImpl.java 中集成
@Service
public class MoodServiceImpl implements MoodService {
    
    @Autowired
    private InspectionServiceClient inspectionClient;
    
    @Override
    public MoodCheckInResponse checkIn(MoodCheckInRequest request) {
        // 审查日记内容
        if (request.getDiaryContent() != null && !request.getDiaryContent().isEmpty()) {
            if (!inspectionClient.isContentSafe(request.getDiaryContent())) {
                // 对于心情服务，检测到危险内容时可能需要特殊处理
                // 比如标记为需要关注，而不是直接拒绝
                System.out.println("警告：用户心情记录包含危险内容，需要关注");
                // 可以发送到专门的危机干预系统
            }
        }
        
        // 继续心情记录逻辑
        // ...
        
        return response;
    }
}
```

## 4. Bot Service 集成

### 在聊天消息处理时进行审查

```java
// 在 ChatWebSocketHandler.java 中集成
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    
    @Autowired
    private InspectionServiceClient inspectionClient;
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 连接建立逻辑
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String content = message.getPayload();
        
        // 审查用户消息
        if (!inspectionClient.isContentSafe(content)) {
            // 发送警告消息
            session.sendMessage(new TextMessage(
                "检测到您的消息可能包含不当内容。如果您需要帮助，请联系专业人员。"
            ));
            
            // 记录危险消息，可能需要人工干预
            System.out.println("危险聊天内容检测: " + content);
            return;
        }
        
        // 继续正常聊天逻辑
        // ...
    }
}
```

## 5. 配置文件示例

### application.properties 配置
```properties
# 各个服务的 application.properties 中添加
inspection.service.url=http://localhost:8088
inspection.service.timeout=10000
```

### application.yml 配置
```yaml
inspection:
  service:
    url: http://localhost:8088
    timeout: 10000
    enabled: true
```

## 6. 统一的客户端配置

### 创建共享的配置类
```java
@Configuration
@EnableConfigurationProperties(InspectionProperties.class)
public class InspectionConfig {
    
    @Bean
    @ConditionalOnProperty(name = "inspection.service.enabled", havingValue = "true", matchIfMissing = true)
    public InspectionServiceClient inspectionServiceClient(InspectionProperties properties) {
        return new InspectionServiceClient(properties);
    }
}
```

```java
@ConfigurationProperties(prefix = "inspection.service")
@Data
public class InspectionProperties {
    private String url = "http://localhost:8088";
    private int timeout = 10000;
    private boolean enabled = true;
}
```

## 7. 异步处理示例

### 异步内容审查
```java
@Service
public class AsyncInspectionService {
    
    @Autowired
    private InspectionServiceClient inspectionClient;
    
    @Async
    public CompletableFuture<Boolean> validateContentAsync(String content) {
        return CompletableFuture.supplyAsync(() -> 
            inspectionClient.isContentSafe(content)
        );
    }
    
    // 使用示例
    public void processPost(PostRequest request) {
        CompletableFuture<Boolean> validationFuture = validateContentAsync(request.getContent());
        
        validationFuture.thenAccept(isSafe -> {
            if (isSafe) {
                // 发布帖子
                publishPost(request);
            } else {
                // 拒绝发布
                rejectPost(request, "内容不符合规范");
            }
        });
    }
}
```

## 8. 批量处理示例

### 批量内容审查
```java
@Service
public class BatchInspectionService {
    
    @Autowired
    private InspectionServiceClient inspectionClient;
    
    public Map<String, Boolean> validateMultipleContent(List<String> contents) {
        return contents.parallelStream()
            .collect(Collectors.toConcurrentMap(
                content -> content,
                content -> inspectionClient.isContentSafe(content)
            ));
    }
}
```

## 9. 监控和日志

### 添加监控指标
```java
@Component
public class InspectionMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter safeContentCounter;
    private final Counter dangerousContentCounter;
    
    public InspectionMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.safeContentCounter = Counter.builder("inspection.safe.content")
            .description("Safe content count")
            .register(meterRegistry);
        this.dangerousContentCounter = Counter.builder("inspection.dangerous.content")
            .description("Dangerous content count")
            .register(meterRegistry);
    }
    
    public void recordSafeContent() {
        safeContentCounter.increment();
    }
    
    public void recordDangerousContent() {
        dangerousContentCounter.increment();
    }
}
```

## 10. 测试示例

### 单元测试
```java
@SpringBootTest
public class InspectionServiceClientTest {
    
    @Autowired
    private InspectionServiceClient inspectionClient;
    
    @Test
    public void testSafeContent() {
        boolean result = inspectionClient.isContentSafe("今天天气很好");
        assertTrue(result);
    }
    
    @Test
    public void testDangerousContent() {
        boolean result = inspectionClient.isContentSafe("我想结束生命");
        assertFalse(result);
    }
}
```

这些示例展示了如何在现有的各个微服务中集成 `InspectionService`，确保内容安全审查功能的全面覆盖。 