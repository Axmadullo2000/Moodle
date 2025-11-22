<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, com.university.moodle.model.Group" %>
<%
  List<Group> groups = (List<Group>) request.getAttribute("groups");
  boolean hasGroups = groups != null && !groups.isEmpty();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>My Groups • Teacher Dashboard</title>
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
      --card-shadow: 0 15px 40px rgba(0, 0, 0, 0.12);
      --hover-shadow: 0 25px 55px rgba(0, 0, 0, 0.18);
    }

    * { margin: 0; padding: 0; box-sizing: border-box; }
    body {
      font-family: 'Inter', 'Segoe UI', Tahoma, sans-serif;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      min-height: 100vh;
      color: #333;
    }

    /* Navbar */
    .navbar {
      background: rgba(255, 255, 255, 0.97);
      backdrop-filter: blur(12px);
      padding: 22px 40px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
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
      gap: 16px;
      align-items: center;
    }

    .nav-link {
      padding: 11px 22px;
      background: var(--primary);
      color: white;
      text-decoration: none;
      border-radius: 12px;
      font-weight: 600;
      font-size: 15px;
      transition: all 0.3s ease;
    }

    .nav-link:hover {
      background: var(--primary-dark);
      transform: translateY(-3px);
      box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
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
      padding: 0 24px;
    }

    .page-header {
      margin-bottom: 36px;
      text-align: center;
    }

    .page-title {
      font-size: 32px;
      font-weight: 700;
      color: white;
      text-shadow: 0 3px 12px rgba(0,0,0,0.3);
    }

    .page-subtitle {
      font-size: 18px;
      color: rgba(255,255,255,0.9);
      margin-top: 8px;
    }

    /* Card */
    .card {
      background: white;
      border-radius: 20px;
      overflow: hidden;
      box-shadow: var(--card-shadow);
      transition: all 0.4s ease;
    }

    .card:hover {
      transform: translateY(-10px);
      box-shadow: var(--hover-shadow);
    }

    .card-header {
      padding: 32px 36px 0;
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 16px;
    }

    .card-title {
      font-size: 26px;
      font-weight: 700;
      color: var(--dark);
    }

    /* Table */
    .groups-table {
      width: 100%;
      border-collapse: separate;
      border-spacing: 0 14px;
    }

    .groups-table thead th {
      padding: 18px 24px;
      text-align: left;
      font-weight: 600;
      color: var(--gray);
      font-size: 13px;
      text-transform: uppercase;
      letter-spacing: 0.8px;
    }

    .groups-table tbody tr {
      background: #f8fafc;
      border-radius: 16px;
      transition: all 0.3s ease;
    }

    .groups-table tbody tr:hover {
      background: #edf2f7;
      transform: translateY(-4px);
      box-shadow: 0 12px 30px rgba(0,0,0,0.1);
    }

    .groups-table td {
      padding: 24px;
      color: var(--dark);
      font-size: 15px;
    }

    .group-name {
      font-weight: 600;
      font-size: 17px;
      color: var(--dark);
    }

    .group-desc {
      color: var(--gray);
      font-size: 14px;
    }

    .stats {
      display: flex;
      gap: 20px;
      font-size: 15px;
    }

    .stat {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .stat strong {
      color: var(--primary);
      font-weight: 700;
    }

    /* Action Button */
    .btn-open {
      padding: 12px 28px;
      background: var(--primary);
      color: white;
      text-decoration: none;
      border-radius: 12px;
      font-weight: 600;
      font-size: 15px;
      transition: all 0.3s ease;
    }

    .btn-open:hover {
      background: var(--primary-dark);
      transform: translateY(-3px);
      box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
    }

    /* Empty State */
    .empty-state {
      text-align: center;
      padding: 90px 40px;
      color: var(--gray);
    }

    .empty-icon {
      font-size: 80px;
      margin-bottom: 24px;
      opacity: 0.4;
    }

    .empty-title {
      font-size: 28px;
      font-weight: 600;
      color: var(--dark);
      margin-bottom: 16px;
    }

    .empty-text {
      font-size: 17px;
      max-width: 600px;
      margin: 0 auto 32px;
      line-height: 1.7;
      color: #718096;
    }

    /* Responsive */
    @media (max-width: 992px) {
      .stats { flex-direction: column; gap: 12px; }
    }

    @media (max-width: 768px) {
      .navbar { flex-direction: column; padding: 20px; text-align: center; gap: 16px; }
      .page-title { font-size: 28px; }
      .groups-table thead { display: none; }
      .groups-table tbody tr {
        display: block;
        margin-bottom: 20px;
        padding: 20px;
      }
      .groups-table td {
        display: block;
        text-align: center;
        padding: 10px 0;
        border: none;
      }
      .groups-table td:before {
        content: attr(data-label);
        font-weight: 600;
        color: var(--primary);
        display: block;
        margin-bottom: 8px;
        font-size: 14px;
      }
    }
  </style>
</head>
<body>

<!-- Navbar -->
<div class="navbar">
  <h1>My Groups</h1>
  <div class="nav-links">
    <a href="<%= request.getContextPath() %>/logout" class="nav-link secondary">Logout</a>
  </div>
</div>

<div class="container">

  <!-- Page Header -->
  <div class="page-header">
    <div class="page-title">Your Teaching Groups</div>
    <div class="page-subtitle">Manage assignments and track student progress</div>
  </div>

  <!-- Groups Card -->
  <div class="card">
    <div class="card-header">
      <div class="card-title">All Groups</div>
    </div>

    <% if (!hasGroups) { %>
    <div class="empty-state">
      <div class="empty-icon">No groups assigned</div>
      <div class="empty-title">No groups yet</div>
      <div class="empty-text">
        You haven't been assigned to any teaching groups.<br>
        Please contact your administrator to get access to your classes.
      </div>
    </div>
    <% } else { %>
    <table class="groups-table">
      <thead>
      <tr>
        <th>Group Name</th>
        <th>Description</th>
        <th>Students & Assignments</th>
        <th>Action</th>
      </tr>
      </thead>
      <tbody>
      <% for (Group g : groups) { %>
      <tr>
        <td class="group-name" data-label="Group">
          <%= g.getGroupName() %>
        </td>
        <td class="group-desc" data-label="Description">
          <%= g.getDescription() != null && !g.getDescription().trim().isEmpty()
                  ? g.getDescription() : "<em>No description</em>" %>
        </td>
        <td data-label="Stats">
          <div class="stats">
            <div class="stat">
              <strong><%= g.getStudentIDs() != null ? g.getStudentIDs().size() : 0 %></strong> students
            </div>
            <div class="stat">
              <strong><%= g.getAssignmentIDs() != null ? g.getAssignmentIDs().size() : 0 %></strong> assignments
            </div>
          </div>
        </td>
        <td data-label="Action">
          <a href="<%= request.getContextPath() %>/teacher/group?groupId=<%= g.getId() %>"
             class="btn-open">
            Open Group
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
