package com.university.moodle.servlet;

import com.university.moodle.dao.AssignmentDAO;
import com.university.moodle.model.Assignment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

@WebServlet("/teacher/upload-assignment")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,       // 10MB
        maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class UploadAssignmentServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "uploads/assignments";
    private final AssignmentDAO assignmentDAO = AssignmentDAO.getInstance();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        try {
            // Проверка авторизации
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            String teacherId = (String) session.getAttribute("userId");
            String role = (String) session.getAttribute("role");

            // Проверка роли
            if (!"TEACHER".equals(role)) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            // Получение параметров формы
            String groupId = request.getParameter("groupId");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String deadlineStr = request.getParameter("deadline");

            // Валидация
            if (groupId == null || groupId.isEmpty() ||
                    title == null || title.trim().isEmpty() ||
                    deadlineStr == null || deadlineStr.isEmpty()) {
                response.sendRedirect(request.getContextPath() +
                        "/teacher/group?id=" + groupId + "&error=Required fields are missing");
                return;
            }

            // Парсинг deadline
            LocalDateTime deadline;
            try {
                deadline = LocalDateTime.parse(deadlineStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() +
                        "/teacher/group?id=" + groupId + "&error=Invalid deadline format");
                return;
            }

            // Проверка что deadline в будущем
            if (deadline.isBefore(LocalDateTime.now())) {
                response.sendRedirect(request.getContextPath() +
                        "/teacher/group?id=" + groupId + "&error=Deadline must be in the future");
                return;
            }

            // Загрузка файла
            Part filePart = request.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                response.sendRedirect(request.getContextPath() +
                        "/teacher/group?id=" + groupId + "&error=File is required");
                return;
            }

            String fileName = getFileName(filePart);
            if (fileName == null || fileName.isEmpty()) {
                response.sendRedirect(request.getContextPath() +
                        "/teacher/group?id=" + groupId + "&error=Invalid file");
                return;
            }

            // Проверка расширения файла
            String fileExtension = "";
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex > 0) {
                fileExtension = fileName.substring(dotIndex).toLowerCase();
            }

            if (!isAllowedExtension(fileExtension)) {
                response.sendRedirect(request.getContextPath() +
                        "/teacher/group?id=" + groupId + "&error=File type not allowed");
                return;
            }

            // Создание директории если не существует
            String applicationPath = request.getServletContext().getRealPath("");
            String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadFilePath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // Создаем директорию, если не существует
            }

            // Генерация уникального имени файла
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            String fullFilePath = uploadFilePath + File.separator + uniqueFileName;

            // Логирование пути сохраненного файла
            System.out.println("Saving file to: " + fullFilePath);

            // Сохранение файла
            filePart.write(fullFilePath);

            // Создание объекта Assignment
            Assignment assignment = Assignment.builder()
                    .id(null) // ID будет установлен в DAO
                    .title(title.trim())
                    .description(description != null ? description.trim() : "")
                    .teacherId(teacherId)
                    .groupId(groupId)
                    .deadline(deadline)
                    .maxScore(100) // По умолчанию
                    .createdAt(LocalDateTime.now())
                    .submissionID(new ArrayList<>())
                    .filePath(UPLOAD_DIR + "/" + uniqueFileName)
                    .build();

            // Сохранение в DAO
            assignmentDAO.save(assignment);

            // Редирект обратно на страницу группы с успехом
            response.sendRedirect(request.getContextPath() +
                    "/teacher/group?id=" + groupId + "&success=Assignment uploaded successfully");

        } catch (Exception e) {
            e.printStackTrace();
            String groupId = request.getParameter("groupId");
            response.sendRedirect(request.getContextPath() +
                    "/teacher/group?id=" + (groupId != null ? groupId : "") +
                    "&error=Upload failed: " + e.getMessage());
        }
    }

    /**
     * Извлекает имя файла из Part header
     */
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;
        }

        String[] items = contentDisposition.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                String filename = item.substring(item.indexOf("=") + 1).trim();
                return filename.replace("\"", "");
            }
        }
        return null;
    }

    /**
     * Проверяет допустимость расширения файла
     */
    private boolean isAllowedExtension(String extension) {
        return extension.equals(".pdf") ||
                extension.equals(".docx") ||
                extension.equals(".zip") ||
                extension.equals(".txt");
    }
}
