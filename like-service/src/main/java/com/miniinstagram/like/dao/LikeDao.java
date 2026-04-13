package com.miniinstagram.like.dao;

import com.miniinstagram.like.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class LikeDao {

    public boolean isAlreadyLiked(long userId, long postId) {
        String sql = "SELECT id FROM likes WHERE user_id=? AND post_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, postId);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Integer> getLikedPostIdsByUser(int userId) {

        List<Integer> postIds = new ArrayList<>();

        String sql = "SELECT post_id FROM likes WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                postIds.add(rs.getInt("post_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return postIds;
    }
    public boolean addLike(long userId, long postId) {
        String sql = "INSERT INTO likes(user_id, post_id) VALUES(?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, postId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean removeLike(long userId, long postId) {
        String sql = "DELETE FROM likes WHERE user_id=? AND post_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, postId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}