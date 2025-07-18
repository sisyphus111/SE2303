package com.se2303.informservice.scheduler;

import com.se2303.informservice.service.InformService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InformSchedulerTest {

    @Mock
    private InformService informService;

    @InjectMocks
    private InformScheduler informScheduler;

    @Test
    void testCleanupExpiredInformsSuccess() {
        informScheduler.cleanupExpiredInforms();

        verify(informService, times(1)).cleanupExpiredInforms();
        verifyNoMoreInteractions(informService);
    }

    @Test
    void testCleanupExpiredInformsWithException() {
        doThrow(new RuntimeException("Test exception")).when(informService).cleanupExpiredInforms();

        informScheduler.cleanupExpiredInforms();

        verify(informService, times(1)).cleanupExpiredInforms();
        verifyNoMoreInteractions(informService);
    }
}