<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.university.moodle.model.Group" %>
<%@ page import="com.university.moodle.model.Student" %>
<%@ page import="com.university.moodle.model.Teacher" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Group Management • University LMS</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <style>
        :root {
            --primary: #7c3aed;
            --primary-light: #a78bfa;
            --primary-dark: #6d28d9;
            --secondary: #06b6d4;
            --accent: #ec4899;
            --success: #10b981;
            --danger: #ef4444;
            --warning: #f59e0b;
            --dark-bg: #0f172a;
            --dark-card: #1e293b;
            --dark-hover: #334155;
            --gray-50: #f8fafc;
            --gray-100: #f1f5f9;
            --gray-200: #e2e8f0;
            --gray-300: #cbd5e1;
            --gray-500: #64748b;
            --gray-600: #475569;
            --gray-700: #334155;
            --gray-900: #0f172a;
            --radius: 16px;
            --shadow-sm: 0 1px 2px 0 rgba(0,0,0,0.3);
            --shadow-md: 0 4px 12px 0 rgba(0,0,0,0.4);
            --shadow-lg: 0 10px 25px 0 rgba(0,0,0,0.5);
            --shadow-xl: 0 15px 35px 0 rgba(0,0,0,0.6);
            --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: linear-gradient(135deg, #0f172a 0%, #1a1f3a 50%, #0f1626 100%);
            color: #cbd5e1;
            line-height: 1.6;
            min-height: 100vh;
        }

        /* Header */
        .header {
            background: linear-gradient(135deg, rgba(15, 23, 42, 0.95), rgba(30, 41, 59, 0.95));
            border-bottom: 1px solid rgba(124, 58, 237, 0.2);
            padding: 1.5rem 0;
            box-shadow: 0 0 50px rgba(124, 58, 237, 0.15);
            position: sticky;
            top: 0;
            z-index: 10;
        }

        .header-container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 0 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 1rem;
        }

        .header-title {
            font-size: 1.75rem;
            font-weight: 700;
            color: #f1f5f9;
            display: flex;
            align-items: center;
            gap: 0.75rem;
            background: linear-gradient(135deg, #7c3aed, #06b6d4);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            filter: drop-shadow(0 0 20px rgba(124, 58, 237, 0.3));
        }

        .back-link {
            color: #cbd5e1;
            text-decoration: none;
            font-weight: 600;
            font-size: 0.95rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            padding: 0.75rem 1.5rem;
            border-radius: 10px;
            background: rgba(124, 58, 237, 0.1);
            backdrop-filter: blur(10px);
            transition: var(--transition);
            border: 1px solid rgba(124, 58, 237, 0.3);
        }

        .back-link:hover {
            background: rgba(124, 58, 237, 0.2);
            transform: translateX(-4px);
            border-color: rgba(124, 58, 237, 0.5);
            color: #e9d5ff;
        }

        /* Layout */
        .container {
            max-width: 1400px;
            margin: 2.5rem auto;
            padding: 0 2rem;
        }

        /* Alerts */
        .alert {
            padding: 1.25rem 1.75rem;
            border-radius: var(--radius);
            margin-bottom: 1.5rem;
            font-weight: 500;
            display: flex;
            align-items: center;
            gap: 0.75rem;
            box-shadow: 0 0 30px rgba(0,0,0,0.5);
            backdrop-filter: blur(10px);
            border: 1px solid;
        }

        .alert-success {
            background: linear-gradient(135deg, rgba(16, 185, 129, 0.15), rgba(16, 185, 129, 0.05));
            color: #6ee7b7;
            border-color: rgba(16, 185, 129, 0.3);
        }

        .alert-error {
            background: linear-gradient(135deg, rgba(239, 68, 68, 0.15), rgba(239, 68, 68, 0.05));
            color: #fca5a5;
            border-color: rgba(239, 68, 68, 0.3);
        }

        /* Cards */
        .card {
            background: linear-gradient(135deg, rgba(30, 41, 59, 0.8), rgba(30, 41, 59, 0.5));
            border-radius: var(--radius);
            padding: 2rem;
            box-shadow: 0 0 40px rgba(124, 58, 237, 0.1);
            margin-bottom: 2rem;
            border: 1px solid rgba(124, 58, 237, 0.2);
            transition: var(--transition);
        }

        .card:hover {
            box-shadow: 0 0 60px rgba(124, 58, 237, 0.2);
            border-color: rgba(124, 58, 237, 0.4);
            background: linear-gradient(135deg, rgba(30, 41, 59, 0.9), rgba(30, 41, 59, 0.6));
        }

        .card-header {
            margin-bottom: 1.75rem;
            padding-bottom: 1.25rem;
            border-bottom: 1px solid rgba(124, 58, 237, 0.2);
        }

        .card-title {
            font-size: 1.375rem;
            font-weight: 700;
            background: linear-gradient(135deg, #7c3aed, #06b6d4);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        /* Stats Grid */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 1.5rem;
            margin-bottom: 1.5rem;
        }

        .stat-card {
            background: rgba(30, 41, 59, 0.6);
            padding: 1.5rem;
            border-radius: var(--radius);
            border: 1px solid rgba(124, 58, 237, 0.1);
            transition: var(--transition);
        }

        .stat-card:hover {
            border-color: rgba(124, 58, 237, 0.4);
            transform: translateY(-4px);
            box-shadow: 0 0 40px rgba(124, 58, 237, 0.15);
            background: rgba(30, 41, 59, 0.8);
        }

        .stat-label {
            font-size: 0.875rem;
            color: #94a3b8;
            margin-bottom: 0.75rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .stat-value {
            font-size: 1.75rem;
            font-weight: 700;
            background: linear-gradient(135deg, #7c3aed, #06b6d4);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        .badge {
            padding: 0.5rem 1rem;
            border-radius: 9999px;
            font-size: 0.875rem;
            font-weight: 700;
            display: inline-block;
            box-shadow: 0 2px 10px rgba(0,0,0,0.3);
        }

        .badge-primary {
            background: linear-gradient(135deg, rgba(124, 58, 237, 0.2), rgba(124, 58, 237, 0.1));
            color: #c4b5fd;
            border: 1px solid rgba(124, 58, 237, 0.3);
        }

        .badge-success {
            background: linear-gradient(135deg, rgba(16, 185, 129, 0.2), rgba(16, 185, 129, 0.1));
            color: #6ee7b7;
            border: 1px solid rgba(16, 185, 129, 0.3);
        }

        /* Members Grid */
        .members-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }

        .member-card {
            background: rgba(30, 41, 59, 0.6);
            border: 1px solid rgba(124, 58, 237, 0.1);
            border-radius: var(--radius);
            padding: 1.5rem;
            display: flex;
            align-items: center;
            gap: 1.25rem;
            transition: var(--transition);
            position: relative;
            overflow: hidden;
        }

        .member-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 2px;
            background: linear-gradient(90deg, #7c3aed, #06b6d4);
            opacity: 0;
            transition: var(--transition);
        }

        .member-card:hover {
            border-color: rgba(124, 58, 237, 0.3);
            box-shadow: 0 0 40px rgba(124, 58, 237, 0.2);
            transform: translateY(-4px);
            background: rgba(30, 41, 59, 0.8);
        }

        .member-card:hover::before {
            opacity: 1;
        }

        .teacher-card { border-left: 4px solid #7c3aed; }
        .student-card { border-left: 4px solid #10b981; }

        .avatar {
            width: 64px;
            height: 64px;
            border-radius: 12px;
            background: linear-gradient(135deg, #7c3aed, #06b6d4);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            font-size: 1.5rem;
            flex-shrink: 0;
            box-shadow: 0 0 30px rgba(124, 58, 237, 0.3);
        }

        .member-info h4 {
            font-weight: 700;
            color: #f1f5f9;
            margin-bottom: 0.35rem;
            font-size: 1.05rem;
        }

        .member-email {
            color: #a78bfa;
            font-size: 0.875rem;
            margin-bottom: 0.5rem;
            font-weight: 500;
        }

        .member-meta {
            color: #94a3b8;
            font-size: 0.813rem;
            font-weight: 500;
        }

        /* Remove Button */
        .remove-btn {
            position: absolute;
            top: 1rem;
            right: 1rem;
            background: linear-gradient(135deg, #ef4444, #dc2626);
            color: white;
            border: none;
            width: 40px;
            height: 40px;
            border-radius: 10px;
            cursor: pointer;
            font-size: 1rem;
            opacity: 0;
            transition: var(--transition);
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 0 20px rgba(239, 68, 68, 0.3);
        }

        .member-card:hover .remove-btn {
            opacity: 1;
        }

        .remove-btn:hover {
            transform: scale(1.15) rotate(5deg);
            box-shadow: 0 0 30px rgba(239, 68, 68, 0.5);
        }

        /* Empty State */
        .empty-state {
            text-align: center;
            padding: 4rem 2rem;
            color: #94a3b8;
        }

        .empty-icon {
            font-size: 4rem;
            margin-bottom: 1rem;
            background: linear-gradient(135deg, #7c3aed, #06b6d4);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            opacity: 0.7;
        }

        /* Add Section */
        .add-section {
            background: rgba(30, 41, 59, 0.5);
            border: 2px dashed rgba(124, 58, 237, 0.4);
            border-radius: var(--radius);
            padding: 2.5rem;
            text-align: center;
            transition: var(--transition);
        }

        .add-section:hover {
            border-color: rgba(124, 58, 237, 0.7);
            background: rgba(30, 41, 59, 0.7);
            box-shadow: 0 0 40px rgba(124, 58, 237, 0.1);
        }

        .add-section h3 {
            margin-bottom: 1.5rem;
            color: #e2e8f0;
            font-weight: 700;
            font-size: 1.125rem;
        }

        .add-form {
            display: flex;
            gap: 1rem;
            max-width: 500px;
            margin: 0 auto;
            flex-wrap: wrap;
        }

        select, .btn {
            padding: 0.875rem 1.25rem;
            border-radius: 10px;
            font-size: 0.95rem;
            font-weight: 600;
            transition: var(--transition);
        }

        select {
            flex: 1;
            min-width: 250px;
            border: 1px solid rgba(124, 58, 237, 0.3);
            background: rgba(30, 41, 59, 0.7);
            color: #e2e8f0;
        }

        select:hover {
            border-color: rgba(124, 58, 237, 0.5);
        }

        select:focus {
            outline: none;
            border-color: #7c3aed;
            box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.2);
        }

        .btn {
            background: linear-gradient(135deg, #7c3aed, #06b6d4);
            color: white;
            border: none;
            cursor: pointer;
            white-space: nowrap;
            box-shadow: 0 0 20px rgba(124, 58, 237, 0.3);
        }

        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 0 30px rgba(124, 58, 237, 0.5);
            background: linear-gradient(135deg, #6d28d9, #0891b2);
        }

        .btn:active {
            transform: translateY(0);
        }

        /* Responsive */
        @media (max-width: 768px) {
            .container, .header-container {
                padding: 0 1rem;
            }
            .header-container {
                flex-direction: column;
                text-align: center;
            }
            .header-title {
                font-size: 1.5rem;
            }
            .members-grid {
                grid-template-columns: 1fr;
            }
            .add-form {
                flex-direction: column;
            }
            select, .btn {
                width: 100%;
            }
            .stats-grid {
                grid-template-columns: 1fr;
            }
            .add-section {
                padding: 1.5rem;
            }
        }
    </style>
</head>
<body>
<%
    Group group = (Group) request.getAttribute("group");
    List<Student> allStudents = (List<Student>) request.getAttribute("students");
    List<Teacher> allTeachers = (List<Teacher>) request.getAttribute("teachers");

    String successMessage = (String) session.getAttribute("successMessage");
    String errorMessage = (String) session.getAttribute("errorMessage");
    session.removeAttribute("successMessage");
    session.removeAttribute("errorMessage");
%>

<header class="header">
    <div class="header-container">
        <h1 class="header-title">
            Group Management: <%= group.getGroupName() %>
        </h1>
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="back-link">
            Back to Dashboard
        </a>
    </div>
</header>

<div class="container">

    <!-- Success / Error Messages -->
    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    <div class="alert alert-success">
        <%= successMessage %>
    </div>
    <% } %>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="alert alert-error">
        <%= errorMessage %>
    </div>
    <% } %>

    <!-- Group Overview -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Group Information</h2>
        </div>
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-label">Group Name</div>
                <div class="stat-value"><%= group.getGroupName() %></div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Group ID</div>
                <div class="stat-value"><%= group.getId() %></div>
            </div>
            <% if (group.getDescription() != null && !group.getDescription().isEmpty()) { %>
            <div class="stat-card">
                <div class="stat-label">Description</div>
                <div class="stat-value"><%= group.getDescription() %></div>
            </div>
            <% } %>
            <div class="stat-card">
                <div class="stat-label">Total Students</div>
                <div class="stat-value">
                    <span class="badge badge-primary">
                        <%= group.getStudentIDs() != null ? group.getStudentIDs().size() : 0 %>
                    </span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Total Teachers</div>
                <div class="stat-value">
                    <span class="badge badge-success">
                        <%= group.getTeacherIDs() != null ? group.getTeacherIDs().size() : 0 %>
                    </span>
                </div>
            </div>
        </div>
    </div>

    <!-- Teachers Section -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Teachers</h2>
        </div>

        <%-- Current Teachers --%>
        <% if (group.getTeacherIDs() != null && !group.getTeacherIDs().isEmpty()) { %>
        <div class="members-grid">
            <%
                for (String teacherId : group.getTeacherIDs()) {
                    for (Teacher teacher : allTeachers) {
                        if (teacher.getId().equals(teacherId)) {
            %>
            <div class="member-card teacher-card">
                <div class="avatar"><%= teacher.getFullName().charAt(0) %></div>
                <div class="member-info">
                    <h4><%= teacher.getFullName() %></h4>
                    <div class="member-email"><%= teacher.getEmail() %></div>
                    <% if (teacher.getSpecialization() != null && !teacher.getSpecialization().isEmpty()) { %>
                    <div class="member-meta"><%= teacher.getSpecialization() %></div>
                    <% } %>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/admin/manage-group"
                      onsubmit="return confirm('Remove <%= teacher.getFullName() %> from this group?');">
                    <input type="hidden" name="action" value="remove-teacher">
                    <input type="hidden" name="groupId" value="<%= group.getId() %>">
                    <input type="hidden" name="teacherId" value="<%= teacher.getId() %>">
                    <button type="submit" class="remove-btn" title="Remove teacher">Remove</button>
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
            <div class="empty-icon">No teachers</div>
            <p>No teachers assigned to this group yet.</p>
        </div>
        <% } %>

        <%-- Add Teacher Form --%>
        <div class="add-section">
            <h3>Add Teacher</h3>
            <form method="post" action="${pageContext.request.contextPath}/admin/manage-group" class="add-form">
                <input type="hidden" name="action" value="add-teacher">
                <input type="hidden" name="groupId" value="<%= group.getId() %>">
                <select name="teacherId" required>
                    <option value="">Select a teacher...</option>
                    <%
                        for (Teacher teacher : allTeachers) {
                            boolean isInGroup = group.getTeacherIDs() != null && group.getTeacherIDs().contains(teacher.getId());
                            if (!isInGroup) {
                    %>
                    <option value="<%= teacher.getId() %>">
                        <%= teacher.getFullName() %>
                        <% if (teacher.getSpecialization() != null && !teacher.getSpecialization().isEmpty()) { %>
                        — <%= teacher.getSpecialization() %>
                        <% } %>
                    </option>
                    <%
                            }
                        }
                    %>
                </select>
                <button type="submit" class="btn">Add Teacher</button>
            </form>
        </div>
    </div>

    <!-- Students Section -->
    <div class="card">
        <div class="card-header">
            <h2 class="card-title">Students</h2>
        </div>

        <%-- Current Students --%>
        <% if (group.getStudentIDs() != null && !group.getStudentIDs().isEmpty()) { %>
        <div class="members-grid">
            <%
                for (String studentId : group.getStudentIDs()) {
                    for (Student student : allStudents) {
                        if (student.getId().equals(studentId)) {
            %>
            <div class="member-card student-card">
                <div class="avatar"><%= student.getFullName().charAt(0) %></div>
                <div class="member-info">
                    <h4><%= student.getFullName() %></h4>
                    <div class="member-email"><%= student.getEmail() %></div>
                    <div class="member-meta">
                        Submissions: <%= student.getSubmissionID() != null ? student.getSubmissionID().size() : 0 %>
                    </div>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/admin/manage-group"
                      onsubmit="return confirm('Remove <%= student.getFullName() %> from this group?');">
                    <input type="hidden" name="action" value="remove-student">
                    <input type="hidden" name="groupId" value="<%= group.getId() %>">
                    <input type="hidden" name="studentId" value="<%= student.getId() %>">
                    <button type="submit" class="remove-btn" title="Remove student">Remove</button>
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
            <div class="empty-icon">No students</div>
            <p>No students enrolled in this group yet.</p>
        </div>
        <% } %>

        <%-- Add Student Form --%>
        <div class="add-section">
            <h3>Add Student</h3>
            <form method="post" action="${pageContext.request.contextPath}/admin/manage-group" class="add-form">
                <input type="hidden" name="action" value="add-student">
                <input type="hidden" name="groupId" value="<%= group.getId() %>">
                <select name="studentId" required>
                    <option value="">Select a student...</option>
                    <%
                        for (Student student : allStudents) {
                            boolean isInGroup = group.getStudentIDs() != null && group.getStudentIDs().contains(student.getId());
                            if (!isInGroup) {
                                boolean inOtherGroup = student.getGroupId() != null && !student.getGroupId().isEmpty();
                    %>
                    <option value="<%= student.getId() %>">
                        <%= student.getFullName() %>
                        <% if (inOtherGroup) { %> (Already in another group)<% } %>
                    </option>
                    <%
                            }
                        }
                    %>
                </select>
                <button type="submit" class="btn">Add Student</button>
            </form>
        </div>
    </div>
</div>
</body>
</html>
