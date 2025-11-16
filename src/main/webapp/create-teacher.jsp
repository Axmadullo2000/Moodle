<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Создать преподавателя</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/create-teacher.css">
</head>
<body>
    <div class="form-container">
        <div class="form-header">
            <h1>👨‍🏫 Создать преподавателя</h1>
            <p>Добавьте нового преподавателя в систему</p>
        </div>

        <% if (request.getAttribute("error") != null) { %>
        <div class="error-message">
            ❌ ${error}
        </div>
    <% } %>

        <form method="post" action="${pageContext.request.contextPath}/admin/create-teacher">
            <div class="form-group">
                <label class="form-label">Полное имя *</label>
                <input type="text"
                       name="fullName"
                       class="form-input"
                       placeholder="Иванов Иван Иванович"
                       value="${fullName != null ? fullName : ''}"
                       required>
            </div>

            <div class="form-group">
                <label class="form-label">Email *</label>
                <input type="email"
                       name="email"
                       class="form-input"
                       placeholder="teacher@university.com"
                       value="${email != null ? email : ''}"
                       required>
            </div>

            <div class="form-group">
                <label class="form-label">Пароль *</label>
                <input type="password"
                       name="password"
                       class="form-input"
                       placeholder="Минимум 6 символов"
                       minlength="6"
                       required>
            </div>

            <div class="form-group">
                <label class="form-label">Специализация</label>
                <input type="text"
                       name="specialization"
                       class="form-input"
                       placeholder="Например: Программирование, Математика"
                       value="${specialization != null ? specialization : ''}">
            </div>

            <div class="button-group">
                <button type="button"
                    class="btn btn-secondary"
                    onclick="window.location.href='${pageContext.request.contextPath}/admin/dashboard'">
                    Отмена
                </button>
                <button type="submit" class="btn btn-primary">
                Создать
                </button>
            </div>
        </form>
    </div>
</body>
</html>