<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, com.university.moodle.model.Assignment" %>

<html>
<head>
  <title>Assignments</title>
  <link rel="stylesheet" href="teacher-assignments.css">
</head>
<body>

  <div class="navbar">
    <h1>Teacher Dashboard</h1>
    <a class="navbar-link" href="${pageContext.request.contextPath}/">Home</a>
  </div>

  <div class="container">

    <div class="section">
      <div class="section-header">
        <h2>Your Assignments</h2>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/teacher/create-assignment.jsp">+ Create</a>
      </div>

      <%
        List<Assignment> assignments = (List<Assignment>) request.getAttribute("assignments");

        if (assignments == null || assignments.isEmpty()) {
      %>
      <div class="empty-state">
        <div class="empty-state-icon">📄</div>
        <p>No assignments found</p>
      </div>
      <%
      } else {
      %>

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
               href="${pageContext.request.contextPath}/teacher/submissions?assignmentId=<%= a.getId() %>">View</a>
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
