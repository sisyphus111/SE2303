package com.se2303.communityservice.config;

import com.se2303.communityservice.response.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input");
        ResponseEntity<ApiResponse> response = exceptionHandler.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.isSuccess());
        assertNull(apiResponse.getObject());
        assertEquals("Invalid input", apiResponse.getMessage());
    }

    @Test
    void testHandleIllegalStateException() {
        IllegalStateException ex = new IllegalStateException("Illegal state");
        ResponseEntity<ApiResponse> response = exceptionHandler.handleIllegalStateException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.isSuccess());
        assertNull(apiResponse.getObject());
        assertEquals("Illegal state", apiResponse.getMessage());
    }

    @Test
    void testHandleOptimisticLockingFailure() {
        OptimisticLockException lockEx = new OptimisticLockException("Optimistic locking failure");
        JpaOptimisticLockingFailureException ex = new JpaOptimisticLockingFailureException(lockEx);
        ResponseEntity<ApiResponse> response = exceptionHandler.handleOptimisticLockingFailure(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.isSuccess());
        assertNull(apiResponse.getObject());
        assertEquals("并发更新冲突，请重试", apiResponse.getMessage());
    }

    @Test
    void testHandleEntityNotFoundException() {
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");
        ResponseEntity<ApiResponse> response = exceptionHandler.handleEntityNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.isSuccess());
        assertNull(apiResponse.getObject());
        assertEquals("Entity not found", apiResponse.getMessage());
    }

    @Test
    void testHandleGeneralException() {
        Exception ex = new Exception("Unexpected error");
        ResponseEntity<ApiResponse> response = exceptionHandler.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiResponse apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.isSuccess());
        assertNull(apiResponse.getObject());
        assertEquals("服务器内部错误", apiResponse.getMessage());
    }
}