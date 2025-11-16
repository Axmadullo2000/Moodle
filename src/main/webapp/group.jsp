<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, com.university.moodle.model.*" %>
<%@ page import="java.text.SimpleDateFormat" %>

<!DOCTYPE html>
<html>
<head>
    <title>Group: <%= request.getAttribute("groupName") %></title>
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
        }
        .btn:hover { background: #219653; }
        .btn-back { background: #3498db; }
        .btn-back:hover { background: #2980b9; }
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
        .upload-box {
            border: 2px dashed #ccc;
            padding: 20px;
            text-align: center;
            border-radius: 10px;
            background: #f9f9f9;
        }
        .upload-box:hover { border-color: #27ae60; }
        input[type="text"], input[type="datetime-local"], textarea {
            width: 100%;
            padding: 10px;
            margin: 8px 0;
            border: 1px solid #ddd;
            border-radius: 6px;
        }
        input[type="file"] {
            margin: 10px 0;
        }
        .file-name { color: #27ae60; font-weight: bold; }
    </style>
</head>
<body>

<% if (request.getAttribute("successMessage") != null) { %>
<div style="background: #d4edda; color: #155724; padding: 12px; border-radius: 6px; margin-bottom: 20px; border: 1px solid #c3e6cb;">
    <%= request.getAttribute("successMessage") %>
</div>
<% } %>

<% if (request.getAttribute("errorMessage") != null) { %>
<div style="background: #f8d7da; color: #721c24; padding: 12px; border-radius: 6px; margin-bottom: 20px; border: 1px solid #f5c6cb;">
    <%= request.getAttribute("errorMessage") %>
</div>
<% } %>

<div class="navbar">
    <h2>Group: <%= request.getAttribute("groupName") %></h2>
    <div>
        <a href="<%= request.getContextPath() %>/teacher/dashboard" class="btn btn-back">Back</a>
        <a href="<%= request.getContextPath() %>/logout" style="margin-left: 10px; color:#eee;">Logout</a>
    </div>
</div>

<div class="container">

    <!-- Форма загрузки задания -->
    <div class="section">
        <h3>Upload New Assignment</h3>
        <form
                action="<%= request.getContextPath() %>/teacher/upload-assignment"
                method="post" enctype="multipart/form-data" class="upload-box">
            <input type="hidden" name="groupId" value="<%= request.getAttribute("groupId") %>">

            <input type="text" name="title" placeholder="Assignment Title" required><br>

            <textarea name="description" rows="3" placeholder="Description (optional)"></textarea><br>

            <input type="datetime-local" name="deadline" required><br>

            <input type="file" name="file" required accept=".pdf,.docx,.zip,.txt"><br>
            <div id="fileName" class="file-name"></div>

            <button type="submit" class="btn">Upload Assignment</button>
        </form>
    </div>

    <!-- Список заданий -->
    <div class="section">
        <h3>Assignments (<%= ((List<?>)request.getAttribute("assignments")).size() %>)</h3>

        <%
            List<Assignment> assignments = (List<Assignment>) request.getAttribute("assignments");
            if (assignments == null || assignments.isEmpty()) {
        %>
        <div class="empty">No assignments yet.</div>
        <%
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm");
            for (Assignment a : assignments) {
        %>
        <div style="padding: 12px 0; border-bottom: 1px solid #eee;">
            <strong><%= a.getTitle() %></strong><br>
            <small>
                Deadline: <%= a.getDeadline() %> |
                <a href="<%= request.getContextPath() %>/download?file=<%= a.getFilePath() %>"
                   style="color:#3498db;">📎 Download File</a>
            </small>
            <% if (a.getDescription() != null && !a.getDescription().isEmpty()) { %>
            <div style="margin-top:5px; font-size:0.9em; color:#555;"><%= a.getDescription() %></div>
            <% } %>
            <div style="margin-top: 8px;">
                <a href="<%= request.getContextPath() %>/teacher/submissions?assignmentId=<%= a.getId() %>" class="btn" style="font-size:0.9em; padding:6px 12px;">
                    View Submissions
                </a>
            </div>
        </div>
        <%
                }
            }
        %>
    </div>

    <!-- Список студентов -->
    <div class="section">
        <h3>Students (<%= request.getAttribute("studentsCount") %>)</h3>

        <%
            List<Student> students = (List<Student>) request.getAttribute("students");
            if (students == null || students.isEmpty()) {
        %>
        <div class="empty">No students in this group.</div>
        <%
        } else {
        %>
        <table>
            <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Action</th>
            </tr>
            <%
                for (Student s : students) {
            %>
            <tr>
                <td><%= s.getFullName() %></td>
                <td><%= s.getEmail() %></td>
                <td>
                    <a href="<%= request.getContextPath() %>"
                       class="btn" style="font-size:0.85em; padding:5px 10px;">View</a>
                </td>
            </tr>
            <%
                }
            %>
        </table>
        <%
            }
        %>
    </div>

</div>

</body>
</html>
