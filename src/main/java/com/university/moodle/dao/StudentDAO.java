package com.university.moodle.dao;

import com.university.moodle.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudentDAO extends AbstractDAO<Student> {
    private static StudentDAO studentDAO;

    private StudentDAO() {}

    public static StudentDAO getInstance() {
        if (studentDAO == null) {
            studentDAO = new StudentDAO();
        }
        return studentDAO;
    }

    @Override
    public List<Student> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    protected void setId(Student student, String id) {
        student.setId(id);
    }

    @Override
    protected String getId(Student student) {
        return student.getId();
    }

    /**
     * Найти студента по email
     */
    public Optional<Student> findByEmail(String email) {
        return items.stream()
                .filter(student -> student.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    /**
     * Проверить существование студента по email
     */
    public boolean existsByEmail(String email) {
        return items.stream()
                .anyMatch(student -> student.getEmail().equalsIgnoreCase(email));
    }

    /**
     * Найти студентов по группе
     */
    public List<Student> findByGroupId(String groupId) {
        return items.stream()
                .filter(student -> groupId.equals(student.getGroupId()))
                .collect(Collectors.toList());
    }

    /**
     * Найти студентов без группы
     */
    public List<Student> findStudentsWithoutGroup() {
        return items.stream()
                .filter(student -> student.getGroupId() == null || student.getGroupId().isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Добавить ответ студенту
     */
    public boolean addSubmission(String studentId, String submissionId) {
        Optional<Student> studentOpt = findById(studentId);

        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();

            if (student.getSubmissionID() == null) {
                student.setSubmissionID(new ArrayList<>());
            }

            if (!student.getSubmissionID().contains(submissionId)) {
                student.getSubmissionID().add(submissionId);
                return true;
            }
        }

        return false;
    }

    /**
     * Установить группу для студента
     */
    public boolean setStudentGroup(String studentId, String groupId) {
        Optional<Student> studentOpt = findById(studentId);

        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setGroupId(groupId);
            return true;
        }

        return false;
    }

    /**
     * Удалить студента из группы
     */
    public boolean removeFromGroup(String studentId) {
        Optional<Student> studentOpt = findById(studentId);

        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setGroupId(null);
            return true;
        }

        return false;
    }
}
