package com.university.moodle.servlet;

import com.university.moodle.dao.GroupDAO;
import com.university.moodle.enums.UserRole;
import com.university.moodle.model.Group;
import com.university.moodle.model.Student;
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
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private AuthService authService;
    private GroupDAO groupDAO;

    @Override
    public void init() {
        authService = AuthService.getInstance();
        groupDAO = GroupDAO.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("user");

        if (currentUser.getRole() != UserRole.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admins only");
            return;
        }

        List<Group> groups = groupDAO.getItems();
        req.setAttribute("groups", groups);
        req.setAttribute("groupCount", groups.size());

        // ←←←←← ОТЛАДКА ←←←←←
        System.out.println("ОТЛАДКА АДМИНКИ: группы и задания");
        for (Group g : groups) {
            int count = g.getAssignmentIDs() != null ? g.getAssignmentIDs().size() : 0;
            System.out.println("   • " + g.getGroupName() + " → заданий: " + count);
        }
        // ←←←←← КОНЕЦ ←←←←←

        try {
            List<Teacher> teachers = authService.getAllTeachers(currentUser);
            List<Student> students = authService.getAllStudents(currentUser);

            req.setAttribute("teacherCount", teachers.size());
            req.setAttribute("studentCount", students.size());
            req.setAttribute("teachers", teachers);
            req.setAttribute("students", students);
            req.setAttribute("currentUser", currentUser);

            req.getRequestDispatcher("/admin-dashboard.jsp").forward(req, resp);
        } catch (RuntimeException e) {
            System.out.println("Error in AdminDashboardServlet: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
