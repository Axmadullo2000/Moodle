<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.university.moodle.model.Assignment" %>
<%@ page import="com.university.moodle.model.Submission" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDateTime" %>
<%
  Assignment assignment = (Assignment) request.getAttribute("assignment");
  Submission submission = (Submission) request.getAttribute("submission");
  DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

  LocalDateTime now = LocalDateTime.now();
  boolean isSubmitted = submission != null;
  boolean isGraded = isSubmitted && submission.getScore() != null;
  boolean isOverdue = assignment.getDeadline() != null && now.isAfter(assignment.getDeadline());
  boolean canSubmitOrEdit = !isOverdue || (isSubmitted && !isGraded);
  boolean isSoon = !isOverdue && assignment.getDeadline() != null && assignment.getDeadline().isBefore(now.plusDays(2));
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Submit: <%= assignment.getTitle() %></title>
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
    }

    * { margin: 0; padding: 0; box-sizing: border-box; }

    body {
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      min-height: 100vh;
      padding: 20px;
      color: #333;
    }

    .container { max-width: 900px; margin: 0 auto; }

    .card {
      background: white;
      border-radius: 16px;
      overflow: hidden;
      box-shadow: 0 15px 40px rgba(0,0,0,0.15);
      margin-bottom: 30px;
    }

    .card-header {
      padding: 28px 32px 0;
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 16px;
    }

    .card-header h1 {
      font-size: 26px;
      font-weight: 700;
      color: var(--dark);
    }

    .back-btn {
      padding: 10px 22px;
      background: #6c757d;
      color: white;
      text-decoration: none;
      border-radius: 8px;
      font-weight: 600;
      transition: all 0.3s;
    }

    .back-btn:hover {
      background: #5a6268;
      transform: translateY(-2px);
    }

    .card-body { padding: 28px 32px; }

    .info-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
      gap: 16px;
      padding: 20px;
      background: var(--light);
      border-radius: 12px;
      margin-bottom: 24px;
    }

    .info-item { display: flex; flex-direction: column; }
    .info-label {
      font-size: 13px;
      color: var(--gray);
      text-transform: uppercase;
      font-weight: 600;
      letter-spacing: 0.5px;
      margin-bottom: 6px;
    }
    .info-value {
      font-size: 16px;
      font-weight: 500;
      color: var(--dark);
    }

    .deadline.overdue { color: var(--danger); font-weight: 700; }
    .deadline.soon    { color: #ff8c00; font-weight: 600; }
    .deadline.normal  { color: var(--success); }

    .alert {
      padding: 16px 20px;
      border-radius: 10px;
      margin-bottom: 24px;
      font-weight: 500;
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .alert-warning { background: #fff8e1; border-left: 5px solid var(--warning); color: #856404; }
    .alert-danger  { background: #fce8e8; border-left: 5px solid var(--danger); color: #721c24; }
    .alert-success { background: #e8f5e8; border-left: 5px solid var(--success); color: #155724; }
    .alert-graded  { background: #e3f2fd; border-left: 5px solid var(--info); color: #0c5460; }

    .section { margin-bottom: 28px; }
    .section-title {
      font-size: 17px;
      font-weight: 600;
      color: var(--dark);
      margin-bottom: 12px;
    }

    .description-box {
      background: var(--light);
      padding: 20px;
      border-radius: 12px;
      line-height: 1.7;
      color: #444;
      border-left: 4px solid var(--primary);
    }

    .download-btn {
      display: inline-flex;
      align-items: center;
      gap: 10px;
      margin-top: 16px;
      padding: 12px 24px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      text-decoration: none;
      border-radius: 10px;
      font-weight: 600;
      transition: all 0.3s ease;
      box-shadow: 0 6px 20px rgba(102,126,234,0.4);
    }

    .download-btn:hover {
      transform: translateY(-3px);
      box-shadow: 0 10px 25px rgba(102,126,234,0.5);
    }

    /* Остальные стили без изменений (textarea, file-upload, btn и т.д.) */
    /* ... (оставил как было, чтобы не удлинять) ... */
    label { display: block; font-weight: 600; color: var(--dark); margin-bottom: 10px; font-size: 15px; }
    textarea { width: 100%; min-height: 200px; padding: 16px; border: 2px solid #e0e0e0; border-radius: 10px; resize: vertical; }
    textarea:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 4px rgba(102,126,234,0.1); }
    textarea:disabled { background: #f5f5f5; color: #666; cursor: not-allowed; }

    .file-upload-area { border: 2px dashed #ccc; border-radius: 12px; padding: 40px 20px; text-align: center; cursor: pointer; background: #fafafa; }
    .file-upload-area:hover { border-color: var(--primary); background: #f8f5ff; }
    .file-upload-area input { display: none; }
    .file-upload-label { color: var(--primary); font-weight: 600; font-size: 16px; }

    .file-info { margin-top: 12px; padding: 12px; background: #e8f4ff; border-radius: 8px; font-size: 14px; display: none; }
    .current-file { background: #e8f4ff; padding: 16px; border-radius: 10px; margin-top: 12px; }
    .current-file a { color: var(--primary); font-weight: 600; text-decoration: none; }
    .current-file a:hover { text-decoration: underline; }

    .form-actions { display: flex; gap: 16px; margin-top: 32px; flex-wrap: wrap; }
    .btn { padding: 14px 28px; border: none; border-radius: 10px; font-size: 15px; font-weight: 600; cursor: pointer; text-decoration: none; display: inline-block; transition: all 0.3s ease; }
    .btn-primary { background: var(--primary); color: white; }
    .btn-primary:hover:not(:disabled) { background: var(--primary-dark); transform: translateY(-3px); box-shadow: 0 8px 20px rgba(102,126,234,0.4); }
    .btn-secondary { background: #6c757d; color: white; }
    .btn:disabled { background: #ced4da; color: #6c757d; cursor: not-allowed; }
  </style>
</head>
<body>
<div class="container">
  <div class="card">
    <div class="card-header">
      <h1><%= assignment.getTitle() %></h1>
      <a href="<%= request.getContextPath() %>/student/assignments" class="back-btn">
        Back to Assignments
      </a>
    </div>

    <div class="card-body">
      <div class="info-grid">
        <div class="info-item">
          <div class="info-label">Deadline</div>
          <div class="info-value deadline <%= isOverdue ? "overdue" : (isSoon ? "soon" : "normal") %>">
            <%= assignment.getDeadline() != null ? assignment.getDeadline().format(fmt) : "No deadline" %>
          </div>
        </div>
        <div class="info-item">
          <div class="info-label">Max Score</div>
          <div class="info-value"><%= assignment.getMaxScore() %> points</div>
        </div>
      </div>

      <!-- Описание задания и файл задания -->
      <% if (assignment.getDescription() != null && !assignment.getDescription().trim().isEmpty()) { %>
      <div class="section">
        <div class="section-title">Assignment Description</div>
        <div class="description-box">
          <%= assignment.getDescription() %>
        </div>

        <!-- Файл, прикреплённый к самому заданию (не к ответу студента) -->
        <% if (assignment.getFilePath() != null && !assignment.getFilePath().trim().isEmpty()) { %>
        <a href="<%= request.getContextPath()%>/download?file=<%= assignment.getFilePath()%>"
           download
           style="display: inline-block;
          padding: 10px 20px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          text-decoration: none;
          border-radius: 8px;
          font-weight: 600;
          transition: all 0.3s ease;
          box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);"
           onmouseover="this.style.transform='translateY(-2px)';
                this.style.boxShadow='0 6px 20px rgba(102, 126, 234, 0.6)';"
           onmouseout="this.style.transform='translateY(0)';
               this.style.boxShadow='0 4px 15px rgba(102, 126, 234, 0.4)';">
          📥 Download
        </a>
        <% } %>
      </div>
      <% } %>

      <!-- Статусные сообщения -->
      <% if (isOverdue && !isSubmitted) { %>
      <div class="alert alert-danger">
        Deadline has passed. You can no longer submit this assignment.
      </div>
      <% } else if (isGraded) { %>
      <div class="alert alert-graded">
        <strong>Graded</strong><br>
        <div style="font-size: 24px; font-weight: bold; margin: 8px 0; color: var(--success);">
          <%= submission.getScore() %> / <%= assignment.getMaxScore() %>
        </div>
        <% if (submission.getFeedback() != null && !submission.getFeedback().trim().isEmpty()) { %>
        <p><strong>Teacher's feedback:</strong><br><%= submission.getFeedback() %></p>
        <% } %>
      </div>
      <% } else if (isSubmitted) { %>
      <div class="alert alert-success">
        Assignment submitted on <%= submission.getSubmittedAt().format(fmt) %><br>
        Status: <strong><%= submission.getStatus() != null ? submission.getStatus().name() : "Pending Review" %></strong>
        <% if (!isOverdue) { %>
        <br><em>You can edit your submission until the deadline.</em>
        <% } %>
      </div>
      <% } else if (isSoon) { %>
      <div class="alert alert-warning">
        Less than 2 days until deadline!
      </div>
      <% } %>

      <!-- Форма отправки ответа -->
      <% if (canSubmitOrEdit) { %>
      <form action="<%= request.getContextPath() %>/student/submit" method="post" enctype="multipart/form-data" id="submitForm">
        <input type="hidden" name="assignmentId" value="<%= assignment.getId() %>">

        <div class="form-group">
          <label for="content">Your Answer <span style="color: #e74c3c;">*</span></label>
          <textarea name="content" id="content" placeholder="Write your answer here..." required
                  <%= isGraded ? "disabled" : "" %>><%= isSubmitted ? submission.getContent() : "" %></textarea>
        </div>

        <div class="form-group">
          <label>Attach File (optional)</label>

          <!-- Уже загруженный студентом файл -->
          <% if (isSubmitted && submission.getFileUrl() != null && !submission.getFileUrl().trim().isEmpty()) { %>
          <div class="current-file">
            Your uploaded file:
            <a href="<%= request.getContextPath() %>/download?file=<%= submission.getFileUrl() %>&type=student" class="btn btn-download">
              Download submitted file
            </a>
          </div>
          <% } %>

          <!-- Загрузка нового файла (только если ещё не оценено) -->
          <% if (!isGraded) { %>
          <div class="file-upload-area" onclick="document.getElementById('fileInput').click()">
            <input type="file" name="file" id="fileInput" accept=".pdf,.doc,.docx,.txt,.zip,.jpg,.png,.jpeg">
            <div class="file-upload-label">Click to upload file</div>
            <p style="color:#888;margin-top:8px;font-size:14px;">Max size: 10 MB</p>
            <div class="file-info" id="fileInfo"></div>
          </div>
          <% } %>
        </div>

        <div class="form-actions">
          <% if (!isGraded) { %>
          <button type="submit" class="btn btn-primary">
            <%= isSubmitted ? "Update Submission" : "Submit Assignment" %>
          </button>
          <% } %>
          <a href="<%= request.getContextPath() %>/student/assignments" class="btn btn-secondary">
            Cancel
          </a>
        </div>
      </form>
      <% } %>
    </div>
  </div>
</div>

<script>
  const fileInput = document.getElementById('fileInput');
  const fileInfo = document.getElementById('fileInfo');

  if (fileInput) {
    fileInput.addEventListener('change', function () {
      if (this.files && this.files[0]) {
        const name = this.files[0].name;
        const size = (this.files[0].size / 1024 / 1024).toFixed(2);
        fileInfo.innerHTML = `Selected: <strong>${name}</strong> (${size} MB)`;
        fileInfo.style.display = 'block';
      }
    });
  }

  const form = document.getElementById('submitForm');
  if (form) {
    form.addEventListener('submit', function (e) {
      const content = document.getElementById('content').value.trim();
      if (content.length < 15) {
        e.preventDefault();
        alert('Please provide a detailed answer (minimum 15 characters).');
        return;
      }
      if (!confirm('<%= isSubmitted ? "Update your submission?" : "Submit this assignment?" %>')) {
        e.preventDefault();
      }
    });
  }
</script>
</body>
</html>
