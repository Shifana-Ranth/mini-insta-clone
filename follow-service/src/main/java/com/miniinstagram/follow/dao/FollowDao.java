package com.miniinstagram.follow.dao;

import com.miniinstagram.follow.model.Follow;
import com.miniinstagram.follow.util.DBConnection;

import java.sql.*;

public class FollowDao {

    private Connection conn;

    public FollowDao() {
        conn = DBConnection.getConnection();
    }

    // Insert follow record
    public boolean followUser(Follow follow) {
        String sql = "INSERT INTO followers (user_id, follower_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, follow.getUserId());
            ps.setLong(2, follow.getFollowerId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete unfollow record
    public boolean unfollowUser(Follow follow) {
        String sql = "DELETE FROM followers WHERE user_id=? AND follower_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, follow.getUserId());
            ps.setLong(2, follow.getFollowerId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean followUser(long userId, long followerId){
        return followUser(new Follow(userId, followerId));
    }

    public boolean unfollowUser(long userId, long followerId){
        return unfollowUser(new Follow(userId, followerId));
    }
    // Check if already following
    public boolean isFollowing(Follow follow) {
        String sql = "SELECT * FROM followers WHERE user_id=? AND follower_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, follow.getUserId());
            ps.setLong(2, follow.getFollowerId());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}