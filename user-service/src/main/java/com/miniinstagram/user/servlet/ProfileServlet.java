package com.miniinstagram.user.servlet;

import com.miniinstagram.user.dao.UserDao;
import com.miniinstagram.user.model.User;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import redis.clients.jedis.Jedis;

import java.io.IOException;

public class ProfileServlet extends HttpServlet {

    private UserDao userDao = new UserDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            Cookie[] cookies = request.getCookies();

            String sessionId = null;

            for(Cookie c : cookies){
                if(c.getName().equals("sessionId")){
                    sessionId = c.getValue();
                }
            }

            if(sessionId == null){
                response.sendRedirect("login.jsp");
                return;
            }

            Jedis jedis = new Jedis("localhost",6379);

            String userId = jedis.get("session:"+sessionId);

            User user = userDao.getUserById(Long.parseLong(userId));
            //User user = userDao.getUserById(Long.parseLong(userId));

            /* 🔥 CALL POST SERVICE */
            try {
                java.net.URL url = new java.net.URL(
                    "http://localhost:8084/post-service/getPostCount?userId=" + user.getId()
                );

                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                java.io.BufferedReader reader =
                    new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));

                String countStr = reader.readLine();

                int postCount = Integer.parseInt(countStr);

                user.setPostCount(postCount);   // ✅ THIS IS KEY

            } catch (Exception e) {
                e.printStackTrace();
            }
            request.setAttribute("user",user);

            request.getRequestDispatcher("profile.jsp").forward(request,response);

        } catch(Exception e){
            e.printStackTrace();
            response.sendRedirect("login.jsp");
        }
    }
}