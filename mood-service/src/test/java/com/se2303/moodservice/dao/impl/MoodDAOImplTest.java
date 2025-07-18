package com.se2303.moodservice.dao.impl;

import com.se2303.moodservice.model.Mood;
import com.se2303.moodservice.repository.MoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MoodDAOImplTest {
    private MoodRepository repo;
    private MoodDAOImpl dao;

    @BeforeEach
    void setUp() {
        repo = mock(MoodRepository.class);
        dao = new MoodDAOImpl();
        ReflectionTestUtils.setField(dao, "moodRepository", repo);
    }

    @Test
    void testSave() {
        Mood mood = new Mood();
        when(repo.save(mood)).thenReturn(mood);
        assertEquals(mood, dao.save(mood));
    }

    @Test
    void testFindById() {
        Mood mood = new Mood();
        when(repo.findById(1L)).thenReturn(Optional.of(mood));
        assertTrue(dao.findById(1L).isPresent());
    }

    @Test
    void testFindByUserIdAndDate() {
        Mood mood = new Mood();
        when(repo.findByUserIdAndDate("u", LocalDate.MIN)).thenReturn(Optional.of(mood));
        assertTrue(dao.findByUserIdAndDate("u", LocalDate.MIN).isPresent());
    }

    @Test
    void testFindByUserIdOrderByDateDesc() {
        when(repo.findByUserIdOrderByDateDesc("u")).thenReturn(Collections.emptyList());
        assertNotNull(dao.findByUserIdOrderByDateDesc("u"));
    }

    @Test
    void testFindByUserIdAndDateBetweenOrderByDateDesc() {
        when(repo.findByUserIdAndDateBetweenOrderByDateDesc("u", LocalDate.MIN, LocalDate.MAX)).thenReturn(Collections.emptyList());
        assertNotNull(dao.findByUserIdAndDateBetweenOrderByDateDesc("u", LocalDate.MIN, LocalDate.MAX));
    }

    @Test
    void testExistsByUserIdAndDate() {
        when(repo.existsByUserIdAndDate("u", LocalDate.MIN)).thenReturn(true);
        assertTrue(dao.existsByUserIdAndDate("u", LocalDate.MIN));
    }

    @Test
    void testCountByUserId() {
        when(repo.countByUserId("u")).thenReturn(5L);
        assertEquals(5L, dao.countByUserId("u"));
    }

    @Test
    void testCountByUserIdAndDateBetween() {
        when(repo.countByUserIdAndDateBetween("u", LocalDate.MIN, LocalDate.MAX)).thenReturn(3L);
        assertEquals(3L, dao.countByUserIdAndDateBetween("u", LocalDate.MIN, LocalDate.MAX));
    }

    @Test
    void testFindByUserIdAndScoreCommittedTrueOrderByDateDesc() {
        when(repo.findByUserIdAndScoreCommittedTrueOrderByDateDesc("u")).thenReturn(Collections.emptyList());
        assertNotNull(dao.findByUserIdAndScoreCommittedTrueOrderByDateDesc("u"));
    }

    @Test
    void testFindByUserIdAndDiaryCommittedTrueOrderByDateDesc() {
        when(repo.findByUserIdAndDiaryCommittedTrueOrderByDateDesc("u")).thenReturn(Collections.emptyList());
        assertNotNull(dao.findByUserIdAndDiaryCommittedTrueOrderByDateDesc("u"));
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
    void testFindAllByOrderByDateDesc() {
        when(repo.findAllByOrderByDateDesc()).thenReturn(Collections.emptyList());
        assertNotNull(dao.findAllByOrderByDateDesc());
    }

    @Test
    void testHasCheckedIn() {
        Mood mood = new Mood();
        mood.setScoreCommitted(true);
        when(repo.findByUserIdAndDate("u", LocalDate.MIN)).thenReturn(Optional.of(mood));
        assertTrue(dao.hasCheckedIn("u", LocalDate.MIN));
    }

    @Test
    void testHasWrittenDiary() {
        Mood mood = new Mood();
        mood.setDiaryCommitted(true);
        when(repo.findByUserIdAndDate("u", LocalDate.MIN)).thenReturn(Optional.of(mood));
        assertTrue(dao.hasWrittenDiary("u", LocalDate.MIN));
    }
} 