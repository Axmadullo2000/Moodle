package com.university.service;

import com.university.moodle.dao.AdminDAO;
import com.university.moodle.dao.StudentDAO;
import com.university.moodle.dao.TeacherDAO;
import com.university.moodle.enums.UserRole;
import com.university.moodle.model.Admin;
import com.university.moodle.model.Student;
import com.university.moodle.model.Teacher;
import com.university.moodle.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthService {
    private static AuthService instance;
    private final StudentDAO studentDAO;
    private final TeacherDAO teacherDAO;
    private final AdminDAO adminDAO;

    private AuthService() {
        try {
            System.out.println("📝 Initializing AuthService...");

            this.studentDAO = StudentDAO.getInstance();
            System.out.println("✅ StudentDAO initialized");

            this.teacherDAO = TeacherDAO.getInstance();
            System.out.println("✅ TeacherDAO initialized");

            this.adminDAO = AdminDAO.getInstance();
            System.out.println("✅ AdminDAO initialized");

            createDefaultAdmin();
            System.out.println("✅ AuthService initialization complete");

        } catch (Exception e) {
            System.err.println("❌ Error in AuthService constructor: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize AuthService", e);
        }
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    private void createDefaultAdmin() {
        String defaultEmail = "admin@university.com";

        if (!adminDAO.existsByEmail(defaultEmail)) {
            Admin admin = new Admin();
            admin.setEmail(defaultEmail);
            admin.setPassword("admin123");
            admin.setFullName("System Administrator");

            adminDAO.save(admin);

            adminDAO.save(admin);
            System.out.println("Админ сохранён с ID: " + admin.getId() + ", в списке: " + adminDAO.getItems().size());
            System.out.println("✅ Default admin created: " + defaultEmail + " / admin123");
        } else {
            System.out.println("ℹ️ Default admin already exists");
        }
    }

    public User login(String email, String password) {
        User user = findUserByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        System.out.println("✅ Login successful: " + user.getEmail() + " (Role: " + user.getRole() + ")");
        return user;
    }

    private User findUserByEmail(String email) {
        Optional<Admin> admin = adminDAO.findByEmail(email);
        if (admin.isPresent()) return admin.get();

        Optional<Teacher> teacher = teacherDAO.findByEmail(email);
        if (teacher.isPresent()) return teacher.get();

        Optional<Student> student = studentDAO.findByEmail(email);
        if (student.isPresent()) return student.get();

        return null;
    }

    public Student registerStudent(String email, String password, String fullName) {
        if (emailExists(email)) {
            throw new RuntimeException("Email already exists");
        }

        Student student = new Student();
        student.setEmail(email);
        student.setPassword(password);
        student.setFullName(fullName);
        student.setRole(UserRole.STUDENT);
        student.setSubmissionID(new ArrayList<>());

        studentDAO.save(student);
        System.out.println("✅ Student registered: " + email);

        return student;
    }

    public Teacher createTeacher(String email, String password, String fullName, String specialization, User currentUser) {
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Access denied. Only admins can create teachers.");
        }

        if (emailExists(email)) {
            throw new RuntimeException("Email already exists");
        }

        Teacher teacher = new Teacher();
        teacher.setEmail(email);
        teacher.setPassword(password);
        teacher.setFullName(fullName);
        teacher.setSpecialization(specialization);
        teacher.setRole(UserRole.TEACHER);
        teacher.setGroupID(new ArrayList<>());
        teacher.setAssignmentID(new ArrayList<>());

        teacherDAO.save(teacher);
        System.out.println("✅ Teacher created by admin: " + email);

        return teacher;
    }

    private boolean emailExists(String email) {
        return adminDAO.existsByEmail(email) ||
                teacherDAO.existsByEmail(email) ||
                studentDAO.existsByEmail(email);
    }

    public User getUserById(String userId) {
        Optional<Admin> admin = adminDAO.findById(userId);
        if (admin.isPresent()) return admin.get();

        Optional<Teacher> teacher = teacherDAO.findById(userId);
        if (teacher.isPresent()) return teacher.get();

        Optional<Student> student = studentDAO.findById(userId);
        return student.orElse(null);
    }

    public List<Teacher> getAllTeachers(User currentUser) {
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Access denied");
        }
        return teacherDAO.findAll();
    }

    public List<Student> getAllStudents(User currentUser) {
        if (currentUser.getRole() != UserRole.ADMIN && currentUser.getRole() != UserRole.TEACHER) {
            throw new RuntimeException("Access denied");
        }
        return studentDAO.findAll();
    }
}
