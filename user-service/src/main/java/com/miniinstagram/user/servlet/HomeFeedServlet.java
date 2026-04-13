package com.miniinstagram.user.servlet;

import com.miniinstagram.user.model.Post;
import com.miniinstagram.user.util.RedisCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import redis.clients.jedis.Jedis;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class HomeFeedServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        long userId = (Long) session.getAttribute("userId");

        List<Post> redisPosts = new ArrayList<>();
        List<Post> followerPosts = new ArrayList<>();
        List<Post> trendingPosts = new ArrayList<>();
        List<Post> finalFeed = new ArrayList<>();

        Set<Long> seenPostIds = new HashSet<>();

        // =========================
        // STEP 1: REDIS
        // =========================
        try (Jedis jedis = RedisCache.getJedis()) {

            String redisKey = "feed:user:" + userId;
            System.out.println("🔎 Checking Redis key: " + redisKey);

            List<String> rawPosts = jedis.lrange(redisKey, 0, -1);

            if (rawPosts != null && !rawPosts.isEmpty()) {
                System.out.println("✅ REDIS HIT: " + rawPosts.size());

                for (String json : rawPosts) {
                    redisPosts.add(mapper.readValue(json, Post.class));
                }
            }

        } catch (Exception e) {
            System.err.println("❌ REDIS ERROR: " + e.getMessage());
        }

        // =========================
        // STEP 2: FOLLOWER POSTS (DB FALLBACK)
        // =========================
        if (!redisPosts.isEmpty()) {

            followerPosts = redisPosts;

        } else {

            System.out.println("⚠️ REDIS EMPTY → FETCH FOLLOWERS FROM USER SERVICE");

            try {

                String followUrlStr = "http://localhost:8080/user-service/getFollowees?userId=" + userId;
                System.out.println("➡️ Calling: " + followUrlStr);

                URL followUrl = new URL(followUrlStr);
                HttpURLConnection conn = (HttpURLConnection) followUrl.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {

                    List<Long> followingIds = mapper.readValue(
                            conn.getInputStream(),
                            new TypeReference<List<Long>>() {}
                    );

                    System.out.println("👥 Following IDs: " + followingIds);

                    if (!followingIds.isEmpty()) {

                        String idsParam = followingIds.stream()
                                .map(String::valueOf)
                                .reduce((a, b) -> a + "," + b)
                                .orElse("");

                        String postUrlStr = "http://localhost:8084/post-service/postsByUserIds?ids=" + idsParam;
                        //System.out.println("➡️ Calling: " + postUrlStr);

                        URL postUrl = new URL(postUrlStr);
                        HttpURLConnection postConn = (HttpURLConnection) postUrl.openConnection();
                        postConn.setRequestMethod("GET");

                        if (postConn.getResponseCode() == 200) {

                            followerPosts = mapper.readValue(
                                    postConn.getInputStream(),
                                    new TypeReference<List<Post>>() {}
                            );

                            System.out.println("✅ FOLLOWER POSTS: " + followerPosts.size());
                        }
                    }
                }

            } catch (Exception e) {
                System.err.println("❌ FOLLOWER FETCH ERROR: " + e.getMessage());
            }
        }

        // =========================
        // STEP 3: TRENDING POSTS
        // =========================
        try {

            String trendingUrlStr = "http://localhost:8084/post-service/getTrendingPosts";
           // System.out.println("➡️ Calling: " + trendingUrlStr);

            URL url = new URL(trendingUrlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {

                trendingPosts = mapper.readValue(
                        conn.getInputStream(),
                        new TypeReference<List<Post>>() {}
                );

                System.out.println("🔥 TRENDING POSTS: " + trendingPosts.size());
            }

        } catch (Exception e) {
            System.err.println("❌ TRENDING ERROR: " + e.getMessage());
        }

        // =========================
        // STEP 4: MERGE
        // =========================
        for (Post p : followerPosts) {
            if (seenPostIds.add(p.getId())) {
                finalFeed.add(p);
            }
        }

        for (Post p : trendingPosts) {
            if (seenPostIds.add(p.getId())) {
                finalFeed.add(p);
            }
        }

        // =========================
        // STEP 5: LIKE SERVICE
        // =========================
        try {

            String likeUrlStr = "http://localhost:8091/like-service/likes/user?userId=" + userId;
            //System.out.println("➡️ Calling: " + likeUrlStr);

            URL url = new URL(likeUrlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder responseData = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseData.append(line);
            }

            reader.close();

            Set<Long> likedPostIds = new HashSet<>();

            String data = responseData.toString().replace("[", "").replace("]", "");

            if (!data.trim().isEmpty()) {
                for (String id : data.split(",")) {
                    likedPostIds.add(Long.parseLong(id.trim()));
                }
            }

            for (Post post : finalFeed) {
                post.setLikedByCurrentUser(likedPostIds.contains(post.getId()));
            }

        } catch (Exception e) {
            System.err.println("❌ LIKE ERROR: " + e.getMessage());
        }

        // =========================
        // STEP 6: USERNAMES
        // =========================
        for (Post post : finalFeed) {
            try {

                String userUrlStr = "http://localhost:8080/user-service/getUserById?userId=" + post.getAuthorId();
                //System.out.println("➡️ Calling: " + userUrlStr);

                URL url = new URL(userUrlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {

                    Map<String, Object> userMap = mapper.readValue(
                            conn.getInputStream(),
                            Map.class
                    );

                    post.setUsername((String) userMap.get("username"));
                }

            } catch (Exception e) {
                System.err.println("❌ USER FETCH ERROR: " + e.getMessage());
                post.setUsername("unknown");
            }
        }

        // =========================
        req.setAttribute("feedPosts", finalFeed);
        req.getRequestDispatcher("feed.jsp").forward(req, resp);
    }
}
//package com.miniinstagram.user.servlet;
//
//import com.miniinstagram.user.model.Post;
//import com.miniinstagram.user.util.RedisCache;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.core.type.TypeReference;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.*;
//
//import redis.clients.jedis.Jedis;
//
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.*;
//
//public class HomeFeedServlet extends HttpServlet {
//
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//            throws ServletException, IOException {
//
//        HttpSession session = req.getSession(false);
//        if (session == null || session.getAttribute("userId") == null) {
//            resp.sendRedirect("login.jsp");
//            return;
//        }
//
//        long userId = (Long) session.getAttribute("userId");
//
//        List<Post> redisPosts = new ArrayList<>();
//        List<Post> followerPosts = new ArrayList<>();
//        List<Post> trendingPosts = new ArrayList<>();
//        List<Post> finalFeed = new ArrayList<>();
//
//        Set<Long> seenPostIds = new HashSet<>();
//
//        // =========================
//        // STEP 1: TRY REDIS
//        // =========================
//        try (Jedis jedis = RedisCache.getJedis()) {
//
//            String redisKey = "feed:user:" + userId;
//            List<String> rawPosts = jedis.lrange(redisKey, 0, -1);
//
//            if (rawPosts != null && !rawPosts.isEmpty()) {
//                System.out.println("✅ REDIS HIT");
//
//                for (String json : rawPosts) {
//                    redisPosts.add(mapper.readValue(json, Post.class));
//                }
//            }
//
//        } catch (Exception e) {
//            System.err.println("REDIS ERROR: " + e.getMessage());
//        }
//
//        // =========================
//        // STEP 2: FOLLOWER POSTS
//        // =========================
//        if (!redisPosts.isEmpty()) {
//
//            followerPosts = redisPosts;
//
//        } else {
//
//            System.out.println("⚠️ REDIS EMPTY → FETCH FOLLOWERS");
//
//            try (Jedis jedis = RedisCache.getJedis()) {
//
//                String followKey = "user:" + userId + ":following";
//                Set<String> followingIds = jedis.smembers(followKey);
//
//                if (followingIds != null && !followingIds.isEmpty()) {
//
//                    String idsParam = String.join(",", followingIds);
//
//                    URL url = new URL("http://localhost:8084/post-service/postsByUserIds?ids=" + idsParam);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("GET");
//
//                    if (conn.getResponseCode() == 200) {
//
//                        followerPosts = mapper.readValue(
//                                conn.getInputStream(),
//                                new TypeReference<List<Post>>() {}
//                        );
//
//                        System.out.println("✅ FOLLOWER POSTS FETCHED: " + followerPosts.size());
//                    }
//                }
//
//            } catch (Exception e) {
//                System.err.println("FOLLOWER FETCH ERROR: " + e.getMessage());
//            }
//        }
//
//        // =========================
//        // STEP 3: TRENDING POSTS
//        // =========================
//        try {
//            URL url = new URL("http://localhost:8084/post-service/getTrendingPosts");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//
//            if (conn.getResponseCode() == 200) {
//
//                trendingPosts = mapper.readValue(
//                        conn.getInputStream(),
//                        new TypeReference<List<Post>>() {}
//                );
//
//                System.out.println("🔥 TRENDING POSTS: " + trendingPosts.size());
//            }
//
//        } catch (Exception e) {
//            System.err.println("TRENDING ERROR: " + e.getMessage());
//        }
//
//        // =========================
//        // STEP 4: MERGE (NO DUPLICATES)
//        // =========================
//        for (Post p : followerPosts) {
//            if (seenPostIds.add(p.getId())) {
//                finalFeed.add(p);
//            }
//        }
//
//        for (Post p : trendingPosts) {
//            if (seenPostIds.add(p.getId())) {
//                finalFeed.add(p);
//            }
//        }
//
//        // =========================
//        // STEP 5: LIKE SERVICE
//        // =========================
//        try {
//
//            URL url = new URL("http://localhost:8091/like-service/likes/user?userId=" + userId);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(conn.getInputStream())
//            );
//
//            StringBuilder responseData = new StringBuilder();
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                responseData.append(line);
//            }
//
//            reader.close();
//
//            String likedPostsStr = responseData.toString()
//                    .replace("[", "")
//                    .replace("]", "");
//
//            Set<Long> likedPostIds = new HashSet<>();
//
//            if (!likedPostsStr.trim().isEmpty()) {
//                String[] ids = likedPostsStr.split(",");
//
//                for (String id : ids) {
//                    likedPostIds.add(Long.parseLong(id.trim()));
//                }
//            }
//
//            for (Post post : finalFeed) {
//                post.setLikedByCurrentUser(
//                        likedPostIds.contains(post.getId())
//                );
//            }
//
//        } catch (Exception e) {
//            System.err.println("LIKE SERVICE ERROR: " + e.getMessage());
//        }
//     // =========================
//     // STEP 6: FETCH USERNAMES
//     // =========================
//     for (Post post : finalFeed) {
//         try {
//        	 URL url = new URL("http://localhost:8080/user-service/getUserById?userId=" + post.getAuthorId());
//             HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//             conn.setRequestMethod("GET");
//
//             if (conn.getResponseCode() == 200) {
//
//                 BufferedReader reader = new BufferedReader(
//                         new InputStreamReader(conn.getInputStream())
//                 );
//
//                 StringBuilder response = new StringBuilder();
//                 String line;
//
//                 while ((line = reader.readLine()) != null) {
//                     response.append(line);
//                 }
//
//                 reader.close();
//
//                 // ⚠️ Assuming response contains username
//                 // If it's JSON, we parse it:
//
//                 Map<String, Object> userMap = mapper.readValue(response.toString(), Map.class);
//
//                 post.setUsername((String) userMap.get("username"));
//             }
//
//         } catch (Exception e) {
//             System.err.println("USER FETCH ERROR: " + e.getMessage());
//             post.setUsername("unknown");
//         }
//     }
//        // =========================
//        req.setAttribute("feedPosts", finalFeed);
//        req.getRequestDispatcher("feed.jsp").forward(req, resp);
//    }
//}

//package com.miniinstagram.user.servlet;
//
//import com.miniinstagram.user.model.Post;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.HashSet;
//import java.util.Set;
//import com.miniinstagram.user.util.RedisCache;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.core.type.TypeReference;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.*;
//import redis.clients.jedis.Jedis;
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
//public class HomeFeedServlet extends HttpServlet {
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        HttpSession session = req.getSession(false);
//        if (session == null || session.getAttribute("userId") == null) {
//            resp.sendRedirect("login.jsp");
//            return;
//        }
//
//        long userId = (Long) session.getAttribute("userId");
//        List<Post> feedPosts = new ArrayList<>();
//
//        // --- STEP 1: TRY REDIS ---
//        try (Jedis jedis = RedisCache.getJedis()) {
//            String redisKey = "feed:user:" + userId;
//            List<String> rawPosts = jedis.lrange(redisKey, 0, -1);
//
//            if (rawPosts != null && !rawPosts.isEmpty()) {
//                System.out.println("SUCCESS: Found " + rawPosts.size() + " posts in Redis for User " + userId);
//                for (String json : rawPosts) {
//                    feedPosts.add(mapper.readValue(json, Post.class));
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("REDIS ERROR: " + e.getMessage());
//        }
//
//        // --- STEP 2: FALLBACK TO POST-SERVICE (MySQL) ---
//        if (feedPosts.isEmpty()) {
//            System.out.println("FALLBACK: Fetching Trending Posts from MySQL...");
//            try {
//                // Adjust port 8082 to match your Post-Service port
//                URL url = new URL("http://localhost:8084/post-service/getTrendingPosts");
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("GET");
//
//                if (conn.getResponseCode() == 200) {
//                	feedPosts = mapper.readValue(conn.getInputStream(), new TypeReference<List<Post>>() {});
//                    //feedPosts = mapper.readValue(conn.getInputStream(), new TypeReference<List<Post>>() {});
//                    req.setAttribute("isTrending", true); // Flag for the UI
//                }
//            } catch (Exception e) {
//                System.err.println("MYSQL FALLBACK FAILED: " + e.getMessage());
//            }
//        }
//        try {
//
//            URL url = new URL("http://localhost:8091/like-service/likes/user?userId=" + userId);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//
//            BufferedReader reader =
//                    new BufferedReader(new InputStreamReader(conn.getInputStream()));
//
//            String line;
//            StringBuilder responseData = new StringBuilder();
//
//            while ((line = reader.readLine()) != null) {
//                responseData.append(line);
//            }
//            System.out.println("LIKE SERVICE RAW RESPONSE: " + responseData.toString());
//            reader.close();
//
//            // Convert "[3,10,47]" → Set<Integer>
//            String likedPostsStr = responseData.toString()
//                    .replace("[", "")
//                    .replace("]", "");
//
//            //Set<Integer> likedPostIds = new HashSet<>();
//            Set<Long> likedPostIds = new HashSet<>();
////            if (!likedPostsStr.trim().isEmpty()) {
////                String[] ids = likedPostsStr.split(",");
////
////                for (String id : ids) {
////                    likedPostIds.add(Integer.parseInt(id.trim()));
////                }
////            }
//            if (!likedPostsStr.trim().isEmpty()) {
//                String[] ids = likedPostsStr.split(",");
//
//                for (String id : ids) {
//                    likedPostIds.add(Long.parseLong(id.trim()));
//                }
//            }
//
//            System.out.println("LIKED POST IDS: " + likedPostIds);
//            // Mark posts liked by current user
//            for (Post post : feedPosts) {
//            	System.out.println("Checking post: " + post.getId());
//                if (likedPostIds.contains(post.getId())) {
//                	System.out.println("User liked post: " + post.getId());
//                    post.setLikedByCurrentUser(true);
//                } else {
//                    post.setLikedByCurrentUser(false);
//                }
//            }
//
//        } catch (Exception e) {
//            System.err.println("LIKE SERVICE ERROR: " + e.getMessage());
//        }
//        req.setAttribute("feedPosts", feedPosts);
//        req.getRequestDispatcher("feed.jsp").forward(req, resp);
//    }
//}