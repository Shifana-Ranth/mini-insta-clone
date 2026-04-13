package com.miniinstagram.user.servlet;

import com.miniinstagram.user.dao.UserDao;
import com.miniinstagram.user.model.User;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import redis.clients.jedis.Jedis;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class RegisterServlet extends HttpServlet {

    private UserDao userDao = new UserDao();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String username = request.getParameter("username");
            String password = request.getParameter("password");

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setStatus("PENDING");

            boolean created = userDao.createUser(user);

            if(!created){
                response.sendRedirect("register.jsp");
                return;
            }
            System.out.println("Calling search service...");

            URL searchUrl = new URL("http://localhost:8092/search-service/indexUser");

            HttpURLConnection searchCon = (HttpURLConnection) searchUrl.openConnection();
            searchCon.setRequestMethod("POST");
            searchCon.setDoOutput(true);

            searchCon.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            String searchParams =
                    "id=" + user.getId() +
                    "&username=" + user.getUsername() +
                    "&description=";

            OutputStream searchOs = searchCon.getOutputStream();
            searchOs.write(searchParams.getBytes());
            searchOs.flush();
            searchOs.close();

            int searchResponse = searchCon.getResponseCode();

            System.out.println("Search Service Response Code: " + searchResponse);
            System.out.println("Calling notification service...");
            /* ---------- CALL NOTIFICATION SERVICE ---------- */
            URL url = new URL("http://localhost:8082/notification-service/notify");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");

            String jsonParams = "{"
                + "\"userId\":" + user.getId() + ","
                + "\"message\":\"Welcome " + username + "\","
                + "\"type\":\"WELCOME\""
                + "}";

            try (OutputStream os = con.getOutputStream()) {
                os.write(jsonParams.getBytes("UTF-8"));
                os.flush();
            }

            // YOU MUST READ THE RESPONSE FOR THE CALL TO FINISH PROPERLY
            int responseCode = con.getResponseCode(); 
            System.out.println("Notification Service Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String result = br.readLine();
                    System.out.println("Notification response: " + result);
                    
                    if("SUCCESS".equals(result)){
                    	System.out.println("set status active");
                        userDao.updateStatus(user.getId(), "ACTIVE");
                    }
                }
            }
//            /* ---------- CALL NOTIFICATION SERVICE ---------- */
//            URL url = new URL("http://localhost:8082/notification-service/notify");
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setRequestMethod("POST");
//            con.setDoOutput(true);
//
//            // 1. Change Content-Type to application/json
//            con.setRequestProperty("Content-Type", "application/json");
//
//            // 2. Build the JSON string manually
//            String jsonParams = "{"
//                + "\"userId\":" + user.getId() + ","
//                + "\"message\":\"Welcome " + username + "\","
//                + "\"type\":\"WELCOME\""
//                + "}";
//
//            OutputStream os = con.getOutputStream();
//            os.write(jsonParams.getBytes("UTF-8"));
//            os.flush();
//            os.close();
//            /* ---------- CALL NOTIFICATION SERVICE ---------- */
//
//            System.out.println("Calling notification service...");
//
//            URL url = new URL("http://localhost:8082/notification-service/notify");
//
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setRequestMethod("POST");
//            con.setDoOutput(true);
//
//            con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
//
//            String params = "userId=" + user.getId() + "&username=" + username;
//
//            OutputStream os = con.getOutputStream();
//            os.write(params.getBytes());
//            os.flush();
//            os.close();
//
//            BufferedReader br = new BufferedReader(
//                    new InputStreamReader(con.getInputStream()));
//
//            String result = br.readLine();
//
//System.out.println("Notification response: " + result);
//            br.close();
//
//            if("SUCCESS".equals(result)){
//                userDao.updateStatus(user.getId(),"ACTIVE");
//            }

            /* ---------- REDIS SESSION ---------- */

            Jedis jedis = new Jedis("localhost",6379);

            String sessionId = UUID.randomUUID().toString();

            jedis.setex("session:"+sessionId,3600,String.valueOf(user.getId()));

            Cookie cookie = new Cookie("sessionId",sessionId);
            cookie.setMaxAge(3600);
            cookie.setPath("/");

            response.addCookie(cookie);

            response.sendRedirect("setup-profile.jsp");
            jedis.close();

        } catch(Exception e){
            e.printStackTrace();
            response.sendRedirect("register.jsp");
        }
    }
}

//package com.miniinstagram.user.servlet;
//
//import com.miniinstagram.user.dao.UserDao;
//import com.miniinstagram.user.model.User;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//
//import redis.clients.jedis.Jedis;
//
//import java.io.IOException;
//import java.util.UUID;
//
//public class RegisterServlet extends HttpServlet {
//
//    private UserDao userDao = new UserDao();
//
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        try {
//
//            String username = request.getParameter("username");
//            String password = request.getParameter("password");
//
//            User user = new User();
//
//            user.setUsername(username);
//            user.setPassword(password);
//
//            userDao.createUser(user);
//
//            Jedis jedis = new Jedis("localhost",6379);
//
//            String sessionId = UUID.randomUUID().toString();
//
//            jedis.setex("session:"+sessionId,3600,String.valueOf(user.getId()));
//
//            Cookie cookie = new Cookie("sessionId",sessionId);
//            cookie.setMaxAge(3600);
//            cookie.setPath("/");
//
//            response.addCookie(cookie);
//
//            response.sendRedirect("setup-profile.jsp");
//
//        } catch(Exception e){
//            e.printStackTrace();
//            response.sendRedirect("register.jsp");
//        }
//    }
//}
////package com.miniinstagram.user.servlet;
////
////import com.miniinstagram.user.dao.UserDao;
////import com.miniinstagram.user.model.User;
////import redis.clients.jedis.Jedis;
////
////import jakarta.servlet.ServletException;
////import jakarta.servlet.http.*;
////import java.io.IOException;
////import java.net.HttpURLConnection;
////import java.net.URL;
////import java.util.UUID;
////
////public class RegisterServlet extends HttpServlet {
////
////    private UserDao userDao = new UserDao();
////
////    protected void doPost(HttpServletRequest request, HttpServletResponse response)
////            throws ServletException, IOException {
////
////        try {
////
////            String username = request.getParameter("username");
////            String email = request.getParameter("email");
////            String password = request.getParameter("password");
////            String description = request.getParameter("description");
////
////            String profilePhoto = "default_profile.png";
////
////            if(userDao.getUserByUsername(username).isPresent()){
////                request.setAttribute("error", "Username already exists!");
////                request.getRequestDispatcher("register.jsp").forward(request,response);
////                return;
////            }
////
////            User user = new User();
////            user.setUsername(username);
////            user.setEmail(email);
////            user.setPassword(password);
////            user.setDescription(description);
////            user.setProfilePhotoUrl(profilePhoto);
////            user.setStatus("PENDING");
////
////            boolean created = userDao.createUser(user);
////
////            if(!created){
////                request.setAttribute("error","User creation failed");
////                request.getRequestDispatcher("register.jsp").forward(request,response);
////                return;
////            }
////
//////            // Call Notification Service
//////            URL url = new URL("http://localhost:8083/notify?userId="+user.getId()+"&username="+username);
//////            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//////            con.setRequestMethod("GET");
//////
//////            int status = con.getResponseCode();
//////
//////            if(status == 200){
//////                userDao.updateStatus(user.getId(),"ACTIVE");
//////            }
////
////            userDao.updateStatus(user.getId(),"ACTIVE");
////            // Redis session
////            Jedis jedis = new Jedis("localhost",6379);
////
////            String sessionId = UUID.randomUUID().toString();
////
////            jedis.setex("session:"+sessionId,3600,String.valueOf(user.getId()));
////            jedis.set("user_status:"+user.getId(),"ACTIVE");
////
////            // Cookie
////            Cookie cookie = new Cookie("sessionId",sessionId);
////            cookie.setMaxAge(3600);
////            cookie.setPath("/");
////
////            response.addCookie(cookie);
////
////            response.sendRedirect("profile.jsp");
////
////        } catch(Exception e){
////            e.printStackTrace();
////            response.sendRedirect("register.jsp");
////        }
////    }
////}