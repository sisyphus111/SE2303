package com.se2303.moodservice.controller;

import com.se2303.moodservice.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testHandleValidationExceptions_Success() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("object", "field1", "Field1 is required");
        FieldError fieldError2 = new FieldError("object", "field2", "Field2 must be positive");
        
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));
        
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // When
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleValidationExceptions(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("参数验证失败", response.getBody().getMessage());
        assertNotNull(response.getBody().getObject());
        Map<String, String> errors = (Map<String, String>) response.getBody().getObject();
        assertEquals("Field1 is required", errors.get("field1"));
        assertEquals("Field2 must be positive", errors.get("field2"));
    }

    @Test
    void testHandleValidationExceptions_WithNoFieldErrors() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        // 只需要 stub getFieldErrors，无需多余 mock
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList());
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // When
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleValidationExceptions(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("参数验证失败", response.getBody().getMessage());
        assertNotNull(response.getBody().getObject());
        Map<String, String> errors = (Map<String, String>) response.getBody().getObject();
        assertTrue(errors.isEmpty());
    }

    @Test
    void testHandleGenericException_Success() {
        // Given
        RuntimeException exception = new RuntimeException("测试异常");

        // When
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleGenericException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("服务器内部错误: 测试异常", response.getBody().getMessage());
    }

    @Test
    void testHandleGenericException_WithEmptyMessage() {
        // Given
        RuntimeException exception = new RuntimeException("");

        // When
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleGenericException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("服务器内部错误: ", response.getBody().getMessage());
    }

    @Test
    void testHandleGenericException_WithNullMessage() {
        // Given
        RuntimeException exception = new RuntimeException((String) null);

        // When
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleGenericException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("服务器内部错误: null", response.getBody().getMessage());
    }

    @Test
    void testHandleIllegalArgumentException_Success() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("参数错误");

        // When
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("参数错误: 参数错误", response.getBody().getMessage());
    }

    @Test
    void testHandleIllegalArgumentException_WithNullMessage() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException((String) null);

        // When
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("参数错误: null", response.getBody().getMessage());
    }

    @Test
    void testHandleValidationExceptions_WithMultipleFieldErrors() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("object", "score", "分数必须在1-5之间");
        FieldError fieldError2 = new FieldError("object", "diaryTitle", "日记标题不能为空");
        FieldError fieldError3 = new FieldError("object", "moodLabels", "心情标签不能为空");
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2, fieldError3));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // When
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleValidationExceptions(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("参数验证失败", response.getBody().getMessage());
        assertNotNull(response.getBody().getObject());
        Map<String, String> errors = (Map<String, String>) response.getBody().getObject();
        assertEquals("分数必须在1-5之间", errors.get("score"));
        assertEquals("日记标题不能为空", errors.get("diaryTitle"));
        assertEquals("心情标签不能为空", errors.get("moodLabels"));
    }

    @Test
    void testHandleValidationExceptions_WithSpecialCharacters() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "字段包含特殊字符: @#$%^&*()");
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // When
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleValidationExceptions(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("参数验证失败", response.getBody().getMessage());
        assertNotNull(response.getBody().getObject());
        Map<String, String> errors = (Map<String, String>) response.getBody().getObject();
        assertEquals("字段包含特殊字符: @#$%^&*()", errors.get("field"));
    }

    @Test
    void testHandleGenericException_WithLongMessage() {
        // Given
        String longMessage = "这是一个非常长的异常消息，包含了很多详细信息，用于测试异常处理器是否能够正确处理长文本消息，确保不会出现截断或其他问题。";
        RuntimeException exception = new RuntimeException(longMessage);

        // When
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleGenericException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("服务器内部错误: " + longMessage, response.getBody().getMessage());
    }

    @Test
    void testHandleGenericException_WithNullPointerException() {
        // Given
        NullPointerException exception = new NullPointerException("空指针异常");

        // When
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleGenericException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("服务器内部错误: 空指针异常", response.getBody().getMessage());
    }

    @Test
    void testHandleGenericException_WithIllegalStateException() {
        // Given
        IllegalStateException exception = new IllegalStateException("状态异常");

        // When
        ResponseEntity<ApiResponse> response = globalExceptionHandler.handleGenericException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("服务器内部错误: 状态异常", response.getBody().getMessage());
    }
} 