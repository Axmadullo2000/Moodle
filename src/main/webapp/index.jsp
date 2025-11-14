<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
    <link rel="shortcut icon" sizes="64x64" href="https://moodle.org/theme/moodleorg/pix/favicons/favicon.ico">
    <link rel="stylesheet" href="styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap" rel="stylesheet">
</head>
<body>
    <div class="login-container">
        <div class="tabs">
            <div class="tab active" onclick="switchTab('signin')">Sign In</div>
            <div class="tab" onclick="switchTab('signup')">Sign Up</div>
        </div>

        <div class="form-container">
            <div class="form-section active" id="signin">
            <form action="auth" method="POST">
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="hidden" name="action" value="login" id="login">
                    <input type="email" name="email" id="email" placeholder="Enter your email" required>
                </div>

                <div class="form-group">
                    <label for="signin-password">Password</label>
                    <input type="password" name="password" id="signin-password" placeholder="Enter your password" required>
                </div>

                <button type="submit" class="submit-btn">Sign In</button>
            </form>
        </div>

            <div class="form-section" id="signup">
                <form action="auth" method="post">
                    <div class="form-group">
                        <label for="signup-username">Username</label>
                        <input type="hidden" name="action" id="register" value="register">
                        <input type="text" name="username" id="signup-username" placeholder="Create a username" required>
                    </div>

                    <div class="form-group">
                        <label for="signup-full_name">Full name</label>
                        <input type="text" name="fullName" id="signup-full_name" placeholder="Create a full name" required>
                    </div>

                    <div class="form-group">
                        <label for="signup-email">Email</label>
                        <input type="email" name="email" id="signup-email" placeholder="Enter your email" required>
                    </div>

                    <div class="form-group">
                        <label for="signup-password">Password</label>
                        <input type="password" name="password" id="signup-password" placeholder="Create a password" required>
                        <div class="password-strength">
                            <div class="password-strength-bar" id="strength-bar"></div>
                        </div>
                    </div>

                    <button type="submit" class="submit-btn">Sign Up</button>
                </form>
            </div>
        </div>
    </div>

    <script>
        function switchTab(tabName) {
            const tabs = document.querySelectorAll('.tab');
            tabs.forEach(tab => tab.classList.remove('active'));
            event.target.classList.add('active');

            const forms = document.querySelectorAll('.form-section');
            forms.forEach(form => form.classList.remove('active'));
            document.getElementById(tabName).classList.add('active');
        }
    </script>
</body>
</html>