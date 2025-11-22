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

import static com.university.moodle.enums.UserRole.ADMIN;

@WebServlet("/admin/create-teacher")
public class AdminCreateTeacherServlet extends HttpServlet {
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        User currentUser = (User) session.getAttribute("user");

        if (!currentUser.getRole().equals(ADMIN)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        req.getRequestDispatcher("/admin/create-teacher.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        User currentUser = (User) session.getAttribute("user");

        if (!currentUser.getRole().equals(ADMIN)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String fullName = req.getParameter("fullName");
        String specialization = req.getParameter("specialization");

        try {
            authService.createTeacher(email, password, fullName, specialization, currentUser);

            resp.sendRedirect(req.getContextPath() + "/admin/dashboard?success=teacher_created");
        }catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("email", email);
            req.setAttribute("fullName", fullName);
            req.setAttribute("specialization", specialization);

            req.getRequestDispatcher("/admin/create-teacher.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
