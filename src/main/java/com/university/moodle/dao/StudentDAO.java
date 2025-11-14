package com.university.moodle.dao;

import com.university.moodle.model.Student;

import java.util.List;
import java.util.Optional;

public class StudentDAO extends AbstractDAO<Student> {
    private static StudentDAO instance;

    private StudentDAO() {
        super();
    }

    public static synchronized StudentDAO getInstance() {
        if (instance == null) {
            instance = new StudentDAO();
        }
        return instance;
    }

    @Override
    protected void setId(Student student, String id) {
        student.setId(id);
    }

    @Override
    protected String getId(Student student) {
        return student.getId();
    }

    @Override
    public List<Student> getItems() {
        return items;
    }

    public Optional<Student> findByGroupId(String groupId) {
        return items.stream()
                .filter(student -> student.getGroupId() != null && student.getGroupId().equals(groupId))
                .findFirst();
    }

    public Optional<Student> findByEmail(String email) {
        System.out.println("🔍 Searching for email: " + email);
        System.out.println("📋 Total students in list: " + items.size());

        return items.stream()
                .filter(student -> student.getEmail() != null && student.getEmail().equals(email))
                .findFirst();
    }

    public boolean existsByEmail(String email) {
        return items.stream()
                .anyMatch(student -> student.getEmail() != null && student.getEmail().equalsIgnoreCase(email));
    }

    public boolean existsByStudentId(String studentId) {
        return items.stream()
                .anyMatch(student -> student.getId() != null && student.getId().equals(studentId));
    }
}
