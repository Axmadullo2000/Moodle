<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Профиль пользователя</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }

        .profile-container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            max-width: 600px;
            width: 100%;
            padding: 40px;
        }

        .profile-header {
            text-align: center;
            margin-bottom: 30px;
        }

        .profile-avatar {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 20px;
            font-size: 48px;
            color: white;
            font-weight: bold;
        }

        .profile-name {
            font-size: 28px;
            font-weight: 600;
            color: #333;
            margin-bottom: 10px;
        }

        .profile-role {
            display: inline-block;
            padding: 6px 16px;
            background: #667eea;
            color: white;
            border-radius: 20px;
            font-size: 14px;
            font-weight: 500;
        }

        .profile-info {
            margin-top: 30px;
        }

        .info-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px 0;
            border-bottom: 1px solid #eee;
        }

        .info-item:last-child {
            border-bottom: none;
        }

        .info-label {
            font-weight: 600;
            color: #666;
            font-size: 14px;
        }

        .info-value {
            color: #333;
            font-size: 16px;
            font-weight: 500;
        }

        .logout-btn {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            margin-top: 30px;
            transition: transform 0.2s;
        }

        .logout-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.4);
        }

        .student-info {
            display: ${isStudent ? 'block' : 'none'};
        }
    </style>
</head>
<body>
<div class="profile-container">
    <div class="profile-header">
        <div class="profile-avatar">${initial}</div>
        <h1 class="profile-name">${fullName}</h1>
        <span class="profile-role">${role}</span>
    </div>

    <div class="profile-info">
        <div class="info-item">
            <span class="info-label">ID пользователя:</span>
            <span class="info-value">${userId}</span>
        </div>

        <div class="info-item">
            <span class="info-label">Email:</span>
            <span class="info-value">${email}</span>
        </div>

        <div class="info-item">
            <span class="info-label">Роль:</span>
            <span class="info-value">${role}</span>
        </div>

        <% if (request.getAttribute("isStudent") != null && (Boolean)request.getAttribute("isStudent")) { %>
        <div class="info-item">
            <span class="info-label">Группа:</span>
            <span class="info-value">${groupId != null && !groupId.isEmpty() ? groupId : 'Не назначена'}</span>
        </div>

        <div class="info-item">
            <span class="info-label">Количество работ:</span>
            <span class="info-value">${submissionCount}</span>
        </div>
        <% } %>
    </div>

    <button class="logout-btn" onclick="logout()">Выйти</button>
</div>

<script>
    function logout() {
        window.location.href = '${pageContext.request.contextPath}/logout';
    }
</script>
</body>
</html>