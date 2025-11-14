package com.university.moodle.servlet;

import com.university.moodle.model.Student;
import com.university.moodle.model.User;
import com.university.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init() {
        authService = AuthService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String userId = (String) session.getAttribute("userId");
        User user = (User) session.getAttribute("user");

        if (user == null) {
            user = authService.getUserById(userId);

            if (user == null) {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/index.jsp");
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

        String initial = user.getFullName() != null && !user.getFullName().isEmpty()
                ? user.getFullName().substring(0, 1).toUpperCase()
                : "U";
        request.setAttribute("initial", initial);

        if (user instanceof Student student) {
            request.setAttribute("isStudent", true);
            request.setAttribute("groupId", student.getGroupId());
            request.setAttribute("submissionCount",
                    student.getSubmissionID() != null ? student.getSubmissionID().size() : 0);
        } else {
            request.setAttribute("isStudent", false);
        }

        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }
}
