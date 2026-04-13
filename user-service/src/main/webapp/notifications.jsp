<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Notifications • Mini-Instagram</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            background-color: #fafafa;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            color: #262626;
        }
        .nav-header {
            background: white;
            border-bottom: 1px solid #dbdbdb;
            padding: 10px 20px;
            text-align: center;
            position: sticky;
            top: 0;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .logo { font-family: 'Cookie', cursive; font-size: 24px; text-decoration: none; color: black; }
        
        .notifications-container {
            max-width: 600px;
            margin: 20px auto;
            background: white;
            border: 1px solid #dbdbdb;
            border-radius: 4px;
        }
        .notif-item {
            display: flex;
            align-items: center;
            padding: 12px 16px;
            border-bottom: 1px solid #efefef;
            transition: background 0.2s;
        }
        .notif-item:hover { background-color: #fafafa; }
        .notif-item:last-child { border-bottom: none; }

        .icon-container {
            width: 44px;
            height: 44px;
            border-radius: 50%;
            background: #f0f0f0;
            display: flex;
            justify-content: center;
            align-items: center;
            margin-right: 12px;
            font-size: 20px;
        }

        .content { flex: 1; font-size: 14px; }
        .message { font-weight: 400; }
        .notif-type { 
            display: block; 
            font-size: 11px; 
            color: #8e8e8e; 
            text-transform: uppercase; 
            margin-top: 2px;
        }

        .back-link { text-decoration: none; color: #0095f6; font-weight: 600; font-size: 14px; }
        
        /* Color coding for types */
        .LIKE { color: #ed4956; } /* Red heart */
        .NEW_FOLLOW { color: #0095f6; } /* Blue follow */
        .LOGIN { color: #262626; }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Cookie&display=swap" rel="stylesheet">
</head>
<body>

    <header class="nav-header">
        <a href="profile" class="back-link">Back</a>
        <span class="logo">Notifications</span>
        <div style="width: 30px;"></div> </header>

    <div class="notifications-container">
    <%
        String rawJson = (String) request.getAttribute("notifications");
        if (rawJson != null && !rawJson.isEmpty()) {
            try {
                // Jackson Magic starts here
                ObjectMapper mapper = new ObjectMapper();
                List<Map<String, Object>> notifications = mapper.readValue(rawJson, List.class);

                for (Map<String, Object> notif : notifications) {
                    String message = (String) notif.get("message");
                    String type = (String) notif.get("type");
                    
                    String icon = "🔔";
                    if("LIKE".equals(type)) icon = "❤️";
                    else if("NEW_FOLLOW".equals(type)) icon = "👤";
                    else if("LOGIN".equals(type)) icon = "📱";
                    else if("WELCOME".equals(type)) icon = "🎉";
    %>
        <div class="notif-item">
            <div class="icon-container">
                <%= icon %>
            </div>
            <div class="content">
                <span class="message"><%= message %></span>
                <span class="notif-type"><%= type %></span>
            </div>
        </div>
    <%
                }
            } catch (Exception e) {
                out.println("<p style='color:red; padding:20px;'>Error parsing notifications: " + e.getMessage() + "</p>");
            }
        } else {
    %>
        <div style="padding: 40px; text-align: center; color: #8e8e8e;">
            No new notifications.
        </div>
    <%
        }
    %>
</div>

</body>
</html>
<%-- <html>
<body>

<h2>Your Notifications</h2>

<pre>
<%= request.getAttribute("notifications") %>
</pre>

<a href="profile">Back to Profile</a>

</body>
</html> --%>