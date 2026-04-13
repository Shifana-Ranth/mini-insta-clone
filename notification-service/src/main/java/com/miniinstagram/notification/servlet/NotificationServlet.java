package com.miniinstagram.notification.servlet;

import com.miniinstagram.notification.dao.NotificationDao;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;

public class NotificationServlet extends HttpServlet {

    private NotificationDao dao = new NotificationDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doPost(request,response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            StringBuilder body = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            String json = body.toString();
            System.out.println("Received JSON: " + json);

            // A more "normal" way to parse without libraries: 
            // We look for the value after the key, ignoring quotes and spaces
            long userId = Long.parseLong(getValue(json, "userId"));
            String message = getValue(json, "message");
            String type = getValue(json, "type");

            boolean success = dao.createNotification(userId, type, message);
            response.getWriter().write(success ? "SUCCESS" : "FAILED");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("ERROR");
        }
    }

    // Simple helper method to extract values from a JSON string manually
    private String getValue(String json, String key) {
        // Find where the key starts (e.g., "userId")
        int keyIndex = json.indexOf("\"" + key + "\"");
        // Find the colon after the key
        int colonIndex = json.indexOf(":", keyIndex);
        // Find where the value ends (either at a comma or the closing brace)
        int endIndex = json.indexOf(",", colonIndex);
        if (endIndex == -1) {
            endIndex = json.indexOf("}", colonIndex);
        }
        
        // Clean up the result (remove quotes, spaces, and newlines)
        String value = json.substring(colonIndex + 1, endIndex);
        return value.replace("\"", "").trim();
    }
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        try {
//
//            StringBuilder body = new StringBuilder();
//            String line;
//
//            BufferedReader reader = request.getReader();
//
//            while ((line = reader.readLine()) != null) {
//                body.append(line);
//            }
//
//            String json = body.toString();
//
//            System.out.println("Received JSON: " + json);
//
//            long userId =
//            		Long.parseLong(json.split("\"userId\":")[1].split(",")[0]);
//            String message = json.split("\"message\":\"")[1].split("\"")[0];
//            String type = json.split("\"type\":\"")[1].split("\"")[0];
//
//            boolean success = dao.createNotification(userId, type, message);
//
//            if (success) {
//                response.getWriter().write("SUCCESS");
//            } else {
//                response.getWriter().write("FAILED");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.getWriter().write("ERROR");
//        }
//    }
}