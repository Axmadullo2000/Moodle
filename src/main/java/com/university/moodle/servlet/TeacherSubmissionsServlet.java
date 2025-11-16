package com.university.moodle.servlet;

import com.university.moodle.dao.GroupDAO;
import com.university.moodle.dao.StudentDAO;
import com.university.moodle.model.Assignment;
import com.university.moodle.model.Group;
import com.university.moodle.model.Student;
import com.university.moodle.model.Submission;
import com.university.service.AssignmentService;
import com.university.service.SubmissionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/teacher/submissions")
public class TeacherSubmissionsServlet extends HttpServlet {
    private final SubmissionService submissionService = new SubmissionService();
    private final AssignmentService assignmentService = AssignmentService.getInstance();
    private final StudentDAO studentDAO = StudentDAO.getInstance();
    private final GroupDAO groupDAO = GroupDAO.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String assignmentId = req.getParameter("assignmentId");
        String teacherId = (String) req.getSession().getAttribute("userId");

        Assignment assignment = assignmentService.getAssignmentsByTeacher(teacherId).stream()
                .filter(a -> a.getId().equals(assignmentId))
                .findFirst()
                .orElse(null);

        if (assignment == null) {
            resp.sendError(403);
            return;
        }

        List<Submission> submissions = submissionService.getSubmissionsByAssignment(assignmentId);
        Group group = groupDAO.findById(assignment.getGroupId()).orElse(null);
        List<Student> groupStudents = group != null ? group.getStudentIDs().stream()
                .map(studentDAO::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList() : List.of();

        req.setAttribute("assignment", assignment);
        req.setAttribute("submissions", submissions);
        req.setAttribute("groupStudents", groupStudents);
        req.setAttribute("totalStudents", groupStudents.size());
        req.setAttribute("submittedCount", submissions.size());

        req.getRequestDispatcher("/teacher-submissions.jsp").forward(req, resp);
    }
}

