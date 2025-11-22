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
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@WebServlet("/student/submit")
@MultipartConfig(
        maxFileSize = 10 * 1024 * 1024,
        maxRequestSize = 15 * 1024 * 1024
)
public class SubmitAssignmentServlet extends HttpServlet {
    private SubmissionDAO submissionDAO;
    private AssignmentDAO assignmentDAO;

    @Override
    public void init() throws ServletException {
        submissionDAO = SubmissionDAO.getInstance();
        assignmentDAO = AssignmentDAO.getInstance();
    }

    private static final String UPLOAD_DIR =
            System.getProperty("user.home") + File.separator + "submissions";

    private String handleFileUpload(Part filePart, String studentId, String assignmentId) {
        try {
            String originalName = getSubmittedFileName(filePart);
            if (originalName == null || originalName.isEmpty()) return null;

            String ext = originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";

            String uniqueName = studentId + "_" + assignmentId + "_" +
                    UUID.randomUUID().toString().substring(0, 8) + ext;

            // создаём папку, если её нет
            Path uploadDir = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadDir);

            // путь к файлу
            Path destination = uploadDir.resolve(uniqueName);

            try (var input = filePart.getInputStream()) {
                Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
            }

            System.out.println("Файл сохранён: " + destination.toAbsolutePath());

            return uniqueName;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Student student = (Student) session.getAttribute("user");
        String assignmentId = request.getParameter("assignmentId");
        String content = request.getParameter("content");

        if (assignmentId == null || content == null || content.trim().length() < 10) {
            sendError(request, response, "Некорректные данные");
            return;
        }

        Optional<Assignment> assignmentOpt = assignmentDAO.findById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            sendError(request, response, "Задание не найдено");
            return;
        }

        Assignment assignment = assignmentOpt.get();
        if (LocalDateTime.now().isAfter(assignment.getDeadline())) {
            sendError(request, response, "Срок сдачи истёк");
            return;
        }

        // ===== ИСПРАВКА: Сначала ищем, потом решаем что делать =====
        Submission submission;
        boolean isNewSubmission = false;

        try {
            Optional<Submission> existingOpt = submissionDAO.findByAssignmentAndStudent(assignmentId, student.getId());

            if (existingOpt.isPresent()) {
                // Обновление существующей отправки
                submission = existingOpt.get();
                submission.setContent(content.trim());
                submission.setSubmittedAt(LocalDateTime.now());
                submission.setStatus(SubmissionStatus.PENDING);
            } else {
                // Новая отправка
                submission = new Submission();
                submission.setId(UUID.randomUUID().toString());
                submission.setAssignmentId(assignmentId);
                submission.setStudentId(student.getId());
                submission.setContent(content.trim());
                submission.setSubmittedAt(LocalDateTime.now());
                submission.setStatus(SubmissionStatus.PENDING);
                isNewSubmission = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // === ЗАГРУЗКА ФАЙЛА ===
        Part filePart = request.getPart("file");
        if (filePart != null && filePart.getSize() > 0) {
            String savedFileName = handleFileUpload(filePart, student.getId(), assignmentId);
            if (savedFileName != null) {
                submission.setFileUrl(savedFileName);
            }
        }

        try {
            if (isNewSubmission) {
                submissionDAO.create(submission);
                session.setAttribute("successMessage", "Assignment successfully sent!");
            } else {
                submissionDAO.update(submission);
                session.setAttribute("successMessage", "Assignment successfully updated!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        response.sendRedirect(request.getContextPath() + "/student/assignments");
    }

    private String getSubmittedFileName(Part part) {
        String header = part.getHeader("content-disposition");
        if (header == null) return null;
        for (String partHeader : header.split(";")) {
            if (partHeader.trim().startsWith("filename")) {
                String fileName = partHeader.substring(partHeader.indexOf('=') + 1).trim();
                return fileName.replace("\"", "").replace("'", "");
            }
        }
        return null;
    }

    private void sendError(HttpServletRequest req, HttpServletResponse resp, String msg)
            throws IOException {
        req.getSession().setAttribute("errorMessage", msg);
        resp.sendRedirect(req.getContextPath() + "/student/assignments");
    }
}
