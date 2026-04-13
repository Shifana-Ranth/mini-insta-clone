package com.miniinstagram.user.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.net.*;

public class DeletePostProxyServlet extends HttpServlet {

    // This MUST be doPost because your JavaScript fetch says method: 'POST'
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String postId = req.getParameter("postId");
        System.out.println("DEBUG: Forwarding POST request for postId: " + postId);

        // 1. The URL should be the base path (without the ?postId= part)
        String targetUrl = "http://localhost:8084/post-service/deletePost";
        
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            // 2. Change to POST
            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // Required to send a body
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // 3. Write the postId into the request body
            String body = "postId=" + URLEncoder.encode(postId, "UTF-8");
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes("UTF-8"));
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            resp.setStatus(responseCode);
            resp.setContentType("application/json");

            // 4. Read the response
            InputStream is = (responseCode >= 200 && responseCode < 300) 
                             ? conn.getInputStream() 
                             : conn.getErrorStream();

            if (is != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        resp.getWriter().write(line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}