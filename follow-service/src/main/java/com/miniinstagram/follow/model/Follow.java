package com.miniinstagram.follow.model;

import java.sql.Timestamp;

public class Follow {
    private long id;
    private long userId;      // User being followed
    private long followerId;  // User who follows
    private Timestamp createdAt;

    public Follow() {}

    public Follow(long userId, long followerId) {
        this.userId = userId;
        this.followerId = followerId;
    }

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getFollowerId() { return followerId; }
    public void setFollowerId(long followerId) { this.followerId = followerId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}