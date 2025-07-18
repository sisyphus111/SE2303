package com.se2303.moodservice.dao;

import com.se2303.moodservice.model.Mood;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MoodDAOTest {
    @Test
    void testFindByUserIdOrderByDateDesc() {
        MoodDAO dao = mock(MoodDAO.class);
        Mood mood = new Mood();
        mood.setId(1L);
        when(dao.findByUserIdOrderByDateDesc("user1")).thenReturn(Collections.singletonList(mood));
        List<Mood> result = dao.findByUserIdOrderByDateDesc("user1");
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
} 