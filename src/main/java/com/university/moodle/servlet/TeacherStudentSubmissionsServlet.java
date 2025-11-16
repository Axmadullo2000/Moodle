package com.university.moodle.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/teacher/student-submissions")
public class TeacherStudentSubmissionsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String studentId = req.getParameter("studentId");
        String groupId = req.getParameter("groupId");

        // TODO: Получить submissions студента
        // TODO: Передать данные в JSP

        req.getRequestDispatcher("/WEB-INF/views/student-submissions.jsp")
                .forward(req, resp);
    }
}
