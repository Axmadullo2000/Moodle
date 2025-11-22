package com.university.moodle.servlet;

import com.university.moodle.dao.GroupDAO;
import com.university.moodle.model.Group;
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

import static com.university.moodle.enums.UserRole.TEACHER;

@WebServlet("/teacher/dashboard")
public class TeacherDashboardServlet extends HttpServlet {
    private GroupDAO groupDAO;
    private AuthService authService;

    @Override
    public void init() {
        groupDAO = GroupDAO.getInstance();

        try {
            authService = AuthService.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        User currentUser = (User) session.getAttribute("user");

        Teacher teacher = (Teacher) currentUser;

        List<Group> teacherGroups = groupDAO.findByTeacherId(teacher.getId());

        req.setAttribute("groups", teacherGroups);
        req.setAttribute("currentUser", teacher);

        req.getRequestDispatcher("/teacher/teacher-dashboard.jsp").forward(req, resp);
    }

    /*@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        String userId = (String) session.getAttribute("userId");
        User user;
        try {
            user = authService.getUserById(userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (user == null || !user.getRole().equals(TEACHER)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        Teacher teacher = (Teacher) user;

        List<Group> teacherGroups;

        teacherGroups = groupDAO.findByTeacherId(teacher.getId());

        req.setAttribute("groups", teacherGroups);
        req.setAttribute("currentUser", teacher);

        req.getRequestDispatcher("/teacher/teacher-dashboard.jsp").forward(req, resp);
    }*/
}
