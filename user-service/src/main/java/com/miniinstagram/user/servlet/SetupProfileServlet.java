package com.miniinstagram.user.servlet;

import com.miniinstagram.user.dao.UserDao;
import com.miniinstagram.user.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class SetupProfileServlet extends HttpServlet {

    private UserDao userDao = new UserDao();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String email = request.getParameter("email");
            String description = request.getParameter("description");
            String photo = request.getParameter("photo");

            System.out.println(email+"-"+description+"-"+photo);
            Cookie[] cookies = request.getCookies();

            String sessionId = null;

            for(Cookie c : cookies){
                if(c.getName().equals("sessionId")){
                    sessionId = c.getValue();
                }
            }

            Jedis jedis = new Jedis("localhost",6379);

            String userId = jedis.get("session:"+sessionId);

            userDao.updateProfile(Long.parseLong(userId),email,description,photo);

            System.out.println("Updating search index...");
            
            User user = userDao.getUserById(Long.parseLong(userId));

            String username = user.getUsername();
            
            URL url = new URL("http://localhost:8092/search-service/indexUser");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setDoOutput(true);

            con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            String params =
                    "id=" + userId +
                    "&username=" + username +
                    "&description=" + description;

            OutputStream os = con.getOutputStream();
            os.write(params.getBytes());
            os.flush();
            os.close();

            int responseCode = con.getResponseCode();

            System.out.println("Search service response: " + responseCode);
            response.sendRedirect("profile");

        } catch(Exception e){
            e.printStackTrace();
            response.sendRedirect("setup-profile.jsp");
        }
    }
}