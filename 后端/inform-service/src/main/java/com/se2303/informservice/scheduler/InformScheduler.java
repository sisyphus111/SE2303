package com.se2303.informservice.scheduler;

import com.se2303.informservice.service.InformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InformScheduler {

    @Autowired
    private InformService informService;

    /**
     * 每天凌晨2点清理过期通知
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredInforms() {
        System.out.println("开始清理过期通知...");
        try {
            informService.cleanupExpiredInforms();
            System.out.println("过期通知清理完成");
        } catch (Exception e) {
            System.err.println("清理过期通知失败: " + e.getMessage());
        }
    }
}
