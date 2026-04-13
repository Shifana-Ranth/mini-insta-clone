<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login • Instagram</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            background-color: #fafafa;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            flex-direction: column;
        }
        .login-container {
            background: white;
            border: 1px solid #dbdbdb;
            width: 350px;
            padding: 40px;
            text-align: center;
            margin-bottom: 10px;
        }
        .logo {
            font-family: 'Cookie', cursive; /* You can use an <img> tag for the real logo too */
            font-size: 50px;
            margin-bottom: 30px;
            font-weight: 500;
        }
        input {
            width: 100%;
            padding: 10px;
            margin-bottom: 10px;
            background: #fafafa;
            border: 1px solid #dbdbdb;
            border-radius: 3px;
            font-size: 12px;
        }
        button {
            width: 100%;
            padding: 7px;
            background-color: #0095f6;
            border: none;
            color: white;
            font-weight: bold;
            border-radius: 8px;
            cursor: pointer;
            margin-top: 10px;
        }
        button:hover { background-color: #1877f2; }
        .signup-box {
            background: white;
            border: 1px solid #dbdbdb;
            width: 350px;
            padding: 20px;
            text-align: center;
            font-size: 14px;
        }
        .signup-box a {
            text-decoration: none;
            color: #0095f6;
            font-weight: bold;
        }
        .error-msg {
            color: #ed4956;
            font-size: 14px;
            margin-top: 15px;
        }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Cookie&display=swap" rel="stylesheet">
</head>
<body>

    <div class="login-container">
        <h1 class="logo">Mini - Instagram</h1>
        
        <form action="login" method="post">
            <input type="text" name="username" placeholder="Username" required>
            <input type="password" name="password" placeholder="Password" required>
            <button type="submit">Log in</button>
        </form>

        <p class="error-msg">${error}</p>
    </div>

    <div class="signup-box">
        Don't have an account? <a href="register.jsp">Sign up</a>
    </div>

</body>
</html>
<%-- <html>
<body>

<h2>Login</h2>

<form action="login" method="post">

Username
<input type="text" name="username">

<br><br>

Password
<input type="password" name="password">

<br><br>

<button type="submit">Login</button>

</form>

<a href="register.jsp">Create Account</a>

<p style="color:red">${error}</p>

</body>
</html> --%>