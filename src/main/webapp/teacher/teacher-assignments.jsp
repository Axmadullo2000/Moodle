<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, com.university.moodle.model.Assignment" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
  List<Assignment> assignments = (List<Assignment>) request.getAttribute("assignments");
  DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
  boolean hasAssignments = assignments != null && !assignments.isEmpty();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Teacher Dashboard</title>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
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
      --card-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
      --hover-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
    }

    * { margin: 0; padding: 0; box-sizing: border-box; }
    body {
      font-family: 'Inter', 'Segoe UI', sans-serif;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      min-height: 100vh;
      color: #333;
    }

    /* Navbar */
    .navbar {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(12px);
      padding: 20px 40px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
      display: flex;
      justify-content: space-between;
      align-items: center;
      position: sticky;
      top: 0;
      z-index: 1000;
    }

    .navbar h1 {
      font-size: 28px;
      font-weight: 700;
      color: var(--dark);
    }

    .nav-links {
      display: flex;
      gap: 14px;
      align-items: center;
    }

    .nav-link {
      padding: 10px 20px;
      background: var(--primary);
      color: white;
      text-decoration: none;
      border-radius: 10px;
      font-weight: 600;
      font-size: 15px;
      transition: all 0.3s ease;
    }

    .nav-link:hover {
      background: var(--primary-dark);
      transform: translateY(-3px);
      box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
    }

    .nav-link.secondary {
      background: transparent;
      color: var(--primary);
      border: 2px solid var(--primary);
    }

    .nav-link.secondary:hover {
      background: var(--primary);
      color: white;
    }

    /* Container */
    .container {
      max-width: 1200px;
      margin: 40px auto;
      padding: 0 20px;
    }

    /* Page Header */
    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 32px;
      flex-wrap: wrap;
      gap: 16px;
    }

    .page-title {
      font-size: 30px;
      font-weight: 700;
      color: white;
      text-shadow: 0 2px 10px rgba(0,0,0,0.3);
    }

    /* Card */
    .card {
      background: white;
      border-radius: 18px;
      padding: 32px;
      box-shadow: var(--card-shadow);
      transition: all 0.4s ease;
    }

    .card:hover {
      transform: translateY(-8px);
      box-shadow: var(--hover-shadow);
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 28px;
      flex-wrap: wrap;
      gap: 16px;
    }

    .card-title {
      font-size: 24px;
      font-weight: 700;
      color: var(--dark);
    }

    /* Table */
    .assignments-table {
      width: 100%;
      border-collapse: separate;
      border-spacing: 0 12px;
    }

    .assignments-table thead {
      background: transparent;
    }

    .assignments-table th {
      padding: 16px 20px;
      text-align: left;
      font-weight: 600;
      color: var(--gray);
      font-size: 13px;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .assignments-table tbody tr {
      background: #f8f9fa;
      border-radius: 12px;
      transition: all 0.3s ease;
    }

    .assignments-table tbody tr:hover {
      background: #edf2f7;
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(0,0,0,0.08);
    }

    .assignments-table td {
      padding: 20px;
      color: var(--dark);
      font-size: 15px;
    }

    .assignments-table .title {
      font-weight: 600;
      color: var(--dark);
    }

    .assignments-table .deadline {
      color: var(--gray);
      font-size: 14px;
    }

    .deadline.overdue { color: var(--danger); font-weight: 600; }
    .deadline.soon { color: #ff8c00; font-weight: 600; }

    /* Action Button */
    .btn-view {
      padding: 10px 22px;
      background: var(--primary);
      color: white;
      text-decoration: none;
      border-radius: 10px;
      font-weight: 600;
      font-size: 14px;
      transition: all 0.3s ease;
    }

    .btn-view:hover {
      background: var(--primary-dark);
      transform: translateY(-2px);
    }

    /* Empty State */
    .empty-state {
      text-align: center;
      padding: 80px 40px;
      color: var(--gray);
    }

    .empty-icon {
      font-size: 70px;
      margin-bottom: 20px;
      opacity: 0.5;
    }

    .empty-title {
      font-size: 24px;
      font-weight: 600;
      margin-bottom: 12px;
      color: var(--dark);
    }

    .empty-text {
      font-size: 16px;
      max-width: 500px;
      margin: 0 auto;
      line-height: 1.6;
    }

    /* Responsive */
    @media (max-width: 768px) {
      .navbar { flex-direction: column; padding: 20px; text-align: center; }
      .page-header { flex-direction: column; text-align: center; }
      .card-header { flex-direction: column; align-items: stretch; }
      .assignments-table { font-size: 14px; }
      .assignments-table th, .assignments-table td { padding: 12px; }
    }
  </style>
</head>
<body>

<!-- Navbar -->
<div class="navbar">
  <h1>Teacher Dashboard</h1>
  <div class="nav-links">
    <a href="<%= request.getContextPath() %>/teacher/create-assignment.jsp" class="nav-link">
      Create Assignment
    </a>
    <a href="<%= request.getContextPath() %>/logout" class="nav-link secondary">
      Logout
    </a>
  </div>
</div>

<div class="container">

  <!-- Page Header -->
  <div class="page-header">
    <div class="page-title">Your Assignments</div>
  </div>

  <!-- Assignments Card -->
  <div class="card">
    <div class="card-header">
      <div class="card-title">All Assignments</div>
      <a href="<%= request.getContextPath() %>/teacher/create-assignment.jsp" class="btn-view">
        + New Assignment
      </a>
    </div>

    <% if (!hasAssignments) { %>
    <div class="empty-state">
      <div class="empty-icon">No assignments yet</div>
      <div class="empty-title">No assignments created</div>
      <div class="empty-text">
        Get started by creating your first assignment. Students will see it immediately after publishing.
      </div>
      <br>
      <a href="<%= request.getContextPath() %>/teacher/create-assignment.jsp" class="btn-view">
        Create Your First Assignment
      </a>
    </div>
    <% } else { %>
    <table class="assignments-table">
      <thead>
      <tr>
        <th>Title</th>
        <th>Deadline</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <%
        LocalDateTime now = LocalDateTime.now();
        for (Assignment a : assignments) {
          boolean isOverdue = now.isAfter(a.getDeadline());
          boolean isSoon = !isOverdue && a.getDeadline().isBefore(now.plusDays(3));
          String deadlineClass = isOverdue ? "overdue" : (isSoon ? "soon" : "");
      %>
      <tr>
        <td class="title"><%= a.getTitle() %></td>
        <td class="deadline <%= deadlineClass %>">
          <%= a.getDeadline().format(fmt) %>
          <% if (isOverdue) { %> (Overdue)<% } %>
          <% if (isSoon) { %> (Soon)<% } %>
        </td>
        <td>
          <a href="<%= request.getContextPath() %>/teacher/submissions?assignmentId=<%= a.getId() %>"
             class="btn-view">
            View Submissions
          </a>
        </td>
      </tr>
      <% } %>
      </tbody>
    </table>
    <% } %>
  </div>
</div>

</body>
</html>
