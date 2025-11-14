package com.university.service;

import com.university.moodle.dao.StudentDAO;
import com.university.moodle.enums.UserRole;
import com.university.moodle.model.Student;
import com.university.moodle.model.User;

import java.util.ArrayList;

public class AuthService {
    private static AuthService instance;
    private final StudentDAO studentDAO;

    private AuthService() {
        this.studentDAO = StudentDAO.getInstance();
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }

        return instance;
    }

    public User login(String email, String password) {
        Student student = studentDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!student.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return student;
    }

    public Student registerStudent(String email, String password, String fullName) {
        if (studentDAO.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        Student student = new Student();
        student.setEmail(email);
        student.setPassword(password);
        student.setFullName(fullName);
        student.setRole(UserRole.STUDENT);
        student.setSubmissionID(new ArrayList<>());

        studentDAO.save(student);

        return student;
    }

    public User getUserById(String userId) {
        return studentDAO.findById(userId).orElse(null);
    }

}
