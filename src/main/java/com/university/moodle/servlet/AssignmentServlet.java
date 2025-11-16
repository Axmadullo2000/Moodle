package com.university.moodle.servlet;

import com.university.service.AssignmentService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/assignments")
public class AssignmentServlet extends HttpServlet {
    private final AssignmentService assignmentService = AssignmentService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, IOException {
        String teacherId = (String) req.getSession().getAttribute("userId");

        var list = assignmentService.getAssignmentsByTeacher(teacherId);
        req.setAttribute("assignments", list);

        try {
            req.getRequestDispatcher("/teacher-assignments.jsp")
                    .forward(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("Error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String teacherId = (String) req.getSession().getAttribute("userId");

        String groupId = req.getParameter("groupId");
        String title = req.getParameter("title");
        String desc = req.getParameter("description");
        String deadline = req.getParameter("deadline");
        Integer maxScore = Integer.parseInt(req.getParameter("maxScore"));

        assignmentService.createAssignment(
                teacherId,
                groupId,
                title,
                desc,
                LocalDateTime.parse(deadline),
                maxScore
        );

        resp.sendRedirect("/assignments");
    }
}
