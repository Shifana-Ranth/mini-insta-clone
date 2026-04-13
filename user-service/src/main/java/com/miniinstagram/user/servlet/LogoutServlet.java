package com.miniinstagram.user.servlet;

import redis.clients.jedis.Jedis;
import com.miniinstagram.user.dao.UserDao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {

    private UserDao userDao = new UserDao();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. Get all cookies and find the sessionId
            Cookie[] cookies = request.getCookies();
            String sessionId = null;

            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (c.getName().equals("sessionId")) {
                        sessionId = c.getValue();
                        break; // Found it, stop the loop
                    }
                }
            }

            // 2. If session exists, update DB and delete from Redis
            if (sessionId != null) {
                try (Jedis jedis = new Jedis("localhost", 6379)) {
                    String userIdStr = jedis.get("session:" + sessionId);
                    
                    if (userIdStr != null) {
                        long userId = Long.parseLong(userIdStr);
                        // Set user to INACTIVE in MySQL
                        userDao.updateStatus(userId, "INACTIVE");
                    }
                    // Remove session key from Redis
                    jedis.del("session:" + sessionId);
                }
            }

            // 3. Clear the cookie from the browser
            Cookie cookie = new Cookie("sessionId", "");
            cookie.setMaxAge(0);
            cookie.setPath("/"); // Matches the path set during login/register
            response.addCookie(cookie);

            // 4. Redirect to login page
            response.sendRedirect("login.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp");
        }
    }
}
//package com.miniinstagram.user.servlet;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//
//import redis.clients.jedis.Jedis;
//
//import java.io.IOException;
//
//public class LogoutServlet extends HttpServlet {

//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        Cookie[] cookies = request.getCookies();
//
//        if(cookies != null){
//
//            for(Cookie c : cookies){
//
//                if(c.getName().equals("sessionId")){
//
//                    Jedis jedis = new Jedis("localhost",6379);
//
//                    jedis.del("session:"+c.getValue());
//
//                    c.setMaxAge(0);
//                    response.addCookie(c);
//                }
//            }
//        }
//
//        response.sendRedirect("login.jsp");
//    }
//}