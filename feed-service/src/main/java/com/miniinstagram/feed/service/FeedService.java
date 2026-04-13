package com.miniinstagram.feed.service;

import com.miniinstagram.feed.cache.RedisCache;
import com.miniinstagram.feed.model.Post;
import java.util.List;

public class FeedService {

	
    /** Push new post to all followers */
    public void pushNewPostToFollowers(Post post, List<Long> followerIds) {
//        // Save post to author's own post list
//        RedisCache.savePostForAuthor(post);
//
//        // Push post to all followers
//        for (Long followerId : followerIds) {
//            RedisCache.pushToFeed(followerId, post);
//        }
    	System.out.println("DEBUG: Post by " + post.getAuthorId() + " has " + (followerIds != null ? followerIds.size() : 0) + " followers in Redis.");
        
        // Save post to author's list so it can be preloaded later
        RedisCache.savePostForAuthor(post);

        if (followerIds == null || followerIds.isEmpty()) {
            System.out.println("DEBUG: No followers found in Redis. Skipping push.");
            return; 
        }

        for (Long followerId : followerIds) {
            RedisCache.pushToFeed(followerId, post);
        }
    }

    /** Get feed for a user */
    public List<Post> getUserFeed(long userId) {
        return RedisCache.getFeed(userId);
    }

    /** Remove a post from all followers' feeds */
    public void removePostFromFeeds(long postId) {
        RedisCache.removePostFromAllFeeds(postId);
        System.out.println("Post " + postId + " removed from all feeds");
    }

    /** Preload last N posts of followee when a new follow happens */
    public void preloadFolloweePosts(long followerId, long followeeId) {
        List<Post> lastPosts = RedisCache.getLastNPostsOfUser(followeeId, 5);
        for (Post post : lastPosts) {
            RedisCache.pushToFeed(followerId, post);
        }
        System.out.println("Preloaded last " + lastPosts.size() + " posts of followee " + followeeId + " for follower " + followerId);
    }

    /** Remove all posts of followee from follower's feed on unfollow */
    public void removeFolloweePosts(long followerId, long followeeId) {
        RedisCache.removePostsOfUserFromFeed(followerId, followeeId);
        System.out.println("Removed posts of followee " + followeeId + " from follower " + followerId);
    }

    public void addFollowing(long userId, long followeeId) {
        RedisCache.addFollowing(userId, followeeId);
    }

    public void removeFollowing(long userId, long followeeId) {
        RedisCache.removeFollowing(userId, followeeId);
    }
    /** Get followers of a user */
    public List<Long> getFollowers(long authorId) {
        return RedisCache.getFollowers(authorId);
    }

    /** Add follower */
    public void addFollower(long followeeId, long followerId) {
        RedisCache.addFollower(followeeId, followerId);
    }

    /** Remove follower */
    public void removeFollower(long followeeId, long followerId) {
        RedisCache.removeFollower(followeeId, followerId);
    }
}