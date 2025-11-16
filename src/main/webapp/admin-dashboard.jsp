<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.university.moodle.model.Teacher" %>
<%@ page import="com.university.moodle.model.Student" %>
<!DOCTYPE html>
<html lang="ru">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Панель администратора</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/admin_panel.css">
</head>
<body>
<div class="navbar">
  <h1>🎓 Панель администратора</h1>
  <div class="navbar-right">
    <span>👤 ${currentUser.fullName}</span>
    <a href="${pageContext.request.contextPath}/profile" class="navbar-link">Профиль</a>
    <a href="${pageContext.request.contextPath}/logout" class="navbar-link">Выйти</a>
  </div>
</div>

<div class="container">
  <% if (request.getParameter("success") != null) { %>
  <div class="success-message">
    <% if ("teacher_created".equals(request.getParameter("success"))) { %>
    ✅ Преподаватель успешно создан!
    <% } %>
  </div>
  <% } %>

  <!-- Статистика -->
  <div class="stats">
    <div class="stat-card">
      <h3>Преподавателей</h3>
      <div class="number">${teacherCount}</div>
    </div>
    <div class="stat-card">
      <h3>Студентов</h3>
      <div class="number">${studentCount}</div>
    </div>
    <div class="stat-card">
      <h3>Всего пользователей</h3>
      <div class="number">${teacherCount + studentCount + 1}</div>
    </div>
  </div>

  <!-- Преподаватели -->
  <div class="section">
    <div class="section-header">
      <h2>👨‍🏫 Преподаватели</h2>
      <a href="${pageContext.request.contextPath}/admin/create-teacher" class="btn btn-primary">
        + Добавить преподавателя
      </a>
    </div>

    <%
      List<Teacher> teachers = (List<Teacher>) request.getAttribute("teachers");
      if (teachers != null && !teachers.isEmpty()) {
    %>
    <table class="table">
      <thead>
      <tr>
        <th>ID</th>
        <th>ФИО</th>
        <th>Email</th>
        <th>Специализация</th>
        <th>Групп</th>
        <th>Заданий</th>
      </tr>
      </thead>
      <tbody>
      <% for (Teacher teacher : teachers) { %>
      <tr>
        <td><span class="badge badge-teacher"><%=teacher.getId().substring(0, 8)%></span></td>
        <td><strong><%=teacher.getFullName()%></strong></td>
        <td><%=teacher.getEmail()%></td>
        <td><%=teacher.getSpecialization() != null ? teacher.getSpecialization() : "Не указана"%></td>
        <td><%=teacher.getGroupID() != null ? teacher.getGroupID().size() : 0%></td>
        <td><%=teacher.getAssignmentID() != null ? teacher.getAssignmentID().size() :  0%></td>
      </tr>
      <% } %>
      </tbody>
    </table>
    <% } else { %>
    <div class="empty-state">
      <div class="empty-state-icon">📚</div>
      <p>Преподавателей пока нет</p>
      <p style="margin-top: 10px;">
        <a href="${pageContext.request.contextPath}/admin/create-teacher" class="btn btn-primary">
          Добавить первого преподавателя
        </a>
      </p>
    </div>
    <% } %>
  </div>

  <!-- Группы -->
  <div class="section">
    <div class="section-header">
      <h2>👥 Группы</h2>
      <a href="${pageContext.request.contextPath}/admin/create-group" class="btn btn-success">
        + Создать группу
      </a>
    </div>

    <%
      List<com.university.moodle.model.Group> groups =
              (List<com.university.moodle.model.Group>) request.getAttribute("groups");
      if (groups != null && !groups.isEmpty()) {
    %>
    <table class="table">
      <thead>
      <tr>
        <th>Название</th>
        <th>Описание</th>
        <th>Преподавателей</th>
        <th>Студентов</th>
        <th>Заданий</th>
        <th>Действия</th>
      </tr>
      </thead>
      <tbody>
      <% for (com.university.moodle.model.Group group : groups) { %>
      <tr>
        <td><strong><%=group.getGroupName()%></strong></td>
        <td><%=group.getDescription() != null ? group.getDescription() : "Нет описания"%></td>
        <td><span class="badge badge-teacher"><%=group.getTeacherIDs() != null ? group.getTeacherIDs().size() : 0%></span></td>
        <td><span class="badge badge-student"><%=group.getStudentIDs() != null ? group.getStudentIDs().size() : 0%></span></td>
        <td><%=group.getAssignmentIDs() != null ? group.getAssignmentIDs().size() : 0%></td>
        <td>
          <a href="<%= request.getContextPath()%>/admin/manage-group?id=<%=group.getId()%>"
             class="btn-action">
            ⚙️ Управление
          </a>
        </td>
      </tr>
      <% } %>
      </tbody>
    </table>
    <% } else { %>
    <div class="empty-state">
      <div class="empty-state-icon">👥</div>
      <p>Групп пока нет</p>
      <p style="margin-top: 10px;">
        <a href="${pageContext.request.contextPath}/admin/create-group" class="btn btn-success">
          Создать первую группу
        </a>
      </p>
    </div>
    <% } %>
  </div>

  <!-- Студенты -->
  <div class="section">
    <div class="section-header">
      <h2>👨‍🎓 Студенты</h2>
    </div>

    <%
      List<Student> students = (List<Student>) request.getAttribute("students");
      if (students != null && !students.isEmpty()) {
    %>
    <table class="table">
      <thead>
      <tr>
        <th>ID</th>
        <th>ФИО</th>
        <th>Email</th>
        <th>Группа</th>
        <th>Работ</th>
      </tr>
      </thead>
      <tbody>
      <% for (Student student : students) { %>
      <tr>
        <td><span class="badge badge-student"><%=student.getId().substring(0, 8)%></span></td>
        <td><strong><%=student.getFullName()%></strong></td>
        <td><%=student.getEmail()%></td>
        <td><%=student.getGroupId() != null ? student.getGroupId() : "Не назначена"%></td>
        <td><%=student.getSubmissionID() != null ? student.getSubmissionID().size() : 0%></td>
      </tr>
      <% } %>
      </tbody>
    </table>
    <% } else { %>
    <div class="empty-state">
      <div class="empty-state-icon">🎓</div>
      <p>Студентов пока нет</p>
    </div>
    <% } %>
  </div>
</div>
</body>
</html>