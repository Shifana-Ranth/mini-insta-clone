package com.miniinstagram.user.dao;

import com.miniinstagram.user.model.User;
import java.util.*;
import com.miniinstagram.user.util.DBConnection;

import java.sql.*;

public class UserDao {

    public boolean createUser(User user) throws SQLException {

        String sql = "INSERT INTO users(username,password,status) VALUES(?,?,?)";

        Connection conn = DBConnection.getConnection();

        PreparedStatement stmt =
                conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPassword());
        stmt.setString(3, "PENDING");

        int rows = stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();

        if(rs.next()){
            user.setId(rs.getLong(1));
            System.out.println("Generated User ID: " + user.getId()); // Debug log
        }

        return rows > 0;
    }


    public User getUserByUsername(String username) throws SQLException {

        String sql = "SELECT * FROM users WHERE username=?";

        Connection conn = DBConnection.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setString(1, username);

        ResultSet rs = stmt.executeQuery();

        if(rs.next()){

            User user = new User();

            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            user.setDescription(rs.getString("description"));
            user.setProfilePhotoUrl(rs.getString("profile_photo_url"));
            user.setFollowerCount(rs.getInt("follower_count"));
            user.setFollowingCount(rs.getInt("following_count"));
            user.setStatus(rs.getString("status"));

            return user;
        }

        return null;
    }


    public void updateProfile(long id, String email, String description, String photo)
            throws SQLException {

        String sql =
        "UPDATE users SET email=?,description=?,profile_photo_url=?,status='ACTIVE' WHERE id=?";

        Connection conn = DBConnection.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setString(1,email);
        stmt.setString(2,description);
        stmt.setString(3,photo);
        stmt.setLong(4,id);

        stmt.executeUpdate();
    }


    public User getUserById(long id) {
        try {
            String sql = "SELECT * FROM users WHERE id=?";
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1,id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setDescription(rs.getString("description"));
                user.setProfilePhotoUrl(rs.getString("profile_photo_url"));
                user.setFollowerCount(rs.getInt("follower_count"));
                user.setFollowingCount(rs.getInt("following_count"));
                user.setStatus(rs.getString("status"));
                return user;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Long> getFollowees(long userId) {
        List<Long> followees = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT followee_id FROM user_follow_map WHERE follower_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                followees.add(rs.getLong("followee_id"));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return followees;
    }
    public List<User> getAllUsers(long currentUserId) {
        List<User> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT id, username, description, follower_count, following_count FROM users";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                if(id == currentUserId) continue; // skip current user
                User u = new User();
                u.setId(id);
                u.setUsername(rs.getString("username"));
                u.setDescription(rs.getString("description"));
                u.setFollowerCount(rs.getInt("follower_count"));
                u.setFollowingCount(rs.getInt("following_count"));
                list.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean updateStatus(long userId, String status){

        try{

            String sql = "UPDATE users SET status=? WHERE id=?";

            Connection conn = DBConnection.getConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1,status);
            stmt.setLong(2,userId);

            stmt.executeUpdate();

            return true;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }
    public boolean addFollowMap(long followerId, long followeeId){

        String sql = "INSERT IGNORE INTO user_follow_map (follower_id, followee_id) VALUES (?, ?)";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, followerId);
            ps.setLong(2, followeeId);

            int rows = ps.executeUpdate();

            return rows > 0;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }
    public boolean removeFollowMap(long followerId, long followeeId){

        String sql = "DELETE FROM user_follow_map WHERE follower_id=? AND followee_id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, followerId);
            ps.setLong(2, followeeId);

            int rows = ps.executeUpdate();

            return rows > 0;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }
    public void incrementFollowerCount(long userId){

        String sql = "UPDATE users SET follower_count = follower_count + 1 WHERE id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, userId);

            ps.executeUpdate();

            System.out.println("Follower count incremented");

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void incrementFollowingCount(long userId){

        String sql = "UPDATE users SET following_count = following_count + 1 WHERE id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, userId);

            ps.executeUpdate();

            System.out.println("Following count incremented");

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void decrementFollowerCount(long userId){

        String sql = "UPDATE users SET follower_count = follower_count - 1 WHERE id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, userId);

            ps.executeUpdate();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void decrementFollowingCount(long userId){

        String sql = "UPDATE users SET following_count = following_count - 1 WHERE id=?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, userId);

            ps.executeUpdate();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}