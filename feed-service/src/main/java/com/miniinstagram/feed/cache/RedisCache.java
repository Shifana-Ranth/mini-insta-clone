package com.miniinstagram.feed.cache;
import java.util.*;
import com.miniinstagram.feed.model.Post;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

public class RedisCache {

    private static final JedisPool jedisPool = new JedisPool("localhost", 6379);
    private static final ObjectMapper mapper = new ObjectMapper();

    /** Push post to a user's feed */
    public static void pushToFeed(Long userId, Post post) {
        System.out.println(">>> ATTEMPTING REDIS PUSH for User: " + userId); // Add this!
        try (Jedis jedis = jedisPool.getResource()) {
            System.out.println(">>> CONNECTION SUCCESSFUL"); // Add this!
            String key = "feed:user:" + userId;
            String postJson = mapper.writeValueAsString(post);
            jedis.lpush(key, postJson);
            jedis.ltrim(key, 0, 49);
            System.out.println(">>> REDIS WRITE DONE: " + key); // Add this!
        } catch (Exception e) {
            System.out.println(">>> REDIS ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Save post to author's own post list (for preloading after follow) */
    public static void savePostForAuthor(Post post) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "posts:user:" + post.getAuthorId();
            String postJson = mapper.writeValueAsString(post);
            System.out.println("DEBUG REDIS: Writing to key " + key + " value: " + postJson);
            jedis.lpush(key, postJson);
            jedis.ltrim(key, 0, 49); // keep last 50 posts
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Get feed for a user */
    public static List<Post> getFeed(Long userId) {
        List<Post> feed = new ArrayList<>();
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "feed:user:" + userId;
            List<String> postJsons = jedis.lrange(key, 0, -1);
            for (String json : postJsons) {
                feed.add(mapper.readValue(json, Post.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feed;
    }

    /** Remove a post from all users' feeds */
    public static void removePostFromAllFeeds(Long postId) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> keys = new ArrayList<>(jedis.keys("feed:user:*"));
            for (String key : keys) {
                List<String> posts = jedis.lrange(key, 0, -1);
                for (String postJson : posts) {
                    Post post = mapper.readValue(postJson, Post.class);
                    if (post.getId() == postId) {
                        jedis.lrem(key, 0, postJson);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Get last N posts of a user (for preload after follow) */
    public static List<Post> getLastNPostsOfUser(Long userId, int n) {
        List<Post> posts = new ArrayList<>();
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "posts:user:" + userId;
            List<String> postJsons = jedis.lrange(key, 0, n - 1);
            for (String json : postJsons) {
                posts.add(mapper.readValue(json, Post.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    /** Remove all posts of a followee from a follower's feed (on unfollow) */
    public static void removePostsOfUserFromFeed(Long followerId, Long followeeId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "feed:user:" + followerId;
            List<String> feedPosts = jedis.lrange(key, 0, -1);
            for (String postJson : feedPosts) {
                Post post = mapper.readValue(postJson, Post.class);
                if (post.getAuthorId() == followeeId) {
                    jedis.lrem(key, 0, postJson);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Store followers of a user */
    public static void addFollower(Long userId, Long followerId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "followers:user:" + userId;
            jedis.sadd(key, String.valueOf(followerId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Remove follower (on unfollow) */
    public static void removeFollower(Long userId, Long followerId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "followers:user:" + userId;
            jedis.srem(key, String.valueOf(followerId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void addFollowing(Long userId, Long followeeId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "following:user:" + userId;
            jedis.sadd(key, String.valueOf(followeeId));
        }
    }
    public static void removeFollowing(Long userId, Long followeeId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "following:user:" + userId;
            jedis.srem(key, String.valueOf(followeeId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Set<String> getFollowing(Long userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers("following:user:" + userId);
        }
    }
    /** Get all followers of a user */
    public static List<Long> getFollowers(Long userId) {
        List<Long> followers = new ArrayList<>();
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "followers:user:" + userId;
            for (String id : jedis.smembers(key)) {
                followers.add(Long.parseLong(id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return followers;
    }
    public static void updateLikeCount(Long postId, boolean increment) {

        try (Jedis jedis = jedisPool.getResource()) {

            List<String> keys = new ArrayList<>(jedis.keys("feed:user:*"));

            for (String key : keys) {

                List<String> posts = jedis.lrange(key, 0, -1);

                for (String postJson : posts) {

                    Post post = mapper.readValue(postJson, Post.class);

                    if(post.getId() == postId) {

                        if (increment) {
                            post.setLikeCount(post.getLikeCount() + 1);
                        } else {
                            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                        }

                        String updatedJson = mapper.writeValueAsString(post);

                        jedis.lrem(key, 0, postJson);
                        jedis.lpush(key, updatedJson);

                        System.out.println("Feed cache updated for post " + postId + " in " + key);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Close the pool (optional, on shutdown) */
    public static void close() {
        jedisPool.close();
    }
}