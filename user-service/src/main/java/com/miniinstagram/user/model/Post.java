package com.miniinstagram.user.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {
    private long id;
    @JsonAlias("userId") 
    private long authorId;
    private String content;
    private long timestamp;
    @JsonAlias({"likes_count", "likesCount", "likeCount"})
    //@JsonAlias({"likes_count","likesCount"})
    private int likesCount;
    private boolean likedByCurrentUser;
    private String username;
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }
    public Post() {}

    public Post(long id, long authorId, String content, long timestamp) {
        this.id = id;
        this.authorId = authorId;
        this.content = content;
        this.timestamp = timestamp;
    }
    //@JsonProperty("likesCount")
    public int getLikesCount() { return likesCount; }   
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; } 
    
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getAuthorId() { return authorId; }
    public void setAuthorId(long authorId) { this.authorId = authorId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}