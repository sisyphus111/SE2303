package com.se2303.userservice.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {
    @Mock
    private COSClient cosClient;
    @Mock
    private MultipartFile file;
    @InjectMocks
    private FileUploadService fileUploadService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileUploadService, "bucketName", "bucket");
        ReflectionTestUtils.setField(fileUploadService, "domain", "http://domain");
    }

    @Test
    void testUploadAvatar_Success() throws IOException {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("avatar.jpg");
        when(file.getSize()).thenReturn(1024L);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[10]));
        when(cosClient.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());
        String url = fileUploadService.uploadAvatar(file, 1L);
        assertTrue(url.startsWith("http://domain/avatars/1/"));
        assertTrue(url.endsWith(".jpg"));
    }

    @Test
    void testUploadAvatar_FileIsNull() {
        assertThrows(IllegalArgumentException.class, () -> fileUploadService.uploadAvatar(null, 1L));
    }

    @Test
    void testUploadAvatar_FileIsEmpty() {
        when(file.isEmpty()).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> fileUploadService.uploadAvatar(file, 1L));
    }

    @Test
    void testUploadAvatar_FilenameNull() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> fileUploadService.uploadAvatar(file, 1L));
    }

    @Test
    void testUploadAvatar_UnsupportedType() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("avatar.txt");
        assertThrows(IllegalArgumentException.class, () -> fileUploadService.uploadAvatar(file, 1L));
    }

    @Test
    void testUploadAvatar_TooLarge() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("avatar.jpg");
        when(file.getSize()).thenReturn(6 * 1024 * 1024L);
        assertThrows(IllegalArgumentException.class, () -> fileUploadService.uploadAvatar(file, 1L));
    }

    @Test
    void testUploadAvatar_CosException() throws IOException {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("avatar.jpg");
        when(file.getSize()).thenReturn(1024L);
        when(file.getInputStream()).thenThrow(new IOException("fail"));
        assertThrows(IOException.class, () -> fileUploadService.uploadAvatar(file, 1L));
    }

    @Test
    void testDeleteFile_Success() {
        doNothing().when(cosClient).deleteObject(anyString(), anyString());
        fileUploadService.deleteFile("http://domain/avatars/1/abc.jpg");
        verify(cosClient).deleteObject(eq("bucket"), eq("avatars/1/abc.jpg"));
    }

    @Test
    void testDeleteFile_NullUrl() {
        fileUploadService.deleteFile(null);
        verify(cosClient, never()).deleteObject(anyString(), anyString());
    }

    @Test
    void testDeleteFile_NotDomain() {
        fileUploadService.deleteFile("http://other/avatars/1/abc.jpg");
        verify(cosClient, never()).deleteObject(anyString(), anyString());
    }

    @Test
    void testDeleteFile_Exception() {
        doThrow(new RuntimeException("fail")).when(cosClient).deleteObject(anyString(), anyString());
        // 不抛异常，只记录错误
        fileUploadService.deleteFile("http://domain/avatars/1/abc.jpg");
        verify(cosClient).deleteObject(eq("bucket"), eq("avatars/1/abc.jpg"));
    }
} 