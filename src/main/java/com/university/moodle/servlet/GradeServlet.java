package com.university.moodle.servlet;

import com.university.service.GradingService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/grade")
public class GradeServlet extends HttpServlet {
    private final GradingService gradingService = new GradingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, IOException {
        req.setAttribute("submissionId", req.getParameter("submissionId"));

        try {
            req.getRequestDispatcher("/WEB-INF/pages/teacher/teacher-grade.jsp")
                    .forward(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("Error: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String teacherId = (String) req.getSession().getAttribute("userId");

        String submissionId = req.getParameter("submissionId");
        Integer score = Integer.parseInt(req.getParameter("score"));
        String feedback = req.getParameter("feedback");

        gradingService.gradeSubmission(teacherId, submissionId, score, feedback);

        resp.sendRedirect("/teacher/submissions?assignmentId=" +
                req.getParameter("assignmentId"));
    }
}
