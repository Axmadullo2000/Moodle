package com.university.moodle.service;

import com.university.moodle.config.DbConfig;
import com.university.moodle.dao.AdminDAO;
import com.university.moodle.dao.StudentDAO;
import com.university.moodle.dao.TeacherDAO;
import com.university.moodle.enums.UserRole;
import com.university.moodle.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.university.moodle.enums.UserRole.*;

public class AuthService {
    private static AuthService instance;
    private final StudentDAO studentDAO;
    private final TeacherDAO teacherDAO;
    private final AdminDAO adminDAO;

    private AuthService() {
        this.studentDAO = StudentDAO.getInstance();

        this.teacherDAO = TeacherDAO.getInstance();

        this.adminDAO = AdminDAO.getInstance();
    }

    public static AuthService getInstance() throws SQLException {
        if (instance == null) {
            instance = new AuthService();
        }

        return instance;
    }

    public User login(String email, String password) throws SQLException {
        Optional<WrapperUser> userByEmail = findUserByEmail(email);

        if (userByEmail.isPresent()) {
            User user = userByEmail.get().getUser();

            if (user == null) {
                throw new RuntimeException("User not found");
            }

            if (!user.getPassword().equals(password)) {
                throw new RuntimeException("Invalid password");
            }

            return user;
        }

        return null;
    }

    private Optional<WrapperUser> findUserByEmail(String email) throws SQLException {
        System.out.println("=== Searching for email: " + email);

        Optional<Admin> admin = adminDAO.findByEmail(email);
        System.out.println("Admin found: " + admin.isPresent());

        if (admin.isPresent()) {
            System.out.println("Admin role: " + admin.get().getRole());
            System.out.println("Admin data: " + admin.get());
            return admin.map(WrapperUser::new);
        }

        Optional<Teacher> teacher = teacherDAO.findByEmail(email);
        System.out.println("Teacher found: " + teacher.isPresent());

        if (teacher.isPresent()) {
            System.out.println("Teacher role: " + teacher.get().getRole());
            System.out.println("Teacher data: " + teacher.get());
            return teacher.map(WrapperUser::new);
        }

        Optional<Student> student = studentDAO.findByEmail(email);
        System.out.println("Student found: " + student.isPresent());

        if (student.isPresent()) {
            System.out.println("Student role: " + student.get().getRole());
            System.out.println("Student data: " + student.get());
            return student.map(WrapperUser::new);
        }

        System.out.println("User not found!");
        return Optional.empty();
    }

    public Student registerStudent(String email, String password, String fullName) throws SQLException {
        if (emailExists(email)) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        Student student = new Student();
        student.setEmail(email);
        student.setPassword(password);
        student.setFullName(fullName);
        student.setRole(UserRole.STUDENT);
        student.setSubmissionID(new ArrayList<>());
        student.setGroupId(null);

        try {
            studentDAO.save(student);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при регистрации: " + e.getMessage(), e);
        }

        return student;
    }

    public Teacher createTeacher(String email, String password, String fullName, String specialization, User currentUser) throws SQLException {
        if (!currentUser.getRole().equals(ADMIN)) {
            throw new RuntimeException("Access denied. Only admins can create teachers.");
        }

        if (emailExists(email)) {
            throw new RuntimeException("Email already exists");
        }

        Teacher teacher = new Teacher();
        teacher.setId(UUID.randomUUID().toString());
        teacher.setEmail(email);
        teacher.setPassword(password);
        teacher.setFullName(fullName);
        teacher.setSpecialization(specialization);
        teacher.setRole(TEACHER);
        teacher.setGroupID(new ArrayList<>());
        teacher.setAssignmentID(new ArrayList<>());

        teacherDAO.save(teacher);

        return teacher;
    }

    private boolean emailExists(String email) throws SQLException {
        return adminDAO.existsByEmail(email) ||
                teacherDAO.existsByEmail(email) ||
                studentDAO.existsByEmail(email);
    }

    /*
    public User getUserById(String userId) throws SQLException {
        Optional<Admin> admin = adminDAO.findById(userId);
        if (admin.isPresent()) return admin.get();

        Optional<Teacher> teacher = teacherDAO.findById(userId);
        if (teacher.isPresent()) return teacher.get();

        Optional<Student> student = studentDAO.findById(userId);
        return student.orElse(null);
    }
    */

    public User getUserById(String userId) throws SQLException {
        // Сначала узнаем роль пользователя
        String roleQuery = "SELECT role FROM users WHERE id = ?";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(roleQuery)) {

            stmt.setString(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");

                    // Ищем только в правильной таблице
                    return switch (role) {
                        case "ADMIN" -> adminDAO.findById(userId).orElse(null);
                        case "TEACHER" -> teacherDAO.findById(userId).orElse(null);
                        case "STUDENT" -> studentDAO.findById(userId).orElse(null);
                        default -> null;
                    };
                }
            }
        }

        return null;
    }


    public List<Teacher> getAllTeachers(User currentUser) throws SQLException {
        if (!currentUser.getRole().equals(ADMIN)) {
            throw new RuntimeException("Access denied");
        }

        return teacherDAO.findAll();
    }

    public List<Student> getAllStudents(User currentUser) throws SQLException {
        if (!currentUser.getRole().equals(ADMIN) && !currentUser.getRole().equals(TEACHER)) {
            throw new RuntimeException("Access denied");
        }

        return studentDAO.getItems();
    }
}
