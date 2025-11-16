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
import java.util.ArrayList;
import java.util.List;

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
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        if (!"ADMIN".equals(user.getRole().toString())) {
            resp.sendRedirect(req.getContextPath() + "/profile/");
            return;
        }

        List<Teacher> teachers = teacherDAO.getItems();
        req.setAttribute("teachers", teachers);

        req.getRequestDispatcher("/create-group.jsp").forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        if (!"ADMIN".equals(user.getRole().toString())) {
            resp.sendRedirect(req.getContextPath() + "/profile/");
            return;
        }

        String groupName = req.getParameter("groupName");
        String description = req.getParameter("description");
        String teacherId = req.getParameter("teacherId");

        if (groupName == null || groupName.trim().isEmpty()) {
            req.setAttribute("error", "Group Name cannot be empty");
            req.setAttribute("groupName", groupName);
            req.setAttribute("description", description);
            req.setAttribute("teacherId", teacherId);

            List<Teacher> teachers = teacherDAO.getItems();
            req.setAttribute("teachers", teachers);

            req.getRequestDispatcher("create-group.jsp").forward(req, resp);

            return;
        }

        if (groupDAO.existsByGroupName(groupName.trim())) {
            req.setAttribute("error", "Group Name already exists");
            req.setAttribute("groupName", groupName);
            req.setAttribute("description", description);
            req.setAttribute("teacherId", teacherId);

            List<Teacher> teachers = teacherDAO.getItems();
            req.setAttribute("teachers", teachers);

            req.getRequestDispatcher("create-group.jsp").forward(req, resp);
            return;
        }

        Group group = new Group();

        group.setGroupName(groupName.trim());
        group.setDescription(description != null ? description.trim() : "");

        if (teacherId != null && !teacherId.isEmpty()) {
            group.getTeacherIDs().add(teacherId);

            teacherDAO.findById(teacherId).ifPresent(teacher -> {
                if (teacher.getGroupID() == null) {
                    teacher.setGroupID(new ArrayList<>());
                }

                teacher.getGroupID().add(group.getId());

            });

        }

        groupDAO.save(group);

        resp.sendRedirect(req.getContextPath() + "/admin/dashboard?success=group_created");
    }
}
