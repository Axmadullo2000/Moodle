package com.university.moodle.servlet;

import com.university.service.SubmissionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/teacher/grade-submission")
public class GradeSubmissionServlet extends HttpServlet {
    private final SubmissionService submissionService = new SubmissionService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String submissionId = req.getParameter("submissionId");
        String assignmentId = req.getParameter("assignmentId");
        String gradeStr = req.getParameter("grade");

        if (submissionId == null || gradeStr == null) {
            resp.sendError(400, "Missing parameters");
            return;
        }

        try {
            int grade = Integer.parseInt(gradeStr);
            submissionService.gradeSubmission(submissionId, grade);
        } catch (NumberFormatException e) {
            resp.sendError(400, "Invalid grade format");
            return;
        }

        // Редирект обратно на страницу submissions
        resp.sendRedirect(req.getContextPath() + "/teacher/submissions?assignmentId=" + assignmentId);
    }
}
