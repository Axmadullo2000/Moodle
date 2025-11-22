<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.university.moodle.model.Group" %>
<%@ page import="java.util.List" %>
<%
    List<Group> groups = (List<Group>) request.getAttribute("groups");
    String successMessage = (String) session.getAttribute("successMessage");
    String errorMessage = (String) session.getAttribute("errorMessage");
    session.removeAttribute("successMessage");
    session.removeAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create New Assignment</title>
    <style>
        :root {
            --primary: #667eea;
            --primary-dark: #5568d3;
            --success: #28a745;
            --warning: #ffc107;
            --danger: #dc3545;
            --info: #17a2b8;
            --light: #f8f9fa;
            --dark: #343a40;
            --gray: #6c757d;
            --border: #dee2e6;
        }

        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }

        .navbar {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            padding: 20px 40px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
            position: sticky;
            top: 0;
            z-index: 100;
        }

        .navbar h1 {
            font-size: 26px;
            font-weight: 700;
            color: var(--dark);
        }

        .nav-links {
            display: flex;
            gap: 12px;
        }

        .nav-link {
            padding: 10px 20px;
            background: var(--primary);
            color: white;
            text-decoration: none;
            border-radius: 8px;
            font-weight: 600;
            transition: all 0.3s ease;
        }

        .nav-link:hover {
            background: var(--primary-dark);
            transform: translateY(-2px);
        }

        .container {
            max-width: 800px;
            margin: 40px auto;
            padding: 40px;
            background: white;
            border-radius: 16px;
            box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
        }

        .page-title {
            font-size: 28px;
            font-weight: 700;
            color: var(--dark);
            margin-bottom: 32px;
            text-align: center;
        }

        .alert {
            padding: 16px 20px;
            border-radius: 12px;
            margin-bottom: 28px;
            font-weight: 500;
            display: flex;
            align-items: center;
            gap: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
        }

        .alert-success {
            background: #d4edda;
            color: #155724;
            border-left: 5px solid var(--success);
        }

        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border-left: 5px solid var(--danger);
        }

        .form-grid {
            display: grid;
            gap: 24px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
        }

        label {
            font-weight: 600;
            color: var(--dark);
            margin-bottom: 10px;
            font-size: 15px;
        }

        .required {
            color: var(--danger);
        }

        input[type="text"],
        input[type="number"],
        input[type="datetime-local"],
        textarea,
        select {
            padding: 14px 16px;
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            font-family: inherit;
            font-size: 15px;
            transition: all 0.3s ease;
        }

        input:focus,
        textarea:focus,
        select:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.15);
        }

        textarea {
            min-height: 140px;
            resize: vertical;
        }

        select {
            cursor: pointer;
        }

        .file-upload {
            border: 2px dashed #ccc;
            border-radius: 12px;
            padding: 32px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s;
            background: #fafafa;
        }

        .file-upload:hover {
            border-color: var(--primary);
            background: #f8f5ff;
        }

        .file-upload input { display: none; }

        .file-upload-label {
            color: var(--primary);
            font-weight: 600;
            font-size: 16px;
        }

        .help-text {
            font-size: 13px;
            color: var(--gray);
            margin-top: 8px;
        }

        .form-actions {
            display: flex;
            gap: 16px;
            margin-top: 32px;
            justify-content: center;
            flex-wrap: wrap;
        }

        .btn {
            padding: 14px 32px;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s ease;
        }

        .btn-primary {
            background: var(--primary);
            color: white;
        }

        .btn-primary:hover {
            background: var(--primary-dark);
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
        }

        .btn-secondary {
            background: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background: #5a6268;
        }

        @media (max-width: 768px) {
            .navbar { flex-direction: column; text-align: center; gap: 16px; padding: 20px; }
            .container { margin: 20px; padding: 24px; }
            .form-actions { flex-direction: column; }
        }
    </style>
</head>
<body>

<div class="navbar">
    <h1>Create Assignment</h1>
    <div class="nav-links">
        <a href="<%= request.getContextPath() %>/teacher/dashboard" class="nav-link">Back to Dashboard</a>
    </div>
</div>

<div class="container">
    <h2 class="page-title">Create New Assignment</h2>

    <% if (successMessage != null) { %>
    <div class="alert alert-success">
        Success: <%= successMessage %>
    </div>
    <% } %>

    <% if (errorMessage != null) { %>
    <div class="alert alert-error">
        Error: <%= errorMessage %>
    </div>
    <% } %>

    <form action="<%= request.getContextPath() %>/teacher/upload-assignment" method="post" enctype="multipart/form-data">
        <div class="form-grid">

            <div class="form-group">
                <label for="title">Assignment Title <span class="required">*</span></label>
                <input type="text" name="title" id="title" required maxlength="150"
                       placeholder="e.g., Lab Work #1 - Java Collections">
            </div>

            <div class="form-group">
                <label for="description">Description</label>
                <textarea name="description" id="description"
                          placeholder="Provide detailed instructions, requirements, and grading criteria..."></textarea>
            </div>

            <div class="form-group">
                <label for="groupId">Select Group <span class="required">*</span></label>
                <select name="groupId" id="groupId" required>
                    <option value="">— Choose a group —</option>
                    <% if (groups != null) {
                        for (Group g : groups) { %>
                    <option value="<%= g.getId() %>">
                        <%= g.getGroupName() %>
                        (<%= g.getStudentIDs() != null ? g.getStudentIDs().size() : 0 %> students)
                    </option>
                    <% }
                    } %>
                </select>
            </div>

            <div class="form-group">
                <label for="deadline">Deadline (optional)</label>
                <input type="datetime-local" name="deadline" id="deadline">
                <span class="help-text">Leave empty for no deadline</span>
            </div>

            <div class="form-group">
                <label for="file">Attach File (optional)</label>
                <div class="file-upload" onclick="document.getElementById('file').click()">
                    <input type="file" name="file" id="file">
                    <div class="file-upload-label">Click to upload supporting materials</div>
                    <span class="help-text">PDF, DOCX, ZIP, images — up to 50 MB</span>
                </div>
            </div>

            <div class="form-group">
                <label for="maxScore">Maximum Score</label>
                <input type="number" name="maxScore" id="maxScore" value="100" min="1" max="1000">
            </div>

        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">
                Create Assignment
            </button>
            <a href="<%= request.getContextPath() %>/teacher/dashboard" class="btn btn-secondary">
                Cancel
            </a>
        </div>
    </form>
</div>

</body>
</html>
