<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, com.university.moodle.model.*, java.time.format.DateTimeFormatter, java.time.LocalDateTime" %>
<%
    Group group = (Group) request.getAttribute("group");
    List<Submission> submissions = (List<Submission>) request.getAttribute("submissions");
    List<Student> students = (List<Student>) request.getAttribute("students");
    List<Assignment> assignments = (List<Assignment>) request.getAttribute("assignments");
    String downloadBaseUrl = (String) request.getAttribute("downloadBaseUrl");

    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    LocalDateTime now = LocalDateTime.now();

    Map<String, Submission> submissionMap = new HashMap<>();
    for (Submission sub : submissions) {
        submissionMap.put(sub.getStudentId(), sub);
    }

%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= group != null ? group.getGroupName() : "Group" %> • University LMS</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <style>
        :root {
            --primary: #6366f1;
            --primary-light: #818cf8;
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
            --radius: 14px;
            --shadow: 0 10px 25px rgba(0,0,0,0.08);
            --shadow-lg: 0 20px 40px rgba(0,0,0,0.12);
            --transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
        }

        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
            background: var(--gray-50);
            color: var(--gray-900);
            line-height: 1.6;
            min-height: 100vh;
        }

        /* Header */
        .header {
            background: white;
            border-bottom: 1px solid var(--gray-200);
            padding: 1.25rem 0;
            position: sticky;
            top: 0;
            z-index: 50;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }

        .header-container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 0 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .header-title {
            font-size: 1.75rem;
            font-weight: 700;
            color: var(--gray-900);
            display: flex;
            align-items: center;
            gap: 0.75rem;
        }

        .back-link {
            color: var(--primary);
            text-decoration: none;
            font-weight: 600;
            font-size: 0.95rem;
            padding: 0.6rem 1.2rem;
            border-radius: 10px;
            transition: var(--transition);
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .back-link:hover {
            background: var(--gray-100);
        }

        /* Main Container */
        .container {
            max-width: 1400px;
            margin: 2.5rem auto;
            padding: 0 2rem;
        }

        /* Group Overview Card */
        .overview-card {
            background: white;
            border-radius: var(--radius);
            padding: 2rem;
            box-shadow: var(--shadow);
            margin-bottom: 2rem;
            border: 1px solid var(--gray-200);
        }

        .overview-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            flex-wrap: wrap;
            gap: 1.5rem;
            margin-bottom: 1.5rem;
        }

        .group-title {
            font-size: 2rem;
            font-weight: 700;
            color: var(--gray-900);
        }

        .group-desc {
            color: var(--gray-600);
            margin-top: 0.5rem;
            font-size: 1.1rem;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 1.5rem;
        }

        .stat-card {
            background: var(--gray-50);
            padding: 1.5rem;
            border-radius: var(--radius);
            text-align: center;
            border: 1px solid var(--gray-200);
        }

        .stat-value {
            font-size: 2.25rem;
            font-weight: 700;
            color: var(--primary);
        }

        .stat-label {
            color: var(--gray-600);
            font-size: 0.9rem;
            margin-top: 0.5rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        /* Section Cards */
        .section-card {
            background: white;
            border-radius: var(--radius);
            padding: 2rem;
            margin-bottom: 2rem;
            box-shadow: var(--shadow);
            border: 1px solid var(--gray-200);
            transition: var(--transition);
        }

        .section-card:hover {
            box-shadow: var(--shadow-lg);
            transform: translateY(-4px);
        }

        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.75rem;
            flex-wrap: wrap;
            gap: 1rem;
        }

        .section-title {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--gray-900);
            display: flex;
            align-items: center;
            gap: 0.75rem;
        }

        /* Students Grid */
        .students-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
            gap: 1.25rem;
        }

        .student-card {
            background: var(--gray-50);
            border-radius: 12px;
            padding: 1.25rem;
            display: flex;
            align-items: center;
            gap: 1rem;
            transition: var(--transition);
            border: 1px solid var(--gray-200);
        }

        .student-card:hover {
            background: #eef2ff;
            border-color: var(--primary-light);
            transform: translateY(-3px);
        }

        .avatar {
            width: 56px;
            height: 56px;
            border-radius: 50%;
            background: var(--primary);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            font-size: 1.4rem;
            flex-shrink: 0;
        }

        .student-info h4 {
            font-weight: 600;
            color: var(--gray-900);
            margin-bottom: 0.25rem;
        }

        .student-info p {
            color: var(--gray-600);
            font-size: 0.95rem;
        }

        /* Assignments Table */
        .assignments-table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0 1rem;
        }

        .assignments-table thead th {
            background: var(--primary);
            color: white;
            padding: 1rem 1.25rem;
            text-align: left;
            font-weight: 600;
            font-size: 0.875rem;
            text-transform: uppercase;
            letter-spacing: 0.8px;
        }

        .assignments-table tbody tr {
            background: var(--gray-50);
            transition: var(--transition);
        }

        .assignments-table tbody tr:hover {
            background: #eef2ff;
            transform: translateY(-4px);
            box-shadow: var(--shadow);
        }

        .assignments-table td {
            padding: 1.5rem 1.25rem;
        }

        .assignment-title {
            font-weight: 600;
            font-size: 1.1rem;
            color: var(--gray-900);
        }

        .deadline {
            font-weight: 500;
            color: var(--gray-700);
        }

        .deadline.overdue { color: var(--danger); font-weight: 700; }
        .deadline.soon { color: var(--warning); font-weight: 600; }

        .status-badge {
            padding: 0.5rem 1rem;
            border-radius: 999px;
            font-size: 0.8rem;
            font-weight: 600;
        }

        .status-attached { background: #d1fae5; color: #065f46; }
        .status-none { background: var(--gray-200); color: var(--gray-600); }

        .action-buttons {
            display: flex;
            gap: 0.75rem;
            flex-wrap: wrap;
        }

        .btn {
            padding: 0.75rem 1.25rem;
            border: none;
            border-radius: 10px;
            font-weight: 600;
            font-size: 0.9rem;
            text-decoration: none;
            cursor: pointer;
            transition: var(--transition);
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
        }

        .btn-primary {
            background: var(--primary);
            color: white;
        }

        .btn-primary:hover {
            background: var(--primary-dark);
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(99,102,241,0.3);
        }

        .btn-success {
            background: var(--success);
            color: white;
        }

        .btn-info {
            background: #3b82f6;
            color: white;
        }

        .btn:hover {
            transform: translateY(-2px);
        }

        /* Empty State */
        .empty-state {
            text-align: center;
            padding: 4rem 2rem;
            color: var(--gray-500);
        }

        .empty-icon {
            font-size: 4.5rem;
            margin-bottom: 1.5rem;
            opacity: 0.4;
        }

        .empty-title {
            font-size: 1.5rem;
            font-weight: 600;
            color: var(--gray-700);
            margin-bottom: 0.5rem;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .container { padding: 0 1rem; }
            .header-container { padding: 0 1rem; flex-direction: column; gap: 1rem; text-align: center; }
            .overview-header { flex-direction: column; align-items: stretch; }
            .stats-grid { grid-template-columns: 1fr; }
            .assignments-table thead { display: none; }
            .assignments-table tbody tr { display: block; margin-bottom: 1.5rem; border-radius: 14px; padding: 1.5rem; }
            .assignments-table td { display: block; text-align: center; padding: 0.75rem 0; }
            .assignments-table td:before {
                content: attr(data-label) ": ";
                font-weight: 600;
                color: var(--primary);
            }
            .action-buttons { justify-content: center; }
        }
    </style>
</head>
<body>

<header class="header">
    <div class="header-container">
        <h1 class="header-title">
            Group: <%= group != null ? group.getGroupName() : "Loading..." %>
        </h1>
        <a href="<%= request.getContextPath() %>/teacher/dashboard" class="back-link">
            Back to Dashboard
        </a>
    </div>
</header>

<div class="container">

    <!-- Group Overview -->
    <div class="overview-card">
        <div class="overview-header">
            <div>
                <h2 class="group-title"><%= group != null ? group.getGroupName() : "Group" %></h2>
                <% if (group != null && group.getDescription() != null && !group.getDescription().trim().isEmpty()) { %>
                <p class="group-desc"><%= group.getDescription() %></p>
                <% } %>
            </div>
        </div>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-value"><%= students != null ? students.size() : 0 %></div>
                <div class="stat-label">Enrolled Students</div>
            </div>
            <div class="stat-card">
                <div class="stat-value"><%= assignments != null ? assignments.size() : 0 %></div>
                <div class="stat-label">Active Assignments</div>
            </div>
        </div>
    </div>

    <!-- Students Section -->
    <div class="section-card">
        <div class="section-header">
            <h3 class="section-title">Enrolled Students</h3>
        </div>

        <% if (students == null || students.isEmpty()) { %>
        <div class="empty-state">
            <div class="empty-icon">No students</div>
            <div class="empty-title">No students enrolled</div>
            <p>This group currently has no enrolled students.</p>
        </div>
        <% } else { %>
        <div class="students-grid">
            <% for (Student s : students) {
                Submission sub = submissionMap.get(s.getId());
                boolean hasSubmission = sub != null;
            %>
            <div class="student-card">
                <div class="avatar"><%= s.getFullName().charAt(0) %></div>
                <div class="student-info">
                    <h4><%= s.getFullName() %></h4>
                    <p><%= s.getEmail() %></p>
                </div>
            </div>
            <% } %>
        </div>
        <% } %>
    </div>

    <!-- Assignments Section -->
    <div class="section-card">
        <div class="section-header">
            <h3 class="section-title">Assignments</h3>
            <form action="<%= request.getContextPath() %>/teacher/create-assignment" style="display:inline;">
                <input type="hidden" name="groupId" value="<%= group != null ? group.getId() : "" %>">
                <button type="submit" class="btn btn-primary">
                    Create New Assignment
                </button>
            </form>
        </div>

        <% if (assignments == null || assignments.isEmpty()) { %>
        <div class="empty-state">
            <div class="empty-icon">No assignments</div>
            <div class="empty-title">No assignments created</div>
            <p>Get started by creating your first assignment for this group.</p>
        </div>
        <% } else { %>
        <table class="assignments-table">
            <thead>
            <tr>
                <th>Title</th>
                <th>Deadline</th>
                <th>Max Score</th>
                <th>Attachment</th>
                <th style="text-align: right;">Actions</th>
            </tr>
            </thead>
            <tbody>
            <% for (Assignment a : assignments) {
                boolean hasFile = a.getFilePath() != null && !a.getFilePath().trim().isEmpty();
                boolean isOverdue = a.getDeadline() != null && now.isAfter(a.getDeadline());
                boolean isSoon = a.getDeadline() != null && !isOverdue && a.getDeadline().isBefore(now.plusDays(3));
            %>
            <tr>
                <td class="assignment-title" data-label="Title"><%= a.getTitle() %></td>
                <td class="deadline <%= isOverdue ? "overdue" : (isSoon ? "soon" : "") %>" data-label="Deadline">
                    <% if (a.getDeadline() != null) { %>
                    <%= a.getDeadline().format(fmt) %>
                    <% if (isOverdue) { %> <strong>(Overdue)</strong><% } %>
                    <% if (isSoon) { %> <em>(Due soon)</em><% } %>
                    <% } else { %>
                    <em>No deadline set</em>
                    <% } %>
                </td>
                <td data-label="Score"><strong><%= a.getMaxScore() %></strong> pts</td>
                <td data-label="File">
                            <span class="status-badge <%= hasFile ? "status-attached" : "status-none" %>">
                                <%= hasFile ? "Attached" : "None" %>
                            </span>
                </td>
                <td class="action-buttons" style="text-align: right;" data-label="Actions">
                    <% if (hasFile) { %>
                        <a href="<%= request.getContextPath() %>/download?file=<%= a.getFilePath() %>"
                           target="_blank"
                           onmouseover="this.style.backgroundColor='#2980b9'; this.style.transform='translateY(-2px)';"
                           onmouseout="this.style.backgroundColor='#3498db'; this.style.transform='translateY(0)';"
                           style="
                           display: inline-flex;
                           align-items: center;
                           padding: 8px 14px;
                           background-color: #3498db;
                           color: #fff;
                           text-decoration: none;
                           border-radius: 6px;
                           font-weight: 500;
                           font-size: 14px;
                           transition: background-color 0.3s, transform 0.2s;">
                            📎 Download File
                        </a>

                    <a href="<%= request.getContextPath() %>/teacher/submissions?assignmentId=<%= a.getId() %>"
                       onmouseover="this.style.backgroundColor='#2980b9'; this.style.transform='translateY(-2px)';"
                       onmouseout="this.style.backgroundColor='#3498db'; this.style.transform='translateY(0)';"
                       style="
                       display: inline-flex;
                       align-items: center;
                       padding: 8px 14px;
                       background-color: #3498db;
                       color: #fff;
                       text-decoration: none;
                       border-radius: 6px;
                       font-weight: 500;
                       font-size: 14px;
                       transition: background-color 0.3s, transform 0.2s;"
                    >
                        View Submissions
                    </a>
                    <% } %>
                    <% } %>
                </td>
            </tr>
            </tbody>
        </table>
        <% } %>
    </div>

</div>
</body>
</html>
