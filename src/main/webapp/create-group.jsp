<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.university.moodle.model.Teacher" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Group - Moodle</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }

        .container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
            padding: 40px;
            width: 100%;
            max-width: 600px;
        }

        .header {
            text-align: center;
            margin-bottom: 30px;
        }

        .header h1 {
            color: #333;
            font-size: 28px;
            margin-bottom: 10px;
        }

        .header p {
            color: #666;
            font-size: 14px;
        }

        .error-message {
            background: #fee;
            border: 1px solid #fcc;
            color: #c33;
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 20px;
            font-size: 14px;
        }

        .form-group {
            margin-bottom: 25px;
        }

        .form-group label {
            display: block;
            color: #333;
            font-weight: 600;
            margin-bottom: 8px;
            font-size: 14px;
        }

        .form-group label .required {
            color: #e74c3c;
        }

        .form-group input,
        .form-group textarea,
        .form-group select {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #e0e0e0;
            border-radius: 6px;
            font-size: 14px;
            transition: border-color 0.3s;
            font-family: inherit;
        }

        .form-group input:focus,
        .form-group textarea:focus,
        .form-group select:focus {
            outline: none;
            border-color: #667eea;
        }

        .form-group textarea {
            resize: vertical;
            min-height: 100px;
        }

        .form-group select {
            cursor: pointer;
            background-color: white;
        }

        .form-group small {
            display: block;
            color: #666;
            font-size: 12px;
            margin-top: 5px;
        }

        .button-group {
            display: flex;
            gap: 15px;
            margin-top: 30px;
        }

        .btn {
            flex: 1;
            padding: 14px 20px;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
            text-align: center;
            display: inline-block;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
        }

        .btn-secondary {
            background: #e0e0e0;
            color: #333;
        }

        .btn-secondary:hover {
            background: #d0d0d0;
        }

        @media (max-width: 768px) {
            .container {
                padding: 30px 20px;
            }

            .header h1 {
                font-size: 24px;
            }

            .button-group {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
<%
    String error = (String) request.getAttribute("error");
    String groupName = (String) request.getAttribute("groupName");
    String description = (String) request.getAttribute("description");
    String teacherId = (String) request.getAttribute("teacherId");
    List<Teacher> teachers = (List<Teacher>) request.getAttribute("teachers");

    if (groupName == null) groupName = "";
    if (description == null) description = "";
    if (teacherId == null) teacherId = "";
%>

<div class="container">
    <div class="header">
        <h1>Create New Group</h1>
        <p>Fill in the details to create a new group</p>
    </div>

    <% if (error != null && !error.isEmpty()) { %>
    <div class="error-message">
        <%= error %>
    </div>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/admin/create-group">
        <div class="form-group">
            <label for="groupName">
                Group Name <span class="required">*</span>
            </label>
            <input
                    type="text"
                    id="groupName"
                    name="groupName"
                    placeholder="Enter group name"
                    value="<%= groupName %>"
                    required
                    autofocus
            />
            <small>Enter a unique name for the group</small>
        </div>

        <div class="form-group">
            <label for="description">
                Description
            </label>
            <textarea
                    id="description"
                    name="description"
                    placeholder="Enter group description (optional)"
            ><%= description %></textarea>
            <small>Provide a brief description of the group</small>
        </div>

        <div class="form-group">
            <label for="teacherId">
                Assign Teacher
            </label>
            <select id="teacherId" name="teacherId">
                <option value="">-- Select a teacher (optional) --</option>
                <%
                    if (teachers != null) {
                        for (Teacher teacher : teachers) {
                            String selected = teacherId.equals(teacher.getId()) ? "selected" : "";
                %>
                <option value="<%= teacher.getId() %>" <%= selected %>>
                    <%= teacher.getFullName() %>
                </option>
                <%
                        }
                    }
                %>
            </select>
            <small>You can assign a teacher to this group</small>
        </div>

        <div class="button-group">
            <a href="<%= request.getContextPath() %>/admin/dashboard" class="btn btn-secondary">
                Cancel
            </a>
            <button type="submit" class="btn btn-primary">
                Create Group
            </button>
        </div>
    </form>
</div>
</body>
</html>
