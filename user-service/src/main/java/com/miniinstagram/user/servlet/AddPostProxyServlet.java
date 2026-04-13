package com.miniinstagram.user.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.net.*;

public class AddPostProxyServlet extends HttpServlet {

    private String postServiceBase = "http://localhost:8084/post-service";

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = Long.parseLong(req.getParameter("userId"));
        String content = req.getParameter("content");
        String mediaUrl = req.getParameter("mediaUrl");

        URL url = new URL(postServiceBase + "/posts");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        String body = "userId=" + userId + "&content=" + URLEncoder.encode(content, "UTF-8") +
                      "&mediaUrl=" + URLEncoder.encode(mediaUrl == null ? "" : mediaUrl, "UTF-8");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes());
            os.flush();
        }

        resp.setContentType("application/json");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                resp.getWriter().write(line);
            }
        }
    }
}