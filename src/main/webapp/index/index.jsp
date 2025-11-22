<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In University Moodle</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary: #667eea;
            --primary-dark: #5568d3;
            --success: #28a745;
            --danger: #dc3545;
            --light: #f8f9fa;
            --dark: #343a40;
            --gray: #6c757d;
            --border: #dee2e6;
            --card-shadow: 0 25px 60px rgba(0, 0, 0, 0.22);
            --hover-shadow: 0 35px 80px rgba(0, 0, 0, 0.3);
        }

        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Inter', 'Segoe UI', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            color: #333;
        }

        .auth-container {
            width: 100%;
            max-width: 480px;
            background: rgba(255, 255, 255, 0.97);
            backdrop-filter: blur(16px);
            border-radius: 24px;
            overflow: hidden;
            box-shadow: var(--card-shadow);
            transition: all 0.4s ease;
        }

        .auth-container:hover {
            transform: translateY(-12px);
            box-shadow: var(--hover-shadow);
        }

        .auth-header {
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            padding: 50px 40px;
            text-align: center;
            color: white;
        }

        .logo {
            width: 90px;
            height: 90px;
            background: rgba(255,255,255,0.25);
            backdrop-filter: blur(10px);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 20px;
            font-size: 38px;
            font-weight: 700;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        }

        .auth-title {
            font-size: 32px;
            font-weight: 700;
            margin-bottom: 8px;
        }

        .auth-subtitle {
            font-size: 17px;
            opacity: 0.9;
        }

        .tabs {
            display: flex;
            background: #f1f3f5;
        }

        .tab {
            flex: 1;
            padding: 20px;
            text-align: center;
            font-weight: 600;
            font-size: 16px;
            color: var(--gray);
            cursor: pointer;
            transition: all 0.3s ease;
            position: relative;
        }

        .tab::after {
            content: '';
            position: absolute;
            bottom: 0;
            left: 50%;
            width: 0;
            height: 4px;
            background: var(--primary);
            transition: all 0.3s ease;
            transform: translateX(-50%);
        }

        .tab.active {
            color: var(--primary);
            background: white;
        }

        .tab.active::after {
            width: 60%;
        }

        .form-container {
            padding: 44px 40px;
            background: white;
        }

        .form-section {
            display: none;
            animation: fadeIn 0.5s ease forwards;
        }

        .form-section.active {
            display: block;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(15px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .form-group {
            margin-bottom: 24px;
        }

        label {
            display: block;
            margin-bottom: 10px;
            font-weight: 600;
            color: var(--dark);
            font-size: 14px;
        }

        input {
            width: 100%;
            padding: 16px 18px;
            border: 2px solid #e2e8f0;
            border-radius: 14px;
            font-size: 15px;
            background: #f8fafc;
            transition: all 0.3s ease;
        }

        input:focus {
            outline: none;
            border-color: var(--primary);
            background: white;
            box-shadow: 0 0 0 5px rgba(102, 126, 234, 0.15);
        }

        .submit-btn {
            width: 100%;
            padding: 18px;
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            color: white;
            border: none;
            border-radius: 14px;
            font-size: 16px;
            font-weight: 700;
            cursor: pointer;
            text-transform: uppercase;
            letter-spacing: 1px;
            transition: all 0.3s ease;
            margin-top: 10px;
        }

        .submit-btn:hover {
            transform: translateY(-4px);
            box-shadow: 0 15px 35px rgba(102, 126, 234, 0.4);
        }

        .footer-text {
            text-align: center;
            margin-top: 28px;
            font-size: 14px;
            color: var(--gray);
        }

        .footer-text a {
            color: var(--primary);
            font-weight: 600;
            text-decoration: none;
        }

        .footer-text a:hover {
            text-decoration: underline;
        }

        /* Password Strength */
        .password-strength {
            height: 6px;
            background: #e2e8f0;
            border-radius: 3px;
            margin-top: 10px;
            overflow: hidden;
        }

        .strength-bar {
            height: 100%;
            width: 0;
            transition: all 0.4s ease;
            border-radius: 3px;
        }

        .weak { background: #f56565; }
        .medium { background: #ed8936; }
        .strong { background: #48bb78; }

        /* Responsive */
        @media (max-width: 480px) {
            .auth-container { border-radius: 20px; }
            .auth-header { padding: 40px 20px; }
            .logo { width: 80px; height: 80px; font-size: 34px; }
            .auth-title { font-size: 28px; }
            .form-container { padding: 36px 24px; }
        }
    </style>
</head>
<body>

<div class="auth-container">
    <!-- Header -->
    <div class="auth-header">
        <div class="logo">M</div>
        <div class="auth-title">University Moodle</div>
        <div class="auth-subtitle">Welcome back! Please sign in to continue</div>
    </div>

    <!-- Tabs -->
    <div class="tabs">
        <div class="tab active" onclick="switchTab('signin')">Sign In</div>
        <div class="tab" onclick="switchTab('signup')">Sign Up</div>
    </div>

    <!-- Forms -->
    <div class="form-container">

        <!-- Sign In -->
        <div class="form-section active" id="signin">
            <form action="${pageContext.request.contextPath}/auth" method="POST">
                <input type="hidden" name="action" value="login">

                <div class="form-group">
                    <label for="login-email">Email Address</label>
                    <input type="email" name="email" id="login-email" placeholder="student@university.edu" required>
                </div>

                <div class="form-group">
                    <label for="login-password">Password</label>
                    <input type="password" name="password" id="login-password" placeholder="Enter password" required>
                </div>

                <button type="submit" class="submit-btn">Sign In</button>
            </form>

            <div class="footer-text">
                Don't have an account? <a href="javascript:switchTab('signup')">Sign up here</a>
            </div>
        </div>

        <!-- Sign Up -->
        <div class="form-section" id="signup">
            <form action="${pageContext.request.contextPath}/auth" method="POST">
                <input type="hidden" name="action" value="register">

                <div class="form-group">
                    <label for="fullName">Full Name</label>
                    <input type="text" name="fullName" id="fullName" placeholder="John Doe" required>
                </div>

                <div class="form-group">
                    <label for="signup-email">Email Address</label>
                    <input type="email" name="email" id="signup-email" placeholder="john@university.edu" required>
                </div>

                <div class="form-group">
                    <label for="signup-password">Password</label>
                    <input type="password" name="password" id="signup-password" placeholder="Create strong password" required>
                    <div class="password-strength">
                        <div class="strength-bar" id="strength-bar"></div>
                    </div>
                </div>

                <button type="submit" class="submit-btn">Create Account</button>
            </form>

            <div class="footer-text">
                Already have an account? <a href="javascript:switchTab('signin')">Sign in</a>
            </div>
        </div>
    </div>
</div>

<script>
    function switchTab(tab) {
        document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
        document.querySelectorAll('.form-section').forEach(f => f.classList.remove('active'));

        event.target.classList.add('active');
        document.getElementById(tab).classList.add('active');
    }

    // Password strength (optional enhancement)
    document.getElementById('signup-password')?.addEventListener('input', function() {
        const val = this.value;
        const bar = document.getElementById('strength-bar');
        let strength = 0;

        if (val.length >= 8) strength += 25;
        if (val.match(/[a-z]/) && val.match(/[A-Z]/)) strength += 25;
        if (val.match(/[0-9]/)) strength += 25;
        if (val.match(/[^a-zA-Z0-9]/)) strength += 25;

        bar.style.width = strength + '%';
        bar.className = 'strength-bar';
        if (strength < 50) bar.classList.add('weak');
        else if (strength < 75) bar.classList.add('medium');
        else bar.classList.add('strong');
    });
</script>

</body>
</html>