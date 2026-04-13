package com.miniinstagram.user.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class UserPostsPageServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userIdParam = req.getParameter("userId");
        if(userIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing userId");
            return;
        }
        long userId = Long.parseLong(userIdParam);
        req.setAttribute("userId", userId); // make available to JSP
        RequestDispatcher dispatcher = req.getRequestDispatcher("/userPostsPage.jsp");
        dispatcher.forward(req, resp);
    }
}
//package com.miniinstagram.user.servlet;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//import java.io.IOException;
//
//public class UserPostsPageServlet extends HttpServlet {
//
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        long userId = Long.parseLong(req.getParameter("userId"));
//
//        // Store userId in request to use in JSP
//        req.setAttribute("userId", userId);
//
//        // Forward to JSP page
//        RequestDispatcher dispatcher = req.getRequestDispatcher("userPostsPage.jsp");
//        dispatcher.forward(req, resp);
//    }
//}