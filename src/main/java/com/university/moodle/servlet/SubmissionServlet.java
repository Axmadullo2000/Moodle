package com.university.moodle.servlet;

import com.university.service.AssignmentService;
import com.university.service.SubmissionService;
import com.university.moodle.model.Assignment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;

@WebServlet("/submit")
public class SubmissionServlet extends HttpServlet {
    private final SubmissionService submissionService = new SubmissionService();
    private final AssignmentService assignmentService = AssignmentService.getInstance(); // Используем AssignmentService

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String assignmentId = req.getParameter("assignmentId");

        try {
            // Получаем задание по ID
            Assignment assignment = assignmentService.getAssignmentById(assignmentId);

            // Если задание найдено, передаем его в запрос
            req.setAttribute("assignment", assignment);

            // Перенаправляем на JSP
            RequestDispatcher dispatcher = req.getRequestDispatcher("/student-submissions.jsp");
            dispatcher.forward(req, resp);

        } catch (RuntimeException | ServletException e) {
            resp.getWriter().write("Ошибка: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String studentId = (String) req.getSession().getAttribute("userId");
        String assignmentId = req.getParameter("assignmentId");
        String content = req.getParameter("content");

        // Обработка загрузки файла, если он присутствует
        String fileUrl = null;
        if (req.getPart("file") != null) {
            try {
                Part filePart = req.getPart("file");
                String fileName = filePart.getSubmittedFileName();
                String uploadPath = getServletContext().getRealPath("/uploads");
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String filePath = uploadPath + File.separator + fileName;
                filePart.write(filePath);
                fileUrl = "/uploads/" + fileName; // Сохраняем URL файла
            } catch (Exception e) {
                resp.getWriter().write("Ошибка при загрузке файла: " + e.getMessage());
                return;
            }
        }

        try {
            // Отправляем задание (содержимое) в сервис для обработки отправки
            submissionService.submitAssignment(assignmentId, studentId, content, fileUrl);
            resp.sendRedirect("/student/assignments");
        } catch (RuntimeException e) {
            resp.getWriter().write("Ошибка: " + e.getMessage());
        }
    }
}
