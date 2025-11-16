package com.university.moodle.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleLogout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleLogout(request, response);
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            // Получаем информацию о пользователе перед удалением сессии
            String userEmail = (String) session.getAttribute("email");
            String userRole = (String) session.getAttribute("role");

            // Логируем выход
            if (userEmail != null) {
                System.out.println("👋 User logged out: " + userEmail + " (Role: " + userRole + ")");
            }

            // Инвалидируем сессию
            session.invalidate();
        }

        // Перенаправляем на главную страницу с сообщением
        response.sendRedirect(request.getContextPath() + "/index.jsp?logout=success");
    }
}
