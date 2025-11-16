<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.university.moodle.model.Assignment" %>
<%@ page import="com.university.moodle.model.Submission" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
  Map<Submission, Assignment> submissionMap = (Map<Submission, Assignment>) request.getAttribute("submissionMap");
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Мои ответы</title>
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

    .submissions-list {
      display: flex;
      flex-direction: column;
      gap: 20px;
    }

    .submission-card {
      background: white;
      border-radius: 10px;
      padding: 25px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      transition: transform 0.3s, box-shadow 0.3s;
    }

    .submission-card:hover {
      transform: translateY(-3px);
      box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
    }

    .submission-header {
      display: flex;
      justify-content: space-between;
      align-items: start;
      margin-bottom: 20px;
      flex-wrap: wrap;
      gap: 15px;
    }

    .submission-title {
      flex: 1;
      min-width: 200px;
    }

    .submission-title h3 {
      font-size: 22px;
      color: #333;
      margin-bottom: 5px;
    }

    .submission-title .assignment-id {
      font-size: 13px;
      color: #999;
    }

    .status-badges {
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
    }

    .status-badge {
      padding: 6px 14px;
      border-radius: 20px;
      font-size: 13px;
      font-weight: 600;
      white-space: nowrap;
    }

    .status-pending {
      background: #fff3cd;
      color: #856404;
    }

    .status-graded {
      background: #d1ecf1;
      color: #0c5460;
    }

    .status-submitted {
      background: #d4edda;
      color: #155724;
    }

    .submission-info {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 15px;
      margin-bottom: 20px;
      padding: 15px;
      background: #f8f9fa;
      border-radius: 8px;
    }

    .info-item {
      display: flex;
      flex-direction: column;
      gap: 5px;
    }

    .info-label {
      font-size: 12px;
      color: #6c757d;
      font-weight: 600;
      text-transform: uppercase;
    }

    .info-value {
      font-size: 15px;
      color: #333;
      font-weight: 500;
    }

    .score-display {
      font-size: 24px;
      color: #28a745;
      font-weight: bold;
    }

    .submission-content {
      margin-bottom: 20px;
    }

    .content-label {
      font-weight: 600;
      color: #495057;
      margin-bottom: 10px;
      display: block;
    }

    .content-text {
      background: #f8f9fa;
      padding: 15px;
      border-radius: 8px;
      border-left: 4px solid #667eea;
      color: #495057;
      line-height: 1.6;
      white-space: pre-wrap;
      word-wrap: break-word;
    }

    .feedback-section {
      background: #e7f3ff;
      padding: 15px;
      border-radius: 8px;
      border-left: 4px solid #17a2b8;
      margin-bottom: 20px;
    }

    .feedback-section h4 {
      color: #0c5460;
      margin-bottom: 10px;
    }

    .feedback-text {
      color: #495057;
      line-height: 1.6;
    }

    .file-attachment {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 10px 15px;
      background: #e7f3ff;
      border-radius: 8px;
      text-decoration: none;
      color: #667eea;
      font-weight: 600;
      transition: all 0.3s;
    }

    .file-attachment:hover {
      background: #d0e7ff;
      transform: translateX(5px);
    }

    .submission-actions {
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
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
      transition: all 0.3s;
    }

    .btn-primary {
      background: #667eea;
      color: white;
    }

    .btn-primary:hover {
      background: #5568d3;
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
      margin-bottom: 15px;
    }

    .empty-state p {
      color: #999;
      margin-bottom: 25px;
    }

    .divider {
      height: 1px;
      background: #e9ecef;
      margin: 20px 0;
    }

    @media (max-width: 768px) {
      .submission-header {
        flex-direction: column;
      }

      .submission-info {
        grid-template-columns: 1fr;
      }
    }
  </style>
</head>
<body>
<div class="container">
  <div class="header">
    <h1>📋 Мои ответы</h1>
    <div class="nav-links">
      <a href="<%= request.getContextPath() %>/student/assignments">Задания</a>
      <a href="<%= request.getContextPath() %>/logout">Выход</a>
    </div>
  </div>

  <% if (submissionMap == null || submissionMap.isEmpty()) { %>
  <div class="empty-state">
    <h2>У вас пока нет отправленных ответов</h2>
    <p>Когда вы отправите задание, оно появится здесь</p>
    <a href="<%= request.getContextPath() %>/student/assignments" class="btn btn-primary">
      Перейти к заданиям
    </a>
  </div>
  <% } else { %>
  <div class="submissions-list">
    <% for (Map.Entry<Submission, Assignment> entry : submissionMap.entrySet()) {
      Submission submission = entry.getKey();
      Assignment assignment = entry.getValue();
      boolean isGraded = submission.getScore() != null;
    %>
    <div class="submission-card">
      <div class="submission-header">
        <div class="submission-title">
          <h3><%= assignment.getTitle() %></h3>
          <span class="assignment-id">ID задания: <%= assignment.getId() %></span>
        </div>
        <div class="status-badges">
          <% if (isGraded) { %>
          <span class="status-badge status-graded">✓ Проверено</span>
          <% } else { %>
          <span class="status-badge status-pending">⏳ На проверке</span>
          <% } %>
          <span class="status-badge status-submitted">Отправлено</span>
        </div>
      </div>

      <div class="submission-info">
        <div class="info-item">
          <span class="info-label">📅 Дата отправки</span>
          <span class="info-value"><%= submission.getSubmittedAt().format(formatter) %></span>
        </div>
        <div class="info-item">
          <span class="info-label">⏰ Дедлайн</span>
          <span class="info-value"><%= assignment.getDeadline().format(formatter) %></span>
        </div>
        <div class="info-item">
          <span class="info-label">📊 Статус</span>
          <span class="info-value"><%= submission.getStatus() %></span>
        </div>
        <% if (isGraded) { %>
        <div class="info-item">
          <span class="info-label">🎯 Оценка</span>
          <span class="score-display">
                                        <%= submission.getScore() %> / <%= assignment.getMaxScore() %>
                                    </span>
        </div>
        <% } %>
      </div>

      <div class="submission-content">
        <span class="content-label">💬 Ваш ответ:</span>
        <div class="content-text">
          <%= submission.getContent() != null ? submission.getContent() : "Нет текста" %>
        </div>
      </div>

      <% if (submission.getFileUrl() != null && !submission.getFileUrl().isEmpty()) { %>
      <div style="margin-bottom: 20px;">
        <span class="content-label">📎 Прикреплённый файл:</span>
        <a href="<%= request.getContextPath() %>/<%= submission.getFileUrl() %>"
           target="_blank"
           class="file-attachment">
          <span>📄</span>
          <span>Скачать файл</span>
        </a>
      </div>
      <% } %>

      <% if (isGraded && submission.getFeedback() != null && !submission.getFeedback().isEmpty()) { %>
      <div class="feedback-section">
        <h4>📝 Комментарий преподавателя:</h4>
        <div class="feedback-text">
          <%= submission.getFeedback() %>
        </div>
        <% if (submission.getGradedAt() != null) { %>
        <div style="margin-top: 10px; font-size: 13px; color: #6c757d;">
          Проверено: <%= submission.getGradedAt().format(formatter) %>
        </div>
        <% } %>
      </div>
      <% } %>

      <div class="submission-actions">
        <a href="<%= request.getContextPath() %>/student/assignments?action=view&id=<%= assignment.getId() %>"
           class="btn btn-primary">
          👁️ Просмотреть задание
        </a>
      </div>
    </div>
    <% } %>
  </div>
  <% } %>
</div>
</body>
</html>
