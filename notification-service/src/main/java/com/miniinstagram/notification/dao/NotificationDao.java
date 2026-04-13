package com.miniinstagram.notification.dao;

import com.miniinstagram.notification.util.DBConnection;
import com.miniinstagram.notification.model.Notification;
import java.sql.Connection;
import java.util.*;
import java.sql.PreparedStatement;
import java.sql.*;

public class NotificationDao {

    public boolean createNotification(long userId, String type, String message){

        try{

            String sql =
            "INSERT INTO notifications(user_id,message_type,message,status) VALUES(?,?,?,?)";

            Connection conn = DBConnection.getConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setLong(1,userId);
            stmt.setString(2,type);
            stmt.setString(3,message);
            stmt.setString(4,"SENT");

            stmt.executeUpdate();

            return true;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }
    public List<Notification> getNotificationsByUser(long userId){

        List<Notification> list = new ArrayList<>();

        try{

            String sql = "SELECT * FROM notifications WHERE user_id=? ORDER BY created_at DESC";

            Connection conn = DBConnection.getConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1,userId);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()){

                Notification n = new Notification();

                n.setId(rs.getLong("id"));
                n.setUserId(rs.getLong("user_id"));
                n.setMessage(rs.getString("message"));
                n.setMessageType(rs.getString("message_type"));

                list.add(n);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }
}