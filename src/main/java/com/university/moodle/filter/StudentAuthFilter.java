package com.university.moodle.filter;

import com.university.moodle.enums.UserRole;
import com.university.moodle.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Фильтр для защиты страниц студента
 * Проверяет авторизацию и роль пользователя
 */
@WebFilter("/student/*")
public class StudentAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("🔒 StudentAuthFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);

        // Проверяем наличие сессии и пользователя
        if (session == null || session.getAttribute("user") == null) {
            System.out.println("⚠️ Unauthorized access attempt to: " + httpRequest.getRequestURI());
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.jsp?error=unauthorized");
            return;
        }

        User user = (User) session.getAttribute("user");

        // Проверяем роль пользователя
        if (user.getRole() != UserRole.STUDENT) {
            System.out.println("⚠️ Access denied for user: " + user.getEmail() + " (Role: " + user.getRole() + ")");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.jsp?error=forbidden");
            return;
        }

        // Пользователь авторизован и имеет нужную роль
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("🔓 StudentAuthFilter destroyed");
    }
}
