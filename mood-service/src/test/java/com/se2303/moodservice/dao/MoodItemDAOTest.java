package com.se2303.moodservice.dao;

import com.se2303.moodservice.model.MoodItem;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MoodItemDAOTest {
    @Test
    void testFindByMoodId() {
        MoodItemDAO dao = mock(MoodItemDAO.class);
        MoodItem item = new MoodItem();
        item.setId(1L);
        when(dao.findByMoodId(1L)).thenReturn(Collections.singletonList(item));
        List<MoodItem> result = dao.findByMoodId(1L);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
} 