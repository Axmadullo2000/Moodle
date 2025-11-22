<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.university.moodle.model.Assignment" %>
<%@ page import="com.university.moodle.model.Submission" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDateTime" %>
<%
  Map<Submission, Assignment> submissionMap = (Map<Submission, Assignment>) request.getAttribute("submissionMap");
  DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
  LocalDateTime now = LocalDateTime.now();

  boolean hasSubmissions = submissionMap != null && !submissionMap.isEmpty();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>My Submissions • University LMS</title>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
  <style>
    :root {
      --primary: #6366f1;
      --primary-dark: #4f46e5;
      --success: #10b981;
      --warning: #f59e0b;
      --danger: #ef4444;
      --gray-50: #f9fafb;
      --gray-100: #f3f4f6;
      --gray-200: #e5e7eb;
      --gray-300: #d1d5db;
      --gray-500: #6b7280;
      --gray-600: #4b5563;
      --gray-700: #374151;
      --gray-900: #111928;
      --radius: 16px;
      --shadow: 0 10px 25px rgba(0,0,0,0.08);
      --shadow-lg: 0 20px 40px rgba(0,0,0,0.14);
      --transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    }

    * { margin: 0; padding: 0; box-sizing: border-box; }
    body {
      font-family: 'Inter', sans-serif;
      background: var(--gray-50);
      color: var(--gray-900);
      line-height: 1.6;
      min-height: 100vh;
    }

    .header {
      background: white;
      padding: 1.5rem 2rem;
      border-bottom: 1px solid var(--gray-200);
      position: sticky;
      top: 0;
      z-index: 50;
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
    }

    .header-container {
      max-width: 1400px;
      margin: 0 auto;
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 1rem;
    }

    .header-title {
      font-size: 1.75rem;
      font-weight: 700;
      color: var(--gray-900);
    }

    .nav-links {
      display: flex;
      gap: 1rem;
    }

    .nav-link {
      padding: 0.75rem 1.5rem;
      background: var(--primary);
      color: white;
      text-decoration: none;
      border-radius: 12px;
      font-weight: 600;
      font-size: 0.95rem;
      transition: var(--transition);
    }

    .nav-link:hover {
      background: var(--primary-dark);
      transform: translateY(-2px);
    }

    .container {
      max-width: 1400px;
      margin: 2.5rem auto;
      padding: 0 2rem;
    }

    .submissions-grid {
      display: grid;
      gap: 2rem;
    }

    .submission-card {
      background: white;
      border-radius: var(--radius);
      overflow: hidden;
      box-shadow: var(--shadow);
      transition: var(--transition);
      border: 1px solid var(--gray-200);
    }

    .submission-card:hover {
      transform: translateY(-6px);
      box-shadow: var(--shadow-lg);
    }

    .card-header {
      background: linear-gradient(135deg, var(--primary), var(--primary-dark));
      color: white;
      padding: 1.75rem 2rem;
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      gap: 1.5rem;
      flex-wrap: wrap;
    }

    .title-section h3 {
      font-size: 1.5rem;
      font-weight: 700;
      margin-bottom: 0.5rem;
    }

    .assignment-meta {
      font-size: 0.9rem;
      opacity: 0.9;
    }

    .badges {
      display: flex;
      gap: 0.75rem;
      flex-wrap: wrap;
    }

    .badge {
      padding: 0.5rem 1rem;
      border-radius: 999px;
      font-size: 0.8rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .badge-graded { background: #ccfbf1; color: #0f766e; }
    .badge-pending { background: #fef9c3; color: #854d0e; }
    .badge-submitted { background: #d1fae5; color: #065f46; }

    .card-body {
      padding: 2rem;
    }

    .info-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
      gap: 1.5rem;
      margin-bottom: 2rem;
      padding: 1.5rem;
      background: var(--gray-50);
      border-radius: 12px;
      border: 1px dashed var(--gray-200);
    }

    .info-item {
      display: flex;
      flex-direction: column;
    }

    .info-label {
      font-size: 0.8rem;
      color: var(--gray-600);
      text-transform: uppercase;
      letter-spacing: 0.5px;
      font-weight: 600;
    }

    .info-value {
      margin-top: 0.5rem;
      font-size: 1.1rem;
      font-weight: 600;
      color: var(--gray-900);
    }

    .score {
      font-size: 2.5rem !important;
      font-weight: 700 !important;
      color: var(--success) !important;
    }

    .section {
      margin-bottom: 1.75rem;
    }

    .section-title {
      font-weight: 600;
      color: var(--gray-700);
      margin-bottom: 0.75rem;
      font-size: 1.1rem;
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }

    .content-box {
      background: white;
      padding: 1.25rem;
      border-radius: 12px;
      border: 1px solid var(--gray-200);
      line-height: 1.7;
      white-space: pre-wrap;
      word-wrap: break-word;
      font-size: 1rem;
      color: var(--gray);
    }

    .feedback-box {
      background: #f0f9ff;
      padding: 1.5rem;
      border-radius: 12px;
      border-left: 5px solid #0ea5e9;
    }

    .feedback-box h4 {
      color: #0c4a6e;
      margin-bottom: 0.75rem;
      font-size: 1.1rem;
    }

    .file-link {
      display: inline-flex;
      align-items: center;
      gap: 0.75rem;
      padding: 0.9rem 1.5rem;
      background: #dbeafe;
      color: var(--primary-dark);
      text-decoration: none;
      border-radius: 12px;
      font-weight: 600;
      transition: var(--transition);
    }

    .file-link:hover {
      background: #c7d2fe;
      transform: translateX(4px);
    }

    .card-footer {
      padding: 1.5rem 2rem;
      background: var(--gray-50);
      border-top: 1px solid var(--gray-200);
      display: flex;
      justify-content: flex-end;
    }

    .btn {
      padding: 0.75rem 1.5rem;
      background: var(--primary);
      color: white;
      text-decoration: none;
      border-radius: 12px;
      font-weight: 600;
      font-size: 0.95rem;
      transition: var(--transition);
    }

    .btn:hover {
      background: var(--primary-dark);
      transform: translateY(-2px);
    }

    .empty-state {
      text-align: center;
      padding: 6rem 2rem;
      background: white;
      border-radius: var(--radius);
      box-shadow: var(--shadow);
    }

    .empty-icon {
      font-size: 5rem;
      color: var(--gray-300);
      margin-bottom: 1.5rem;
    }

    .empty-title {
      font-size: 1.75rem;
      font-weight: 700;
      color: var(--gray-700);
      margin-bottom: 0.75rem;
    }

    .empty-text {
      color: var(--gray-600);
      margin-bottom: 2rem;
      font-size: 1.1rem;
    }

    @media (max-width: 768px) {
      .container { padding: 0 1rem; }
      .header-container { flex-direction: column; text-align: center; }
      .card-header { flex-direction: column; align-items: stretch; }
      .info-grid { grid-template-columns: 1fr; }
    }
  </style>
</head>
<body>

<header class="header">
  <div class="header-container">
    <h1 class="header-title">My Submissions</h1>
    <div class="nav-links">
      <a href="<%= request.getContextPath() %>/student/assignments" class="nav-link">Assignments</a>
      <a href="<%= request.getContextPath() %>/logout" class="nav-link">Logout</a>
    </div>
  </div>
</header>

<div class="container">

  <% if (!hasSubmissions) { %>
  <div class="empty-state">
    <div class="empty-icon">No submissions</div>
    <h2 class="empty-title">No submissions yet</h2>
    <p class="empty-text">You haven't submitted any assignments. Once you submit, they will appear here.</p>
    <a href="<%= request.getContextPath() %>/student/assignments" class="btn">Browse Assignments</a>
  </div>
  <% } else { %>
  <div class="submissions-grid">
    <% for (Map.Entry<Submission, Assignment> entry : submissionMap.entrySet()) {
      Submission s = entry.getKey();
      Assignment a = entry.getValue();

      boolean isGraded = s.getScore() != null;
      boolean hasFeedback = s.getFeedback() != null && !s.getFeedback().trim().isEmpty();
      boolean hasFile = s.getFileUrl() != null && !s.getFileUrl().trim().isEmpty();
    %>
    <div class="submission-card">
      <div class="card-header">
        <div class="title-section">
          <h3><%= a.getTitle() %></h3>
          <div class="assignment-meta">Assignment ID: <%= a.getId() %></div>
        </div>
        <div class="badges">
          <% if (isGraded) { %>
          <span class="badge badge-graded">Graded</span>
          <% } else { %>
          <span class="badge badge-pending">Pending Review</span>
          <% } %>
          <span class="badge badge-submitted">Submitted</span>
        </div>
      </div>

      <div class="card-body">
        <div class="info-grid">
          <div class="info-item">
            <div class="info-label">Submitted On</div>
            <div class="info-value"><%= s.getSubmittedAt() != null ? s.getSubmittedAt().format(fmt) : "—"%></div>
          </div>
          <div class="info-item">
            <div class="info-label">Deadline</div>
            <div class="info-value"><%= a.getDeadline() != null ? a.getDeadline().format(fmt) : "No deadline" %></div>
          </div>
          <div class="info-item">
            <div class="info-label">Status</div>
            <div class="info-value"><%= s.getStatus() != null ? s.getStatus().name() : "Submitted" %></div>
          </div>
          <% if (isGraded) { %>
          <div class="info-item">
            <div class="info-label">Your Score</div>
            <div class="info-value score"><%= s.getScore() %> / <%= a.getMaxScore() %></div>
          </div>
          <% } %>
        </div>

        <% if (s.getContent() != null && !s.getContent().trim().isEmpty()) { %>
        <div class="section">
          <div class="section-title">Your Answer</div>
          <div class="content-box"><%= s.getContent() %></div>
        </div>
        <% } %>

        <% if (hasFile) { %>
        <div class="section">
          <div class="section-title">Submitted File</div>
          <a href="<%= request.getContextPath() %>moodle_uploads/<%= s.getFileUrl() %>"
             target="_blank" class="file-link">
            Download file
          </a>
        </div>
        <% } %>

        <% if (hasFeedback) { %>
        <div class="section">
          <div class="feedback-box">
            <h4>Teacher Feedback</h4>
            <p><%= s.getFeedback() %></p>
            <% if (s.getGradedAt() != null) { %>
            <small style="color: #64748b; margin-top: 0.75rem; display: block;">
              Graded on <%= s.getGradedAt().format(fmt) %>
            </small>
            <% } %>
          </div>
        </div>
        <% } %>
      </div>

      <div class="card-footer">
        <a href="<%= request.getContextPath() %>/student/assignments?action=view&id=<%= a.getId() %>"
           class="btn">
          View Assignment Details
        </a>
      </div>
    </div>
    <% } %>
  </div>
  <% } %>
</div>

</body>
</html>