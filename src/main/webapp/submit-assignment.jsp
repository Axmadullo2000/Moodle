<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.university.moodle.model.Assignment" %>
<%@ page import="com.university.moodle.model.Submission" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDateTime" %>
<%
  Assignment assignment = (Assignment) request.getAttribute("assignment");
  Submission submission = (Submission) request.getAttribute("submission");
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

  boolean isSubmitted = submission != null;
  boolean isOverdue = LocalDateTime.now().isAfter(assignment.getDeadline());
  boolean canEdit = isSubmitted && !isOverdue;
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Отправка задания: <%= assignment.getTitle() %></title>
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
      max-width: 900px;
      margin: 0 auto;
    }

    .card {
      background: white;
      border-radius: 10px;
      padding: 30px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      margin-bottom: 20px;
    }

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30px;
    }

    .header h1 {
      color: #333;
      font-size: 28px;
    }

    .back-link {
      padding: 10px 20px;
      background: #6c757d;
      color: white;
      text-decoration: none;
      border-radius: 5px;
      transition: background 0.3s;
    }

    .back-link:hover {
      background: #5a6268;
    }

    .assignment-info {
      background: #f8f9fa;
      padding: 20px;
      border-radius: 8px;
      margin-bottom: 30px;
    }

    .info-row {
      display: flex;
      justify-content: space-between;
      margin-bottom: 15px;
      padding-bottom: 15px;
      border-bottom: 1px solid #dee2e6;
    }

    .info-row:last-child {
      margin-bottom: 0;
      padding-bottom: 0;
      border-bottom: none;
    }

    .info-label {
      font-weight: 600;
      color: #495057;
    }

    .info-value {
      color: #6c757d;
    }

    .deadline-warning {
      background: #fff3cd;
      border: 1px solid #ffc107;
      color: #856404;
      padding: 15px;
      border-radius: 8px;
      margin-bottom: 20px;
      font-weight: 500;
    }

    .deadline-error {
      background: #f8d7da;
      border: 1px solid #dc3545;
      color: #721c24;
      padding: 15px;
      border-radius: 8px;
      margin-bottom: 20px;
      font-weight: 500;
    }

    .submission-status {
      background: #d4edda;
      border: 1px solid #28a745;
      color: #155724;
      padding: 15px;
      border-radius: 8px;
      margin-bottom: 20px;
    }

    .submission-status h3 {
      margin-bottom: 10px;
    }

    .graded-info {
      background: #d1ecf1;
      border: 1px solid #17a2b8;
      color: #0c5460;
      padding: 15px;
      border-radius: 8px;
      margin-bottom: 20px;
    }

    .score {
      font-size: 24px;
      font-weight: bold;
      margin: 10px 0;
    }

    .form-group {
      margin-bottom: 25px;
    }

    .form-group label {
      display: block;
      font-weight: 600;
      color: #333;
      margin-bottom: 8px;
    }

    .form-group textarea {
      width: 100%;
      min-height: 200px;
      padding: 15px;
      border: 2px solid #e9ecef;
      border-radius: 8px;
      font-size: 15px;
      font-family: inherit;
      resize: vertical;
      transition: border-color 0.3s;
    }

    .form-group textarea:focus {
      outline: none;
      border-color: #667eea;
    }

    .form-group textarea:disabled {
      background: #f8f9fa;
      cursor: not-allowed;
    }

    .file-upload {
      border: 2px dashed #dee2e6;
      border-radius: 8px;
      padding: 30px;
      text-align: center;
      transition: all 0.3s;
      cursor: pointer;
    }

    .file-upload:hover {
      border-color: #667eea;
      background: #f8f9fa;
    }

    .file-upload input[type="file"] {
      display: none;
    }

    .file-upload-label {
      cursor: pointer;
      color: #667eea;
      font-weight: 600;
    }

    .file-info {
      margin-top: 15px;
      padding: 10px;
      background: #e7f3ff;
      border-radius: 5px;
      display: none;
    }

    .current-file {
      background: #e7f3ff;
      padding: 15px;
      border-radius: 8px;
      margin-top: 10px;
    }

    .current-file a {
      color: #667eea;
      text-decoration: none;
      font-weight: 600;
    }

    .current-file a:hover {
      text-decoration: underline;
    }

    .form-actions {
      display: flex;
      gap: 15px;
      margin-top: 30px;
    }

    .btn {
      padding: 12px 30px;
      border: none;
      border-radius: 8px;
      font-size: 16px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s;
      text-decoration: none;
      display: inline-block;
    }

    .btn-primary {
      background: #667eea;
      color: white;
    }

    .btn-primary:hover {
      background: #5568d3;
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(102, 126, 234, 0.3);
    }

    .btn-secondary {
      background: #6c757d;
      color: white;
    }

    .btn-secondary:hover {
      background: #5a6268;
    }

    .btn:disabled {
      background: #e9ecef;
      color: #6c757d;
      cursor: not-allowed;
      transform: none;
    }

    .description-section {
      background: #f8f9fa;
      padding: 20px;
      border-radius: 8px;
      margin-bottom: 30px;
    }

    .description-section h3 {
      color: #333;
      margin-bottom: 15px;
    }

    .description-section p {
      color: #6c757d;
      line-height: 1.6;
    }
  </style>
</head>
<body>
<div class="container">
  <div class="card">
    <div class="header">
      <h1>📝 <%= assignment.getTitle() %></h1>
      <a href="<%= request.getContextPath() %>/student/assignments" class="back-link">
        ← Назад к заданиям
      </a>
    </div>

    <div class="assignment-info">
      <div class="info-row">
        <span class="info-label">📅 Дедлайн:</span>
        <span class="info-value"><%= assignment.getDeadline().format(formatter) %></span>
      </div>
      <div class="info-row">
        <span class="info-label">🎯 Максимальный балл:</span>
        <span class="info-value"><%= assignment.getMaxScore() %></span>
      </div>
    </div>

    <% if (assignment.getDescription() != null && !assignment.getDescription().isEmpty()) { %>
    <div class="description-section">
      <h3>📋 Описание задания:</h3>
      <p><%= assignment.getDescription() %></p>
    </div>
    <% } %>

    <% if (isOverdue && !isSubmitted) { %>
    <div class="deadline-error">
      ⚠️ Срок сдачи истёк. Вы не можете отправить задание.
    </div>
    <% } else if (isSubmitted && submission.getScore() != null) { %>
    <div class="graded-info">
      <h3>✅ Задание проверено</h3>
      <div class="score">Оценка: <%= submission.getScore() %> / <%= assignment.getMaxScore() %></div>
      <% if (submission.getFeedback() != null && !submission.getFeedback().isEmpty()) { %>
      <p><strong>Комментарий преподавателя:</strong></p>
      <p><%= submission.getFeedback() %></p>
      <% } %>
    </div>
    <% } else if (isSubmitted) { %>
    <div class="submission-status">
      <h3>✓ Задание отправлено</h3>
      <p>Отправлено: <%= submission.getSubmittedAt().format(formatter) %></p>
      <p>Статус: <%= submission.getStatus() %></p>
      <% if (canEdit) { %>
      <p style="margin-top: 10px;">💡 Вы можете изменить ответ до истечения срока сдачи.</p>
      <% } %>
    </div>
    <% } else if (LocalDateTime.now().plusDays(2).isAfter(assignment.getDeadline())) { %>
    <div class="deadline-warning">
      ⏰ Внимание! До дедлайна осталось менее 2 дней!
    </div>
    <% } %>

    <% if (!isOverdue || isSubmitted) { %>
    <form action="<%= request.getContextPath() %>/student/submit"
          method="post"
          enctype="multipart/form-data"
          id="submissionForm">

      <input type="hidden" name="assignmentId" value="<%= assignment.getId() %>">

      <div class="form-group">
        <label for="content">Ваше задание: *</label>
        <textarea
                name="content"
                id="content"
                required
                placeholder="Введите ваш ответ здесь..."
                <%= (isSubmitted && submission.getScore() != null) ? "disabled" : "" %>
        ><%= isSubmitted ? submission.getContent() : "" %></textarea>
      </div>

      <div class="form-group">
        <label>📎 Прикрепить файл (необязательно):</label>

        <% if (isSubmitted && submission.getFileUrl() != null) { %>
        <div class="current-file">
          📄 Текущий файл:
          <a href="<%= request.getContextPath() %>/<%= submission.getFileUrl() %>"
             target="_blank">
            Скачать файл
          </a>
        </div>
        <% } %>

        <% if (submission == null || submission.getScore() == null) { %>
        <div class="file-upload" onclick="document.getElementById('fileInput').click()">
          <label for="fileInput" class="file-upload-label">
            📁 Нажмите для выбора файла
          </label>
          <input
                  type="file"
                  name="file"
                  id="fileInput"
                  accept=".pdf,.doc,.docx,.txt,.zip,.jpg,.png"
                  onchange="showFileName(this)">
          <p style="color: #6c757d; margin-top: 10px; font-size: 14px;">
            Максимальный размер: 10 МБ
          </p>
          <div class="file-info" id="fileInfo"></div>
        </div>
        <% } %>
      </div>

      <div class="form-actions">
        <% if (submission == null || submission.getScore() == null) { %>
        <button type="submit" class="btn btn-primary" id="submitBtn">
          <%= isSubmitted ? "💾 Обновить ответ" : "📤 Отправить" %>
        </button>
        <% } %>
        <a href="<%= request.getContextPath() %>/student/assignments"
           class="btn btn-secondary">
          Отмена
        </a>
      </div>
    </form>
    <% } %>
  </div>
</div>

<script>
  function showFileName(input) {
    const fileInfo = document.getElementById('fileInfo');
    if (input.files && input.files[0]) {
      const fileName = input.files[0].name;
      const fileSize = (input.files[0].size / 1024 / 1024).toFixed(2);
      fileInfo.innerHTML = `✓ Выбран файл: <strong>${fileName}</strong> (${fileSize} МБ)`;
      fileInfo.style.display = 'block';
    } else {
      fileInfo.style.display = 'none';
    }
  }

  // Подтверждение перед отправкой
  document.getElementById('submissionForm')?.addEventListener('submit', function(e) {
    const content = document.getElementById('content').value.trim();
    if (content.length < 10) {
      e.preventDefault();
      alert('Пожалуйста, напишите более подробный ответ (минимум 10 символов)');
      return false;
    }

    <% if (isSubmitted) { %>
    if (!confirm('Вы уверены, что хотите обновить свой ответ?')) {
      e.preventDefault();
      return false;
    }
    <% } else { %>
    if (!confirm('Отправить задание?')) {
      e.preventDefault();
      return false;
    }
    <% } %>
  });
</script>
</body>
</html>
