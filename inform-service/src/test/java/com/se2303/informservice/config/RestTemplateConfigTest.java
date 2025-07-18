package com.se2303.informservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RestTemplateConfigTest {

    @Test
    void testRestTemplateConfigAnnotations() {
        // 验证类上是否有@Configuration注解
        Class<RestTemplateConfig> configClass = RestTemplateConfig.class;
        Configuration configuration = configClass.getAnnotation(Configuration.class);
        assertNotNull(configuration, "RestTemplateConfig 应该包含 @Configuration 注解");
    }

    @Test
    void testRestTemplateBeanCreation() throws NoSuchMethodException {
        // 验证restTemplate方法创建RestTemplate实例
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplate restTemplate = config.restTemplate();

        assertNotNull(restTemplate, "RestTemplate 实例应该能成功创建");
        assertTrue(restTemplate instanceof RestTemplate, "restTemplate 方法应返回 RestTemplate 类型的实例");

        // 验证方法上是否有@Bean注解
        Method restTemplateMethod = RestTemplateConfig.class.getMethod("restTemplate");
        assertNotNull(restTemplateMethod.getAnnotation(org.springframework.context.annotation.Bean.class),
                "restTemplate 方法应该包含 @Bean 注解");
    }
}