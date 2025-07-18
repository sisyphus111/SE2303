package com.se2303.moodservice.dao.impl;

import com.se2303.moodservice.model.Mood;
import com.se2303.moodservice.model.MoodItem;
import com.se2303.moodservice.repository.MoodItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MoodItemDAOImplTest {
    private MoodItemRepository repo;
    private MoodItemDAOImpl dao;

    @BeforeEach
    void setUp() {
        repo = mock(MoodItemRepository.class);
        dao = new MoodItemDAOImpl();
        ReflectionTestUtils.setField(dao, "moodItemRepository", repo);
    }

    @Test
    void testSave() {
        MoodItem item = new MoodItem("happy");
        when(repo.save(item)).thenReturn(item);
        assertEquals(item, dao.save(item));
    }

    @Test
    void testFindById() {
        MoodItem item = new MoodItem("happy");
        when(repo.findById(1L)).thenReturn(Optional.of(item));
        assertEquals(item, dao.findById(1L));
    }

    @Test
    void testFindByMoodId() {
        when(repo.findByMoodId(1L)).thenReturn(Collections.emptyList());
        assertNotNull(dao.findByMoodId(1L));
    }

    @Test
    void testFindByLabel() {
        when(repo.findByLabel("happy")).thenReturn(Collections.emptyList());
        assertNotNull(dao.findByLabel("happy"));
    }

    @Test
    void testFindByLabelContainingIgnoreCase() {
        when(repo.findByLabelContainingIgnoreCase("happy")).thenReturn(Collections.emptyList());
        assertNotNull(dao.findByLabelContainingIgnoreCase("happy"));
    }

    @Test
    void testDeleteByMoodId() {
        dao.deleteByMoodId(1L);
        verify(repo).deleteByMoodId(1L);
    }

    @Test
    void testDeleteById() {
        dao.deleteById(1L);
        verify(repo).deleteById(1L);
    }

    @Test
    void testDeleteAll() {
        dao.deleteAll();
        verify(repo).deleteAll();
    }

    @Test
    void testFindAll() {
        when(repo.findAll()).thenReturn(Collections.emptyList());
        assertNotNull(dao.findAll());
    }

    @Test
    void testCountByLabel() {
        when(repo.countByLabel("happy")).thenReturn(2L);
        assertEquals(2L, dao.countByLabel("happy"));
    }

    @Test
    void testFindMostUsedLabels() {
        when(repo.findMostUsedLabels()).thenReturn(Collections.emptyList());
        assertNotNull(dao.findMostUsedLabels());
    }

    @Test
    void testFindByUserId() {
        when(repo.findByUserId(1L)).thenReturn(Collections.emptyList());
        assertNotNull(dao.findByUserId(1L));
    }

    @Test
    void testFindByUserIdAndDateBetween() {
        when(repo.findByUserIdAndDateBetween(1L, "2024-01-01", "2024-01-31")).thenReturn(Collections.emptyList());
        assertNotNull(dao.findByUserIdAndDateBetween(1L, "2024-01-01", "2024-01-31"));
    }

    @Test
    void testSaveAll() {
        List<MoodItem> items = Collections.emptyList();
        when(repo.saveAll(items)).thenReturn(items);
        assertEquals(items, dao.saveAll(items));
    }

    @Test
    void testExistsByLabel() {
        when(repo.countByLabel("happy")).thenReturn(1L);
        assertTrue(dao.existsByLabel("happy"));
    }

    @Test
    void testFindByMoodIdAndLabel() {
        MoodItem item = new MoodItem("happy");
        item.setId(1L);
        when(repo.findByMoodId(1L)).thenReturn(List.of(item));
        assertEquals(item, dao.findByMoodIdAndLabel(1L, "happy"));
    }
} 