<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign up • Mini-Instagram</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            background-color: #fafafa;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            flex-direction: column;
            padding: 20px 0;
        }
        .register-container {
            background: white;
            border: 1px solid #dbdbdb;
            width: 350px;
            padding: 40px;
            text-align: center;
            margin-bottom: 10px;
        }
        .logo {
            font-family: 'Cookie', cursive;
            font-size: 45px;
            margin-bottom: 15px;
            font-weight: 500;
        }
        .tagline {
            color: #8e8e8e;
            font-weight: 600;
            font-size: 17px;
            line-height: 20px;
            margin-bottom: 20px;
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
            font-size: 14px;
        }
        button:hover { background-color: #1877f2; }
        .login-box {
            background: white;
            border: 1px solid #dbdbdb;
            width: 350px;
            padding: 20px;
            text-align: center;
            font-size: 14px;
        }
        .login-box a {
            text-decoration: none;
            color: #0095f6;
            font-weight: bold;
        }
        .terms {
            color: #8e8e8e;
            font-size: 12px;
            margin-top: 20px;
            line-height: 16px;
        }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Cookie&display=swap" rel="stylesheet">
</head>
<body>

    <div class="register-container">
        <h1 class="logo">Mini - Instagram</h1>
        <p class="tagline">Sign up to see photos and videos from your friends.</p>
        
        <form action="register" method="post">
            <input type="text" name="username" placeholder="Username" required>
            <input type="password" name="password" placeholder="Password" required>
            <button type="submit">Sign up</button>
        </form>

        <p class="terms">By signing up, you agree to our Terms, Privacy Policy and Cookies Policy.</p>
    </div>

    <div class="login-box">
        Have an account? <a href="login.jsp">Log in</a>
    </div>

</body>
</html>