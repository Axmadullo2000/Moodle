<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, com.university.moodle.model.*" %>

<html>
<head>
  <title>Teacher Dashboard</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/teacher-assignments.css">
</head>
<body>

  <div class="navbar">
    <h1>Teacher Dashboard</h1>
    <a class="navbar-link" href="${pageContext.request.contextPath}/logout">Logout</a>
  </div>

  <div class="container">
    <div class="section">
      <div class="section-header">
        <h2>Your Groups</h2>
        <!-- КНОПКА СОЗДАНИЯ УБРАНА — только админ создаёт -->
      </div>

      <%
        List<Group> groups = (List<Group>) request.getAttribute("groups");
        if (groups == null || groups.isEmpty()) {
      %>
      <div class="empty-state">
        <div class="empty-state-icon">No groups</div>
        <p>You are not assigned to any group yet.</p>
        <p>Contact your administrator.</p>
      </div>
      <%
      } else {
      %>
      <table class="table">
        <thead>
        <tr>
          <th>Group Name</th>
          <th>Description</th>
          <th>Students</th>
          <th>Assignments</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <%
          for (Group g : groups) {
        %>
        <tr>
          <td><%= g.getGroupName() %></td>
          <td><%= g.getDescription() != null ? g.getDescription() : "-" %></td>
          <td><%= g.getStudentIDs() != null ? g.getStudentIDs().size() : 0 %></td>
          <td><%= g.getAssignmentIDs() != null ? g.getAssignmentIDs().size() : 0 %></td>
          <td>
            <a class="btn btn-primary"
               href="<%= request.getContextPath()%>/teacher/group?id=<%= g.getId() %>&groupName=<%= g.getGroupName()%>">Open</a>
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