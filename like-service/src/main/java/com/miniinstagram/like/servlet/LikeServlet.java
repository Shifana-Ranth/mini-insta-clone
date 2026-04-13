package com.miniinstagram.like.servlet;

import com.miniinstagram.like.service.LikeService;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;

public class LikeServlet extends HttpServlet {

    private LikeService likeService = new LikeService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long userId = Long.parseLong(request.getParameter("userId"));
        long postId = Long.parseLong(request.getParameter("postId"));

        String action = request.getParameter("action");

        if ("like".equalsIgnoreCase(action)) {

            long postOwnerId = Long.parseLong(request.getParameter("postOwnerId"));

            likeService.likePost(userId, postId, postOwnerId);

            System.out.println("LIKE request processed");

            response.getWriter().write("Like processed");

        } else if ("unlike".equalsIgnoreCase(action)) {

            likeService.unlikePost(userId, postId);

            System.out.println("UNLIKE request processed");

            response.getWriter().write("Unlike processed");

        } else {

            response.getWriter().write("Invalid action");

        }
    }

    // Allow browser GET requests for testing
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doPost(request, response);
    }
}
//package com.miniinstagram.like.servlet;
//
//import com.miniinstagram.like.service.LikeService;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//
//import java.io.IOException;
//
//public class LikeServlet extends HttpServlet {
//
//    private LikeService likeService = new LikeService();
//
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        long userId = Long.parseLong(request.getParameter("userId"));
//        long postId = Long.parseLong(request.getParameter("postId"));
//        long postOwnerId = Long.parseLong(request.getParameter("postOwnerId"));
//
//        likeService.likePost(userId, postId, postOwnerId);
//
//        response.getWriter().write("Like processed");
//    }
//}