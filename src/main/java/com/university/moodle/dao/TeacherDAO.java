package com.university.moodle.dao;

import com.university.moodle.model.Teacher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TeacherDAO extends AbstractDAO<Teacher> {
    private static TeacherDAO teacherDAO;

    private TeacherDAO() {}

    public static TeacherDAO getInstance() {
        if (teacherDAO == null) {
            teacherDAO = new TeacherDAO();
        }
        return teacherDAO;
    }

    @Override
    public List<Teacher> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    protected void setId(Teacher teacher, String id) {
        teacher.setId(id);
    }

    @Override
    protected String getId(Teacher teacher) {
        return teacher.getId();
    }

    /**
     * Найти учителя по email
     */
    public Optional<Teacher> findByEmail(String email) {
        return items.stream()
                .filter(teacher -> teacher.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    /**
     * Проверить существование учителя по email
     */
    public boolean existsByEmail(String email) {
        return items.stream()
                .anyMatch(teacher -> teacher.getEmail().equalsIgnoreCase(email));
    }

    /**
     * Найти учителей по специализации
     */
    public List<Teacher> findBySpecialization(String specialization) {
        return items.stream()
                .filter(teacher -> teacher.getSpecialization() != null &&
                        teacher.getSpecialization().equalsIgnoreCase(specialization))
                .collect(Collectors.toList());
    }

    /**
     * Найти учителей по группе
     */
    public List<Teacher> findByGroupId(String groupId) {
        return items.stream()
                .filter(teacher -> teacher.getGroupID() != null &&
                        teacher.getGroupID().contains(groupId))
                .collect(Collectors.toList());
    }

    /**
     * Добавить группу учителю
     */
    public boolean addGroup(String teacherId, String groupId) {
        Optional<Teacher> teacherOpt = findById(teacherId);

        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();

            if (teacher.getGroupID() == null) {
                teacher.setGroupID(new ArrayList<>());
            }

            if (!teacher.getGroupID().contains(groupId)) {
                teacher.getGroupID().add(groupId);
                return true;
            }
        }

        return false;
    }

    /**
     * Удалить группу у учителя
     */
    public boolean removeGroup(String teacherId, String groupId) {
        Optional<Teacher> teacherOpt = findById(teacherId);

        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();

            if (teacher.getGroupID() != null) {
                return teacher.getGroupID().remove(groupId);
            }
        }

        return false;
    }

    /**
     * Добавить задание учителю
     */
    public boolean addAssignment(String teacherId, String assignmentId) {
        Optional<Teacher> teacherOpt = findById(teacherId);

        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();

            if (teacher.getAssignmentID() == null) {
                teacher.setAssignmentID(new ArrayList<>());
            }

            if (!teacher.getAssignmentID().contains(assignmentId)) {
                teacher.getAssignmentID().add(assignmentId);
                return true;
            }
        }

        return false;
    }

    /**
     * Получить количество групп учителя
     */
    public int getGroupCount(String teacherId) {
        Optional<Teacher> teacherOpt = findById(teacherId);

        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            return teacher.getGroupID() != null ? teacher.getGroupID().size() : 0;
        }

        return 0;
    }

    /**
     * Получить количество заданий учителя
     */
    public int getAssignmentCount(String teacherId) {
        Optional<Teacher> teacherOpt = findById(teacherId);

        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            return teacher.getAssignmentID() != null ? teacher.getAssignmentID().size() : 0;
        }

        return 0;
    }
}
