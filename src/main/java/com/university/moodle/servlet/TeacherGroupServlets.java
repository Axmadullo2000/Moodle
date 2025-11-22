package com.university.moodle.servlet;

import com.university.moodle.dao.AssignmentDAO;
import com.university.moodle.dao.GroupDAO;
import com.university.moodle.dao.StudentDAO;
import com.university.moodle.dao.SubmissionDAO;
import com.university.moodle.model.Assignment;
import com.university.moodle.model.Group;
import com.university.moodle.model.Student;
import com.university.moodle.model.Submission;
import com.university.moodle.service.SubmissionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@WebServlet("/teacher/group")
public class TeacherGroupServlets extends HttpServlet {
    private final AssignmentDAO assignmentDAO = AssignmentDAO.getInstance();
    private final StudentDAO studentDAO = StudentDAO.getInstance();
    private final GroupDAO groupDAO = GroupDAO.getInstance();
    private final SubmissionService submissionService = new SubmissionService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String groupId = req.getParameter("groupId");

        if (groupId == null || groupId.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/teacher/dashboard?error=Missing group ID");
            return;
        }

        Optional<Group> groupOpt = groupDAO.findById(groupId);
        if (groupOpt.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/teacher/dashboard?error=Group not found");
            return;
        }

        Group group = groupOpt.get();
        List<Assignment> assignments = assignmentDAO.findByGroupId(groupId);
        if (assignments == null) {
            assignments = Collections.emptyList();
        }

        List<Student> students = new ArrayList<>();
        if (group.getStudentIDs() != null && !group.getStudentIDs().isEmpty()) {
            for (String studentId : group.getStudentIDs()) {
                try {
                    studentDAO.findById(studentId).ifPresent(students::add);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        String assignmentId = req.getParameter("assignmentId");

        List<Submission> submissionsByAssignment;

        try {
            submissionsByAssignment = submissionService.getSubmissionsByAssignment(assignmentId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        req.setAttribute("group", group);
        req.setAttribute("groupId", group.getId());
        req.setAttribute("assignments", assignments);
        req.setAttribute("submissions", submissionsByAssignment);
        req.setAttribute("students", students);
        req.setAttribute("studentsCount", students.size());

        // ✅ ПРАВИЛЬНЫЙ ПУТЬ - используем сервлет для скачивания
        req.setAttribute("downloadBaseUrl", req.getContextPath());

        String success = req.getParameter("success");
        String error = req.getParameter("error");
        if (success != null && !success.isBlank()) {
            req.setAttribute("successMessage", success);
        }
        if (error != null && !error.isBlank()) {
            req.setAttribute("errorMessage", error);
        }

        req.getRequestDispatcher("/teacher/teacher-group.jsp").forward(req, resp);
    }

}
