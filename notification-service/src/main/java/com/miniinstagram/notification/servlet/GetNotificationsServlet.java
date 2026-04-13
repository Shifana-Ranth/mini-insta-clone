package com.miniinstagram.notification.servlet;

import com.miniinstagram.notification.dao.NotificationDao;
import com.miniinstagram.notification.model.Notification;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

public class GetNotificationsServlet extends HttpServlet {

    private NotificationDao dao = new NotificationDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long userId = Long.parseLong(request.getParameter("userId"));

        List<Notification> notifications = dao.getNotificationsByUser(userId);

        response.setContentType("application/json");

        StringBuilder json = new StringBuilder("[");
        for(int i=0;i<notifications.size();i++){

            Notification n = notifications.get(i);

            json.append("{")
                    .append("\"message\":\"").append(n.getMessage()).append("\",")
                    .append("\"type\":\"").append(n.getMessageType()).append("\"")
                    .append("}");

            if(i < notifications.size()-1){
                json.append(",");
            }
        }

        json.append("]");

        response.getWriter().write(json.toString());
    }
}