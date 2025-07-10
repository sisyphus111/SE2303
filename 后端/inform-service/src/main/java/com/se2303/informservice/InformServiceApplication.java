package com.se2303.informservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InformServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(InformServiceApplication.class);

    public static void main(String[] args) {
        logger.info("🚀 启动 Inform Service 应用程序...");
        SpringApplication.run(InformServiceApplication.class, args);
        logger.info("✅ Inform Service 启动完成！");
    }

}
