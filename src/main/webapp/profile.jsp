<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Профиль пользователя</title>
    <link rel="stylesheet" href="profile.css">
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
            <span class="info-label">User ID:</span>
            <span class="info-value">${userId}</span>
        </div>
        <div class="info-item">
            <span class="info-label">Email:</span>
            <span class="info-value">${email}</span>
        </div>

        <div class="info-item">
            <span class="info-label">Role:</span>
            <span class="info-value">${role}</span>
        </div>

        <% if (request.getAttribute("isStudent") != null && (Boolean)request.getAttribute("isStudent")) { %>
        <div class="info-item">
            <span class="info-label">Group:</span>
            <span class="info-value">${groupId != null && !groupId.isEmpty() ? groupId : 'Не назначена'}</span>
        </div>

        <div class="info-item">
            <span class="info-label">Count of tasks:</span>
            <span class="info-value">${submissionCount}</span>
        </div>
        <% } %>

        <!-- Для преподавателей -->
        <% if (request.getAttribute("isTeacher") != null && (Boolean)request.getAttribute("isTeacher")) { %>
        <div class="info-item">
            <span class="info-label">Специализация:</span>
            <span class="info-value">${specialization != null && !specialization.isEmpty() ? specialization : 'Не указана'}</span>
        </div>

        <div class="info-item">
            <span class="info-label">Количество групп:</span>
            <span class="info-value">${groupCount}</span>
        </div>

        <div class="info-item">
            <span class="info-label">Количество заданий:</span>
            <span class="info-value">${assignmentCount}</span>
        </div>
        <% } %>
    </div>

    <div class="action-buttons">
        <!-- Кнопки для админа -->
        <% if (request.getAttribute("isAdmin") != null && (Boolean)request.getAttribute("isAdmin")) { %>
        <button class="btn btn-primary" onclick="window.location.href='${pageContext.request.contextPath}/admin/dashboard'">
            📊 Панель управления
        </button>
        <button class="btn btn-success" onclick="window.location.href='${pageContext.request.contextPath}/admin/create-teacher'">
            👨‍🏫 Создать преподавателя
        </button>
        <% } %>
    </div>

    <button class="logout-btn" onclick="logout()">Logout</button>
</div>

<script>
    function logout() {
        window.location.href = '${pageContext.request.contextPath}/logout';
    }
</script>
</body>
</html>