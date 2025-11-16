<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.university.moodle.model.Assignment" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    List<Assignment> assignments = (List<Assignment>) request.getAttribute("assignments");
    String studentId = (String) request.getAttribute("studentId");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    String successMessage = (String) session.getAttribute("successMessage");
    String errorMessage = (String) session.getAttribute("errorMessage");
    session.removeAttribute("successMessage");
    session.removeAttribute("errorMessage");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Мои задания</title>
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
            padding: 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
        }

        .header {
            background: white;
            padding: 20px 30px;
            border-radius: 10px;
            margin-bottom: 30px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .header h1 {
            color: #333;
            font-size: 28px;
        }

        .nav-links {
            display: flex;
            gap: 15px;
        }

        .nav-links a {
            padding: 10px 20px;
            background: #667eea;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: background 0.3s;
        }

        .nav-links a:hover {
            background: #5568d3;
        }

        .alert {
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-weight: 500;
        }

        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .assignments-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            gap: 20px;
        }

        .assignment-card {
            background: white;
            border-radius: 10px;
            padding: 25px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: transform 0.3s, box-shadow 0.3s;
            position: relative;
        }

        .assignment-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 12px rgba(0, 0, 0, 0.15);
        }

        .assignment-header {
            margin-bottom: 15px;
        }

        .assignment-title {
            font-size: 20px;
            color: #333;
            margin-bottom: 10px;
            font-weight: 600;
        }

        .assignment-meta {
            display: flex;
            flex-direction: column;
            gap: 8px;
            margin-bottom: 15px;
        }

        .meta-item {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            color: #666;
        }

        .meta-icon {
            width: 16px;
            height: 16px;
        }

        .deadline {
            font-weight: 600;
        }

        .deadline.overdue {
            color: #dc3545;
        }

        .deadline.soon {
            color: #ff9800;
        }

        .deadline.normal {
            color: #28a745;
        }

        .assignment-description {
            color: #666;
            margin-bottom: 15px;
            line-height: 1.5;
            max-height: 60px;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .status-badge {
            display: inline-block;
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            margin-bottom: 15px;
        }

        .status-submitted {
            background: #d4edda;
            color: #155724;
        }

        .status-pending {
            background: #fff3cd;
            color: #856404;
        }

        .status-graded {
            background: #d1ecf1;
            color: #0c5460;
        }

        .assignment-actions {
            display: flex;
            gap: 10px;
        }

        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            text-decoration: none;
            display: inline-block;
            text-align: center;
            transition: all 0.3s;
        }

        .btn-primary {
            background: #667eea;
            color: white;
        }

        .btn-primary:hover {
            background: #5568d3;
        }

        .btn-secondary {
            background: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background: #5a6268;
        }

        .btn-disabled {
            background: #e9ecef;
            color: #6c757d;
            cursor: not-allowed;
        }

        .empty-state {
            background: white;
            border-radius: 10px;
            padding: 60px 30px;
            text-align: center;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        .empty-state h2 {
            color: #666;
            margin-bottom: 10px;
        }

        .empty-state p {
            color: #999;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>📚 Мои задания</h1>
        <div class="nav-links">
            <a href="<%= request.getContextPath() %>/student/submissions">Мои ответы</a>
            <a href="<%= request.getContextPath() %>/logout">Выход</a>
        </div>
    </div>

    <% if (successMessage != null) { %>
    <div class="alert alert-success">
        ✓ <%= successMessage %>
    </div>
    <% } %>

    <% if (errorMessage != null) { %>
    <div class="alert alert-error">
        ✗ <%= errorMessage %>
    </div>
    <% } %>

    <% if (assignments == null || assignments.isEmpty()) { %>
    <div class="empty-state">
        <h2>Нет доступных заданий</h2>
        <p>Задания появятся здесь, когда преподаватель их создаст.</p>
    </div>
    <% } else { %>
    <div class="assignments-grid">
        <% for (Assignment assignment : assignments) {
            boolean isSubmitted = assignment.getSubmissionID() != null && !assignment.getSubmissionID().isEmpty();
            LocalDateTime now = LocalDateTime.now();
            boolean isOverdue = now.isAfter(assignment.getDeadline());
            boolean isSoon = !isOverdue && assignment.getDeadline().isBefore(now.plusDays(2));

            String deadlineClass = isOverdue ? "overdue" : (isSoon ? "soon" : "normal");
        %>
        <div class="assignment-card">
            <div class="assignment-header">
                <h3 class="assignment-title"><%= assignment.getTitle() %></h3>

                <% if (isSubmitted) { %>
                <span class="status-badge status-submitted">✓ Отправлено</span>
                <% } else if (isOverdue) { %>
                <span class="status-badge status-pending">⏰ Просрочено</span>
                <% } else { %>
                <span class="status-badge status-pending">⏳ Не отправлено</span>
                <% } %>
            </div>

            <div class="assignment-meta">
                <div class="meta-item">
                    <span>📅</span>
                    <span class="deadline <%= deadlineClass %>">
                                    Дедлайн: <%= assignment.getDeadline().format(formatter) %>
                                </span>
                </div>
                <div class="meta-item">
                    <span>🎯</span>
                    <span>Макс. балл: <%= assignment.getMaxScore() %></span>
                </div>
            </div>

            <p class="assignment-description">
                <%= assignment.getDescription() != null ? assignment.getDescription() : "Нет описания" %>
            </p>

            <div class="assignment-actions">
                <% if (isOverdue && !isSubmitted) { %>
                <button class="btn btn-disabled" disabled>Срок истёк</button>
                <% } else { %>
                <a href="<%= request.getContextPath() %>/student/assignments?action=view&id=<%= assignment.getId() %>"
                   class="btn btn-primary">
                    <%= isSubmitted ? "Просмотреть" : "Отправить" %>
                </a>
                <% } %>
            </div>
        </div>
        <% } %>
    </div>
    <% } %>
</div>
</body>
</html>
