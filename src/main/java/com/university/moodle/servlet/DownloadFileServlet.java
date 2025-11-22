package com.university.moodle.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/download")
public class DownloadFileServlet extends HttpServlet {
    /*private static final String TEACHER_UPLOAD_DIR =
            System.getProperty("user.home") + File.separator + "moodle_uploads";

    private static final String STUDENT_UPLOAD_DIR =
            System.getProperty("user.home") + File.separator + "submissions";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fileName = request.getParameter("file");
        if (fileName == null || fileName.isEmpty()) {
            response.getWriter().println("Missing file parameter");
            return;
        }

        File downloadFile;
        if (fileName.startsWith("submissions/")) {
            downloadFile = new File(STUDENT_UPLOAD_DIR, fileName.substring("submissions/".length()));
        } else {
            downloadFile = new File(TEACHER_UPLOAD_DIR, fileName);
        }

        if (!downloadFile.exists()) {
            response.getWriter().println("File not found: " + downloadFile.getAbsolutePath());
            return;
        }

        String mimeType = getServletContext().getMimeType(downloadFile.getName());
        if (mimeType == null) mimeType = "application/octet-stream";

        response.setContentType(mimeType);
        response.setContentLengthLong(downloadFile.length());
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + downloadFile.getName() + "\"");

        try (FileInputStream inStream = new FileInputStream(downloadFile);
             OutputStream outStream = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        }
    }*/

    // Папки для файлов
    private static final String TEACHER_DIR =
            System.getProperty("user.home") + File.separator + "moodle_uploads";
    private static final String STUDENT_DIR =
            System.getProperty("user.home") + File.separator + "submissions";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fileName = request.getParameter("file");
        String type = request.getParameter("type"); // "teacher" или "student"

        if (fileName == null || fileName.isEmpty()) {
            response.getWriter().println("Missing file parameter");
            return;
        }

        // Определяем папку
        String baseDir = TEACHER_DIR;
        if ("student".equals(type)) {
            baseDir = STUDENT_DIR;
        }

        File downloadFile = new File(baseDir, fileName);
        if (!downloadFile.exists()) {
            response.getWriter().println("File not found: " + downloadFile.getAbsolutePath());
            return;
        }

        // Отдаём файл
        try (var in = new FileInputStream(downloadFile);
             var out = response.getOutputStream()) {

            String mimeType = getServletContext().getMimeType(fileName);
            if (mimeType == null) mimeType = "application/octet-stream";

            response.setContentType(mimeType);
            response.setContentLengthLong(downloadFile.length());
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + downloadFile.getName() + "\"");

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
