package com.university.moodle.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/download")
public class DownloadFileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Проверка авторизации
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        // Получаем путь к файлу из параметра запроса
        String filePath = request.getParameter("file");

        if (filePath == null || filePath.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File path is required");
            return;
        }

        // Защита от path traversal атак
        if (filePath.contains("..") || filePath.contains("\\..") || filePath.contains("/..")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid file path");
            return;
        }

        // Получаем полный путь к файлу
        String applicationPath = request.getServletContext().getRealPath("");
        String fullFilePath = applicationPath + File.separator + filePath;

        File file = new File(fullFilePath);

        // Проверяем существование файла
        if (!file.exists() || !file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }

        // Проверяем, что файл находится в разрешенной директории (uploads)
        if (!filePath.startsWith("uploads/")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        // Получаем оригинальное имя файла (последняя часть пути)
        String fileName = file.getName();

        // Определяем MIME тип
        String mimeType = getServletContext().getMimeType(fullFilePath);
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // Если MIME тип не определен, то используем тип по умолчанию
        }

        // Устанавливаем заголовки ответа
        response.setContentType(mimeType);
        response.setContentLengthLong(file.length());

        // Кодируем имя файла для корректного отображения кириллицы
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                .replaceAll("\\+", "%20");

        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + encodedFileName);

        // Читаем и отправляем файл
        try (FileInputStream inStream = new FileInputStream(file);
             OutputStream outStream = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead); // Записываем файл в ответ
            }

            outStream.flush(); // Отправляем оставшиеся данные

        } catch (IOException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error downloading file: " + e.getMessage());
        }
    }
}
