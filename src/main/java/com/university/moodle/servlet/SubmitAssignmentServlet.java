package com.university.moodle.servlet;

import com.university.moodle.dao.AssignmentDAO;
import com.university.moodle.dao.SubmissionDAO;
import com.university.moodle.enums.SubmissionStatus;
import com.university.moodle.model.Assignment;
import com.university.moodle.model.Student;
import com.university.moodle.model.Submission;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@WebServlet("/student/submit")
@MultipartConfig(
        maxFileSize = 10 * 1024 * 1024,      // 10 MB
        maxRequestSize = 15 * 1024 * 1024     // 15 MB
)
public class SubmitAssignmentServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads/assignments";
    private SubmissionDAO submissionDAO;
    private AssignmentDAO assignmentDAO;

    @Override
    public void init() {
        submissionDAO = SubmissionDAO.getInstance();
        assignmentDAO = AssignmentDAO.getInstance();

        // Создаём директорию для загрузок, если её нет
        createUploadDirectory();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        Student student = (Student) session.getAttribute("user");
        String assignmentId = request.getParameter("assignmentId");
        String content = request.getParameter("content");

        // Валидация
        if (assignmentId == null || assignmentId.isBlank()) {
            sendError(request, response, "Задание не найдено");
            return;
        }

        Optional<Assignment> assignmentOpt = assignmentDAO.getById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            sendError(request, response, "Задание не найдено");
            return;
        }

        Assignment assignment = assignmentOpt.get();

        // Проверка дедлайна
        if (LocalDateTime.now().isAfter(assignment.getDeadline())) {
            sendError(request, response, "Срок сдачи истёк");
            return;
        }

        // Проверяем, не отправлял ли студент уже ответ
        Optional<Submission> existingSubmission = submissionDAO.findByAssignmentAndStudent(
                assignmentId,
                student.getId()
        );

        Submission submission;
        if (existingSubmission.isPresent()) {
            // Обновляем существующий ответ
            submission = existingSubmission.get();
            submission.setContent(content);
            submission.setSubmittedAt(LocalDateTime.now());
            submission.setStatus(SubmissionStatus.PENDING);
        } else {
            // Создаём новый ответ
            submission = new Submission();
            submission.setAssignmentId(assignmentId);
            submission.setStudentId(student.getId());
            submission.setContent(content);
        }

        // Обработка файла
        Part filePart = request.getPart("file");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = handleFileUpload(filePart, student.getId(), assignmentId);
            if (fileName != null) {
                submission.setFileUrl(fileName);
            }
        }

        // Сохраняем в базу
        if (existingSubmission.isPresent()) {
            submissionDAO.update(submission);
        } else {
            submissionDAO.create(submission);
        }

        // Перенаправление с сообщением об успехе
        session.setAttribute("successMessage", "Задание успешно отправлено!");
        response.sendRedirect(request.getContextPath() + "/student/assignments");
    }

    private String handleFileUpload(Part filePart, String studentId, String assignmentId) {
        try {
            String originalFileName = getFileName(filePart);
            if (originalFileName == null || originalFileName.isEmpty()) {
                return null;
            }

            // Генерируем уникальное имя файла
            String fileExtension = getFileExtension(originalFileName);
            String uniqueFileName = String.format("%s_%s_%s%s",
                    studentId,
                    assignmentId,
                    UUID.randomUUID().toString().substring(0, 8),
                    fileExtension
            );

            // Путь для сохранения
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Path filePath = Paths.get(uploadPath, uniqueFileName);

            // Сохраняем файл
            Files.copy(filePart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return UPLOAD_DIR + "/" + uniqueFileName;

        } catch (IOException e) {
            System.err.println("Ошибка при загрузке файла: " + e.getMessage());
            return null;
        }
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition != null) {
            for (String token : contentDisposition.split(";")) {
                if (token.trim().startsWith("filename")) {
                    return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                }
            }
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }

    private void createUploadDirectory() {
        try {
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
                System.out.println("Создана директория для загрузок: " + uploadPath);
            }
        } catch (Exception e) {
            System.err.println("Не удалось создать директорию для загрузок: " + e.getMessage());
        }
    }

    private void sendError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.setAttribute("errorMessage", message);
        response.sendRedirect(request.getContextPath() + "/student/assignments");
    }
}
