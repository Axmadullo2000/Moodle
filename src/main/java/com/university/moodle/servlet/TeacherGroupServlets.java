package com.university.moodle.servlet;

import com.university.moodle.dao.AssignmentDAO;
import com.university.moodle.dao.GroupDAO;
import com.university.moodle.dao.StudentDAO;
import com.university.moodle.model.Assignment;
import com.university.moodle.model.Group;
import com.university.moodle.model.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/teacher/group")
public class TeacherGroupServlets extends HttpServlet {

    private final AssignmentDAO assignmentDAO = AssignmentDAO.getInstance();
    private final StudentDAO studentDAO = StudentDAO.getInstance();
    private final GroupDAO groupDAO = GroupDAO.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Проверка авторизации
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        String teacherId = (String) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (!"TEACHER".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        // Получение параметров
        String groupId = req.getParameter("id");

        if (groupId == null || groupId.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/teacher/dashboard");
            return;
        }

        // Получаем группу
        Optional<Group> groupOpt = groupDAO.findById(groupId);
        if (!groupOpt.isPresent()) {
            resp.sendRedirect(req.getContextPath() + "/teacher/dashboard?error=Group not found");
            return;
        }

        Group group = groupOpt.get();

        // Проверяем, что учитель преподаёт в этой группе
        if (group.getTeacherIDs() == null || !group.getTeacherIDs().contains(teacherId)) {
            resp.sendRedirect(req.getContextPath() + "/teacher/dashboard?error=Access denied");
            return;
        }

        // Получаем список заданий для этой группы
        List<Assignment> assignments = assignmentDAO.findByGroupId(groupId);

        // Получаем студентов этой группы
        List<Student> students = new ArrayList<>();
        if (group.getStudentIDs() != null && !group.getStudentIDs().isEmpty()) {
            students = studentDAO.getItems().stream()
                    .filter(student -> student.getGroupId() != null &&
                            student.getGroupId().equals(groupId))
                    .collect(Collectors.toList());
        }

        // Устанавливаем атрибуты для JSP
        req.setAttribute("groupId", groupId);
        req.setAttribute("groupName", group.getGroupName());
        req.setAttribute("assignments", assignments != null ? assignments : new ArrayList<>());
        req.setAttribute("students", students);
        req.setAttribute("studentsCount", students.size());

        // Обработка сообщений (success/error)
        String success = req.getParameter("success");
        String error = req.getParameter("error");

        if (success != null) {
            req.setAttribute("successMessage", success);
        }
        if (error != null) {
            req.setAttribute("errorMessage", error);
        }

        req.getRequestDispatcher("/group.jsp").forward(req, resp);
    }
}
