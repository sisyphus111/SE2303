package com.se2303.communityservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.Set;

@Document(indexName = "posts")
public class PostDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Keyword)
    private String userId;

    @Field(type = FieldType.Keyword)
    private Set<String> tags;

    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS||uuuu-MM-dd HH:mm:ss||uuuu-MM-dd||epoch_millis")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Field(type = FieldType.Keyword)
    private String reviewStatus;

    // 构造函数
    public PostDocument() {}

    public PostDocument(String id, String title, String content, String userId, Set<String> tags, LocalDateTime createdAt, String reviewStatus) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.tags = tags;
        this.createdAt = createdAt;
        this.reviewStatus = reviewStatus;
    }

    // 从Post实体转换为PostDocument
    public static PostDocument fromPost(Post post) {
        return new PostDocument(
            post.getId().toString(),
            post.getTitle(),
            post.getContent(),
            post.getUserId(),
            post.getTags(),
            post.getCreatedAt(),
            post.getReviewStatus()
        );
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    @Override
    public String toString() {
        return "PostDocument{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content.length=" + (content != null ? content.length() : 0) +
                ", userId='" + userId + '\'' +
                ", tags=" + tags +
                ", createdAt=" + createdAt +
                ", reviewStatus='" + reviewStatus + '\'' +
                '}';
    }
} 