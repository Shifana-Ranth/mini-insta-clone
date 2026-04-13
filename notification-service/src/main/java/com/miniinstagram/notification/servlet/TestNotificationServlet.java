package com.miniinstagram.notification.servlet;

import com.miniinstagram.notification.dao.NotificationDao;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;

public class TestNotificationServlet extends HttpServlet {

    private NotificationDao dao = new NotificationDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            long userId = 1;  // just for testing
            String message = "Test notification from browser";

            boolean success = dao.createNotification(userId, "WELCOME", message);

            if(success){
                response.getWriter().write("Notification inserted successfully!");
            } else {
                response.getWriter().write("Notification failed!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Error occurred");
        }
    }
}