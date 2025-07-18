package com.se2303.informservice.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RedisConfigTest {

    @Mock
    private RedisConnectionFactory connectionFactory;

    @InjectMocks
    private RedisConfig redisConfig;

    @Test
    void testRedisConfigAnnotations() {
        // 验证类上是否有@Configuration注解
        Class<RedisConfig> configClass = RedisConfig.class;
        Configuration configuration = configClass.getAnnotation(Configuration.class);
        assertNotNull(configuration, "RedisConfig 应该包含 @Configuration 注解");
    }

    @Test
    void testRedisTemplateBeanCreation() throws NoSuchMethodException {
        // 验证redisTemplate方法创建RedisTemplate实例
        RedisTemplate<String, Object> redisTemplate = redisConfig.redisTemplate(connectionFactory);

        assertNotNull(redisTemplate, "RedisTemplate 实例应该能成功创建");
        assertTrue(redisTemplate.getKeySerializer() instanceof StringRedisSerializer,
                "KeySerializer 应该是 StringRedisSerializer 类型");
        assertTrue(redisTemplate.getHashKeySerializer() instanceof StringRedisSerializer,
                "HashKeySerializer 应该是 StringRedisSerializer 类型");
        assertTrue(redisTemplate.getValueSerializer() instanceof GenericJackson2JsonRedisSerializer,
                "ValueSerializer 应该是 GenericJackson2JsonRedisSerializer 类型");
        assertTrue(redisTemplate.getHashValueSerializer() instanceof GenericJackson2JsonRedisSerializer,
                "HashValueSerializer 应该是 GenericJackson2JsonRedisSerializer 类型");

        // 验证方法上是否有@Bean注解
        Method redisTemplateMethod = RedisConfig.class.getMethod("redisTemplate", RedisConnectionFactory.class);
        assertNotNull(redisTemplateMethod.getAnnotation(org.springframework.context.annotation.Bean.class),
                "redisTemplate 方法应该包含 @Bean 注解");
    }
}