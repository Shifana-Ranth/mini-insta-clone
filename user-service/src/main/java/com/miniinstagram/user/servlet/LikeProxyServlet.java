package com.miniinstagram.user.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.net.*;

public class LikeProxyServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

    	System.out.println("LIKE PROXY HIT");

        String action = req.getParameter("action");
        String userId = req.getParameter("userId");
        String postId = req.getParameter("postId");
        String postOwnerId = req.getParameter("postOwnerId");

        String likeServiceUrl =
                "http://localhost:8091/like-service/like"
                + "?action=" + action
                + "&userId=" + userId
                + "&postId=" + postId
                + "&postOwnerId=" + postOwnerId;

        URL url = new URL(likeServiceUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line;
        StringBuilder responseData = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            responseData.append(line);
        }

        reader.close();

        resp.getWriter().write(responseData.toString());
    }
}