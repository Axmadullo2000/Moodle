package com.university.moodle.servlet;

import com.university.moodle.dao.AssignmentDAO;
import com.university.moodle.dao.SubmissionDAO;
import com.university.moodle.model.Assignment;
import com.university.moodle.model.Student;
import com.university.moodle.model.Submission;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/student/submissions")
public class StudentSubmissionServlet extends HttpServlet {
    private SubmissionDAO submissionDAO;
    private AssignmentDAO assignmentDAO;

    @Override
    public void init() {
        submissionDAO = SubmissionDAO.getInstance();
        assignmentDAO = AssignmentDAO.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        Student student = (Student) session.getAttribute("user");

        // Получаем все ответы студента
        List<Submission> submissions = submissionDAO.findByStudent(student.getId());

        // Создаём карту ответов с информацией о заданиях
        Map<Submission, Assignment> submissionMap = new LinkedHashMap<>();

        for (Submission submission : submissions) {
            Optional<Assignment> assignmentOpt = assignmentDAO.getById(submission.getAssignmentId());
            assignmentOpt.ifPresent(assignment -> submissionMap.put(submission, assignment));
        }

        request.setAttribute("submissionMap", submissionMap);
        request.getRequestDispatcher("/student-submissions.jsp")
                .forward(request, response);
    }
}
