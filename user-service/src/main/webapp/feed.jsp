<%@ page import="java.util.List" %>
<%@ page import="com.miniinstagram.user.model.Post" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mini Instagram • Home</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { 
            background-color: #ffffff; 
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            color: #262626;
            padding-bottom: 60px;
            padding-top: 50px;
        }

        /* Fixed Top Header */
        .header {
            background: white;
            border-bottom: 1px solid #dbdbdb;
            padding: 10px 16px;
            position: fixed;
            top: 0; width: 100%; z-index: 1000;
            display: flex; justify-content: space-between; align-items: center;
        }
        .logo { font-family: 'Cookie', cursive; font-size: 26px; text-decoration: none; color: black; }

        .feed-wrapper { max-width: 500px; margin: 0 auto; }

        /* Modern Post Card */
        .post-card { background: white; margin-bottom: 10px; border-bottom: 1px solid #efefef; }
        .post-header { 
            padding: 12px 14px; display: flex; align-items: center; 
        }
        .avatar-small { 
            width: 32px; height: 32px; 
            background: linear-gradient(45deg, #f09433, #e6683c, #dc2743, #cc2366, #bc1888); 
            border-radius: 50%; margin-right: 10px; 
            padding: 2px;
        }
        .avatar-inner { background: #efefef; width: 100%; height: 100%; border-radius: 50%; border: 2px solid white; }
        
        .user-badge { font-weight: 600; font-size: 14px; text-decoration: none; color: #262626; }
        
        .post-body { 
            padding: 15px 14px; font-size: 15px; line-height: 1.5; 
            color: #262626;
        }

        .post-footer { padding: 0 14px 15px 14px; }
        
        /* Action row: Like, Comment, Share */
        .actions { display: flex; gap: 16px; margin-bottom: 8px; align-items: center; }
        
        .like-btn { background: none; border: none; cursor: pointer; font-size: 24px; padding: 0; outline: none; }
        .liked { color: #ed4956; } 

        .footer-meta {
            display: flex;
            justify-content: space-between;
            align-items: baseline;
            margin-top: 4px;
        }

        .likes-count { font-weight: 700; font-size: 14px; color: #262626; }
        
        .post-id-label { 
            font-size: 10px; 
            color: #8e8e8e; 
            text-transform: uppercase; 
            letter-spacing: 0.5px;
        }

        /* Fixed Bottom Navigation */
        .bottom-nav {
            position: fixed; bottom: 0; width: 100%; background: white;
            border-top: 1px solid #dbdbdb; display: flex; justify-content: space-around;
            padding: 12px 0; z-index: 1000;
        }
        .nav-icon { text-decoration: none; color: #262626; font-size: 24px; }

        .empty-msg { text-align: center; color: #8e8e8e; padding: 100px 20px; }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Cookie&display=swap" rel="stylesheet">
</head>
<body>

    <header class="header">
        <a href="homeFeed" class="logo">Mini Instagram</a>
        <div style="font-size: 22px;"><span>❤️</span> <span style="margin-left:15px;">💬</span></div>
    </header>

    <div class="feed-wrapper">
        <%
            List<Post> feedPosts = (List<Post>) request.getAttribute("feedPosts");
            if (feedPosts != null && !feedPosts.isEmpty()) {
                for (Post post : feedPosts) {
                    boolean isLiked = post.isLikedByCurrentUser();
        %>
                <div class="post-card">
                    <div class="post-header">
                        <div class="avatar-small"><div class="avatar-inner"></div></div>
                        <a href="viewUser?userId=<%= post.getAuthorId() %>" class="user-badge">
                            <%= post.getUsername() %>
                        </a>
                    </div>

                    <div class="post-body">
                        <%= post.getContent() %>
                    </div>

                    <div class="post-footer">
                        <div class="actions">
                            <button class="like-btn <%= isLiked ? "liked" : "" %>" 
                                onclick="toggleLike(this, <%= post.getId() %>, <%= post.getAuthorId() %>)"
                                data-liked="<%= isLiked %>">
                                <%= isLiked ? "❤️" : "🤍" %>
                            </button>
                            <span style="font-size: 22px; cursor: pointer;">💬</span>
                            <span style="font-size: 22px; cursor: pointer;">✈️</span>
                        </div>
                        
                        <div class="footer-meta">
                            <span class="likes-count"><%= post.getLikesCount() %> likes</span>
                            <span class="post-id-label">#<%= post.getId() %></span>
                        </div>
                    </div>
                </div>
        <%
                }
            } else {
        %>
                <div class="empty-msg">
                    <h3>Welcome to Mini Instagram</h3>
                    <p>Follow users to see their photos and videos here.</p>
                </div>
        <%
            }
        %>
    </div>

    <nav class="bottom-nav">
        <a href="homeFeed" class="nav-icon">🏠</a>
        <a href="#" class="nav-icon">🔍</a>
        <a href="#" class="nav-icon">➕</a>
        <a href="notifications" class="nav-icon">🔔</a>
        <a href="profile" class="nav-icon">👤</a>
    </nav>

<script>
function toggleLike(button, postId, postOwnerId){
    const userId = <%= session.getAttribute("userId") %>;
    let currentlyLiked = button.getAttribute("data-liked") === "true";
    let action = currentlyLiked ? "unlike" : "like";

    const url = "<%= request.getContextPath() %>/like"
        + "?action=" + action
        + "&userId=" + userId
        + "&postId=" + postId
        + "&postOwnerId=" + postOwnerId;

    fetch(url)
    .then(res => res.text())
    .then(data => {
        if(action === "like"){
            button.setAttribute("data-liked", "true");
            button.innerText = "❤️";
            button.classList.add("liked");
        } else {
            button.setAttribute("data-liked", "false");
            button.innerText = "🤍";
            button.classList.remove("liked");
        }
        
        // Refresh to update count
        setTimeout(() => { window.location.reload(); }, 400);
    });
}
</script>

</body>
</html>
<%-- <%@ page import="java.util.List" %>
<%@ page import="com.miniinstagram.user.model.Post" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Mini Instagram | Home Feed</title>
    <style>
        body { background-color: #fafafa; font-family: sans-serif; margin: 0; }
        .navbar { background: white; border-bottom: 1px solid #dbdbdb; padding: 15px; text-align: center; position: sticky; top: 0; z-index: 100; }
        .feed-wrapper { max-width: 500px; margin: 30px auto; }
        .post-card { background: white; border: 1px solid #dbdbdb; border-radius: 8px; margin-bottom: 25px; }
        .post-header { padding: 12px 16px; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #efefef; }
        .user-badge { font-weight: 600; color: #262626; font-size: 14px; }
        .post-id { color: #8e8e8e; font-size: 11px; }
        .post-body { padding: 20px 16px; font-size: 15px; line-height: 1.5; color: #262626; min-height: 60px; }
        .post-footer { padding: 10px 16px; border-top: 1px solid #efefef; display: flex; gap: 15px; }
        .btn{
    background:#0095f6;
    border:none;
    color:white;
    font-size:13px;
    padding:6px 10px;
    border-radius:4px;
    cursor:pointer;
}
        .empty-msg { text-align: center; color: #8e8e8e; margin-top: 100px; }
    </style>
</head>
<body>

<div class="navbar">
    <strong style="font-size: 1.2em;">Mini Instagram</strong> 
    <span style="margin: 0 15px; color: #ccc;">|</span>
    <a href="profile" style="text-decoration: none; color: #0095f6; font-weight: bold;">My Profile</a>
</div>

<div class="feed-wrapper">
    <%
        // Fetch the list from the request attribute
        List<Post> feedPosts = (List<Post>) request.getAttribute("feedPosts");
	
        if (feedPosts != null && !feedPosts.isEmpty()) {
        	//System.out.println("DEBUG: Post from DB likes: " + feedPosts.get(0).getLikesCount());
            for (Post post : feedPosts) {
    %>
                <div class="post-card">
                    <div class="post-header">
                        <span class="user-badge"><a href="viewUser?userId=<%= post.getAuthorId() %>">
   @<%= post.getUsername() %>
</a></span>
                        <span class="post-id">Post #<%= post.getId() %></span>
                    </div>

                    <div class="post-body">
                        <%= post.getContent() %>
                    </div>

                    <div class="post-footer">
         <button class="btn like-btn"
    data-liked="<%= post.isLikedByCurrentUser() %>"
    onclick="toggleLike(this, <%= post.getId() %>, <%= post.getAuthorId() %>)">

<%= post.isLikedByCurrentUser() ? "Unlike" : "Like" %>

</button>            

<span>Likes: <%= post.getLikesCount() %></span>
                        <button class="btn">💬</button>
                        <button class="btn">✈️</button>
                    </div>
                </div>
    <%
            }
        } else {
    %>
            <div class="empty-msg">
                <h3>Your Feed is Empty</h3>
                <p>Try following some users to see their latest posts here!</p>
            </div>
    <%
        }
    %>
</div>

</body>
</html>
<script>
function toggleLike(button, postId, postOwnerId){

    const userId = <%= session.getAttribute("userId") %>;

    let action = button.getAttribute("data-liked") === "true" ? "unlike" : "like";

    const url = "<%= request.getContextPath() %>/like"
        + "?action=" + action
        + "&userId=" + userId
        + "&postId=" + postId
        + "&postOwnerId=" + postOwnerId;

    fetch(url)
    .then(res => res.text())
    .then(data => {

        console.log("Response:", data);

        if(action === "like"){
            button.setAttribute("data-liked", "true");
            button.innerText = "Unlike";
        } else {
            button.setAttribute("data-liked", "false");
            button.innerText = "Like";
        }

        // give Kafka + post-service time to update likes_count
        setTimeout(function(){
            window.location.reload();
        }, 700);

    }).catch(err => {
    	   console.error("Fetch error:", err);
    });
}

</script> --%>