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
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

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
        System.out.println("✅ ManageGroupServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        if (!"ADMIN".equals(user.getRole().toString())) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        String groupId = req.getParameter("id");

        if (groupId == null || groupId.isEmpty()) {
            session.setAttribute("errorMessage", "ID группы не указан");
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }

        Optional<Group> groupOpt = groupDAO.findById(groupId);

        if (groupOpt.isEmpty()) {
            session.setAttribute("errorMessage", "Группа не найдена");
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }

        Group group = groupOpt.get();
        List<Student> allStudents = studentDAO.getItems();
        List<Teacher> allTeachers = teacherDAO.getItems();

        req.setAttribute("group", group);
        req.setAttribute("students", allStudents);
        req.setAttribute("teachers", allTeachers);

        req.getRequestDispatcher("/manage-group.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // ОТЛАДКА: Выводим ВСЕ параметры запроса
        System.out.println("========================================");
        System.out.println("📝 ALL REQUEST PARAMETERS:");
        Enumeration<String> paramNames = req.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = req.getParameter(paramName);
            System.out.println("  " + paramName + " = '" + paramValue + "' (length: " + (paramValue != null ? paramValue.length() : 0) + ")");
        }
        System.out.println("========================================");

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        if (!"ADMIN".equals(user.getRole().toString())) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        String action = req.getParameter("action");
        String groupId = req.getParameter("groupId");

        System.out.println("📝 ManageGroup action: " + action + ", groupId: " + groupId);

        if (groupId == null || action == null) {
            session.setAttribute("errorMessage", "Неверные параметры запроса");
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }

        Optional<Group> groupOpt = groupDAO.findById(groupId);

        if (groupOpt.isEmpty()) {
            session.setAttribute("errorMessage", "Группа не найдена");
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
                    session.setAttribute("errorMessage", "Неизвестное действие: " + action);
                    System.err.println("❌ Unknown action: " + action);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error in ManageGroupServlet: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "Ошибка при выполнении операции: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/manage-group?id=" + groupId);
    }

    private void handleAddStudent(HttpServletRequest request, HttpSession session, String groupId) {
        String studentId = request.getParameter("studentId");

        if (studentId == null || studentId.isEmpty()) {
            session.setAttribute("errorMessage", "ID студента не указан");
            return;
        }

        System.out.println("📝 Adding student " + studentId + " to group " + groupId);

        Optional<Student> studentOpt = studentDAO.findById(studentId);

        if (studentOpt.isEmpty()) {
            session.setAttribute("errorMessage", "Студент не найден");
            return;
        }

        Student student = studentOpt.get();

        // Если студент уже в другой группе, удалить его оттуда
        if (student.getGroupId() != null && !student.getGroupId().isEmpty()) {
            String oldGroupId = student.getGroupId();
            groupDAO.removeStudentFromGroup(oldGroupId, studentId);
            System.out.println("📝 Removed student from old group: " + oldGroupId);
        }

        // Добавить в новую группу
        boolean added = groupDAO.addStudentToGroup(groupId, studentId);

        if (added) {
            student.setGroupId(groupId);
            studentDAO.save(student);
            session.setAttribute("successMessage", "Студент успешно добавлен в группу");
            System.out.println("✅ Student added successfully");
        } else {
            session.setAttribute("errorMessage", "Не удалось добавить студента в группу");
            System.err.println("❌ Failed to add student to group");
        }
    }

    private void handleRemoveStudent(HttpServletRequest request, HttpSession session, String groupId) {
        String studentId = request.getParameter("studentId");

        if (studentId == null || studentId.isEmpty()) {
            session.setAttribute("errorMessage", "ID студента не указан");
            return;
        }

        System.out.println("📝 Removing student " + studentId + " from group " + groupId);

        Optional<Student> studentOpt = studentDAO.findById(studentId);

        if (studentOpt.isEmpty()) {
            session.setAttribute("errorMessage", "Студент не найден");
            return;
        }

        Student student = studentOpt.get();

        // Удалить из группы
        boolean removed = groupDAO.removeStudentFromGroup(groupId, studentId);

        if (removed) {
            student.setGroupId(null);
            studentDAO.save(student);
            session.setAttribute("successMessage", "Студент удалён из группы");
            System.out.println("✅ Student removed successfully");
        } else {
            session.setAttribute("errorMessage", "Не удалось удалить студента из группы");
            System.err.println("❌ Failed to remove student from group");
        }
    }

    private void handleAddTeacher(HttpServletRequest request, HttpSession session, String groupId) {
        String teacherId = request.getParameter("teacherId");

        if (teacherId == null || teacherId.isEmpty()) {
            session.setAttribute("errorMessage", "ID преподавателя не указан");
            return;
        }

        System.out.println("📝 Adding teacher " + teacherId + " to group " + groupId);

        Optional<Teacher> teacherOpt = teacherDAO.findById(teacherId);

        if (teacherOpt.isEmpty()) {
            session.setAttribute("errorMessage", "Преподаватель не найден");
            return;
        }

        Teacher teacher = teacherOpt.get();

        // Добавить группу к преподавателю
        boolean addedToGroup = groupDAO.addTeacherToGroup(groupId, teacherId);

        if (addedToGroup) {
            // Обновить список групп у преподавателя
            if (teacher.getGroupID() == null) {
                teacher.setGroupID(new java.util.ArrayList<>());
            }

            if (!teacher.getGroupID().contains(groupId)) {
                teacher.getGroupID().add(groupId);
                teacherDAO.save(teacher);
            }

            session.setAttribute("successMessage", "Преподаватель успешно добавлен в группу");
            System.out.println("✅ Teacher added successfully");
        } else {
            session.setAttribute("errorMessage", "Не удалось добавить преподавателя в группу");
            System.err.println("❌ Failed to add teacher to group");
        }
    }

    private void handleRemoveTeacher(HttpServletRequest request, HttpSession session, String groupId) {
        System.out.println("========================================");
        System.out.println("🔍 REMOVE TEACHER - START");

        String teacherId = request.getParameter("teacherId");

        System.out.println(teacherId);

        System.out.println("🔍 Group ID: '" + groupId + "'");
        System.out.println("🔍 Teacher ID from parameter: '" + teacherId + "'");
        System.out.println("🔍 Teacher ID is null: " + (teacherId == null));
        System.out.println("🔍 Teacher ID is empty: " + (teacherId != null && teacherId.isEmpty()));

        if (teacherId == null || teacherId.isEmpty()) {
            String errorMsg = "ID преподавателя не указан (teacherId = " + teacherId + ")";
            session.setAttribute("errorMessage", errorMsg);
            System.err.println("❌ " + errorMsg);
            System.out.println("========================================");
            return;
        }

        // Проверяем группу
        Optional<Group> groupOpt = groupDAO.findById(groupId);
        if (groupOpt.isEmpty()) {
            session.setAttribute("errorMessage", "Группа не найдена");
            System.err.println("❌ Group not found: " + groupId);
            System.out.println("========================================");
            return;
        }

        Group group = groupOpt.get();
        System.out.println("🔍 Group found: " + group.getGroupName());
        System.out.println("🔍 Teachers in group BEFORE: " + group.getTeacherIDs());

        Optional<Teacher> teacherOpt = teacherDAO.findById(teacherId);

        if (teacherOpt.isEmpty()) {
            session.setAttribute("errorMessage", "Преподаватель не найден с ID: " + teacherId);
            System.err.println("❌ Teacher not found: " + teacherId);
            System.out.println("========================================");
            return;
        }

        Teacher teacher = teacherOpt.get();
        System.out.println("🔍 Teacher found: " + teacher.getFullName() + " (ID: " + teacher.getId() + ")");
        System.out.println("🔍 Teacher's groups BEFORE: " + teacher.getGroupID());

        // Удалить из группы
        boolean removedFromGroup = groupDAO.removeTeacherFromGroup(groupId, teacherId);

        System.out.println("🔍 removeTeacherFromGroup result: " + removedFromGroup);

        if (removedFromGroup) {
            // Обновить список групп у преподавателя
            if (teacher.getGroupID() != null) {
                System.out.println("🔍 Updating teacher's group list...");
                boolean removedFromTeacher = false;
                List<String> teacherGroups = teacher.getGroupID();

                for (int i = 0; i < teacherGroups.size(); i++) {
                    if (teacherGroups.get(i).equals(groupId)) {
                        teacherGroups.remove(i);
                        removedFromTeacher = true;
                        break;
                    }
                }

                System.out.println("🔍 Removed from teacher's list: " + removedFromTeacher);
                System.out.println("🔍 Teacher's groups AFTER: " + teacher.getGroupID());

                teacherDAO.save(teacher);
                System.out.println("🔍 Teacher saved");
            }

            // Проверяем финальное состояние
            Group updatedGroup = groupDAO.findById(groupId).get();
            System.out.println("🔍 Teachers in group AFTER: " + updatedGroup.getTeacherIDs());

            session.setAttribute("successMessage", "Преподаватель удалён из группы");
            System.out.println("✅ REMOVE TEACHER - SUCCESS");
        } else {
            session.setAttribute("errorMessage", "Не удалось удалить преподавателя из группы");
            System.err.println("❌ REMOVE TEACHER - FAILED");
        }

        System.out.println("========================================");
    }
}
