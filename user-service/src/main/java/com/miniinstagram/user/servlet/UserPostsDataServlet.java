package com.miniinstagram.user.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.net.*;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class UserPostsDataServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String userId = req.getParameter("userId");

        if (userId == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Missing userId");
            return;
        }

        // Get current logged-in user
        HttpSession session = req.getSession(false);
        long currentUserId = (Long) session.getAttribute("userId");

        // --- STEP 1: Call Post Service ---
        URL url = new URL("http://localhost:8084/post-service/userPosts?userId=" + userId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder json = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            json.append(line);
        }

        reader.close();

        System.out.println("Data received from Post-Service: " + json.toString());

        // Convert JSON → List<Map>
        List<Map<String, Object>> posts =
                mapper.readValue(json.toString(),
                new TypeReference<List<Map<String, Object>>>() {});

        // --- STEP 2: Call Like Service ---
        Set<Long> likedPostIds = new HashSet<>();

        try {

            URL likeUrl = new URL(
                    "http://localhost:8091/like-service/likes/user?userId=" + currentUserId);

            HttpURLConnection likeConn = (HttpURLConnection) likeUrl.openConnection();
            likeConn.setRequestMethod("GET");

            BufferedReader likeReader =
                    new BufferedReader(new InputStreamReader(likeConn.getInputStream()));

            StringBuilder likeResponse = new StringBuilder();
            String l;

            while ((l = likeReader.readLine()) != null) {
                likeResponse.append(l);
            }

            likeReader.close();

            System.out.println("LIKE SERVICE RAW RESPONSE: " + likeResponse.toString());

            String likedPostsStr = likeResponse.toString()
                    .replace("[", "")
                    .replace("]", "");

            if (!likedPostsStr.trim().isEmpty()) {

                String[] ids = likedPostsStr.split(",");

                for (String id : ids) {
                    likedPostIds.add(Long.parseLong(id.trim()));
                }
            }

            System.out.println("LIKED POST IDS: " + likedPostIds);

        } catch (Exception e) {
            System.err.println("LIKE SERVICE ERROR: " + e.getMessage());
        }

        // --- STEP 3: Mark liked posts ---
        for (Map<String, Object> post : posts) {

            Long postId = ((Number) post.get("postId")).longValue();

            if (likedPostIds.contains(postId)) {
                post.put("likedByCurrentUser", true);
            } else {
                post.put("likedByCurrentUser", false);
            }
        }

        // --- STEP 4: Return enriched JSON ---
        String finalJson = mapper.writeValueAsString(posts);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(finalJson);
    }
}
//package com.miniinstagram.user.servlet;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//import java.io.*;
//import java.net.*;
//
//public class UserPostsDataServlet extends HttpServlet {
//
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String userId = req.getParameter("userId");
//        if(userId == null) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            resp.getWriter().write("Missing userId");
//            return;
//        }
//
//        // Call the Post Service URL directly
//        URL url = new URL("http://localhost:8084/post-service/userPosts?userId=" + userId);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("GET");
//
//        // Read response
//        InputStream in = conn.getInputStream();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//        StringBuilder json = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            json.append(line);
//        }
//
//        System.out.println("Data received from Post-Service: " + json.toString());
//        
//        // Forward JSON to browser
//        resp.setContentType("application/json");
//        resp.setCharacterEncoding("UTF-8");
//        resp.getWriter().write(json.toString());
//    }
//}
//package com.miniinstagram.user.servlet;
//
//import com.miniinstagram.user.model.Post;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.core.type.TypeReference;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.*;
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.io.*;
//import java.net.*;
//public class UserPostsDataServlet extends HttpServlet {
//
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String targetUserId = req.getParameter("userId");
//        HttpSession session = req.getSession(false);
//        
//        if (targetUserId == null || session == null || session.getAttribute("userId") == null) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            resp.getWriter().write("Missing userId or session");
//            return;
//        }
//
//        long currentUserId = (Long) session.getAttribute("userId");
//
//        try {
//            // 1️⃣ Fetch Posts from Post-Service
//            URL postUrl = new URL("http://localhost:8084/post-service/userPosts?userId=" + targetUserId);
//            HttpURLConnection postConn = (HttpURLConnection) postUrl.openConnection();
//            postConn.setRequestMethod("GET");
//
//            // Convert JSON stream directly into List<Post>
//            List<Post> posts = mapper.readValue(postConn.getInputStream(), new TypeReference<List<Post>>() {});
//
//            // 2️⃣ Fetch Liked IDs from Like-Service
//            URL likeUrl = new URL("http://localhost:8091/like-service/likes/user?userId=" + currentUserId);
//            HttpURLConnection likeConn = (HttpURLConnection) likeUrl.openConnection();
//            likeConn.setRequestMethod("GET");
//            
//            BufferedReader reader = new BufferedReader(new InputStreamReader(likeConn.getInputStream()));
//            String likedIdsRaw = reader.readLine(); 
//            
//            Set<Long> likedPostIds = new HashSet<>();
//            if (likedIdsRaw != null && likedIdsRaw.contains("[")) {
//                // Remove brackets and split by comma
//                String cleanIds = likedIdsRaw.replace("[", "").replace("]", "").trim();
//                if (!cleanIds.isEmpty()) {
//                    for (String id : cleanIds.split(",")) {
//                        likedPostIds.add(Long.parseLong(id.trim()));
//                    }
//                }
//            }
//
//            // 3️⃣ Merge Logic: Compare Post IDs with Liked IDs
//            for (Post p : posts) {
//                if (likedPostIds.contains(p.getId())) {
//                    p.setLikedByCurrentUser(true);
//                } else {
//                    p.setLikedByCurrentUser(false);
//                }
//            }
//            //System.out.println("Data received from Post-Service: " + json.toString());
////          
//            // 4️⃣ Return the Enriched JSON (with proper like status and counts)
//            resp.setContentType("application/json");
//            resp.setCharacterEncoding("UTF-8");
//            mapper.writeValue(resp.getWriter(), posts);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            resp.getWriter().write("Error aggregating post data");
//        }
//    }
//}

//package com.miniinstagram.user.servlet;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class UserPostsDataServlet extends HttpServlet {
//
//    private String postServiceBase = "http://localhost:8084/post-service";
//
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        long userId = Long.parseLong(req.getParameter("userId"));
//        URL url = new URL(postServiceBase + "/userPosts?userId=" + userId);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("GET");
//
//        resp.setContentType("application/json");
//        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                resp.getWriter().write(line);
//            }
//        }
//    }
//}