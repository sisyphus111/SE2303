package com.se2303.talkservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 聊天服务启动类
 */
@SpringBootApplication
@EnableScheduling
public class TalkServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TalkServiceApplication.class, args);
    }
} 