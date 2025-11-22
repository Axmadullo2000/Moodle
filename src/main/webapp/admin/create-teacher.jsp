<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Teacher • Admin Panel</title>
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
            --card-shadow: 0 20px 50px rgba(0, 0, 0, 0.18);
            --hover-shadow: 0 35px 80px rgba(0, 0, 0, 0.28);
        }

        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Inter', 'Segoe UI', Tahoma, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 30px 20px;
            color: #333;
        }

        .form-container {
            background: white;
            border-radius: 24px;
            overflow: hidden;
            box-shadow: var(--card-shadow);
            max-width: 560px;
            width: 100%;
            transition: all 0.4s ease;
        }

        .form-container:hover {
            transform: translateY(-12px);
            box-shadow: var(--hover-shadow);
        }

        .form-header {
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            padding: 50px 40px;
            text-align: center;
            color: white;
        }

        .form-icon {
            width: 90px;
            height: 90px;
            background: rgba(255,255,255,0.2);
            backdrop-filter: blur(10px);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 20px;
            font-size: 38px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        }

        .form-title {
            font-size: 32px;
            font-weight: 700;
            margin-bottom: 10px;
        }

        .form-subtitle {
            font-size: 17px;
            opacity: 0.95;
        }

        .form-body {
            padding: 44px 40px;
        }

        .error-alert {
            background: #f8d7da;
            color: #721c24;
            padding: 16px 20px;
            border-radius: 14px;
            border-left: 5px solid var(--danger);
            margin-bottom: 28px;
            font-size: 15px;
            font-weight: 500;
        }

        .form-group {
            margin-bottom: 28px;
        }

        label {
            display: block;
            margin-bottom: 10px;
            font-weight: 600;
            color: var(--dark);
            font-size: 15px;
        }

        .required {
            color: var(--danger);
            margin-left: 4px;
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

        .help-text {
            font-size: 13px;
            color: var(--gray);
            margin-top: 8px;
            display: block;
        }

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

        .button-group {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
            margin-top: 32px;
        }

        .btn {
            padding: 18px 20px;
            border: none;
            border-radius: 14px;
            font-weight: 600;
            font-size: 16px;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
        }

        .btn-primary {
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            color: white;
        }

        .btn-primary:hover {
            transform: translateY(-4px);
            box-shadow: 0 15px 35px rgba(102, 126, 234, 0.4);
        }

        .btn-secondary {
            background: #e9ecef;
            color: var(--dark);
        }

        .btn-secondary:hover {
            background: #dee2e6;
            transform: translateY(-4px);
        }

        @media (max-width: 576px) {
            .form-header { padding: 40px 20px; }
            .form-icon { width: 80px; height: 80px; font-size: 34px; }
            .form-title { font-size: 28px; }
            .form-body { padding: 36px 24px; }
            .button-group { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>

<div class="form-container">
    <!-- Header -->
    <div class="form-header">
        <div class="form-icon">Teacher</div>
        <div class="form-title">Create New Teacher</div>
        <div class="form-subtitle">Add a new instructor to the university platform</div>
    </div>

    <!-- Body -->
    <div class="form-body">

        <!-- Error Message -->
        <% if (request.getAttribute("error") != null) { %>
        <div class="error-alert">
            ${error}
        </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/admin/create-teacher" method="POST">

            <div class="form-group">
                <label for="fullName">
                    Full Name <span class="required">*</span>
                </label>
                <input
                        type="text"
                        id="fullName"
                        name="fullName"
                        value="${fullName != null ? fullName : ''}"
                        placeholder="Dr. Sarah Johnson"
                        required
                        autofocus
                />
                <span class="help-text">Enter the teacher's full name as it should appear</span>
            </div>

            <div class="form-group">
                <label for="email">
                    Email Address <span class="required">*</span>
                </label>
                <input
                        type="email"
                        id="email"
                        name="email"
                        value="${email != null ? email : ''}"
                        placeholder="sarah.johnson@university.edu"
                        required
                />
                <span class="help-text">This will be used for login and notifications</span>
            </div>

            <div class="form-group">
                <label for="password">
                    Password <span class="required">*</span>
                </label>
                <input
                        type="password"
                        id="password"
                        name="password"
                        placeholder="Create a secure password"
                        minlength="6"
                        required
                />
                <div class="password-strength">
                    <div class="strength-bar" id="strength-bar"></div>
                </div>
                <span class="help-text">Minimum 6 characters • Use letters, numbers, and symbols for strength</span>
            </div>

            <div class="form-group">
                <label for="specialization">Specialization (Optional)</label>
                <input
                        type="text"
                        id="specialization"
                        name="specialization"
                        value="${specialization != null ? specialization : ''}"
                        placeholder="e.g. Computer Science, Mathematics, Physics"
                />
                <span class="help-text">Helps identify the teacher's primary subject area</span>
            </div>

            <div class="button-group">
                <button type="button" class="btn btn-secondary"
                        onclick="window.location.href='${pageContext.request.contextPath}/admin/dashboard'">
                    Cancel
                </button>
                <button type="submit" class="btn btn-primary">
                    Create Teacher
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    // Password strength indicator
    document.getElementById('password').addEventListener('input', function() {
        const val = this.value;
        const bar = document.getElementById('strength-bar');
        let strength = 0;

        if (val.length >= 6) strength += 20;
        if (val.length >= 8) strength += 15;
        if (val.match(/[a-z]/) && val.match(/[A-Z]/)) strength += 25;
        if (val.match(/[0-9]/)) strength += 20;
        if (val.match(/[^a-zA-Z0-9]/)) strength += 20;

        bar.style.width = Math.min(strength, 100) + '%';
        bar.className = 'strength-bar';
        if (strength < 50) bar.classList.add('weak');
        else if (strength < 80) bar.classList.add('medium');
        else bar.classList.add('strong');
    });
</script>

</body>
</html>