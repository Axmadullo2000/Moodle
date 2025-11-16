package com.university.moodle.servlet;


import com.university.moodle.enums.UserRole;
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

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init() {
        authService = AuthService.getInstance();
        System.out.println("AuthService initialized in AuthServlet");
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
            sendError(response, "Invalid action", HttpServletResponse.SC_BAD_REQUEST);
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
            session.setAttribute("role", user.getRole().name());
            session.setAttribute("fullName", user.getFullName());

            // Перенаправление в зависимости от роли
            String redirectUrl = switch (user.getRole()) {
                case TEACHER -> "/teacher/dashboard";
                case STUDENT -> "/student/assignments";
                case ADMIN -> "/admin/dashboard";
                default -> "/";
            };

            response.sendRedirect(request.getContextPath() + redirectUrl);

        } catch (RuntimeException e) {
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
            sendError(response, "All fields are required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Student student = authService.registerStudent(email, password, fullName);
            System.out.println("Student registered: " + student.getEmail());

            // Автологин после регистрации
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", student.getId());
            session.setAttribute("role", UserRole.STUDENT.name());
            session.setAttribute("fullName", student.getFullName());

            response.sendRedirect(request.getContextPath() + "/student/assignments");

        } catch (RuntimeException e) {
            sendError(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // Утилиты
    private void forwardWithError(HttpServletRequest req, HttpServletResponse resp, String error)
            throws ServletException, IOException {
        req.setAttribute("error", error);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    private void sendError(HttpServletResponse resp, String message, int status) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
