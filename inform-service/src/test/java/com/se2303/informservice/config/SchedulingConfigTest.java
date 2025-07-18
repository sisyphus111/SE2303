package com.se2303.informservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SchedulingConfigTest {

    @Test
    void testSchedulingConfigAnnotations() {
        // 验证类上是否有@Configuration和@EnableScheduling注解
        Class<SchedulingConfig> configClass = SchedulingConfig.class;

        // 检查@Configuration注解
        Configuration configuration = configClass.getAnnotation(Configuration.class);
        assertNotNull(configuration, "SchedulingConfig 应该包含 @Configuration 注解");

        // 检查@EnableScheduling注解
        EnableScheduling enableScheduling = configClass.getAnnotation(EnableScheduling.class);
        assertNotNull(enableScheduling, "SchedulingConfig 应该包含 @EnableScheduling 注解");
    }

    @Test
    void testSchedulingConfigInstance() {
        // 验证可以创建SchedulingConfig实例
        SchedulingConfig config = new SchedulingConfig();
        assertNotNull(config, "SchedulingConfig 实例应该能成功创建");
    }
}