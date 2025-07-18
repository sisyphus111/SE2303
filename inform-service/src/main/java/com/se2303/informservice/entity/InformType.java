package com.se2303.informservice.entity;

public enum InformType {
    COMMENT("评论"),
    LIKE("点赞");
    
    private final String description;
    
    InformType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
