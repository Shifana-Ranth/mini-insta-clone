package com.miniinstagram.feed.model;

public class Post {
    private long id;
    private long authorId;
    private String content;
    private long timestamp;
    private int likeCount;

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    public Post() {}

    public Post(long id, long authorId, String content, long timestamp) {
        this.id = id;
        this.authorId = authorId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getAuthorId() { return authorId; }
    public void setAuthorId(long authorId) { this.authorId = authorId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}