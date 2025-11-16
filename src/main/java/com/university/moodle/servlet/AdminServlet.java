package com.university.moodle.servlet;

import com.university.moodle.model.Teacher;
import com.university.moodle.model.User;
import com.university.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin/")
public class AdminServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init() {
        authService = AuthService.getInstance();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null ||session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("user");

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String specialization = request.getParameter("specialization");

        try {
            Teacher teacher = authService.createTeacher(email, password, fullName, specialization, currentUser);

            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\": true, \"message\": \"Teacher created successfully\", \"teacherId\": \"" + teacher.getId() + "\"}");
        }catch (RuntimeException e) {
            response.setContentType("application/json; charset=UTF-8");
            response.setStatus((HttpServletResponse.SC_BAD_REQUEST));
            response.getWriter().write("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }




}
