package com.miniinstagram.user.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.net.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniinstagram.user.dao.UserDao;
import com.miniinstagram.user.model.User;

public class SearchUsersServlet extends HttpServlet {

    private static final String SEARCH_SERVICE_URL = "http://localhost:8092/search-service/searchUsers?q=";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sessionObj = request.getSession(false);
        if (sessionObj == null || sessionObj.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        long currentUserId = (Long) sessionObj.getAttribute("userId");
        String query = request.getParameter("q");
        List<User> users = new ArrayList<>();
        
        if (query != null && !query.isEmpty()) {
            try {
                // 1️⃣ Call search-service endpoint
                URL url = new URL(SEARCH_SERVICE_URL + URLEncoder.encode(query, "UTF-8"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                // 2️⃣ Parse JSON array of IDs using Jackson
                ObjectMapper mapper = new ObjectMapper();
                String[] idArray = mapper.readValue(result.toString(), String[].class);

                // 3️⃣ Fetch full User objects from DB
                UserDao userDao = new UserDao();
                for (String idStr : idArray) {
                    long id = Long.parseLong(idStr);
                    if (id == currentUserId) continue; // skip yourself
                    User u = userDao.getUserById(id);
                    if (u != null) {
                        u.setIsFollowed(userDao.getFollowees(currentUserId).contains(id));
                        users.add(u);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(500, "Server error during search");
                return;
            }
        }

        // 4️⃣ Forward to JSP
        request.setAttribute("users", users);
        RequestDispatcher rd = request.getRequestDispatcher("searchResults.jsp");
        rd.forward(request, response);
    }
}
//package com.miniinstagram.user.servlet;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//import java.io.*;
//import java.net.*;
//
//public class SearchUsersServlet extends HttpServlet {
//
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        String query = request.getParameter("q");
//
//        try {
//
//            URL url = new URL("http://localhost:8085/search?q=" + query);
//
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//
//            BufferedReader reader =
//                    new BufferedReader(new InputStreamReader(conn.getInputStream()));
//
//            StringBuilder result = new StringBuilder();
//            String line;
//
//            while((line = reader.readLine()) != null){
//                result.append(line);
//            }
//
//            request.setAttribute("results", result.toString());
//
//            request.getRequestDispatcher("searchResults.jsp")
//                   .forward(request,response);
//
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//}