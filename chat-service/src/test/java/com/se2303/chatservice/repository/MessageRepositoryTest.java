package com.se2303.chatservice.repository;

import com.se2303.chatservice.model.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class MessageRepositoryTest {
    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void testSaveAndFind() {
        Message msg = new Message(1L, "hello");
        Message saved = messageRepository.save(msg);
        assertNotNull(saved.getId());
        assertEquals("hello", saved.getContent());
    }

    @Test
    public void testFindAllByOrderByCreatedAtDesc() {
        Message msg1 = new Message(1L, "a");
        Message msg2 = new Message(2L, "b");
        messageRepository.save(msg1);
        messageRepository.save(msg2);
        List<Message> list = messageRepository.findAllByOrderByCreatedAtDesc();
        assertTrue(list.size() >= 2);
    }

    @Test
    public void testFindByValidTrueOrderByCreatedAtDesc() {
        Message msg1 = new Message(1L, "a", true);
        Message msg2 = new Message(2L, "b", false);
        messageRepository.save(msg1);
        messageRepository.save(msg2);
        List<Message> list = messageRepository.findByValidTrueOrderByCreatedAtDesc();
        assertTrue(list.stream().allMatch(Message::getValid));
    }
} 