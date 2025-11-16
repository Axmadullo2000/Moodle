<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, com.university.moodle.model.*" %>
<%@ page import="java.text.SimpleDateFormat" %>

<!DOCTYPE html>
<html>
<head>
    <title>Submissions - <%= ((Assignment)request.getAttribute("assignment")).getTitle() %></title>
    <meta charset="UTF-8">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            color: #333;
            padding: 20px;
        }
        .navbar {
            background: #2c3e50;
            color: white;
            padding: 15px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-radius: 8px;
            margin-bottom: 20px;
        }
        .navbar a {
            color: #fff;
            text-decoration: none;
            font-weight: bold;
        }
        .container { max-width: 1100px; margin: 0 auto; }
        .section {
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        .section h3 {
            margin-bottom: 15px;
            color: #2c3e50;
            border-bottom: 2px solid #eee;
            padding-bottom: 8px;
        }
        .btn {
            display: inline-block;
            padding: 8px 16px;
            background: #27ae60;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-size: 14px;
            margin: 5px 0;
            border: none;
            cursor: pointer;
        }
        .btn:hover { background: #219653; }
        .btn-back { background: #3498db; }
        .btn-back:hover { background: #2980b9; }
        .btn-download { background: #9b59b6; font-size: 0.85em; padding: 5px 10px; }
        .btn-download:hover { background: #8e44ad; }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background: #f8f9fa;
            font-weight: bold;
        }
        .empty {
            text-align: center;
            padding: 30px;
            color: #777;
            font-style: italic;
        }
        .stats {
            display: flex;
            gap: 20px;
            margin-bottom: 15px;
        }
        .stat-box {
            flex: 1;
            background: #ecf0f1;
            padding: 15px;
            border-radius: 8px;
            text-align: center;
        }
        .stat-box h4 {
            color: #7f8c8d;
            font-size: 0.9em;
            margin-bottom: 5px;
        }
        .stat-box p {
            font-size: 1.8em;
            font-weight: bold;
            color: #2c3e50;
        }
        .status-submitted { color: #27ae60; font-weight: bold; }
        .status-not-submitted { color: #e74c3c; font-weight: bold; }
        .grade-form {
            display: inline-flex;
            gap: 5px;
            align-items: center;
        }
        .grade-form input {
            width: 60px;
            padding: 5px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .grade-form button {
            padding: 5px 10px;
            font-size: 0.85em;
        }
    </style>
</head>
<body>

<%
    Assignment assignment = (Assignment) request.getAttribute("assignment");
    List<Submission> submissions = (List<Submission>) request.getAttribute("submissions");
    List<Student> groupStudents = (List<Student>) request.getAttribute("groupStudents");
    int totalStudents = (int) request.getAttribute("totalStudents");
    int submittedCount = (int) request.getAttribute("submittedCount");
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm");

    // Создаём Map для быстрого поиска submissions по studentId
    Map<String, Submission> submissionMap = new HashMap<>();
    for (Submission sub : submissions) {
        submissionMap.put(sub.getStudentId(), sub);
    }
%>

<div class="navbar">
    <h2>Submissions: <%= assignment.getTitle() %></h2>
    <div>
        <a href="javascript:history.back()" class="btn btn-back">Back</a>
        <a href="<%= request.getContextPath() %>/logout" style="margin-left:10px; color:#eee;">Logout</a>
    </div>
</div>

<div class="container">
    <!-- Статистика -->
    <div class="section">
        <div class="stats">
            <div class="stat-box">
                <h4>Total Students</h4>
                <p><%= totalStudents %></p>
            </div>
            <div class="stat-box">
                <h4>Submitted</h4>
                <p style="color: #27ae60;"><%= submittedCount %></p>
            </div>
            <div class="stat-box">
                <h4>Not Submitted</h4>
                <p style="color: #e74c3c;"><%= totalStudents - submittedCount %></p>
            </div>
            <div class="stat-box">
                <h4>Deadline</h4>
                <p style="font-size: 1.2em;"><%= assignment.getDeadline() %></p>
            </div>
        </div>
    </div>

    <!-- Таблица submissions -->
    <div class="section">
        <h3>Student Submissions</h3>

        <% if (groupStudents.isEmpty()) { %>
        <div class="empty">No students in this group.</div>
        <% } else { %>
        <table>
            <tr>
                <th>Student Name</th>
                <th>Status</th>
                <th>Submitted At</th>
                <th>Grade</th>
                <th>Action</th>
            </tr>
            <%
                for (Student student : groupStudents) {
                    Submission sub = submissionMap.get(student.getId());
                    boolean hasSubmission = sub != null;
            %>
            <tr>
                <td><%= student.getFullName() %></td>
                <td>
                    <% if (hasSubmission) { %>
                    <span class="status-submitted">✓ Submitted</span>
                    <% } else { %>
                    <span class="status-not-submitted">✗ Not Submitted</span>
                    <% } %>
                </td>
                <td>
                    <% if (hasSubmission) { %>
                    <%= sub.getSubmittedAt() %>
                    <% } else { %>
                    <span style="color: #999;">—</span>
                    <% } %>
                </td>
                <td>
                    <% if (hasSubmission) { %>
                    <% if (sub.getScore() != null) { %>
                    <strong><%= sub.getScore() %> / <%= assignment.getMaxScore() %></strong>
                    <% } else { %>
                    <form action="<%= request.getContextPath() %>/teacher/grade-submission" method="post" class="grade-form">
                        <input type="hidden" name="submissionId" value="<%= sub.getId() %>">
                        <input type="hidden" name="assignmentId" value="<%= assignment.getId() %>">
                        <input type="number" name="grade" min="0" max="<%= assignment.getMaxScore() %>" placeholder="0-<%= assignment.getMaxScore() %>" required>
                        <button type="submit" class="btn">Grade</button>
                    </form>
                    <% } %>
                    <% } else { %>
                    <span style="color: #999;">—</span>
                    <% } %>
                </td>
                <td>
                    <% if (hasSubmission) { %>
                    <a href="<%= request.getContextPath() %>/download?file=<%= sub.getFileUrl() %>" class="btn btn-download">
                        Download
                    </a>
                    <% } else { %>
                    <span style="color: #999;">—</span>
                    <% } %>
                </td>
            </tr>
            <%
                }
            %>
        </table>
        <% } %>
    </div>

</div>

</body>
</html>