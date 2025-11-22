package com.university.moodle.servlet;

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

import static com.university.moodle.enums.UserRole.*;

@WebServlet(urlPatterns = {"/auth", "/"})
public class AuthServlet extends HttpServlet {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String action = request.getParameter("action");

        if ("login".equals(action)) {
            handleLogin(request, response);
        } else if ("register".equals(action)) {
            handleRegistration(request, response);
        } else {
            sendError(response, "Invalid action");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            forwardWithError(request, response, "Email and password are required");
            return;
        }

        try {
            User user = authService.login(email, password);

            HttpSession session = request.getSession(true);

            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("role", user.getRole());
            session.setAttribute("fullName", user.getFullName());

            if (user.getRole().equals(STUDENT)) {
                response.sendRedirect(request.getContextPath() + "/student/assignments");
            }else if (user.getRole().equals(TEACHER)) {
                response.sendRedirect(request.getContextPath() + "/teacher/dashboard");
            }else if (user.getRole().equals(ADMIN)) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            }else {
                forwardWithError(request, response, "Invalid role");
            }

        } catch (RuntimeException | SQLException e) {
            forwardWithError(request, response, e.getMessage());
        }
    }

    private void handleRegistration(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");

        if (email == null || password == null || fullName == null ||
                email.isBlank() || password.isBlank() || fullName.isBlank()) {
            sendError(response, "All fields are required");
            return;
        }

        try {
            User user = authService.registerStudent(email, password, fullName);

            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("role", STUDENT.name());
            session.setAttribute("fullName", user.getFullName());

            response.sendRedirect(request.getContextPath() + "/");
        } catch (RuntimeException e) {
            sendError(response, e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void forwardWithError(HttpServletRequest req, HttpServletResponse resp, String error)
            throws ServletException, IOException {
        req.setAttribute("error", error);
        req.getRequestDispatcher("/").forward(req, resp);
    }

    private void sendError(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.setContentType("application/json");
        resp.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
