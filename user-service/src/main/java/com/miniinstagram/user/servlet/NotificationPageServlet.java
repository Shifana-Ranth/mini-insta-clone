package com.miniinstagram.user.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import redis.clients.jedis.Jedis;

import java.io.*;
import java.net.*;
import java.util.stream.Collectors;

public class NotificationPageServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();

        String sessionId = null;

        for(Cookie c : cookies){
            if("sessionId".equals(c.getName())){
                sessionId = c.getValue();
            }
        }

        Jedis jedis = new Jedis("localhost",6379);

        String userId = jedis.get("session:"+sessionId);

        URL url = new URL("http://localhost:8082/notification-service/notifications?userId="+userId);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream()));

        String result = br.lines().collect(Collectors.joining());
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        request.setAttribute("notifications", result);

        request.getRequestDispatcher("notifications.jsp").forward(request,response);
    }
}