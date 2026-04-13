<%@ page import="com.miniinstagram.user.model.User" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    User u = (User) request.getAttribute("targetUser");
    if(u == null || session == null || session.getAttribute("userId") == null){
        response.sendRedirect("homeFeed"); // Better to redirect to feed than home.jsp
        return;
    }
    long currentUserIdJsp = (Long) session.getAttribute("userId");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= u.getUsername() %> (@<%= u.getUsername() %>) • Mini-Instagram</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            background-color: #fafafa;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            color: #262626;
            padding-bottom: 60px;
        }

        /* Navbar */
        .navbar {
            background: white; border-bottom: 1px solid #dbdbdb;
            padding: 10px 20px; text-align: center; position: sticky; top: 0; z-index: 100;
        }
        .logo { font-family: 'Cookie', cursive; font-size: 28px; text-decoration: none; color: black; }

        /* Profile Header Wrapper */
        .profile-container { max-width: 935px; margin: 30px auto; padding: 0 20px; }
        
        .profile-header { display: flex; align-items: center; margin-bottom: 44px; }
        .avatar-section { flex: 1; text-align: center; }
        .profile-photo {
            width: 150px; height: 150px; border-radius: 50%;
            object-fit: cover; border: 1px solid #dbdbdb;
        }

        .info-section { flex: 2; }
        .username-row { display: flex; align-items: center; gap: 20px; margin-bottom: 20px; }
        .username { font-size: 28px; font-weight: 300; }
        
        /* Stats Row */
        .stats-row { display: flex; gap: 40px; margin-bottom: 20px; list-style: none; }
        .stats-row span { font-weight: bold; }
        
        .bio-row { font-size: 16px; line-height: 24px; }
        .bio-name { font-weight: bold; display: block; }

        /* Buttons */
        .btn {
            padding: 5px 24px; border-radius: 4px; font-weight: 600; font-size: 14px;
            cursor: pointer; border: 1px solid #dbdbdb;
        }
        .btn-follow { background-color: #0095f6; color: white; border: none; }
        .btn-unfollow { background-color: white; color: #262626; }

        /* Posts Grid */
        .posts-grid {
            display: grid; grid-template-columns: repeat(3, 1fr);
            gap: 28px; border-top: 1px solid #dbdbdb; padding-top: 40px;
        }
        .post-card {
            background: white; border: 1px solid #dbdbdb; aspect-ratio: 1/1;
            display: flex; flex-direction: column; justify-content: space-between;
            padding: 15px; text-align: center; position: relative;
        }
        .post-content { font-size: 14px; flex-grow: 1; display: flex; align-items: center; justify-content: center; }
        .post-actions { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }
        .like-btn-small { background: none; border: none; font-size: 18px; cursor: pointer; }
        .likes-count-small { font-size: 12px; font-weight: bold; }

        /* Bottom Nav */
        .bottom-nav {
            position: fixed; bottom: 0; width: 100%; background: white;
            border-top: 1px solid #dbdbdb; display: flex; justify-content: space-around;
            padding: 12px 0; z-index: 1000;
        }
        .nav-icon { text-decoration: none; color: #262626; font-size: 22px; }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Cookie&display=swap" rel="stylesheet">
</head>
<body>

    <nav class="navbar">
        <a href="homeFeed" class="logo">Mini-Instagram</a>
    </nav>

    <main class="profile-container">
        <header class="profile-header">
            <div class="avatar-section">
                <img src="<%= (u.getProfilePhotoUrl() != null && !u.getProfilePhotoUrl().isEmpty()) ? u.getProfilePhotoUrl() : "https://via.placeholder.com/150" %>" 
                     class="profile-photo" alt="Profile Photo">
            </div>

            <section class="info-section">
                <div class="username-row">
                    <h2 class="username"><%= u.getUsername() %></h2>
                    
                    <form action="user-follow" method="post">
                        <input type="hidden" name="userId" value="<%= u.getId() %>">
                        <input type="hidden" name="isFollow" value="<%= u.getIsFollowed() ? "false" : "true" %>">
                        <input type="hidden" name="redirect" value="viewUser?userId=<%= u.getId() %>">
                        
                        <button type="submit" class="btn <%= u.getIsFollowed() ? "btn-unfollow" : "btn-follow" %>">
                            <%= u.getIsFollowed() ? "Following" : "Follow" %>
                        </button>
                    </form>
                </div>

                <ul class="stats-row">
                    <li><span id="post-count-label">0</span> posts</li>
                    <li><span><%= u.getFollowerCount() %></span> followers</li>
                    <li><span><%= u.getFollowingCount() %></span> following</li>
                </ul>

                <div class="bio-row">
                    <span class="bio-name"><%= u.getUsername() %></span>
                    <p><%= u.getDescription() != null ? u.getDescription() : "No bio yet." %></p>
                </div>
            </section>
        </header>

        <div id="posts-grid" class="posts-grid">
            <div style="grid-column: 1/4; text-align: center; padding: 40px; color: #8e8e8e;">Loading posts...</div>
        </div>
    </main>

    <nav class="bottom-nav">
        <a href="homeFeed" class="nav-icon">🏠</a>
        <a href="searchUsers" class="nav-icon">🔍</a>
        <a href="#" class="nav-icon">➕</a>
        <a href="notifications" class="nav-icon">🔔</a>
        <a href="profile" class="nav-icon">👤</a>
    </nav>

<script>
const postsGrid = document.getElementById("posts-grid");
const postCountLabel = document.getElementById("post-count-label");
const currentUserId = <%= currentUserIdJsp %>;

function loadPosts(){
    fetch("<%= request.getContextPath() %>/userPostsData?userId=<%= u.getId() %>")
    .then(res => res.json())
    .then(data => {
        postsGrid.innerHTML = "";
        postCountLabel.innerText = data.length || 0;

        if(!data || data.length === 0){
            postsGrid.innerHTML = "<div style='grid-column: 1/4; text-align:center; padding: 50px; color:#8e8e8e;'>No posts yet.</div>";
            return;
        }

        data.forEach(p => {
            const isLiked = p.likedByCurrentUser || false;
            const card = document.createElement("div");
            card.className = "post-card";
            
            card.innerHTML = `
                <div class="post-content">\${p.content || ""}</div>
                <div class="post-actions">
                    <span class="likes-count-small">❤️ \${p.likes || 0}</span>
                    <button class="like-btn-small" onclick="toggleLike(this, \${p.postId}, \${p.userId})" data-liked="\${isLiked}">
                        \${isLiked ? '❤️' : '🤍'}
                    </button>
                </div>
            `;
            postsGrid.appendChild(card);
        });
    })
    .catch(err => {
        postsGrid.innerHTML = "<p>Error loading posts.</p>";
    });
}

function toggleLike(button, postId, postOwnerId){
    let currentlyLiked = button.getAttribute("data-liked") === "true";
    let action = currentlyLiked ? "unlike" : "like";

    const url = "<%= request.getContextPath() %>/like"
        + "?action=" + action
        + "&userId=" + currentUserId
        + "&postId=" + postId
        + "&postOwnerId=" + postOwnerId;

    fetch(url)
    .then(res => res.text())
    .then(data => {
        // Optimistic toggle
        if(action === "like"){
            button.setAttribute("data-liked", "true");
            button.innerText = "❤️";
        } else {
            button.setAttribute("data-liked", "false");
            button.innerText = "🤍";
        }
        setTimeout(loadPosts, 500);
    });
}

loadPosts();
</script>

</body>
</html>
<%-- <%@ page import="com.miniinstagram.user.model.User" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    User u = (User) request.getAttribute("targetUser");

    if(u == null || session == null || session.getAttribute("userId") == null){
        response.sendRedirect("home.jsp");
        return;
    }

    long currentUserIdJsp = (Long) session.getAttribute("userId");
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title><%= u.getUsername() %>'s Profile</title>

<style>

body{
    font-family: Arial, sans-serif;
    background:#fafafa;
}

.header{
    border-bottom:1px solid #dbdbdb;
    padding:20px;
    margin-bottom:20px;
    background:white;
    text-align:center;
}

.post-container{
    display:grid;
    grid-template-columns:repeat(3,1fr);
    gap:15px;
    max-width:900px;
    margin:auto;
}

.post-box{
    border:1px solid #dbdbdb;
    padding:15px;
    background:white;
    border-radius:4px;
}

.post-footer{
    margin-top:10px;
    display:flex;
    align-items:center;
    gap:10px;
}

.btn{
    cursor:pointer;
    padding:5px 10px;
    border-radius:4px;
    border:1px solid #dbdbdb;
    background:#0095f6;
    color:white;
}

.follow-btn{
    margin-top:10px;
}

</style>
</head>

<body>

<div class="header">

<h1><%= u.getUsername() %></h1>

<p>
<%= u.getDescription() != null ? u.getDescription() : "No bio yet." %>
</p>

<p>
Followers: <strong><%= u.getFollowerCount() %></strong>
|
Following: <strong><%= u.getFollowingCount() %></strong>
</p>

<form action="user-follow" method="post" class="follow-btn">

<input type="hidden" name="userId" value="<%= u.getId() %>">

<input type="hidden" name="isFollow"
value="<%= u.getIsFollowed() ? "false" : "true" %>">

<input type="hidden" name="redirect"
value="viewUser?userId=<%= u.getId() %>">

<input type="submit" class="btn"
value="<%= u.getIsFollowed() ? "Unfollow" : "Follow" %>">

</form>

</div>



<div style="max-width:900px;margin:auto">

<h3>Posts</h3>

<div id="posts-grid" class="post-container">
Loading posts...
</div>

</div>


<script>

const postsGrid = document.getElementById("posts-grid");

const currentUserId = <%= currentUserIdJsp %>;

function loadPosts(){

    console.log("Fetching posts for target user:", <%= u.getId() %>);

    fetch("<%= request.getContextPath() %>/userPostsData?userId=<%= u.getId() %>")

    .then(res => {

        console.log("Response status:", res.status);

        if(!res.ok){
            throw new Error("Server error " + res.status);
        }

        return res.json();
    })

    .then(data => {

        console.log("Received JSON:", data);

        postsGrid.innerHTML = "";

        if(!data || data.length === 0){

            postsGrid.innerHTML = "<p>No posts yet.</p>";
            return;
        }

        data.forEach(p => {

            console.log("Rendering post:", p);

            const postBox = document.createElement("div");
            postBox.classList.add("post-box");

            const content = p.content || "No content";
            const postId = p.postId;
            const authorId = p.userId;
            const likes = p.likes || 0;
            const isLiked = p.likedByCurrentUser || false;


            const text = document.createElement("p");
            text.textContent = content;


            const footer = document.createElement("div");
            footer.classList.add("post-footer");


            const likeBtn = document.createElement("button");
            likeBtn.classList.add("btn","like-btn");
            likeBtn.setAttribute("data-liked", isLiked);

            likeBtn.innerText = isLiked ? "Unlike" : "Like";

            likeBtn.onclick = function(){
                toggleLike(this, postId, authorId);
            };


            const likeText = document.createElement("span");
            likeText.innerHTML = "Likes: <strong>" + likes + "</strong>";


            footer.appendChild(likeBtn);
            footer.appendChild(likeText);


            postBox.appendChild(text);
            postBox.appendChild(footer);


            postsGrid.appendChild(postBox);

        });

    })

    .catch(err => {

        console.error("Fetch error:", err);

        postsGrid.innerHTML =
        "<p>Error loading posts. Check console.</p>";

    });

}



function toggleLike(button, postId, postOwnerId){

    let action =
    button.getAttribute("data-liked") === "true"
    ? "unlike"
    : "like";

    const url =
    "<%= request.getContextPath() %>/like"
    + "?action=" + action
    + "&userId=" + currentUserId
    + "&postId=" + postId
    + "&postOwnerId=" + postOwnerId;


    console.log("Attempting to", action, "post", postId);


    fetch(url)

    .then(res => res.text())

    .then(data => {

        console.log("Like response:", data);

        if(action === "like"){

            button.setAttribute("data-liked","true");
            button.innerText = "Unlike";

        }else{

            button.setAttribute("data-liked","false");
            button.innerText = "Like";
        }


        setTimeout(() => {
        	loadPosts();
            //window.location.reload();

        },700);

    })

    .catch(err => console.error("Like error:", err));

}


loadPosts();

</script>

</body>
</html> --%>