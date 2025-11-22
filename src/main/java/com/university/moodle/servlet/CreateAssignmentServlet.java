package com.university.moodle.servlet;

import com.university.moodle.dao.AssignmentDAO;
import com.university.moodle.dao.GroupDAO;
import com.university.moodle.model.Assignment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;


@WebServlet("/teacher/create-assignment")
@MultipartConfig
public class CreateAssignmentServlet extends HttpServlet { ;
    // Папка для файлов учителя
    public static final String UPLOAD_DIR =
            System.getProperty("user.home") + File.separator + "moodle_uploads";

    private AssignmentDAO assignmentDAO;
    private GroupDAO groupDAO;

    @Override
    public void init() throws ServletException {
        assignmentDAO = AssignmentDAO.getInstance();
        groupDAO = GroupDAO.getInstance();

        try {
            // Создаём папку для хранения файлов, если её нет
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new ServletException("Не удалось создать папку для файлов", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("groups", groupDAO.getItems());
        } catch (Exception e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/teacher/create-assignment.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        String teacherId = (String) session.getAttribute("userId");

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String deadlineStr = req.getParameter("deadline");
        String maxScoreStr = req.getParameter("maxScore");
        String groupId = req.getParameter("groupId");

        if (title == null || title.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Название задания обязательно");
            resp.sendRedirect(req.getContextPath() + "/teacher/create-assignment");
            return;
        }

        String fileName = null;
        Part part = req.getPart("file");

        if (part != null && part.getSize() > 0) {
            String originalName = part.getSubmittedFileName();
            if (originalName != null && !originalName.isEmpty()) {
                // Генерируем уникальное имя файла
                fileName = UUID.randomUUID() + "_" + originalName;

                // Сохраняем файл в папку moodle_uploads
                Path savePath = Paths.get(UPLOAD_DIR, fileName);
                try (InputStream in = part.getInputStream()) {
                    Files.copy(in, savePath);
                }
            }
        }

        Assignment assignment = Assignment.builder()
                .id(UUID.randomUUID().toString())
                .title(title.trim())
                .description(description != null ? description.trim() : "")
                .teacherId(teacherId)
                .groupId(groupId)
                .maxScore(maxScoreStr != null && !maxScoreStr.isEmpty() ? Integer.parseInt(maxScoreStr) : 100)
                .deadline(deadlineStr != null && !deadlineStr.isEmpty() ? LocalDateTime.parse(deadlineStr) : null)
                .filePath(fileName)  // хранится только имя файла
                .submissionID(new ArrayList<>())
                .build();

        try {
            assignmentDAO.save(assignment);
            if (groupId != null && !groupId.isEmpty()) {
                groupDAO.addAssignmentToGroup(groupId, assignment.getId());
            }
            session.setAttribute("successMessage", "Assignment created successfully!");
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Ошибка при создании задания!");
            e.printStackTrace();
        }

        resp.sendRedirect(req.getContextPath() + "/teacher/dashboard");
    }
}
