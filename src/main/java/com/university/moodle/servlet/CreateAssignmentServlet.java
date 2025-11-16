package com.university.moodle.servlet;

import com.university.moodle.dao.AssignmentDAO;
import com.university.moodle.dao.GroupDAO;
import com.university.moodle.model.Assignment;
import com.university.moodle.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/admin/create-assignment")
public class CreateAssignmentServlet extends HttpServlet {

    private AssignmentDAO assignmentDAO;
    private GroupDAO groupDAO;

    @Override
    public void init() throws ServletException {
        assignmentDAO = AssignmentDAO.getInstance();
        groupDAO = GroupDAO.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"ADMIN".equals(user.getRole().toString())) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        // Передаём все группы в JSP
        req.setAttribute("groups", groupDAO.getItems());
        req.getRequestDispatcher("/create-assignment.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String deadlineStr = req.getParameter("deadline");
        String maxScoreStr = req.getParameter("maxScore");
        String groupId = req.getParameter("groupId"); // ← ВОТ ОНО!

        if (title == null || title.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Название задания обязательно");
            resp.sendRedirect(req.getContextPath() + "/admin/create-assignment");
            return;
        }

        Assignment assignment = Assignment.builder()
                .title(title.trim())
                .description(description != null ? description.trim() : "")
                .maxScore(maxScoreStr != null && !maxScoreStr.isEmpty() ? Integer.parseInt(maxScoreStr) : 100)
                .build();
        assignment.setTitle(title.trim());
        if (deadlineStr != null && !deadlineStr.isEmpty()) {
            assignment.setDeadline(LocalDateTime.parse(deadlineStr));
        }

        // Сохраняем задание
        assignmentDAO.save(assignment);
        System.out.println("Создано задание: " + assignment.getTitle() + " (ID: " + assignment.getId() + ")");

        // Привязываем к группе
        if (groupId != null && !groupId.isEmpty()) {
            boolean added = groupDAO.addAssignmentToGroup(groupId, assignment.getId());

            if (added) {
                session.setAttribute("successMessage", "Задание создано и добавлено в группу!");
            } else {
                session.setAttribute("errorMessage", "Задание создано, но не добавлено в группу (уже есть?)");
            }
        } else {
            session.setAttribute("successMessage", "Задание создано (без привязки к группе)");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
    }
}
