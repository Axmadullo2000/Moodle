<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.university.moodle.model.*" %>
<%
  List<Teacher> teachers = (List<Teacher>) request.getAttribute("teachers");
  List<Student> students = (List<Student>) request.getAttribute("students");
  List<Group> groups = (List<Group>) request.getAttribute("groups");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Admin Dashboard • University Moodle</title>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
  <style>
    :root {
      --primary: #7c3aed;
      --primary-light: #a78bfa;
      --primary-dark: #6d28d9;
      --secondary: #06b6d4;
      --accent: #ec4899;
      --success: #10b981;
      --warning: #f59e0b;
      --danger: #ef4444;
      --dark-bg: #0f172a;
      --dark-card: #1e293b;
      --dark-hover: #334155;
      --gray-50: #f8fafc;
      --gray-100: #f1f5f9;
      --gray-200: #e2e8f0;
      --gray-300: #cbd5e1;
      --gray-500: #64748b;
      --gray-600: #475569;
      --gray-700: #334155;
      --gray-900: #0f172a;
      --radius: 16px;
      --shadow-sm: 0 1px 2px 0 rgba(0,0,0,0.3);
      --shadow-md: 0 4px 12px 0 rgba(0,0,0,0.4);
      --shadow-lg: 0 10px 25px 0 rgba(0,0,0,0.5);
      --shadow-xl: 0 15px 35px 0 rgba(0,0,0,0.6);
      --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    }

    * { margin: 0; padding: 0; box-sizing: border-box; }

    body {
      font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      background: linear-gradient(135deg, #0f172a 0%, #1a1f3a 50%, #0f1626 100%);
      min-height: 100vh;
      color: #e2e8f0;
    }

    /* Navbar */
    .navbar {
      background: linear-gradient(135deg, rgba(15, 23, 42, 0.95), rgba(30, 41, 59, 0.95));
      backdrop-filter: blur(20px);
      padding: 1.5rem 2.5rem;
      box-shadow: 0 0 50px rgba(124, 58, 237, 0.15);
      display: flex;
      justify-content: space-between;
      align-items: center;
      position: sticky;
      top: 0;
      z-index: 1000;
      border-bottom: 1px solid rgba(124, 58, 237, 0.2);
    }

    .navbar h1 {
      font-size: 1.75rem;
      font-weight: 700;
      background: linear-gradient(135deg, #7c3aed, #06b6d4);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      filter: drop-shadow(0 0 20px rgba(124, 58, 237, 0.3));
    }

    .nav-user {
      display: flex;
      align-items: center;
      gap: 1.5rem;
      font-size: 0.95rem;
      color: #cbd5e1;
      font-weight: 500;
    }

    .nav-links {
      display: flex;
      gap: 1rem;
    }

    .nav-link {
      padding: 0.7rem 1.5rem;
      background: linear-gradient(135deg, #7c3aed, #6d28d9);
      color: white;
      text-decoration: none;
      border-radius: 10px;
      font-weight: 600;
      font-size: 0.95rem;
      transition: var(--transition);
      box-shadow: 0 4px 15px rgba(124, 58, 237, 0.3);
    }

    .nav-link:hover {
      background: linear-gradient(135deg, #6d28d9, #7c3aed);
      transform: translateY(-3px);
      box-shadow: 0 8px 25px rgba(124, 58, 237, 0.5);
    }

    .nav-link.secondary {
      background: transparent;
      color: #a78bfa;
      border: 2px solid #7c3aed;
      box-shadow: none;
    }

    .nav-link.secondary:hover {
      background: rgba(124, 58, 237, 0.2);
      color: #e9d5ff;
    }

    /* Container */
    .container {
      max-width: 1400px;
      margin: 2.5rem auto;
      padding: 0 1.5rem;
    }

    .page-header {
      margin-bottom: 3rem;
      text-align: center;
    }

    .page-title {
      font-size: 2.5rem;
      font-weight: 700;
      background: linear-gradient(135deg, #7c3aed, #06b6d4, #ec4899);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      text-shadow: 0 0 30px rgba(124, 58, 237, 0.2);
      filter: drop-shadow(0 0 10px rgba(124, 58, 237, 0.2));
    }

    .page-subtitle {
      font-size: 1.1rem;
      color: #94a3b8;
      margin-top: 0.75rem;
      font-weight: 500;
    }

    /* Stats Grid */
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
      gap: 1.5rem;
      margin-bottom: 3rem;
    }

    .stat-card {
      background: linear-gradient(135deg, rgba(30, 41, 59, 0.8), rgba(30, 41, 59, 0.5));
      border-radius: var(--radius);
      padding: 2rem;
      text-align: center;
      box-shadow: 0 0 40px rgba(124, 58, 237, 0.1);
      transition: var(--transition);
      border: 1px solid rgba(124, 58, 237, 0.2);
      position: relative;
      overflow: hidden;
    }

    .stat-card::before {
      content: '';
      position: absolute;
      top: -50%;
      right: -50%;
      width: 200px;
      height: 200px;
      background: radial-gradient(circle, rgba(124, 58, 237, 0.1), transparent);
      animation: float 6s ease-in-out infinite;
    }

    @keyframes float {
      0%, 100% { transform: translate(0, 0); }
      50% { transform: translate(-30px, -30px); }
    }

    .stat-card::after {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 2px;
      background: linear-gradient(90deg, transparent, #7c3aed, transparent);
      opacity: 0;
      transition: var(--transition);
    }

    .stat-card:hover {
      transform: translateY(-8px);
      box-shadow: 0 0 60px rgba(124, 58, 237, 0.3);
      border-color: rgba(124, 58, 237, 0.5);
      background: linear-gradient(135deg, rgba(30, 41, 59, 0.9), rgba(30, 41, 59, 0.6));
    }

    .stat-card:hover::after {
      opacity: 1;
    }

    .stat-icon {
      font-size: 2.5rem;
      margin-bottom: 1rem;
      background: linear-gradient(135deg, #7c3aed, #06b6d4);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .stat-value {
      font-size: 2.5rem;
      font-weight: 700;
      background: linear-gradient(135deg, #7c3aed, #06b6d4);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin-bottom: 0.5rem;
    }

    .stat-label {
      font-size: 0.875rem;
      color: #94a3b8;
      text-transform: uppercase;
      letter-spacing: 0.8px;
      font-weight: 600;
    }

    /* Section Cards */
    .section-card {
      background: linear-gradient(135deg, rgba(30, 41, 59, 0.8), rgba(30, 41, 59, 0.5));
      border-radius: var(--radius);
      padding: 2.25rem;
      margin-bottom: 2rem;
      box-shadow: 0 0 40px rgba(124, 58, 237, 0.1);
      transition: var(--transition);
      border: 1px solid rgba(124, 58, 237, 0.2);
    }

    .section-card:hover {
      box-shadow: 0 0 60px rgba(124, 58, 237, 0.2);
      border-color: rgba(124, 58, 237, 0.4);
    }

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.75rem;
      flex-wrap: wrap;
      gap: 1rem;
      padding-bottom: 1.25rem;
      border-bottom: 1px solid rgba(124, 58, 237, 0.2);
    }

    .section-title {
      font-size: 1.5rem;
      font-weight: 700;
      background: linear-gradient(135deg, #7c3aed, #06b6d4);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .btn {
      padding: 0.875rem 1.75rem;
      border: none;
      border-radius: 10px;
      font-weight: 600;
      font-size: 0.95rem;
      text-decoration: none;
      display: inline-flex;
      align-items: center;
      gap: 0.75rem;
      transition: var(--transition);
      cursor: pointer;
      box-shadow: 0 4px 15px rgba(124, 58, 237, 0.3);
    }

    .btn-primary {
      background: linear-gradient(135deg, #7c3aed, #6d28d9);
      color: white;
    }

    .btn-primary:hover {
      transform: translateY(-3px);
      box-shadow: 0 8px 30px rgba(124, 58, 237, 0.5);
      background: linear-gradient(135deg, #6d28d9, #7c3aed);
    }

    .btn-success {
      background: linear-gradient(135deg, #10b981, #059669);
      color: white;
    }

    .btn-success:hover {
      transform: translateY(-3px);
      box-shadow: 0 8px 30px rgba(16, 185, 129, 0.5);
      background: linear-gradient(135deg, #059669, #10b981);
    }

    /* Tables */
    .data-table {
      width: 100%;
      border-collapse: separate;
      border-spacing: 0 0.875rem;
    }

    .data-table thead th {
      padding: 1.1rem 1.25rem;
      background: linear-gradient(135deg, rgba(124, 58, 237, 0.2), rgba(6, 182, 212, 0.2));
      color: #e9d5ff;
      font-weight: 600;
      font-size: 0.85rem;
      text-transform: uppercase;
      letter-spacing: 0.8px;
      text-align: left;
      border-radius: 10px 0 0 10px;
      border-bottom: 1px solid rgba(124, 58, 237, 0.2);
    }

    .data-table thead th:last-child {
      border-radius: 0 10px 10px 0;
    }

    .data-table tbody tr {
      background: rgba(30, 41, 59, 0.6);
      transition: var(--transition);
      border-radius: 10px;
      border: 1px solid rgba(124, 58, 237, 0.1);
    }

    .data-table tbody tr:hover {
      background: linear-gradient(135deg, rgba(124, 58, 237, 0.1), rgba(6, 182, 212, 0.05));
      transform: translateY(-3px);
      box-shadow: 0 0 40px rgba(124, 58, 237, 0.2);
      border-color: rgba(124, 58, 237, 0.3);
    }

    .data-table td {
      padding: 1.5rem 1.25rem;
      color: #cbd5e1;
    }

    .data-table tbody tr td:first-child {
      border-radius: 10px 0 0 10px;
    }

    .data-table tbody tr td:last-child {
      border-radius: 0 10px 10px 0;
    }

    .badge {
      padding: 0.4rem 0.875rem;
      border-radius: 20px;
      font-size: 0.8rem;
      font-weight: 700;
      display: inline-block;
      box-shadow: 0 2px 10px rgba(0,0,0,0.3);
    }

    .badge-teacher {
      background: linear-gradient(135deg, rgba(124, 58, 237, 0.2), rgba(124, 58, 237, 0.1));
      color: #c4b5fd;
      border: 1px solid rgba(124, 58, 237, 0.3);
    }

    .badge-student {
      background: linear-gradient(135deg, rgba(236, 72, 153, 0.2), rgba(236, 72, 153, 0.1));
      color: #f472b6;
      border: 1px solid rgba(236, 72, 153, 0.3);
    }

    .badge-group {
      background: linear-gradient(135deg, rgba(16, 185, 129, 0.2), rgba(16, 185, 129, 0.1));
      color: #6ee7b7;
      border: 1px solid rgba(16, 185, 129, 0.3);
    }

    .name {
      font-weight: 700;
      font-size: 1rem;
      color: #f1f5f9;
    }

    .empty-state {
      text-align: center;
      padding: 5rem 2.5rem;
      color: #94a3b8;
    }

    .empty-icon {
      font-size: 4.5rem;
      margin-bottom: 1.5rem;
      background: linear-gradient(135deg, #7c3aed, #06b6d4);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      opacity: 0.7;
    }

    .empty-title {
      font-size: 1.5rem;
      font-weight: 700;
      color: #cbd5e1;
      margin-bottom: 0.75rem;
    }

    .success-alert {
      background: linear-gradient(135deg, rgba(16, 185, 129, 0.15), rgba(16, 185, 129, 0.05));
      color: #6ee7b7;
      padding: 1.25rem;
      border-radius: var(--radius);
      border-left: 4px solid #10b981;
      margin-bottom: 2rem;
      font-size: 1rem;
      font-weight: 600;
      box-shadow: 0 0 30px rgba(16, 185, 129, 0.15);
      display: flex;
      align-items: center;
      gap: 0.75rem;
      border: 1px solid rgba(16, 185, 129, 0.2);
    }

    /* Responsive */
    @media (max-width: 992px) {
      .stats-grid { grid-template-columns: repeat(2, 1fr); }
    }

    @media (max-width: 768px) {
      .navbar {
        flex-direction: column;
        gap: 1rem;
        text-align: center;
        padding: 1.25rem;
      }
      .page-title { font-size: 2rem; }
      .section-header {
        flex-direction: column;
        align-items: stretch;
      }
      .data-table thead { display: none; }
      .data-table tbody tr {
        display: block;
        margin-bottom: 1.5rem;
        padding: 1.5rem;
        border-radius: var(--radius);
        background: rgba(30, 41, 59, 0.8);
        box-shadow: 0 0 30px rgba(124, 58, 237, 0.1);
      }
      .data-table td {
        display: block;
        text-align: left;
        padding: 0.75rem 0;
        border: none;
      }
      .data-table td:before {
        content: attr(data-label);
        font-weight: 700;
        color: #a78bfa;
        display: block;
        margin-bottom: 0.5rem;
      }
      .stats-grid { grid-template-columns: 1fr; }
    }
  </style>
</head>
<body>

<!-- Navbar -->
<div class="navbar">
  <h1>Admin Dashboard</h1>
  <div class="nav-user">
    <span>${currentUser.fullName}</span>
    <div class="nav-links">
      <a href="${pageContext.request.contextPath}/profile" class="nav-link secondary">Profile</a>
      <a href="${pageContext.request.contextPath}/logout" class="nav-link">Logout</a>
    </div>
  </div>
</div>

<div class="container">

  <!-- Page Header -->
  <div class="page-header">
    <div class="page-title">System Overview</div>
    <div class="page-subtitle">Manage teachers, students, and groups</div>
  </div>

  <!-- Success Message -->
  <% if (request.getParameter("success") != null) { %>
  <div class="success-alert">
    <i class="fas fa-check-circle"></i>
    <% if ("teacher_created".equals(request.getParameter("success"))) { %>
    Teacher successfully created!
    <% } else if ("group_created".equals(request.getParameter("success"))) { %>
    Group successfully created!
    <% } %>
  </div>
  <% } %>

  <!-- Statistics -->
  <div class="stats-grid">
    <div class="stat-card">
      <div class="stat-icon"><i class="fas fa-chalkboard-user"></i></div>
      <div class="stat-value">${teacherCount}</div>
      <div class="stat-label">Teachers</div>
    </div>
    <div class="stat-card">
      <div class="stat-icon"><i class="fas fa-graduation-cap"></i></div>
      <div class="stat-value">${studentCount}</div>
      <div class="stat-label">Students</div>
    </div>
    <div class="stat-card">
      <div class="stat-icon"><i class="fas fa-users"></i></div>
      <div class="stat-value">${groups != null ? groups.size() : 0}</div>
      <div class="stat-label">Active Groups</div>
    </div>
    <div class="stat-card">
      <div class="stat-icon"><i class="fas fa-user-tie"></i></div>
      <div class="stat-value">${teacherCount + studentCount + 1}</div>
      <div class="stat-label">Total Users</div>
    </div>
  </div>

  <!-- Teachers Section -->
  <div class="section-card">
    <div class="section-header">
      <div class="section-title"><i class="fas fa-chalkboard-user"></i> Teachers</div>
      <a href="${pageContext.request.contextPath}/admin/create-teacher" class="btn btn-primary">
        <i class="fas fa-plus"></i> Add Teacher
      </a>
    </div>

    <% if (teachers == null || teachers.isEmpty()) { %>
    <div class="empty-state">
      <div class="empty-icon"><i class="fas fa-user-graduate"></i></div>
      <div class="empty-title">No teachers yet</div>
      <a href="${pageContext.request.contextPath}/admin/create-teacher" class="btn btn-primary" style="margin-top: 1.25rem;">
        <i class="fas fa-plus"></i> Add First Teacher
      </a>
    </div>
    <% } else { %>
    <table class="data-table">
      <thead>
      <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Email</th>
        <th>Specialization</th>
        <th>Groups</th>
        <th>Assignments</th>
      </tr>
      </thead>
      <tbody>
      <% for (Teacher t : teachers) { %>
      <tr>
        <td data-label="ID"><span class="badge badge-teacher"><%= t.getId().substring(0, 8) %></span></td>
        <td data-label="Name" class="name"><%= t.getFullName() %></td>
        <td data-label="Email"><%= t.getEmail() %></td>
        <td data-label="Specialization"><%= t.getSpecialization() != null ? t.getSpecialization() : "Not specified" %></td>
        <td data-label="Groups"><span class="badge badge-group"><%= t.getGroupID() != null ? t.getGroupID().size() : 0 %></span></td>
        <td data-label="Assignments"><span class="badge badge-group"><%= t.getAssignmentID() != null ? t.getAssignmentID().size() : 0 %></span></td>
      </tr>
      <% } %>
      </tbody>
    </table>
    <% } %>
  </div>

  <!-- Groups Section -->
  <div class="section-card">
    <div class="section-header">
      <div class="section-title"><i class="fas fa-users"></i> Groups</div>
      <a href="${pageContext.request.contextPath}/admin/create-group" class="btn btn-success">
        <i class="fas fa-plus"></i> Create Group
      </a>
    </div>

    <% if (groups == null || groups.isEmpty()) { %>
    <div class="empty-state">
      <div class="empty-icon"><i class="fas fa-layer-group"></i></div>
      <div class="empty-title">No groups created yet</div>
      <a href="${pageContext.request.contextPath}/admin/create-group" class="btn btn-success" style="margin-top: 1.25rem;">
        <i class="fas fa-plus"></i> Create First Group
      </a>
    </div>
    <% } else { %>
    <table class="data-table">
      <thead>
      <tr>
        <th>Name</th>
        <th>Description</th>
        <th>Teachers</th>
        <th>Students</th>
        <th>Assignments</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <% for (Group g : groups) { %>
      <tr>
        <td data-label="Name" class="name"><%= g.getGroupName() %></td>
        <td data-label="Description"><%= g.getDescription() != null && !g.getDescription().trim().isEmpty() ? g.getDescription() : "<em>No description</em>" %></td>
        <td data-label="Teachers"><span class="badge badge-teacher"><%= g.getTeacherIDs() != null ? g.getTeacherIDs().size() : 0 %></span></td>
        <td data-label="Students"><span class="badge badge-student"><%= g.getStudentIDs() != null ? g.getStudentIDs().size() : 0 %></span></td>
        <td data-label="Assignments"><span class="badge badge-group"><%= g.getAssignmentIDs() != null ? g.getAssignmentIDs().size() : 0 %></span></td>
        <td data-label="Actions">
          <a href="${pageContext.request.contextPath}/admin/manage-group?id=<%= g.getId() %>" class="btn btn-primary" style="padding: 0.625rem 1.25rem; font-size: 0.875rem;">
            <i class="fas fa-edit"></i> Manage
          </a>
        </td>
      </tr>
      <% } %>
      </tbody>
    </table>
    <% } %>
  </div>

  <!-- Students Section -->
  <div class="section-card">
    <div class="section-header">
      <div class="section-title"><i class="fas fa-graduation-cap"></i> Students</div>
    </div>

    <% if (students == null || students.isEmpty()) { %>
    <div class="empty-state">
      <div class="empty-icon"><i class="fas fa-user-graduate"></i></div>
      <div class="empty-title">No students registered yet</div>
    </div>
    <% } else { %>
    <table class="data-table">
      <thead>
      <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Email</th>
        <th>Group</th>
        <th>Submissions</th>
      </tr>
      </thead>
      <tbody>
      <% for (Student s : students) { %>
      <tr>
        <td data-label="ID"><span class="badge badge-student"><%= s.getId().substring(0, 8) %></span></td>
        <td data-label="Name" class="name"><%= s.getFullName() %></td>
        <td data-label="Email"><%= s.getEmail() %></td>
        <td data-label="Group"><%= s.getGroupId() != null ? s.getGroupId() : "<em>Not assigned</em>" %></td>
        <td data-label="Submissions"><span class="badge badge-group"><%= s.getSubmissionID() != null ? s.getSubmissionID().size() : 0 %></span></td>
      </tr>
      <% } %>
      </tbody>
    </table>
    <% } %>
  </div>

</div>
</body>
</html>
