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
import java.util.ArrayList;
import java.util.UUID;

@WebServlet("/teacher/upload-assignment")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,       // 10MB
        maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class UploadAssignmentServlet extends HttpServlet {
    public static final String UPLOAD_DIR =
            System.getProperty("user.home") + File.separator + "moodle_uploads";

    private final AssignmentDAO assignmentDAO = AssignmentDAO.getInstance();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        String teacherId = (String) session.getAttribute("userId");

        try {
            String groupId = request.getParameter("groupId");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String deadlineStr = request.getParameter("deadline");
            String maxScoreStr = request.getParameter("maxScore");

            // Валидация обязательных полей
            if (isEmpty(groupId) || isEmpty(title)) {
                throw new Exception("Title and group are required");
            }

            title = title.trim();
            if (title.isEmpty()) throw new Exception("Title cannot be empty");

            // Дедлайн — НЕ обязательный
            LocalDateTime deadline = null;
            if (!isEmpty(deadlineStr)) {
                try {
                    deadline = LocalDateTime.parse(deadlineStr); // datetime-local → ISO формат
                    if (deadline.isBefore(LocalDateTime.now())) {
                        throw new Exception("Deadline must be in the future");
                    }
                } catch (Exception e) {
                    throw new Exception("Invalid deadline format. Use correct date and time.");
                }
            }

            // Макс. балл
            int maxScore = 100;
            if (!isEmpty(maxScoreStr)) {
                try {
                    maxScore = Integer.parseInt(maxScoreStr);
                    if (maxScore < 1 || maxScore > 10000) throw new Exception();
                } catch (Exception e) {
                    throw new Exception("Invalid max score");
                }
            }

            // === Файл — ОПЦИОНАЛЬНЫЙ ===
            String filePath = null;
            Part filePart = request.getPart("file");
            if (filePart != null && filePart.getSize() > 0) {
                String originalName = getSubmittedFileName(filePart);
                if (!isEmpty(originalName)) {
                    String ext = "";
                    int i = originalName.lastIndexOf('.');
                    if (i > 0) ext = originalName.substring(i).toLowerCase();

                    if (!isAllowedExtension(ext)) {
                        throw new Exception("File type not allowed. Allowed: pdf, docx, zip, txt");
                    }

                    // создаём папку, если её нет
                    File uploadDir = new File(UPLOAD_DIR);
                    if (!uploadDir.exists()) uploadDir.mkdirs();

                    // уникальное имя файла
                    String uniqueFileName = UUID.randomUUID().toString() + ext;
                    String fullPath = UPLOAD_DIR + File.separator + uniqueFileName;

                    // сохраняем файл
                    filePart.write(fullPath);

                    // в базе сохраняем только имя файла
                    filePath = uniqueFileName;

                    System.out.println("SAVE >>> " + fullPath);
                }
            }

            // Создаём задание
            Assignment assignment = Assignment.builder()
                    .id(UUID.randomUUID().toString())
                    .title(title)
                    .description(description != null ? description.trim() : "")
                    .teacherId(teacherId)
                    .groupId(groupId)
                    .deadline(deadline)
                    .maxScore(maxScore)
                    .createdAt(LocalDateTime.now())
                    .submissionID(new ArrayList<>())
                    .filePath(filePath) // может быть null — это нормально
                    .build();

            // Сохраняем в базу
            assignmentDAO.save(assignment);

            session.setAttribute("successMessage", "Assignment created successfully!");
            response.sendRedirect(request.getContextPath() + "/teacher/group?id=" + groupId);

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Failed to create assignment: " + e.getMessage());
            String groupId = request.getParameter("groupId");
            String redirect = "/teacher/create-assignment";
            if (!isEmpty(groupId)) {
                redirect = "/teacher/group?id=" + groupId;
            }
            response.sendRedirect(request.getContextPath() + redirect);
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String getSubmittedFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp != null) {
            for (String cd : contentDisp.split(";")) {
                if (cd.trim().startsWith("filename")) {
                    return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                }
            }
        }
        return null;
    }

    private boolean isAllowedExtension(String extension) {
        return extension.equals(".pdf") ||
                extension.equals(".docx") ||
                extension.equals(".zip") ||
                extension.equals(".txt");
    }
}
