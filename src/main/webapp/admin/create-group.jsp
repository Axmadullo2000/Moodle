<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.university.moodle.model.Teacher" %>
<%
    String error = (String) request.getAttribute("error");
    String id = (String) request.getAttribute("id");
    String groupName = (String) request.getAttribute("groupName");
    String description = (String) request.getAttribute("description");
    String teacherId = (String) request.getAttribute("teacherId");
    List<Teacher> teachers = (List<Teacher>) request.getAttribute("teachers");
    boolean isEdit = id != null && !id.isEmpty();

    if (groupName == null) groupName = "";
    if (description == null) description = "";
    if (teacherId == null) teacherId = "";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= isEdit ? "Edit Group" : "Create New Group" %> • Admin</title>
    <style>
        :root {
            --primary: #667eea;
            --primary-dark: #5568d3;
            --success: #28a745;
            --danger: #dc3545;
            --light: #f8f9fa;
            --dark: #343a40;
            --gray: #073c71;
            --border: #dee2e6;
            --card-shadow: 0 20px 50px rgba(0, 0, 0, 0.18);
            --hover-shadow: 0 35px 80px rgba(0, 0, 0, 0.28);
        }

        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Inter', 'Segoe UI', Tahoma, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 30px 20px;
            color: #333;
        }

        .form-container {
            background: white;
            border-radius: 24px;
            overflow: hidden;
            box-shadow: var(--card-shadow);
            max-width: 560px;
            width: 100%;
            transition: all 0.4s ease;
        }

        .form-container:hover {
            transform: translateY(-12px);
            box-shadow: var(--hover-shadow);
        }

        .form-header {
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            padding: 50px 40px;
            text-align: center;
            color: white;
        }

        .form-icon {
            width: 90px;
            height: 90px;
            background: rgba(255,255,255,0.2);
            backdrop-filter: blur(10px);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 20px;
            font-size: 38px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        }

        .form-title {
            font-size: 32px;
            font-weight: 700;
            margin-bottom: 10px;
        }

        .form-subtitle {
            font-size: 17px;
            opacity: 0.95;
        }

        .form-body {
            padding: 44px 40px;
        }

        .error-alert {
            background: #f8d7da;
            color: #721c24;
            padding: 16px 20px;
            border-radius: 14px;
            border-left: 5px solid var(--danger);
            margin-bottom: 28px;
            font-size: 15px;
            font-weight: 500;
        }

        .form-group {
            margin-bottom: 28px;
        }

        label {
            display: block;
            margin-bottom: 10px;
            font-weight: 600;
            color: var(--dark);
            font-size: 15px;
        }

        .required {
            color: var(--danger);
            margin-left: 4px;
        }

        input, textarea, select {
            width: 100%;
            padding: 16px 18px;
            border: 2px solid #e2e8f0;
            border-radius: 14px;
            font-size: 15px;
            background: #f8fafc;
            transition: all 0.3s ease;
            font-family: inherit;
        }

        input:focus, textarea:focus, select:focus {
            outline: none;
            border-color: var(--primary);
            background: white;
            box-shadow: 0 0 0 5px rgba(102, 126, 234, 0.15);
        }

        textarea {
            min-height: 120px;
            resize: vertical;
        }

        select {
            cursor: pointer;
            appearance: none;
            background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%23667eea'%3e%3cpath d='M7 10l5 5 5-5z'/%3e%3c/svg%3e");
            background-repeat: no-repeat;
            background-position: right 16px center;
            background-size: 16px;
        }

        .help-text {
            font-size: 13px;
            color: var(--gray);
            margin-top: 8px;
            display: block;
        }

        .button-group {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
            margin-top: 32px;
        }

        .btn {
            padding: 18px 20px;
            border: none;
            border-radius: 14px;
            font-weight: 600;
            font-size: 16px;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
        }

        .btn-primary {
            background: linear-gradient(135deg, var(--primary), var(--primary-dark));
            color: white;
        }

        .btn-primary:hover {
            transform: translateY(-4px);
            box-shadow: 0 15px 35px rgba(102, 126, 234, 0.4);
        }

        .btn-secondary {
            background: #e9ecef;
            color: var(--dark);
        }

        .btn-secondary:hover {
            background: #dee2e6;
            transform: translateY(-4px);
        }

        /* Responsive */
        @media (max-width: 576px) {
            .form-header { padding: 40px 20px; }
            .form-icon { width: 80px; height: 80px; font-size: 34px; }
            .form-title { font-size: 28px; }
            .form-body { padding: 36px 24px; }
            .button-group { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>

<div class="form-container">
    <!-- Header -->
    <div class="form-header">
        <div class="form-icon"><%= isEdit ? "Edit" : "Group" %></div>
        <div class="form-title"><%= isEdit ? "Edit Group" : "Create New Group" %></div>
        <div class="form-subtitle">
            <%= isEdit ? "Update group details below" : "Fill in the information to create a new academic group" %>
        </div>
    </div>

    <!-- Body -->
    <div class="form-body">

        <!-- Error Message -->
        <% if (error != null && !error.isEmpty()) { %>
        <div class="error-alert">
            <%= error %>
        </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/admin/create-group" method="POST">
            <input type="hidden" name="id" value="<%= id %>">

            <div class="form-group">
                <label for="groupName">
                    Group Name <span class="required">*</span>
                </label>
                <input
                        type="text"
                        id="groupName"
                        name="groupName"
                        value="<%= groupName %>"
                        placeholder="e.g. Computer Science 2025"
                        required
                        autofocus
                />
                <span class="help-text">Choose a clear and unique name for the group</span>
            </div>

            <div class="form-group">
                <label for="description">Description</label>
                <textarea
                        id="description"
                        name="description"
                        placeholder="Brief description of the group, its purpose, year, etc. (optional)"
                ><%= description %></textarea>
                <span class="help-text">Optional — helps teachers and students understand the group</span>
            </div>

            <div class="form-group">
                <label for="teacherId">Assign Lead Teacher</label>
                <select id="teacherId" name="teacherId">
                    <option value="">-- No teacher assigned (optional) --</option>
                    <% if (teachers != null) {
                        for (Teacher t : teachers) {
                            String selected = teacherId.equals(t.getId()) ? "selected" : "";
                    %>
                    <option value="<%= t.getId() %>" <%= selected %>>
                        <%= t.getFullName() %> <%= t.getSpecialization() != null ? "• " + t.getSpecialization() : "" %>
                    </option>
                    <%  }
                    } %>
                </select>
                <span class="help-text">You can assign a primary teacher or leave it unassigned</span>
            </div>

            <div class="button-group">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary">
                    Cancel
                </a>
                <button type="submit" class="btn btn-primary">
                    <%= isEdit ? "Update Group" : "Create Group" %>
                </button>
            </div>
        </form>
    </div>
</div>

</body>
</html>