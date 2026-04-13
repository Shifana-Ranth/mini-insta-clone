package com.miniinstagram.search.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import com.miniinstagram.search.lucene.LuceneManager;

public class SearchUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String query = request.getParameter("q");

            response.setContentType("application/json");

            if (query == null || query.isEmpty()) {
                response.getWriter().println("[]"); // empty JSON array
                return;
            }

            System.out.println("Search query received in search-service: " + query);

            // 1️⃣ Search Lucene index
            List<String> userIds = LuceneManager.searchUsers(query);

            // 2️⃣ Return JSON array of IDs
            PrintWriter out = response.getWriter();
            out.println(userIds.toString());

        } catch(Exception e){
            e.printStackTrace();
            response.sendError(500, "Server error in search-service");
        }
    }
}
//package com.miniinstagram.search.servlet;

//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//import java.io.IOException;
//import java.util.*;
//import com.miniinstagram.search.dao.UserDao;
//import com.miniinstagram.search.model.User;
//import com.miniinstagram.search.lucene.LuceneManager;
//
//public class SearchUserServlet extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession sessionObj = request.getSession(false);
//        if(sessionObj == null || sessionObj.getAttribute("userId") == null){
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        long currentUserId = (Long) sessionObj.getAttribute("userId");
//
//        try {
//            String query = request.getParameter("q");
//            if(query == null || query.isEmpty()){
//                request.setAttribute("users", Collections.emptyList());
//            } else {
//                // 1️⃣ Get matching user IDs from Lucene
//                List<String> userIds = LuceneManager.searchUsers(query);
//
//                UserDao userDao = new UserDao();
//                List<User> users = new ArrayList<>();
//
//                // 2️⃣ Convert IDs to User objects
//                for(String idStr : userIds){
//                    long id = Long.parseLong(idStr);
//                    if(id == currentUserId) continue; // skip yourself
//                    User u = userDao.getUserById(id);
//                    // 3️⃣ Check if current user follows them
//                    u.setIsFollowed(userDao.getFollowees(currentUserId).contains(id));
//                    users.add(u);
//                }
//
//                request.setAttribute("users", users);
//            }
//
//            // 4️⃣ Forward to JSP
//            RequestDispatcher rd = request.getRequestDispatcher("searchResults.jsp");
//            rd.forward(request, response);
//
//        } catch(Exception e){
//            e.printStackTrace();
//            response.sendError(500, "Server error during search");
//        }
//    }
//}
//package com.miniinstagram.search.servlet;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//
//public class SearchUserServlet extends HttpServlet {
//
//	@Override
//	protected void doGet(HttpServletRequest request, HttpServletResponse response)
//	        throws ServletException, IOException {
//
//	    try {
//
//	        String query = request.getParameter("q");
//
//	        if(query == null || query.isEmpty()){
//	            response.getWriter().println("No search query received");
//	            return;
//	        }
//
//	        System.out.println("Search query received: " + query);
//
//	        java.util.List<String> users =
//	                com.miniinstagram.search.lucene.LuceneManager.searchUsers(query);
//
//	        response.setContentType("application/json");
//
//	        PrintWriter out = response.getWriter();
//
//	        out.println(users.toString());
//
//	    } catch(Exception e){
//	        e.printStackTrace();
//	    }
//	}
//}