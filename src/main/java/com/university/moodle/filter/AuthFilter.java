package com.university.moodle.filter;

import com.university.moodle.enums.UserRole;
import com.university.moodle.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.university.moodle.enums.UserRole.*;

@WebFilter("/*")
public class AuthFilter extends HttpFilter {
    public static final Set<String> PUBLIC_PATHS = Set.of(
            "/auth",
            "/"
    );

    public static final Map<UserRole, Set<String>> ROLE_PATHS = Map.of(
            ADMIN, Set.of(
                    "/admin/dashboard",
                    "/profile",
                    "/admin/create-teacher",
                    "/admin/manage-group",
                    "/logout"
            ),
            TEACHER, Set.of(
                    "/teacher/dashboard",
                    "/teacher/assignments",
                    "/teacher/group",
                    "/teacher/create-assignment",
                    "/teacher/submissions",
                    "/teacher/edit-assignment",
                    "/teacher/delete-assignment",
                    "/teacher/grade-submission",
                    "teacher/submissions",
                    "/download-assignment",
                    "/teacher/upload-assignment",
                    "/download",
                    "/logout"
            ),
            STUDENT, Set.of(
                    "/student/assignments",
                    "/student/submissions",
                    "/download-assignment",
                    "/student/submit",
                    "/uploads",
                    "/download",
                    "/logout"
            )
    );

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        String uri = req.getRequestURI();
        String ctx = req.getContextPath();

        String path = uri.startsWith(ctx) ? uri.substring(ctx.length()) : uri;

        int q = path.indexOf('?');
        if (q != -1) {
            path = path.substring(0, q);
        }

        if (isPublic(path)) {
            chain.doFilter(req, res);
            return;
        }

        Object userId = req.getSession().getAttribute("userId");
        User currentUser = (User) req.getSession().getAttribute("user");
        UserRole role = (UserRole) req.getSession().getAttribute("role");

        if (userId == null || currentUser == null || currentUser.getRole() == null) {
            res.sendRedirect(ctx + "/");
            return;
        }

        if (!isAllowed(role, path)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        chain.doFilter(req, res);
    }

    public boolean isPublic(String path) {
        return PUBLIC_PATHS.contains(path);
    }

    public boolean isAllowed(UserRole role, String path) {
        Set<String> allowed = ROLE_PATHS.get(role);

        if (allowed == null) {
            return false;
        }

        if (allowed.contains(path)) {
            return true;
        }

        return allowed.stream().anyMatch(allowedPath -> {
            if (allowedPath.endsWith("/*")) {
                String basePath = allowedPath.substring(0, allowedPath.length() - 2);
                return path.startsWith(basePath + "/");
            }
            return path.startsWith(allowedPath);
        });
    }
}
