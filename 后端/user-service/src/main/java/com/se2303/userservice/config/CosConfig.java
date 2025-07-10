package com.se2303.userservice.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云COS对象存储配置类
 * 用于初始化COS客户端，提供文件上传到腾讯云存储桶的功能
 */
@Configuration
public class CosConfig {

    @Value("${tencent.cos.secret-id}")
    private String secretId;

    @Value("${tencent.cos.secret-key}")
    private String secretKey;

    @Value("${tencent.cos.region}")
    private String region;

    /**
     * 创建COS客户端Bean
     * 使用配置文件中的secretId、secretKey和region信息
     * @return COSClient 腾讯云COS客户端
     */
    @Bean
    public COSClient cosClient() {
        // 1. 初始化用户身份信息（secretId, secretKey）
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        
        // 2. 设置bucket的地域
        Region regionObj = new Region(region);
        ClientConfig clientConfig = new ClientConfig(regionObj);
        
        // 3. 生成cos客户端
        return new COSClient(cred, clientConfig);
    }
} 