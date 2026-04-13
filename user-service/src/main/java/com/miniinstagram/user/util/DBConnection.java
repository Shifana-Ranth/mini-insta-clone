package com.miniinstagram.user.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/user_service_db";
    private static final String DB_USER = "root";      // change if needed
    private static final String DB_PASSWORD = "Shifana@26";  // change if needed

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // load MySQL driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }
}