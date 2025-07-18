package com.se2303.userservice.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传服务类
 * 负责处理文件上传到腾讯云COS对象存储
 */
@Service
public class FileUploadService {

    @Autowired
    private COSClient cosClient;

    @Value("${tencent.cos.bucket-name}")
    private String bucketName;

    @Value("${tencent.cos.domain}")
    private String domain;

    /**
     * 上传用户头像到腾讯云COS
     * 
     * 整个流程如下：
     * 1. 验证文件类型（只允许图片格式）
     * 2. 生成唯一的文件名（使用UUID避免文件名冲突）
     * 3. 设置文件元数据（Content-Type等）
     * 4. 上传文件到腾讯云COS存储桶
     * 5. 返回文件的完整访问URL
     * 
     * @param file 上传的头像文件
     * @param userId 用户ID，用于组织文件路径
     * @return String 上传后的文件URL
     * @throws IOException 文件处理异常
     * @throws IllegalArgumentException 文件类型不支持异常
     */
    public String uploadAvatar(MultipartFile file, Long userId) throws IOException {
        // 1. 验证文件
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 2. 验证文件类型（只允许图片）
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        if (!isImageFile(fileExtension)) {
            throw new IllegalArgumentException("只支持图片格式文件 (jpg, jpeg, png, gif, bmp, webp)");
        }

        // 3. 验证文件大小（最大5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }

        try {
            // 4. 生成唯一文件名：avatars/用户ID/UUID.扩展名
            String fileName = String.format("avatars/%d/%s.%s", 
                userId, 
                UUID.randomUUID().toString(), 
                fileExtension
            );

            // 5. 设置文件元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(getContentType(fileExtension));
            metadata.setContentLength(file.getSize());
            // 设置缓存控制，提高访问性能
            metadata.setCacheControl("max-age=31536000"); // 1年缓存

            // 6. 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, 
                fileName, 
                file.getInputStream(), 
                metadata
            );

            // 7. 上传文件到腾讯云COS
            PutObjectResult result = cosClient.putObject(putObjectRequest);
            
            // 8. 返回文件的完整访问URL
            String fileUrl = domain + "/" + fileName;
            
            System.out.println("文件上传成功: " + fileUrl);
            return fileUrl;

        } catch (Exception e) {
            System.err.println("文件上传失败: " + e.getMessage());
            throw new IOException("文件上传失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取文件扩展名
     * @param filename 文件名
     * @return 文件扩展名（不包含点号）
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * 检查是否为支持的图片格式
     * @param extension 文件扩展名
     * @return true表示支持的图片格式
     */
    private boolean isImageFile(String extension) {
        String[] supportedFormats = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
        for (String format : supportedFormats) {
            if (format.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据文件扩展名获取Content-Type
     * @param extension 文件扩展名
     * @return Content-Type字符串
     */
    private String getContentType(String extension) {
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 删除COS中的文件（用于更换头像时删除旧头像）
     * @param fileUrl 要删除的文件URL
     */
    public void deleteFile(String fileUrl) {
        try {
            if (fileUrl != null && fileUrl.startsWith(domain)) {
                // 从URL中提取文件Key（去除域名部分）
                String fileKey = fileUrl.substring(domain.length() + 1);
                cosClient.deleteObject(bucketName, fileKey);
                System.out.println("旧头像删除成功: " + fileUrl);
            }
        } catch (Exception e) {
            System.err.println("删除旧头像失败: " + e.getMessage());
            // 删除失败不影响新头像上传，只记录错误
        }
    }
} 