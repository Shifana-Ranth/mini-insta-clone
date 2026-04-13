package com.miniinstagram.user.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationClient {

    public static boolean send(long userId, String message, String type) {
        try {
            URL url = new URL("http://localhost:8082/notification-service/notify");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");

            String json = "{"
                + "\"userId\":" + userId + ","
                + "\"message\":\"" + message + "\","
                + "\"type\":\"" + type + "\""
                + "}";

            try (OutputStream os = con.getOutputStream()) {
                os.write(json.getBytes("UTF-8"));
                os.flush();
            }

            // 1. Check if the server responded with 200 OK
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 2. Read the actual response body ("SUCCESS" or "FAILED")
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String responseBody = br.readLine();
                    System.out.println("Notification Service response body: " + responseBody);
                    
                    // Return true only if the body says "SUCCESS"
                    return "SUCCESS".equalsIgnoreCase(responseBody);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}