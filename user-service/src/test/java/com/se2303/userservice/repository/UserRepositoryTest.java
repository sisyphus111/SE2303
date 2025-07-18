package com.se2303.userservice.repository;

import com.se2303.userservice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {
    @Mock
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(123L);
        user.setNickname("test");
    }

    @Test
    void testFindById() {
        when(userRepository.findById(123L)).thenReturn(java.util.Optional.of(user));
        assertTrue(userRepository.findById(123L).isPresent());
        verify(userRepository, times(1)).findById(123L);
    }

    @Test
    void testSave() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        assertNotNull(userRepository.save(user));
        verify(userRepository, times(1)).save(any(User.class));
    }
} 