<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, com.university.moodle.model.*" %>

<html>
<head>
    <title>Group</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/teacher-assignments.css">
</head>
<body>

<div class="navbar">
    <h1>Group Information</h1>
    <a class="navbar-link" href="${pageContext.request.contextPath}/teacher/dashboard">Back</a>
</div>

<div class="container">

    <div class="group-info-card">
        <h2>Group Info</h2>

        <div class="info-grid">
            <div class="info-item">
                <span class="label">Group Name:</span>
                <span class="value"><%= request.getAttribute("groupName") %></span>
            </div>

            <div class="info-item">
                <span class="label">Students:</span>
                <span class="value badge badge-student">
                    <%= request.getAttribute("studentsCount") %>
                </span>
            </div>

            <div class="info-item">
                <span class="label">Teachers:</span>
                <span class="value badge badge-teacher">
                    <%= request.getAttribute("teachersCount") %>
                </span>
            </div>
        </div>
    </div>

    <!-- STUDENTS -->
    <div class="section">
        <div class="section-header"><h2>Students</h2></div>

        <%
            List<Student> students = (List<Student>) request.getAttribute("students");

            if (students == null || students.isEmpty()) {
        %>
        <div class="empty-state">
            <div class="empty-state-icon">🎓</div>
            <p>No students in this group</p>
        </div>
        <%
        } else {
        %>

        <div class="members-grid">
            <%
                for (Student s : students) {
            %>
            <div class="member-card student-card">
                <div class="member-avatar"><%= s.getFullName().charAt(0) %></div>
                <div class="member-info">
                    <div class="member-name"><%= s.getFullName() %></div>
                    <div class="member-email"><%= s.getEmail() %></div>
                </div>
            </div>
            <% } %>
        </div>

        <% } %>

    </div>

    <!-- ASSIGNMENTS -->
    <div class="section">
        <div class="section-header">
            <h2>Assignments</h2>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/teacher/create-assignment.jsp?groupId=<%= request.getAttribute("groupId") %>">
                + Add Assignment
            </a>
        </div>

        <%
            List<Assignment> assignments = (List<Assignment>) request.getAttribute("assignments");

            if (assignments == null || assignments.isEmpty()) {
        %>
        <div class="empty-state">
            <div class="empty-state-icon">📄</div>
            <p>No assignments yet</p>
        </div>

        <% } else { %>

        <table class="table">
            <thead>
            <tr>
                <th>Title</th>
                <th>Deadline</th>
                <th></th>
            </tr>
            </thead>
            <tbody>

            <%
                for (Assignment a : assignments) {
            %>
            <tr>
                <td><%= a.getTitle() %></td>
                <td><%= a.getDeadline() %></td>
                <td>
                    <a class="btn btn-primary"
                       href="${pageContext.request.contextPath}/teacher/submissions?assignmentId=<%= a.getId() %>">
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
