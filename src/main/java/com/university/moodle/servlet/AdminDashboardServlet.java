package com.university.moodle.servlet;

import com.university.moodle.dao.GroupDAO;
import com.university.moodle.model.Group;
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
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private AuthService authService;
    private GroupDAO groupDAO;

    @Override
    public void init() {
        try {
            authService = AuthService.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        groupDAO = GroupDAO.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        User currentUser = (User) session.getAttribute("user");

        List<Group> groups;

        try {
            groups = groupDAO.getItems();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        req.setAttribute("groups", groups);
        req.setAttribute("groupCount", groups.size());

        try {
            List<Teacher> teachers = authService.getAllTeachers(currentUser);
            List<Student> students = authService.getAllStudents(currentUser);

            req.setAttribute("teacherCount", teachers.size());
            req.setAttribute("studentCount", students.size());
            req.setAttribute("teachers", teachers);
            req.setAttribute("students", students);
            req.setAttribute("currentUser", currentUser);

            req.getRequestDispatcher("/admin/admin-dashboard.jsp").forward(req, resp);
        } catch (RuntimeException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
