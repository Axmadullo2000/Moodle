package com.university.moodle.servlet;

import com.university.moodle.dao.GroupDAO;
import com.university.moodle.enums.UserRole;
import com.university.moodle.model.Group;
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

@WebServlet("/teacher/dashboard")
public class TeacherDashboardServlet extends HttpServlet {
    private GroupDAO groupDAO;
    private AuthService authService;

    @Override
    public void init() {
        groupDAO = GroupDAO.getInstance();
        authService = AuthService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        String userId = (String) session.getAttribute("userId");
        User user = authService.getUserById(userId);

        if (user == null || user.getRole() != UserRole.TEACHER) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        Teacher teacher = (Teacher) user;

        // Используем DAO, а не ServletContext!
        List<Group> teacherGroups = groupDAO.findByTeacherId(teacher.getId());

        req.setAttribute("groups", teacherGroups);
        req.setAttribute("currentUser", teacher);

        req.getRequestDispatcher("/teacher-dashboard.jsp").forward(req, resp);
    }
}

