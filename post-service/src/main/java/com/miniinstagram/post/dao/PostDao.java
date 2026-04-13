package com.miniinstagram.post.dao;

import com.miniinstagram.post.model.Post;
import com.miniinstagram.post.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDao {

    public long createPost(Post post) {
        String postQuery = "INSERT INTO posts(user_id, content, media_url) VALUES (?, ?, ?)";
        String metaQuery = "INSERT INTO post_metadata(post_id, likes_count) VALUES (?, 0)";

        long postId=-1;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement postStmt = conn.prepareStatement(postQuery, Statement.RETURN_GENERATED_KEYS)) {

            postStmt.setLong(1, post.getUserId());
            postStmt.setString(2, post.getContent());
            postStmt.setString(3, post.getMediaUrl());

            int rows = postStmt.executeUpdate();

            if (rows > 0) {

                ResultSet rs = postStmt.getGeneratedKeys();
                if (rs.next()) {
                    postId = rs.getLong(1);

                    PreparedStatement metaStmt = conn.prepareStatement(metaQuery);
                    metaStmt.setLong(1, postId);
                    metaStmt.executeUpdate();
                }

                return postId;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public List<Post> getTrendingPosts(int limit) {

        List<Post> posts = new ArrayList<>();

        String query = "SELECT p.id, p.user_id, p.content, m.likes_count " +
                       "FROM posts p " +
                       "LEFT JOIN post_metadata m ON p.id = m.post_id " +
                       "ORDER BY m.likes_count DESC " +
                       "LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, limit);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Post post = new Post();

                post.setId(rs.getLong("id"));
                post.setUserId(rs.getLong("user_id"));
                post.setContent(rs.getString("content"));
                post.setLikesCount(rs.getInt("likes_count"));

                posts.add(post);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posts;
    }
    public List<Post> getPostsByUser(long userId) {

        List<Post> posts = new ArrayList<>();

        String query = "SELECT p.*, m.likes_count " +
                "FROM posts p LEFT JOIN post_metadata m " +
                "ON p.id = m.post_id " +
                "WHERE p.user_id = ? ORDER BY p.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Post post = new Post();

                post.setId(rs.getLong("id"));
                post.setUserId(rs.getLong("user_id"));
                post.setContent(rs.getString("content"));
                post.setMediaUrl(rs.getString("media_url"));
                post.setCreatedAt(rs.getTimestamp("created_at"));
                post.setLikesCount(rs.getInt("likes_count"));

                posts.add(post);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return posts;
    }

    public List<Post> getPostsByUserIds(List<Long> userIds) {

        List<Post> posts = new ArrayList<>();

        if (userIds == null || userIds.isEmpty()) return posts;

        StringBuilder query = new StringBuilder(
            "SELECT * FROM posts WHERE user_id IN ("
        );

        for (int i = 0; i < userIds.size(); i++) {
            query.append("?");
            if (i < userIds.size() - 1) {
                query.append(",");
            }
        }

        query.append(") ORDER BY created_at DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < userIds.size(); i++) {
                ps.setLong(i + 1, userIds.get(i));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Post p = new Post();
                p.setId(rs.getLong("id"));
                p.setUserId(rs.getLong("user_id"));
                p.setContent(rs.getString("content"));
                p.setLikesCount(rs.getInt("likes_count"));
                posts.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return posts;
    }
    public boolean deletePost(long postId) {
        String deleteMetadata = "DELETE FROM post_metadata WHERE post_id=?";
        String deletePost = "DELETE FROM posts WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psMeta = conn.prepareStatement(deleteMetadata);
             PreparedStatement psPost = conn.prepareStatement(deletePost)) {

            // Delete metadata first
            psMeta.setLong(1, postId);
            psMeta.executeUpdate();

            // Delete post
            psPost.setLong(1, postId);
            int rows = psPost.executeUpdate();

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void decrementLikeCount(long postId){

        try{

            Connection conn = DBConnection.getConnection();

            String sql = "UPDATE post_metadata SET likes_count = GREATEST(likes_count - 1, 0) WHERE post_id=?";

            //String sql = "UPDATE post_metadata SET like_count = like_count - 1 WHERE post_id=?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, postId);

            stmt.executeUpdate();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public int getPostCountByUser(long userId) {

        int count = 0;

        String query = "SELECT COUNT(*) FROM posts WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
    public void incrementLikeCount(long postId){

        try{

            Connection conn = DBConnection.getConnection();

            String sql = "UPDATE post_metadata SET likes_count = likes_count + 1 WHERE post_id=?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, postId);

            stmt.executeUpdate();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}