<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, com.university.moodle.model.*, java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDateTime" %>
<%
    Assignment assignment = (Assignment) request.getAttribute("assignment");
    List<Submission> submissions = (List<Submission>) request.getAttribute("submissions");
    List<Student> groupStudents = (List<Student>) request.getAttribute("groupStudents");
    String downloadBaseUrl = (String) request.getAttribute("downloadBaseUrl");

    int totalStudents = groupStudents != null ? groupStudents.size() : 0;
    int submittedCount = submissions != null ? submissions.size() : 0;
    int gradedCount = 0;
    if (submissions != null) {
        for (Submission s : submissions) {
            if (s.getScore() != null) gradedCount++;
        }
    }

    DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    LocalDateTime now = LocalDateTime.now();

    // Map for fast lookup
    Map<String, Submission> submissionMap = new HashMap<>();
    if (submissions != null) {
        for (Submission s : submissions) {
            submissionMap.put(s.getStudentId(), s);
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Submissions • <%= assignment.getTitle() %></title>
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
            padding: 24px 40px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
            display: flex;
            justify-content: space-between;
            align-items: center;
            position: sticky;
            top: 0;
            z-index: 1000;
            border-radius: 0 0 20px 20px;
        }

        .assignment-header {
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .assignment-title {
            font-size: 26px;
            font-weight: 700;
            color: var(--dark);
        }

        .assignment-meta {
            font-size: 15px;
            color: var(--gray);
        }

        .nav-actions {
            display: flex;
            gap: 14px;
            align-items: center;
        }

        .btn {
            padding: 11px 22px;
            border: none;
            border-radius: 12px;
            font-weight: 600;
            font-size: 14px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
            cursor: pointer;
        }

        .btn-back { background: #6c757d; color: white; }
        .btn-back:hover { background: #5a6268; transform: translateY(-3px); }

        .btn-download { background: #27ae60; color: white; }
        .btn-download:hover { background: #229954; transform: translateY(-3px); box-shadow: 0 10px 25px rgba(39,174,96,0.4); }

        .btn-logout { background: #e74c3c; color: white; }
        .btn-logout:hover { background: #c0392b; }

        /* Container */
        .container {
            max-width: 1280px;
            margin: 40px auto;
            padding: 0 24px;
        }

        /* Stats Grid */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 24px;
            margin-bottom: 36px;
        }

        .stat-card {
            background: white;
            border-radius: 18px;
            padding: 28px;
            text-align: center;
            box-shadow: var(--card-shadow);
            transition: all 0.4s ease;
        }

        .stat-card:hover {
            transform: translateY(-8px);
            box-shadow: var(--hover-shadow);
        }

        .stat-value {
            font-size: 42px;
            font-weight: 700;
            color: var(--primary);
            margin-bottom: 8px;
        }

        .stat-label {
            font-size: 15px;
            color: var(--gray);
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .stat-sub {
            font-size: 14px;
            color: var(--gray);
            margin-top: 4px;
        }

        /* Submissions Table Card */
        .table-card {
            background: white;
            border-radius: 20px;
            overflow: hidden;
            box-shadow: var(--card-shadow);
            margin-bottom: 32px;
        }

        .table-header {
            padding: 32px 36px 0;
        }

        .table-title {
            font-size: 24px;
            font-weight: 700;
            color: var(--dark);
            margin-bottom: 8px;
        }

        .table-subtitle {
            color: var(--gray);
            font-size: 16px;
        }

        .submissions-table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0 14px;
        }

        .submissions-table thead th {
            padding: 20px;
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            color: white;
            font-weight: 600;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 0.8px;
            text-align: left;
        }

        .submissions-table tbody tr {
            background: #f8fafc;
            transition: all 0.3s ease;
        }

        .submissions-table tbody tr:hover {
            background: #edf2f7;
            transform: translateY(-4px);
            box-shadow: 0 12px 30px rgba(0,0,0,0.1);
        }

        .submissions-table td {
            padding: 24px 20px;
            vertical-align: middle;
        }

        .student-name {
            font-weight: 600;
            font-size: 16px;
            color: var(--dark);
        }

        .status {
            padding: 8px 16px;
            border-radius: 30px;
            font-size: 13px;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }

        .status-submitted { background: #d4edda; color: #155724; }
        .status-missing { background: #f8d7da; color: #721c24; }

        .submitted-time {
            font-size: 14px;
            color: var(--gray);
        }

        .grade-display {
            font-size: 18px;
            font-weight: 700;
            color: var(--success);
        }

        .grade-form {
            display: flex;
            gap: 10px;
            align-items: center;
        }

        .grade-input {
            width: 80px;
            padding: 10px;
            border: 2px solid #e2e8f0;
            border-radius: 10px;
            font-size: 15px;
            text-align: center;
        }

        .grade-input:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 4px rgba(102,126,234,0.15);
        }

        .btn-grade {
            background: var(--success);
            color: white;
            padding: 10px 18px;
            font-size: 13px;
        }

        .btn-grade:hover {
            background: #218838;
        }

        .btn-download-file {
            background: #17a2b8;
            color: white;
            padding: 10px 18px;
            font-size: 13px;
        }

        .btn-download-file:hover {
            background: #138496;
        }

        .no-file {
            color: var(--gray);
            font-style: italic;
        }

        /* Empty State */
        .empty-state {
            text-align: center;
            padding: 100px 40px;
            color: var(--gray);
        }

        .empty-icon {
            font-size: 80px;
            margin-bottom: 24px;
            opacity: 0.4;
        }

        .empty-title {
            font-size: 26px;
            font-weight: 600;
            color: var(--dark);
            margin-bottom: 12px;
        }

        /* Responsive */
        @media (max-width: 992px) {
            .stats-grid { grid-template-columns: 1fr 1fr; }
        }

        @media (max-width: 768px) {
            .navbar { flex-direction: column; text-align: center; gap: 16px; padding: 20px; }
            .assignment-header { flex-direction: column; align-items: flex-start; }
            .submissions-table thead { display: none; }
            .submissions-table tbody tr { display: block; margin-bottom: 20px; padding: 20px; border-radius: 16px; }
            .submissions-table td { display: block; text-align: center; padding: 10px 0; border: none; }
            .submissions-table td:before {
                content: attr(data-label);
                font-weight: 600;
                color: var(--primary);
                display: block;
                margin-bottom: 8px;
                font-size: 14px;
            }
            .grade-form { justify-content: center; }
        }
    </style>
</head>
<body>

<!-- Navbar -->
<div class="navbar">
    <div class="assignment-header">
        <div>
            <div class="assignment-title"><%= assignment.getTitle() %></div>
            <div class="assignment-meta">Submissions Overview • <%= totalStudents %> students</div>
        </div>
    </div>
    <div class="nav-actions">
        <% if (assignment.getFilePath() != null && !assignment.getFilePath().isEmpty()) { %>
        <a href="<%= request.getContextPath() %>/download?file=<%= assignment.getFilePath() %>&type=teacher" class="btn btn-download">
            Download
        </a>

        <% } %>
        <a href="javascript:history.back()" class="btn btn-back">
            Back
        </a>
        <a href="<%= request.getContextPath() %>/logout" class="btn btn-logout">
            Logout
        </a>
    </div>
</div>

<div class="container">

    <!-- Stats -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-value"><%= totalStudents %></div>
            <div class="stat-label">Total Students</div>
        </div>
        <div class="stat-card">
            <div class="stat-value"><%= submittedCount %></div>
            <div class="stat-label">Submitted</div>
            <div class="stat-sub"><%= String.format("%.0f", totalStudents > 0 ? (submittedCount * 100.0 / totalStudents) : 0) %>% of class</div>
        </div>
        <div class="stat-card">
            <div class="stat-value"><%= gradedCount %></div>
            <div class="stat-label">Graded</div>
        </div>
        <div class="stat-card">
            <div class="stat-value">
                <% if (assignment.getDeadline() != null) { %>
                <%= assignment.getDeadline().format(dateFmt) %>
                <% if (now.isAfter(assignment.getDeadline())) { %>
                <span style="font-size: 14px; color: var(--danger); display: block;">(Overdue)</span>
                <% } %>
                <% } else { %>
                No deadline
                <% } %>
            </div>
            <div class="stat-label">Deadline</div>
        </div>
    </div>

    <!-- Submissions Table -->
    <div class="table-card">
        <div class="table-header">
            <div class="table-title">Student Submissions</div>
            <div class="table-subtitle">Review and grade student work</div>
        </div>

        <% if (groupStudents == null || groupStudents.isEmpty()) { %>
        <div class="empty-state">
            <div class="empty-icon">No students</div>
            <div class="empty-title">No students in this group</div>
        </div>
        <% } else { %>
        <table class="submissions-table">
            <thead>
            <tr>
                <th>Student</th>
                <th>Status</th>
                <th>Submitted At</th>
                <th>Grade</th>
                <th>File</th>
            </tr>
            </thead>
            <tbody>
            <% for (Student student : groupStudents) {
                Submission sub = submissionMap.get(student.getId());
                System.out.println(sub);
                boolean hasSubmitted = sub != null;
                boolean hasFile = hasSubmitted && sub.getFileUrl() != null && !sub.getFileUrl().isEmpty();
                boolean isGraded = hasSubmitted && sub.getScore() != null;
            %>
            <tr>
                <td class="student-name" data-label="Student">
                    <%= student.getFullName() %>
                </td>
                <td data-label="Status">
                            <span class="status <%= hasSubmitted ? "status-submitted" : "status-missing" %>">
                                <%= hasSubmitted ? "Submitted" : "Not Submitted" %>
                            </span>
                </td>
                <td class="submitted-time" data-label="Submitted">
                    <%= hasSubmitted ? sub.getSubmittedAt().format(dateFmt) : "—" %>
                </td>
                <td data-label="Grade">
                    <% if (isGraded) { %>
                    <div class="grade-display"><%= sub.getScore() %> / <%= assignment.getMaxScore() %></div>
                    <% } else if (hasSubmitted) { %>
                    <form action="<%= request.getContextPath() %>/teacher/grade-submission" method="post" class="grade-form">
                        <input type="hidden" name="submissionId" value="<%= sub.getId() %>">
                        <input type="hidden" name="assignmentId" value="<%= assignment.getId() %>">
                        <input type="number" name="grade" class="grade-input" min="0" max="<%= assignment.getMaxScore() %>"
                               placeholder="0" required>
                        <button type="submit" class="btn btn-grade">Grade</button>
                    </form>
                    <% } else { %>
                    <span style="color: #999;">—</span>
                    <% } %>
                </td>
                <td data-label="File">
                    <% if (hasFile) { %>
                    <a href="<%= request.getContextPath() %>/download?file=<%= sub.getFileUrl() %>&type=student" class="btn btn-download">
                        Download
                    </a>

                    <% } else if (hasSubmitted) { %>
                    <span class="no-file">Text only</span>
                    <% } else { %>
                    <span class="no-file">—</span>
                    <% } %>
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
