package com.miniinstagram.post.model;

import java.sql.Timestamp;

public class Post {

    private long id;
    private long userId;
    private String content;
    private String mediaUrl;
    private Timestamp createdAt;
    private int likesCount;

    public Post() {
    }

    public Post(long userId, String content, String mediaUrl) {
        this.userId = userId;
        this.content = content;
        this.mediaUrl = mediaUrl;
    }

    public Post(long id, long userId, String content, String mediaUrl, Timestamp createdAt, int likesCount) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.createdAt = createdAt;
        this.likesCount = likesCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
}