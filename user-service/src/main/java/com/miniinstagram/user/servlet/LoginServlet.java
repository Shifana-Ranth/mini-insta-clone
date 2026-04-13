package com.miniinstagram.user.servlet;

import com.miniinstagram.user.dao.UserDao;
import com.miniinstagram.user.model.User;
import com.miniinstagram.user.util.NotificationClient;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.*;
import java.util.UUID;

public class LoginServlet extends HttpServlet {

    private UserDao userDao = new UserDao();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String username = request.getParameter("username");
            String password = request.getParameter("password");

            User user = userDao.getUserByUsername(username);

            if(user == null || !user.getPassword().equals(password)){
                request.setAttribute("error","Invalid credentials");
                request.getRequestDispatcher("login.jsp").forward(request,response);
                return;
            }

            Boolean res=NotificationClient.send(user.getId(), "New login detected for " + username, "LOGIN");
            System.out.println("Response from notification service: " + res);

            if(res){
                System.out.println("set status active");
                userDao.updateStatus(user.getId(), "ACTIVE");
            }
         // create/get HttpSession
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            Jedis jedis = new Jedis("localhost",6379);

            String sessionId = UUID.randomUUID().toString();

            jedis.setex("session:"+sessionId,3600,String.valueOf(user.getId()));

            Cookie cookie = new Cookie("sessionId",sessionId);
            cookie.setMaxAge(3600);
            cookie.setPath("/");

            response.addCookie(cookie);

            response.sendRedirect("profile");

        } catch(Exception e){
            e.printStackTrace();
            response.sendRedirect("login.jsp");
        }
    }
}