<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.university.moodle.model.Group" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Создать задание</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/admin_panel.css">
    <style>
        .required { color: red; }
        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input, .form-group textarea, .form-group select { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; }
        .btn { padding: 10px 20px; margin-right: 10px; text-decoration: none; border-radius: 4px; }
        .btn-primary { background: #007bff; color: white; border: none; cursor: pointer; }
        .btn-secondary { background: #6c757d; color: white; }
        .success-message { background: #d4edda; color: #155724; padding: 15px; border-radius: 4px; margin-bottom: 20px; }
        .error-message { background: #f8d7da; color: #721c24; padding: 15px; border-radius: 4px; margin-bottom: 20px; }
    </style>
</head>
<body>

<div class="navbar">
    <h1>Создать новое задание</h1>
    <div class="navbar-right">
        <a href="<%= request.getContextPath() %>/admin/dashboard" class="navbar-link">Назад в админку</a>
    </div>
</div>

<div class="container">

    <!-- Сообщения -->
    <%
        String successMessage = (String) session.getAttribute("successMessage");
        String errorMessage = (String) session.getAttribute("errorMessage");

        if (successMessage != null) {
    %>
    <div class="success-message">
        <%= successMessage %>
    </div>
    <%
            session.removeAttribute("successMessage");
        }
        if (errorMessage != null) {
    %>
    <div class="error-message">
        <%= errorMessage %>
    </div>
    <%
            session.removeAttribute("errorMessage");
        }
    %>

    <form method="post" class="form">

        <div class="form-group">
            <label>Название задания <span class="required">*</span></label>
            <input type="text" name="title" required maxlength="100" placeholder="Например: Лабораторная работа №1">
        </div>

        <div class="form-group">
            <label>Описание</label>
            <textarea name="description" rows="5" placeholder="Подробно опишите задание..."></textarea>
        </div>

        <div class="form-group">
            <label>Группа <span class="required">*</span></label>
            <select name="groupId" required>
                <option value="">— Выберите группу —</option>
                <%
                    List<Group> groups = (List<Group>) request.getAttribute("groups");
                    if (groups != null) {
                        for (Group group : groups) {
                %>
                <option value="<%= group.getId() %>">
                    <%= group.getGroupName() %>
                    (студентов: <%= group.getStudentIDs() != null ? group.getStudentIDs().size() : 0 %>)
                </option>
                <%
                        }
                    }
                %>
            </select>
        </div>

        <div class="form-group">
            <label>Дедлайн (необязательно)</label>
            <input type="datetime-local" name="deadline">
            <small>Оставьте пустым — без ограничения по времени</small>
        </div>

        <div class="form-group">
            <label>Максимальный балл</label>
            <input type="number" name="maxScore" value="100" min="1" max="1000">
        </div>

        <div style="margin-top: 30px;">
            <button type="submit" class="btn btn-primary">Создать задание</button>
            <a href="<%= request.getContextPath() %>/admin/dashboard" class="btn btn-secondary">Отмена</a>
        </div>

    </form>
</div>

</body>
</html>
