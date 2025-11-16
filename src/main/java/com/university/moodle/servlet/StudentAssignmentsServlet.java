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
import java.util.List;
import java.util.Optional;

@WebServlet("/student/assignments")
public class StudentAssignmentsServlet extends HttpServlet {
    private AssignmentDAO assignmentDAO;
    private SubmissionDAO submissionDAO;

    @Override
    public void init() {
        assignmentDAO = AssignmentDAO.getInstance();
        submissionDAO = SubmissionDAO.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        Student student = (Student) session.getAttribute("user");
        String action = request.getParameter("action");

        if ("view".equals(action)) {
            viewAssignment(request, response, student);
        } else {
            listAssignments(request, response, student);
        }
    }

    private void listAssignments(HttpServletRequest request, HttpServletResponse response, Student student)
            throws ServletException, IOException {

        // Получаем задания для группы студента
        List<Assignment> assignments = assignmentDAO.findByGroupId(student.getGroupId());

        // Добавляем информацию о статусе отправки для каждого задания
        for (Assignment assignment : assignments) {
            Optional<Submission> submission = submissionDAO.findByAssignmentAndStudent(
                    assignment.getId(),
                    student.getId()
            );
            assignment.setSubmissionID(submission.map(s -> List.of(s.getId())).orElse(List.of()));
        }

        request.setAttribute("assignments", assignments);
        request.setAttribute("studentId", student.getId());
        request.getRequestDispatcher("/student-assignments.jsp")
                .forward(request, response);
    }

    private void viewAssignment(HttpServletRequest request, HttpServletResponse response, Student student)
            throws ServletException, IOException {

        String assignmentId = request.getParameter("id");
        if (assignmentId == null || assignmentId.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/student/assignments");
            return;
        }

        Optional<Assignment> assignmentOpt = assignmentDAO.getById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/student/assignments");
            return;
        }

        Assignment assignment = assignmentOpt.get();

        // Проверяем, есть ли уже отправленный ответ
        Optional<Submission> submissionOpt = submissionDAO.findByAssignmentAndStudent(
                assignmentId,
                student.getId()
        );

        request.setAttribute("assignment", assignment);
        request.setAttribute("submission", submissionOpt.orElse(null));
        request.getRequestDispatcher("/submit-assignment.jsp")
                .forward(request, response);
    }
}
