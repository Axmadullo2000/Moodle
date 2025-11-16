<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.university.moodle.model.Group" %>
<%@ page import="com.university.moodle.model.Student" %>
<%@ page import="com.university.moodle.model.Teacher" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Управление группой</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/manage-group.css">
    <style>
        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 8px;
            font-weight: 500;
        }
        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>
<%
    Group group = (Group) request.getAttribute("group");
    List<Student> allStudents = (List<Student>) request.getAttribute("students");
    List<Teacher> allTeachers = (List<Teacher>) request.getAttribute("teachers");

    // Сообщения об успехе/ошибке
    String successMessage = (String) session.getAttribute("successMessage");
    String errorMessage = (String) session.getAttribute("errorMessage");
    session.removeAttribute("successMessage");
    session.removeAttribute("errorMessage");
%>

<div class="navbar">
    <h1>👥 Управление группой: <%=group.getGroupName()%></h1>
    <div class="navbar-right">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="navbar-link">← Назад</a>
    </div>
</div>

<div class="container">
    <!-- Сообщения -->
    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    <div class="alert alert-success">
        ✓ <%= successMessage %>
    </div>
    <% } %>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="alert alert-error">
        ✗ <%= errorMessage %>
    </div>
    <% } %>

    <!-- Информация о группе -->
    <div class="group-info-card">
        <h2>📋 Информация о группе</h2>
        <div class="info-grid">
            <div class="info-item">
                <span class="label">Название:</span>
                <span class="value"><%=group.getGroupName()%></span>
            </div>
            <div class="info-item">
                <span class="label">ID группы:</span>
                <span class="value" style="font-size: 12px; color: #666;"><%=group.getId()%></span>
            </div>
            <% if (group.getDescription() != null && !group.getDescription().isEmpty()) { %>
            <div class="info-item">
                <span class="label">Описание:</span>
                <span class="value"><%=group.getDescription()%></span>
            </div>
            <% } %>
            <div class="info-item">
                <span class="label">Студентов:</span>
                <span class="value badge-primary">
                    <%=group.getStudentIDs() != null ? group.getStudentIDs().size() : 0%>
                </span>
            </div>
            <div class="info-item">
                <span class="label">Преподавателей:</span>
                <span class="value badge-success">
                    <%=group.getTeacherIDs() != null ? group.getTeacherIDs().size() : 0%>
                </span>
            </div>
        </div>
    </div>

    <!-- Преподаватели группы -->
    <div class="section">
        <div class="section-header">
            <h2>👨‍🏫 Преподаватели группы</h2>
        </div>

        <!-- Текущие преподаватели -->
        <% if (group.getTeacherIDs() != null && !group.getTeacherIDs().isEmpty()) { %>
        <div class="members-grid">
            <%
                for (String teacherId : group.getTeacherIDs()) {
                    for (Teacher teacher : allTeachers) {
                        if (teacher.getId().equals(teacherId)) {
            %>
            <div class="member-card teacher-card">
                <div class="member-avatar"><%=teacher.getFullName().charAt(0)%></div>
                <div class="member-info">
                    <div class="member-name"><%=teacher.getFullName()%></div>
                    <div class="member-email"><%=teacher.getEmail()%></div>
                    <div class="member-meta" style="font-size: 11px; color: #999;">ID: <%=teacher.getId()%></div>
                    <% if (teacher.getSpecialization() != null) { %>
                    <div class="member-meta"><%=teacher.getSpecialization()%></div>
                    <% } %>
                </div>
                <form method="post"
                      action="<%= request.getContextPath()%>/admin/manage-group"
                      onsubmit="return confirm('Удалить преподавателя <%= teacher.getFullName()%> из группы?');"
                      style="display: inline;">
                    <input type="hidden" name="action" value="remove-teacher">
                    <input type="hidden" name="groupId" value="<%=group.getId()%>">
                    <input type="hidden" name="teacherId" value="<%=teacher.getId()%>">
                    <button type="submit" class="btn-remove" title="Удалить">❌</button>
                </form>
            </div>
            <%
                            break;
                        }
                    }
                }
            %>
        </div>
        <% } else { %>
        <div class="empty-state">
            <div class="empty-icon">👨‍🏫</div>
            <p>В группе пока нет преподавателей</p>
        </div>
        <% } %>

        <!-- Добавить преподавателя -->
        <div class="add-member-form">
            <h3>➕ Добавить преподавателя</h3>
            <form method="post"
                  action="${pageContext.request.contextPath}/admin/manage-group"
                  class="inline-form">
                <input type="hidden" name="action" value="add-teacher">
                <input type="hidden" name="groupId" value="<%=group.getId()%>">
                <select name="teacherId" class="form-select" required>
                    <option value="">Выберите преподавателя...</option>
                    <%
                        for (Teacher teacher : allTeachers) {
                            boolean isInGroup = group.getTeacherIDs() != null &&
                                    group.getTeacherIDs().contains(teacher.getId());
                            if (!isInGroup) {
                    %>
                    <option value="<%=teacher.getId()%>">
                        <%=teacher.getFullName()%>
                        <% if (teacher.getSpecialization() != null) { %>
                        (<%=teacher.getSpecialization()%>)
                        <% } %>
                    </option>
                    <%
                            }
                        }
                    %>
                </select>
                <button type="submit" class="btn btn-primary">Добавить</button>
            </form>
        </div>
    </div>

    <!-- Студенты группы -->
    <div class="section">
        <div class="section-header">
            <h2>👨‍🎓 Студенты группы</h2>
        </div>

        <!-- Текущие студенты -->
        <% if (group.getStudentIDs() != null && !group.getStudentIDs().isEmpty()) { %>
        <div class="members-grid">
            <%
                for (String studentId : group.getStudentIDs()) {
                    for (Student student : allStudents) {
                        if (student.getId().equals(studentId)) {
            %>
            <div class="member-card student-card">
                <div class="member-avatar"><%=student.getFullName().charAt(0)%></div>
                <div class="member-info">
                    <div class="member-name"><%=student.getFullName()%></div>
                    <div class="member-email"><%=student.getEmail()%></div>
                    <div class="member-meta">
                        Работ: <%=student.getSubmissionID() != null ? student.getSubmissionID().size() : 0%>
                    </div>
                </div>
                <form method="post"
                      action="${pageContext.request.contextPath}/admin/manage-group"
                      onsubmit="return confirm('Удалить студента <%=student.getFullName()%> из группы?');"
                      style="display: inline;">
                    <input type="hidden" name="action" value="remove-student">
                    <input type="hidden" name="groupId" value="<%=group.getId()%>">
                    <input type="hidden" name="studentId" value="<%=student.getId()%>">
                    <button type="submit" class="btn-remove" title="Удалить">❌</button>
                </form>
            </div>
            <%
                            break;
                        }
                    }
                }
            %>
        </div>
        <% } else { %>
        <div class="empty-state">
            <div class="empty-icon">👨‍🎓</div>
            <p>В группе пока нет студентов</p>
        </div>
        <% } %>

        <!-- Добавить студента -->
        <div class="add-member-form">
            <h3>➕ Добавить студента</h3>
            <form method="post"
                  action="${pageContext.request.contextPath}/admin/manage-group"
                  class="inline-form">
                <input type="hidden" name="action" value="add-student">
                <input type="hidden" name="groupId" value="<%=group.getId()%>">
                <select name="studentId" class="form-select" required>
                    <option value="">Выберите студента...</option>
                    <%
                        for (Student student : allStudents) {
                            boolean isInGroup = group.getStudentIDs() != null &&
                                    group.getStudentIDs().contains(student.getId());
                            if (!isInGroup) {
                                boolean inOtherGroup = student.getGroupId() != null &&
                                        !student.getGroupId().isEmpty();
                    %>
                    <option value="<%=student.getId()%>">
                        <%=student.getFullName()%>
                        <% if (inOtherGroup) { %> (⚠️ В другой группе)<% } %>
                    </option>
                    <%
                            }
                        }
                    %>
                </select>
                <button type="submit" class="btn btn-primary">Добавить</button>
            </form>
        </div>
    </div>
</div>

<script>
    // Отладка: показываем все hidden поля при отправке формы
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', function(e) {
            console.log('=== FORM SUBMIT ===');
            const formData = new FormData(this);
            for (let [key, value] of formData.entries()) {
                console.log(key + ': "' + value + '"');
            }
            console.log('===================');
        });
    });
</script>
</body>
</html>
