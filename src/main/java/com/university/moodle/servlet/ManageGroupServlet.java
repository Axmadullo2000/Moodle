package com.university.moodle.servlet;

import com.university.moodle.dao.GroupDAO;
import com.university.moodle.dao.StudentDAO;
import com.university.moodle.dao.TeacherDAO;
import com.university.moodle.model.Group;
import com.university.moodle.model.Student;
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
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import static com.university.moodle.enums.UserRole.ADMIN;

@WebServlet("/admin/manage-group")
public class ManageGroupServlet extends HttpServlet {
    private GroupDAO groupDAO;
    private StudentDAO studentDAO;
    private TeacherDAO teacherDAO;

    @Override
    public void init() throws ServletException {
        groupDAO = GroupDAO.getInstance();
        studentDAO = StudentDAO.getInstance();
        teacherDAO = TeacherDAO.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        System.out.println("Session: " + session.getAttribute("user"));

        User user = (User) session.getAttribute("user");

        if (!ADMIN.equals(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        System.out.println("User role -> " + user.getRole());

        String groupId = req.getParameter("id");

        System.out.println( "Group id -> " + groupId);

        if (groupId == null || groupId.isEmpty()) {
            session.setAttribute("errorMessage", "ID of group doesn't exist");
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }

        System.out.println("Group id -> " + groupId);

        Optional<Group> groupOpt = groupDAO.findById(groupId);

        if (groupOpt.isEmpty()) {
            session.setAttribute("errorMessage", "Group is not found");
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }

        Group group = groupOpt.get();

        System.out.println(group);

        List<Student> allStudents;
        try {
            allStudents = studentDAO.getItems();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        List<Teacher> allTeachers;
        try {
            allTeachers = teacherDAO.getItems();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        req.setAttribute("group", group);
        req.setAttribute("students", allStudents);
        req.setAttribute("teachers", allTeachers);

        req.getRequestDispatcher("/admin/manage-group.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // ОТЛАДКА: Выводим ВСЕ параметры запроса
        Enumeration<String> paramNames = req.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = req.getParameter(paramName);

            System.out.println(paramValue);
        }

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        User user = (User) session.getAttribute("user");

        if (!ADMIN.equals(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        String action = req.getParameter("action");
        String groupId = req.getParameter("groupId");

        if (groupId == null || action == null) {
            session.setAttribute("errorMessage", "Wrong params in query");
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }

        Optional<Group> groupOpt = groupDAO.findById(groupId);

        if (groupOpt.isEmpty()) {
            session.setAttribute("errorMessage", "Group is not found");
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }

        try {
            switch (action) {
                case "add-student" -> handleAddStudent(req, session, groupId);
                case "remove-student" -> handleRemoveStudent(req, session, groupId);
                case "add-teacher" -> handleAddTeacher(req, session, groupId);
                case "remove-teacher" -> handleRemoveTeacher(req, session, groupId);
                default -> {
                    session.setAttribute("errorMessage", "Unknown action: " + action);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Something goes wrong while the process: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/manage-group?id=" + groupId);
    }

    private void handleAddStudent(HttpServletRequest request, HttpSession session, String groupId) {
        String studentId = request.getParameter("studentId");

        if (studentId == null || studentId.isEmpty()) {
            session.setAttribute("errorMessage", "ID of student doesn't exist");
            return;
        }

        Optional<Student> studentOpt;
        try {
            studentOpt = studentDAO.findById(studentId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (studentOpt.isEmpty()) {
            session.setAttribute("errorMessage", "Student is not found");
            return;
        }

        Student student = studentOpt.get();

        if (student.getGroupId() != null && !student.getGroupId().isEmpty()) {
            String oldGroupId = student.getGroupId();
            groupDAO.removeStudentFromGroup(oldGroupId, studentId);
        }

        // Добавить в новую группу
        boolean added;
        added = groupDAO.addStudentToGroup(groupId, studentId);

        if (added) {
            student.setGroupId(groupId);

            try {
                studentDAO.save(student);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            session.setAttribute("successMessage", "Student is successfully added to the group");
        } else {
            session.setAttribute("errorMessage", "We cannot add student to the group");
        }
    }

    private void handleRemoveStudent(HttpServletRequest request, HttpSession session, String groupId) {
        String studentId = request.getParameter("studentId");

        if (studentId == null || studentId.isEmpty()) {
            session.setAttribute("errorMessage", "ID of student doesn't exist");
            return;
        }

        Optional<Student> studentOpt;
        try {
            studentOpt = studentDAO.findById(studentId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (studentOpt.isEmpty()) {
            session.setAttribute("errorMessage", "Student is not found");
            return;
        }

        Student student = studentOpt.get();

        // Удалить из группы
        boolean removed;
        removed = groupDAO.removeStudentFromGroup(groupId, studentId);

        if (removed) {
            student.setGroupId(null);
            try {
                studentDAO.save(student);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            session.setAttribute("successMessage", "Student is removed from the group");
        } else {
            session.setAttribute("errorMessage", "We cannot remove student from group");
        }
    }

    private void handleAddTeacher(HttpServletRequest request, HttpSession session, String groupId) {
        String teacherId = request.getParameter("teacherId");

        if (teacherId == null || teacherId.isEmpty()) {
            session.setAttribute("errorMessage", "ID of teacher doesn't exist");
            return;
        }

        Optional<Teacher> teacherOpt;
        try {
            teacherOpt = teacherDAO.findById(teacherId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (teacherOpt.isEmpty()) {
            session.setAttribute("errorMessage", "Teacher not found!");
            return;
        }

        Teacher teacher = teacherOpt.get();

        // Добавить группу к преподавателю
        groupDAO.addTeacherToGroup(groupId, teacherId);

        if (teacher.getGroupID() == null) {
            teacher.setGroupID(new ArrayList<>());
        }

        if (!teacher.getGroupID().contains(groupId)) {
            teacher.getGroupID().add(groupId);
            try {
                teacherDAO.save(teacher);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            session.setAttribute("successMessage", "Teacher is successfully added to the group!");
        }
    }

    private void handleRemoveTeacher(HttpServletRequest request, HttpSession session, String groupId) {
        String teacherId = request.getParameter("teacherId");

        if (teacherId == null || teacherId.isEmpty()) {
            String errorMsg = "ID of teacher doesn't exist (teacherId = " + teacherId + ")";
            session.setAttribute("errorMessage", errorMsg);
            return;
        }

        // Проверяем группу
        Optional<Group> groupOpt = groupDAO.findById(groupId);
        if (groupOpt.isEmpty()) {
            session.setAttribute("errorMessage", "Group is not found");
            return;
        }

        Group group = groupOpt.get();

        Optional<Teacher> teacherOpt;
        try {
            teacherOpt = teacherDAO.findById(teacherId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (teacherOpt.isEmpty()) {
            session.setAttribute("errorMessage", "Teacher with ID: %s is not found: ".formatted(teacherId));
            return;
        }

        Teacher teacher = teacherOpt.get();
        // Удалить из группы
        boolean removedFromGroup;
        removedFromGroup = groupDAO.removeTeacherFromGroup(groupId, teacherId);

        if (removedFromGroup) {
            // Обновить список групп у преподавателя
            if (teacher.getGroupID() != null) {

                List<String> teacherGroups = teacher.getGroupID();

                for (int i = 0; i < teacherGroups.size(); i++) {
                    if (teacherGroups.get(i).equals(groupId)) {
                        teacherGroups.remove(i);
                        break;
                    }
                }

                try {
                    teacherDAO.save(teacher);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            // Проверяем финальное состояние
            if (groupDAO.findById(groupId).isPresent()) {
                groupDAO.findById(groupId).get();
            }

            session.setAttribute("successMessage", "Teacher is removed from group");
        } else {
            session.setAttribute("errorMessage", "We cannot remove teacher from group");
        }
    }
}
