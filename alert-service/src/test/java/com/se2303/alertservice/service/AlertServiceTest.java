package com.se2303.alertservice.service;

import com.se2303.alertservice.dto.AlertResponse;
import com.se2303.alertservice.dto.CreateAlertRequest;
import com.se2303.alertservice.model.Alert;
import com.se2303.alertservice.model.AlertLevel;
import com.se2303.alertservice.model.AlertStatus;
import com.se2303.alertservice.model.AlertType;
import com.se2303.alertservice.repository.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    private Alert testAlert;
    private CreateAlertRequest createRequest;

    @BeforeEach
    void setUp() {
        testAlert = new Alert();
        testAlert.setId(1L);
        testAlert.setUserId("user123");
        testAlert.setAlertType(AlertType.EMOTIONAL_CRISIS);
        testAlert.setAlertLevel(AlertLevel.HIGH);
        testAlert.setDescription("Test alert");
        testAlert.setStatus(AlertStatus.PENDING);
        testAlert.setCreatedAt(LocalDateTime.now());

        createRequest = new CreateAlertRequest();
        createRequest.setUserId("user123");
        createRequest.setAlertType(AlertType.EMOTIONAL_CRISIS);
        createRequest.setAlertLevel(AlertLevel.HIGH);
        createRequest.setDescription("Test alert");
        createRequest.setOperationDetails("Test operation");
        createRequest.setAiSessionId("session123");
        createRequest.setAiTimeoutDuration(30);
    }

    @Test
    void testCreateAlert_Success() {
        // Arrange
        when(alertRepository.save(any(Alert.class))).thenReturn(testAlert);

        // Act
        AlertResponse response = alertService.createAlert(createRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testAlert.getId(), response.getId());
        assertEquals(testAlert.getUserId(), response.getUserId());
        assertEquals(testAlert.getAlertType(), response.getAlertType());
        assertEquals(testAlert.getAlertLevel(), response.getAlertLevel());
        assertEquals(testAlert.getDescription(), response.getDescription());
        assertEquals(AlertStatus.PENDING, response.getStatus());

        verify(alertRepository, times(1)).save(any(Alert.class));
    }

    @Test
    void testFindById_Exists() {
        // Arrange
        when(alertRepository.findById(1L)).thenReturn(Optional.of(testAlert));

        // Act
        AlertResponse response = alertService.findById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(testAlert.getId(), response.getId());
        assertEquals(testAlert.getUserId(), response.getUserId());

        verify(alertRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotExists() {
        // Arrange
        when(alertRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        AlertResponse response = alertService.findById(999L);

        // Assert
        assertNull(response);

        verify(alertRepository, times(1)).findById(999L);
    }

    @Test
    void testFindByUserId_Success() {
        // Arrange
        List<Alert> alerts = Arrays.asList(testAlert);
        when(alertRepository.findByUserIdOrderByCreatedAtDesc("user123")).thenReturn(alerts);

        // Act
        List<AlertResponse> responses = alertService.findByUserId("user123");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testAlert.getId(), responses.get(0).getId());

        verify(alertRepository, times(1)).findByUserIdOrderByCreatedAtDesc("user123");
    }

    @Test
    void testFindByUserId_Empty() {
        // Arrange
        when(alertRepository.findByUserIdOrderByCreatedAtDesc("user123")).thenReturn(Arrays.asList());

        // Act
        List<AlertResponse> responses = alertService.findByUserId("user123");

        // Assert
        assertNotNull(responses);
        assertEquals(0, responses.size());

        verify(alertRepository, times(1)).findByUserIdOrderByCreatedAtDesc("user123");
    }

    @Test
    void testFindByStatus_Success() {
        // Arrange
        List<Alert> alerts = Arrays.asList(testAlert);
        when(alertRepository.findByStatusOrderByCreatedAtDesc(AlertStatus.PENDING)).thenReturn(alerts);

        // Act
        List<AlertResponse> responses = alertService.findByStatus(AlertStatus.PENDING);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testAlert.getId(), responses.get(0).getId());

        verify(alertRepository, times(1)).findByStatusOrderByCreatedAtDesc(AlertStatus.PENDING);
    }

    @Test
    void testGetPendingAlerts_Success() {
        // Arrange
        List<Alert> alerts = Arrays.asList(testAlert);
        when(alertRepository.findPendingAlerts()).thenReturn(alerts);

        // Act
        List<AlertResponse> responses = alertService.getPendingAlerts();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testAlert.getId(), responses.get(0).getId());

        verify(alertRepository, times(1)).findPendingAlerts();
    }

    @Test
    void testFindByCounselorId_Success() {
        // Arrange
        List<Alert> alerts = Arrays.asList(testAlert);
        when(alertRepository.findByAssignedCounselorIdOrderByCreatedAtDesc("counselor123")).thenReturn(alerts);

        // Act
        List<AlertResponse> responses = alertService.findByCounselorId("counselor123");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testAlert.getId(), responses.get(0).getId());

        verify(alertRepository, times(1)).findByAssignedCounselorIdOrderByCreatedAtDesc("counselor123");
    }

    @Test
    void testAssignAlert_Success() {
        // Arrange
        when(alertRepository.findById(1L)).thenReturn(Optional.of(testAlert));
        when(alertRepository.save(any(Alert.class))).thenReturn(testAlert);

        // Act
        AlertResponse response = alertService.assignAlert(1L, "counselor123");

        // Assert
        assertNotNull(response);
        assertEquals(testAlert.getId(), response.getId());
        assertEquals("counselor123", testAlert.getAssignedCounselorId());
        assertEquals(AlertStatus.ASSIGNED, testAlert.getStatus());
        assertNotNull(testAlert.getUpdatedAt());

        verify(alertRepository, times(1)).findById(1L);
        verify(alertRepository, times(1)).save(testAlert);
    }

    @Test
    void testAssignAlert_AlertNotFound() {
        // Arrange
        when(alertRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        AlertResponse response = alertService.assignAlert(999L, "counselor123");

        // Assert
        assertNull(response);

        verify(alertRepository, times(1)).findById(999L);
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    void testUpdateAlertStatus_Success() {
        // Arrange
        when(alertRepository.findById(1L)).thenReturn(Optional.of(testAlert));
        when(alertRepository.save(any(Alert.class))).thenReturn(testAlert);

        // Act
        AlertResponse response = alertService.updateAlertStatus(1L, AlertStatus.IN_PROGRESS);

        // Assert
        assertNotNull(response);
        assertEquals(testAlert.getId(), response.getId());
        assertEquals(AlertStatus.IN_PROGRESS, testAlert.getStatus());
        assertNotNull(testAlert.getUpdatedAt());

        verify(alertRepository, times(1)).findById(1L);
        verify(alertRepository, times(1)).save(testAlert);
    }

    @Test
    void testUpdateAlertStatus_AlertNotFound() {
        // Arrange
        when(alertRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        AlertResponse response = alertService.updateAlertStatus(999L, AlertStatus.IN_PROGRESS);

        // Assert
        assertNull(response);

        verify(alertRepository, times(1)).findById(999L);
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    void testAddFollowUpNotes_Success() {
        // Arrange
        when(alertRepository.findById(1L)).thenReturn(Optional.of(testAlert));
        when(alertRepository.save(any(Alert.class))).thenReturn(testAlert);

        // Act
        AlertResponse response = alertService.addFollowUpNotes(1L, "Test follow-up notes");

        // Assert
        assertNotNull(response);
        assertEquals(testAlert.getId(), response.getId());
        assertEquals("Test follow-up notes", testAlert.getFollowUpNotes());
        assertNotNull(testAlert.getFollowUpDate());
        assertNotNull(testAlert.getUpdatedAt());

        verify(alertRepository, times(1)).findById(1L);
        verify(alertRepository, times(1)).save(testAlert);
    }

    @Test
    void testAddFollowUpNotes_AlertNotFound() {
        // Arrange
        when(alertRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        AlertResponse response = alertService.addFollowUpNotes(999L, "Test follow-up notes");

        // Assert
        assertNull(response);

        verify(alertRepository, times(1)).findById(999L);
        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    void testFindAlertsByConditions_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Alert> alertPage = new PageImpl<>(Arrays.asList(testAlert), pageable, 1);
        when(alertRepository.findByConditions(anyString(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(alertPage);

        // Act
        Page<AlertResponse> responsePage = alertService.findAlertsByConditions(
                "user123", AlertStatus.PENDING, AlertType.EMOTIONAL_CRISIS, AlertLevel.HIGH, pageable);

        // Assert
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals(1, responsePage.getContent().size());
        assertEquals(testAlert.getId(), responsePage.getContent().get(0).getId());

        verify(alertRepository, times(1)).findByConditions(
                "user123", AlertStatus.PENDING, AlertType.EMOTIONAL_CRISIS, AlertLevel.HIGH, pageable);
    }

    @Test
    void testGetAlertStatistics_Success() {
        // Arrange
        when(alertRepository.count()).thenReturn(100L);
        when(alertRepository.countByStatus(AlertStatus.PENDING)).thenReturn(20L);
        when(alertRepository.countByStatus(AlertStatus.ASSIGNED)).thenReturn(30L);
        when(alertRepository.countByStatus(AlertStatus.IN_PROGRESS)).thenReturn(25L);
        when(alertRepository.countByStatus(AlertStatus.COMPLETED)).thenReturn(25L);

        // Act
        AlertService.AlertStatistics stats = alertService.getAlertStatistics();

        // Assert
        assertNotNull(stats);
        assertEquals(100L, stats.getTotalAlerts());
        assertEquals(20L, stats.getPendingAlerts());
        assertEquals(30L, stats.getAssignedAlerts());
        assertEquals(25L, stats.getInProgressAlerts());
        assertEquals(25L, stats.getCompletedAlerts());

        verify(alertRepository, times(1)).count();
        verify(alertRepository, times(1)).countByStatus(AlertStatus.PENDING);
        verify(alertRepository, times(1)).countByStatus(AlertStatus.ASSIGNED);
        verify(alertRepository, times(1)).countByStatus(AlertStatus.IN_PROGRESS);
        verify(alertRepository, times(1)).countByStatus(AlertStatus.COMPLETED);
    }

    @Test
    void testDeleteAlert_Success() {
        // Arrange
        when(alertRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = alertService.deleteAlert(1L);

        // Assert
        assertTrue(result);

        verify(alertRepository, times(1)).existsById(1L);
        verify(alertRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAlert_NotExists() {
        // Arrange
        when(alertRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = alertService.deleteAlert(999L);

        // Assert
        assertFalse(result);

        verify(alertRepository, times(1)).existsById(999L);
        verify(alertRepository, never()).deleteById(anyLong());
    }

    @Test
    void testAlertStatistics_SettersAndGetters() {
        // Arrange
        AlertService.AlertStatistics stats = new AlertService.AlertStatistics();

        // Act & Assert
        stats.setTotalAlerts(100L);
        assertEquals(100L, stats.getTotalAlerts());

        stats.setPendingAlerts(20L);
        assertEquals(20L, stats.getPendingAlerts());

        stats.setAssignedAlerts(30L);
        assertEquals(30L, stats.getAssignedAlerts());

        stats.setInProgressAlerts(25L);
        assertEquals(25L, stats.getInProgressAlerts());

        stats.setCompletedAlerts(25L);
        assertEquals(25L, stats.getCompletedAlerts());
    }
} 