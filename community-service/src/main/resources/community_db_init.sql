-- Creating database and tables for the community service application
CREATE DATABASE IF NOT EXISTS community;
USE community;

-- Creating table for Post entity
CREATE TABLE posts (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       title VARCHAR(255) NOT NULL,
                       content TEXT NOT NULL,
                       user_id VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       review_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                       CONSTRAINT chk_review_status CHECK (review_status IN ('PENDING', 'APPROVED', 'BANNED'))
);


-- Creating table for post_tags (ElementCollection)
CREATE TABLE post_tags (
                           post_id BIGINT NOT NULL,
                           tag_name VARCHAR(255) NOT NULL,
                           FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                           PRIMARY KEY (post_id, tag_name)
);

-- Creating table for Comment entity
CREATE TABLE comments (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          post_id BIGINT NOT NULL,
                          user_id VARCHAR(255) NOT NULL,
                          content TEXT NOT NULL,
                          created_at TIMESTAMP NOT NULL,
                          status VARCHAR(50) NOT NULL,
                          review_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                          FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                          CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'DELETED')),
                          CONSTRAINT chk_comment_review_status CHECK (review_status IN ('PENDING', 'APPROVED', 'BANNED'))
);

-- Creating table for Report entity
CREATE TABLE reports (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         post_id BIGINT,
                         comment_id BIGINT,
                         type VARCHAR(50) NOT NULL,
                         user_id VARCHAR(255) NOT NULL,
                         reason TEXT NOT NULL,
                         detail TEXT NOT NULL,
                         status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                         created_at TIMESTAMP NOT NULL,
                         FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                         FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,
                         CONSTRAINT chk_report_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

-- Creating table for Like entity
CREATE TABLE likes (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       post_id BIGINT NOT NULL,
                       user_id VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                       CONSTRAINT idx_post_user UNIQUE (post_id, user_id)
);
