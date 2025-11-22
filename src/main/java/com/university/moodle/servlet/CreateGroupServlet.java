package com.university.moodle.servlet;

import com.university.moodle.dao.GroupDAO;
import com.university.moodle.dao.TeacherDAO;
import com.university.moodle.model.Group;
import com.university.moodle.model.Teacher;
import com.university.moodle.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.university.moodle.enums.UserRole.ADMIN;

@WebServlet("/admin/create-group")
public class CreateGroupServlet extends HttpServlet {
    private GroupDAO groupDAO;
    private TeacherDAO teacherDAO;

    @Override
    public void init() throws ServletException {
        groupDAO = GroupDAO.getInstance();
        teacherDAO = TeacherDAO.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (optimizeSession(req, resp)) return;

        List<Teacher> teachers;
        try {
            teachers = teacherDAO.getItems();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        req.setAttribute("teachers", teachers);

        req.getRequestDispatcher("/admin/create-group.jsp").forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (optimizeSession(req, resp)) return;

        String id = UUID.randomUUID().toString();
        String groupName = req.getParameter("groupName");
        String description = req.getParameter("description");
        String teacherId = req.getParameter("teacherId");

        System.out.println(id);
        System.out.println(groupName);
        System.out.println(description);

        if (groupName == null || groupName.trim().isEmpty()) {
            req.setAttribute("error", "Group Name cannot be empty");

            try {
                optimizeGroupQueries(req, resp, id, groupName, description, teacherId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            if (groupDAO.existsByGroupName(groupName != null ? groupName.trim() : "")) {
                req.setAttribute("error", "Group Name already exists");
                optimizeGroupQueries(req, resp, id, groupName, description, teacherId);
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Group group = new Group();

        group.setId(id);
        group.setGroupName(groupName != null ? groupName.trim() : "");
        group.setDescription(description != null ? description.trim() : "");

        if (teacherId != null && !teacherId.isEmpty()) {
            group.getTeacherIDs().add(teacherId);

            try {
                teacherDAO.findById(teacherId).ifPresent(teacher -> {
                    if (teacher.getGroupID() == null) {
                        teacher.setGroupID(new ArrayList<>());
                    }

                    teacher.getGroupID().add(group.getId());

                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        try {
            groupDAO.save(group);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        resp.sendRedirect(req.getContextPath() + "/admin/dashboard?success=group_created");
    }

    private boolean optimizeSession(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return true;
        }

        User user = (User) session.getAttribute("user");

        if (!ADMIN.equals(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return true;
        }

        return false;
    }

    private void optimizeGroupQueries(HttpServletRequest req, HttpServletResponse resp, String id, String groupName, String description, String teacherId) throws SQLException, ServletException, IOException {
        req.setAttribute("id", id);
        req.setAttribute("groupName", groupName);
        req.setAttribute("description", description);
        req.setAttribute("teacherId", teacherId);

        System.out.println(id);
        System.out.println(groupName);
        System.out.println(description);
        System.out.println(teacherId);

        List<Teacher> teachers = teacherDAO.getItems();
        req.setAttribute("teachers", teachers);

        req.getRequestDispatcher("admin/create-group.jsp").forward(req, resp);
    }
}
