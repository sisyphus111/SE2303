package com.se2303.communityservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.se2303.communityservice.entity.Comment;
import com.se2303.communityservice.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class RedisConfigTest {

    private RedisConfig redisConfig;
    private RedisConnectionFactory redisConnectionFactory;

    @BeforeEach
    void setUp() {
        redisConfig = new RedisConfig();
        redisConnectionFactory = mock(RedisConnectionFactory.class);
    }

    @Test
    void testRestTemplateBean() {
        RestTemplate restTemplate = redisConfig.restTemplate();
        assertNotNull(restTemplate, "RestTemplate bean should not be null");
    }

    @Test
    void testCreateObjectMapper() {
        ObjectMapper objectMapper = redisConfig.createObjectMapper();
        assertNotNull(objectMapper, "ObjectMapper should not be null");
        assertTrue(objectMapper.findModules().stream().anyMatch(module -> module instanceof JavaTimeModule),
                "JavaTimeModule should be registered");
        assertTrue(objectMapper.findModules().stream().anyMatch(module -> module instanceof Hibernate6Module),
                "Hibernate6Module should be registered");
        assertEquals("yyyy-MM-dd HH:mm:ss", ((SimpleDateFormat) objectMapper.getDateFormat()).toPattern(),
                "Date format should be yyyy-MM-dd HH:mm:ss");
    }

    @Test
    void testRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = redisConfig.redisTemplate(redisConnectionFactory);
        assertNotNull(redisTemplate, "RedisTemplate should not be null");
        assertTrue(redisTemplate.getKeySerializer() instanceof StringRedisSerializer,
                "Key serializer should be StringRedisSerializer");
        assertTrue(redisTemplate.getValueSerializer() instanceof Jackson2JsonRedisSerializer,
                "Value serializer should be Jackson2JsonRedisSerializer");
        assertTrue(redisTemplate.getHashKeySerializer() instanceof StringRedisSerializer,
                "Hash key serializer should be StringRedisSerializer");
        assertTrue(redisTemplate.getHashValueSerializer() instanceof Jackson2JsonRedisSerializer,
                "Hash value serializer should be Jackson2JsonRedisSerializer");
    }

    @Test
    void testPostRedisTemplate() throws Exception {
        RedisTemplate<String, Post> postRedisTemplate = redisConfig.postRedisTemplate(redisConnectionFactory);
        assertNotNull(postRedisTemplate, "Post RedisTemplate should not be null");
        assertTrue(postRedisTemplate.getKeySerializer() instanceof StringRedisSerializer,
                "Post key serializer should be StringRedisSerializer");
        assertTrue(postRedisTemplate.getValueSerializer() instanceof Jackson2JsonRedisSerializer,
                "Post value serializer should be Jackson2JsonRedisSerializer");

        // 测试序列化/反序列化
        Jackson2JsonRedisSerializer<Post> serializer = (Jackson2JsonRedisSerializer<Post>) postRedisTemplate.getValueSerializer();
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setContent("Test Content");
        post.setUserId("user1");
        post.setCreatedAt(LocalDateTime.now());
        post.setReviewStatus("PENDING");
        post.setTags(new HashSet<>(Set.of("tag1", "tag2")));

        byte[] serialized = serializer.serialize(post);
        assertNotNull(serialized, "Serialized Post should not be null");
        Post deserialized = serializer.deserialize(serialized);
        assertNotNull(deserialized, "Deserialized Post should not be null");
        assertEquals(post.getId(), deserialized.getId(), "Post ID should match");
        assertEquals(post.getTitle(), deserialized.getTitle(), "Post title should match");
        assertEquals(post.getContent(), deserialized.getContent(), "Post content should match");
        assertEquals(post.getUserId(), deserialized.getUserId(), "Post userId should match");
        assertEquals(post.getReviewStatus(), deserialized.getReviewStatus(), "Post reviewStatus should match");
        assertEquals(post.getTags(), deserialized.getTags(), "Post tags should match");
    }

    @Test
    void testCommentRedisTemplate() throws Exception {
        RedisTemplate<String, Comment> commentRedisTemplate = redisConfig.commentRedisTemplate(redisConnectionFactory);
        assertNotNull(commentRedisTemplate, "Comment RedisTemplate should not be null");
        assertTrue(commentRedisTemplate.getKeySerializer() instanceof StringRedisSerializer,
                "Comment key serializer should be StringRedisSerializer");
        assertTrue(commentRedisTemplate.getValueSerializer() instanceof Jackson2JsonRedisSerializer,
                "Comment value serializer should be Jackson2JsonRedisSerializer");

        // 测试序列化/反序列化
        Jackson2JsonRedisSerializer<Comment> serializer = (Jackson2JsonRedisSerializer<Comment>) commentRedisTemplate.getValueSerializer();
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setPostId(1L);
        comment.setUserId("user1");
        comment.setContent("Test Comment");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setReviewStatus("PENDING");

        byte[] serialized = serializer.serialize(comment);
        assertNotNull(serialized, "Serialized Comment should not be null");
        Comment deserialized = serializer.deserialize(serialized);
        assertNotNull(deserialized, "Deserialized Comment should not be null");
        assertEquals(comment.getId(), deserialized.getId(), "Comment ID should match");
        assertEquals(comment.getPostId(), deserialized.getPostId(), "Comment postId should match");
        assertEquals(comment.getUserId(), deserialized.getUserId(), "Comment userId should match");
        assertEquals(comment.getContent(), deserialized.getContent(), "Comment content should match");
        assertEquals(comment.getReviewStatus(), deserialized.getReviewStatus(), "Comment reviewStatus should match");
    }
}