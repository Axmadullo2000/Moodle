<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.university.moodle.model.Assignment" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    List<Assignment> assignments = (List<Assignment>) request.getAttribute("assignments");
    String successMessage = (String) session.getAttribute("successMessage");
    String errorMessage = (String) session.getAttribute("errorMessage");
    session.removeAttribute("successMessage");
    session.removeAttribute("errorMessage");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    LocalDateTime now = LocalDateTime.now();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Assignments</title>
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
            padding: 20px;
            color: #333;
        }

        .container { max-width: 1200px; margin: 0 auto; }

        .header {
            background: white;
            padding: 24px 32px;
            border-radius: 12px;
            margin-bottom: 32px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 16px;
        }

        .header h1 {
            font-size: 28px;
            font-weight: 700;
            color: var(--dark);
        }

        .nav-links {
            display: flex;
            gap: 12px;
        }

        .nav-links a {
            padding: 10px 20px;
            background: var(--primary);
            color: white;
            text-decoration: none;
            border-radius: 8px;
            font-weight: 600;
            transition: all 0.3s ease;
        }

        .nav-links a:hover {
            background: var(--primary-dark);
            transform: translateY(-2px);
        }

        .alert {
            padding: 16px 24px;
            border-radius: 10px;
            margin-bottom: 24px;
            font-weight: 500;
            display: flex;
            align-items: center;
            gap: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.05);
        }

        .alert-success { background: #d1e7dd; color: #0f5132; border-left: 5px solid var(--success); }
        .alert-error   { background: #f8d7da; color: #721c24; border-left: 5px solid var(--danger); }

        .assignments-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
            gap: 24px;
        }

        .assignment-card {
            background: white;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
        }

        .assignment-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
        }

        .card-header {
            padding: 24px 28px 0;
        }

        .assignment-title {
            font-size: 21px;
            font-weight: 600;
            color: var(--dark);
            margin-bottom: 8px;
        }

        .status-badge {
            display: inline-block;
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .status-submitted   { background: #d4edda; color: #155724; }
        .status-overdue     { background: #f8d7da; color: #721c24; }
        .status-pending     { background: #fff3cd; color: #856404; }
        .status-not-submitted { background: #e2e3e5; color: #383d41; }

        .card-body {
            padding: 20px 28px;
        }

        .meta-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 12px;
            margin-bottom: 16px;
        }

        .meta-item {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            color: var(--gray);
        }

        .deadline {
            font-weight: 600;
            padding: 4px 0;
        }

        .deadline.overdue { color: var(--danger); }
        .deadline.soon    { color: #ff8c00; }
        .deadline.normal  { color: var(--success); }

        .description {
            color: #555;
            line-height: 1.6;
            margin: 16px 0;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .card-footer {
            padding: 20px 28px;
            background: #f8f9fa;
            border-top: 1px solid var(--border);
            display: flex;
            justify-content: flex-end;
        }

        .btn {
            padding: 11px 24px;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s ease;
        }

        .btn-primary {
            background: var(--primary);
            color: white;
        }

        .btn-primary:hover:not(:disabled) {
            background: var(--primary-dark);
            transform: translateY(-2px);
        }

        .btn-disabled {
            background: #ced4da;
            color: #6c757d;
            cursor: not-allowed;
        }

        .empty-state {
            text-align: center;
            padding: 80px 20px;
            background: white;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }

        .empty-state h2 {
            font-size: 26px;
            color: var(--gray);
            margin-bottom: 12px;
        }

        .empty-state p {
            color: #999;
            font-size: 16px;
        }

        @media (max-width: 768px) {
            .header { flex-direction: column; text-align: center; }
            .meta-grid { grid-template-columns: 1fr; }
            .card-footer { justify-content: center; }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>My Assignments</h1>
        <div class="nav-links">
            <a href="<%= request.getContextPath() %>/student/submissions">My Submissions</a>
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </div>
    </div>

    <% if (successMessage != null) { %>
    <div class="alert alert-success">Success: <%= successMessage %></div>
    <% } %>

    <% if (errorMessage != null) { %>
    <div class="alert alert-error">Error: <%= errorMessage %></div>
    <% } %>

    <% if (assignments == null || assignments.isEmpty()) { %>
    <div class="empty-state">
        <h2>No assignments available</h2>
        <p>Your teacher hasn't created any assignments yet. Check back later!</p>
    </div>
    <% } else { %>
    <div class="assignments-grid">
        <% for (Assignment a : assignments) {
            boolean isSubmitted = a.getSubmissionID() != null && !a.getSubmissionID().isEmpty();
            boolean isOverdue = now.isAfter(a.getDeadline());
            boolean isSoon = !isOverdue && a.getDeadline().isBefore(now.plusDays(2));

            String deadlineStyle = isOverdue ? "overdue" : (isSoon ? "soon" : "normal");
        %>
        <div class="assignment-card">
            <div class="card-header">
                <div class="assignment-title"><%= a.getTitle() %></div>
                <% if (isSubmitted) { %>
                <span class="status-badge status-submitted">Submitted</span>
                <% } else if (isOverdue) { %>
                <span class="status-badge status-overdue">Overdue</span>
                <% } else { %>
                <span class="status-badge status-pending">Not Submitted</span>
                <% } %>
            </div>

            <div class="card-body">
                <div class="meta-grid">
                    <div class="meta-item">
                        <span>Deadline</span>
                        <span class="deadline <%= deadlineStyle %>">
                            <%= a.getDeadline().format(formatter) %>
                        </span>
                    </div>
                    <div class="meta-item">
                        <span>Max Score</span>
                        <span><%= a.getMaxScore() %> points</span>
                    </div>
                </div>

                <div class="description">
                    <%= a.getDescription() != null && !a.getDescription().trim().isEmpty()
                            ? a.getDescription()
                            : "<em>No description provided.</em>" %>
                </div>
            </div>

            <div class="card-footer">
                <% if (isOverdue && !isSubmitted) { %>
                <button class="btn btn-disabled" disabled>Deadline Passed</button>
                <% } else { %>
                <a href="<%= request.getContextPath() %>/student/assignments?action=view&id=<%= a.getId() %>"
                   class="btn btn-primary">
                    <%= isSubmitted ? "View / Edit" : "Submit Now" %>
                </a>
                <% } %>
            </div>
        </div>
        <% } %>
    </div>
    <% } %>
</div>
</body>
</html>
