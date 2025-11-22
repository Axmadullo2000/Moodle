<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Profile • ${fullName}</title>
    <style>
        :root {
            --primary: #667eea;
            --primary-dark: #5568d3;
            --success: #28a745;
            --warning: #ffc107;
            --danger: #dc3545;
            --info: #17a2b8;
            --light: #f8f9fa;
            --dark: #343a40;
            --gray: #6c757d;
            --border: #dee2e6;
            --card-shadow: 0 20px 50px rgba(0, 0, 0, 0.18);
            --hover-shadow: 0 30px 70px rgba(0, 0, 0, 0.25);
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

        .profile-container {
            background: white;
            border-radius: 24px;
            overflow: hidden;
            box-shadow: var(--card-shadow);
            max-width: 560px;
            width: 100%;
            transition: all 0.4s ease;
        }

        .profile-container:hover {
            transform: translateY(-10px);
            box-shadow: var(--hover-shadow);
        }

        .profile-header {
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            padding: 50px 40px;
            text-align: center;
            color: white;
            position: relative;
        }

        .avatar {
            width: 120px;
            height: 120px;
            background: rgba(255,255,255,0.2);
            backdrop-filter: blur(10px);
            border: 4px solid white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 48px;
            font-weight: 700;
            margin: 0 auto 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        }

        .profile-name {
            font-size: 32px;
            font-weight: 700;
            margin-bottom: 10px;
        }

        .profile-role {
            display: inline-flex;
            align-items: center;
            gap: 10px;
            background: rgba(255,255,255,0.2);
            padding: 10px 24px;
            border-radius: 50px;
            font-size: 15px;
            font-weight: 600;
            backdrop-filter: blur(10px);
        }

        .profile-body {
            padding: 40px;
        }

        .info-grid {
            display: grid;
            gap: 20px;
            margin-bottom: 32px;
        }

        .info-card {
            background: #f8fafc;
            border-radius: 16px;
            padding: 20px;
            border-left: 5px solid var(--primary);
        }

        .info-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid #e2e8f0;
        }

        .info-row:last-child { border-bottom: none; }

        .info-label {
            font-weight: 600;
            color: var(--gray);
            font-size: 14px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .info-value {
            font-weight: 600;
            color: var(--dark);
            font-size: 16px;
            text-align: right;
            max-width: 60%;
            word-break: break-word;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
            gap: 16px;
            margin: 28px 0;
        }

        .stat-item {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 16px;
            text-align: center;
            box-shadow: 0 8px 25px rgba(102,126,234,0.3);
        }

        .stat-value {
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 6px;
        }

        .stat-label {
            font-size: 13px;
            opacity: 0.9;
            text-transform: uppercase;
            letter-spacing: 0.8px;
        }

        .action-buttons {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
            margin-bottom: 24px;
        }

        .btn {
            padding: 16px 20px;
            border: none;
            border-radius: 14px;
            font-weight: 600;
            font-size: 15px;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        .btn-primary:hover {
            transform: translateY(-4px);
            box-shadow: 0 15px 35px rgba(102,126,234,0.4);
        }

        .btn-success {
            background: linear-gradient(135deg, #56ab2f 0%, #a8e063 100%);
            color: white;
        }

        .btn-success:hover {
            transform: translateY(-4px);
            box-shadow: 0 15px 35px rgba(86,171,47,0.4);
        }

        .btn-logout {
            grid-column: 1 / -1;
            background: #e74c3c;
            color: white;
            padding: 18px;
            font-size: 16px;
        }

        .btn-logout:hover {
            background: #c0392b;
            transform: translateY(-4px);
            box-shadow: 0 15px 35px rgba(220,53,69,0.4);
        }

        /* Responsive */
        @media (max-width: 768px) {
            .profile-header { padding: 40px 20px; }
            .avatar { width: 100px; height: 100px; font-size: 40px; }
            .profile-name { font-size: 28px; }
            .action-buttons { grid-template-columns: 1fr; }
            .info-row { flex-direction: column; align-items: flex-start; gap: 8px; }
            .info-value { text-align: left; max-width: 100%; }
            .stats-grid { grid-template-columns: 1fr 1fr; }
        }

        @media (max-width: 480px) {
            .profile-body { padding: 30px 20px; }
            .stats-grid { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>

<div class="profile-container">
    <!-- Header -->
    <div class="profile-header">
        <div class="avatar">${fullName.substring(0,1).toUpperCase()}</div>
        <div class="profile-name">${fullName}</div>
        <div class="profile-role">
            ${role == 'STUDENT' ? 'Student' : (role == 'TEACHER' ? 'Teacher' : 'Administrator')}
        </div>
    </div>

    <!-- Body -->
    <div class="profile-body">

        <!-- Basic Info -->
        <div class="info-grid">
            <div class="info-card">
                <div class="info-row">
                    <span class="info-label">User ID</span>
                    <span class="info-value">${userId}</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Email</span>
                    <span class="info-value">${email}</span>
                </div>
                <div class="info-row">
                    <span class="info-label">Role</span>
                    <span class="info-value">${role}</span>
                </div>
            </div>
        </div>

        <!-- Role-specific Stats -->
        <%
            Boolean isStudent = (Boolean) request.getAttribute("isStudent");
            Boolean isTeacher = (Boolean) request.getAttribute("isTeacher");
            Boolean isAdmin = (Boolean) request.getAttribute("isAdmin");
        %>

        <% if (isStudent != null && isStudent) { %>
        <div class="stats-grid">
            <div class="stat-item">
                <div class="stat-value">${groupId != null && !groupId.isEmpty() ? groupId : '—'}</div>
                <div class="stat-label">Group</div>
            </div>
            <div class="stat-item">
                <div class="stat-value">${submissionCount}</div>
                <div class="stat-label">Submissions</div>
            </div>
        </div>
        <% } %>

        <% if (isTeacher != null && isTeacher) { %>
        <div class="stats-grid">
            <div class="stat-item">
                <div class="stat-value">${groupCount}</div>
                <div class="stat-label">Teaching Groups</div>
            </div>
            <div class="stat-item">
                <div class="stat-value">${assignmentCount}</div>
                <div class="stat-label">Assignments Created</div>
            </div>
        </div>
        <% } %>

        <!-- Admin Actions -->
        <% if (isAdmin != null && isAdmin) { %>
        <div class="action-buttons">
            <button class="btn btn-primary" onclick="location.href='${pageContext.request.contextPath}/admin/dashboard'">
                Admin Dashboard
            </button>
            <button class="btn btn-success" onclick="location.href='${pageContext.request.contextPath}/admin/create-teacher'">
                Create Teacher
            </button>
        </div>
        <% } %>

        <!-- Logout -->
        <button class="btn btn-logout" onclick="location.href='${pageContext.request.contextPath}/logout'">
            Logout
        </button>
    </div>
</div>

</body>
</html>