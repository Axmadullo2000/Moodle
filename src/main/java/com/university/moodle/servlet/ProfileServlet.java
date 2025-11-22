package com.university.moodle.servlet;

import com.university.moodle.model.Admin;
import com.university.moodle.model.Student;
import com.university.moodle.model.Teacher;
import com.university.moodle.model.User;
import com.university.moodle.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init() {
        try {
            authService = AuthService.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String userId = (String) session.getAttribute("userId");
        User user = (User) session.getAttribute("user");

        if (user == null) {
            try {
                user = authService.getUserById(userId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (user == null) {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }
        }

        if (user.getEmail() == null || user.getFullName() == null || user.getRole() == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "User data is incomplete");
            return;
        }

        request.setAttribute("user", user);
        request.setAttribute("userId", userId);
        request.setAttribute("email", user.getEmail());
        request.setAttribute("fullName", user.getFullName());

        String role = user.getRole() != null ? user.getRole().toString() : "UNKNOWN";
        request.setAttribute("role", role);

        if (user instanceof Student student) {
            request.setAttribute("userType", "student");
            request.setAttribute("isStudent", true);
            request.setAttribute("isTeacher", false);
            request.setAttribute("isAdmin", false);
            request.setAttribute("groupId", student.getGroupId());
            request.setAttribute("submissionCount",
                    student.getSubmissionID() != null ? student.getSubmissionID().size() : 0);

        }else if (user instanceof Teacher teacher) {
            request.setAttribute("userType", "teacher");
            request.setAttribute("isStudent", false);
            request.setAttribute("isTeacher", true);
            request.setAttribute("isAdmin", false);
            request.setAttribute("specialization", teacher.getSpecialization());
            request.setAttribute("groupCount",
                    teacher.getGroupID() != null ? teacher.getGroupID().size() : 0);
            request.setAttribute("assignmentCount",
                    teacher.getAssignmentID() != null ? teacher.getAssignmentID().size() : 0);
        }else if (user instanceof Admin) {
            request.setAttribute("userType", "admin");
            request.setAttribute("isStudent", false);
            request.setAttribute("isTeacher", false);
            request.setAttribute("isAdmin", true);
        }
        else {
            request.setAttribute("isStudent", false);
        }

        request.getRequestDispatcher("/profile/profile.jsp").forward(request, response);
    }
}
