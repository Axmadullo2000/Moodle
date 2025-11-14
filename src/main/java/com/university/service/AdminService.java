package com.university.service;

import com.university.moodle.dao.GroupDAO;
import com.university.moodle.dao.StudentDAO;
import com.university.moodle.dao.TeacherDAO;
import com.university.moodle.model.Group;
import com.university.moodle.model.Student;
import com.university.moodle.model.Teacher;
import com.university.moodle.util.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class AdminService {
    @Inject
    private TeacherDAO teacherDAO;

    @Inject
    private StudentDAO studentDAO;

    @Inject
    private GroupDAO groupDAO;

    public Teacher createTeacher(String email, String password,
                                 String fullName, String specialization) {
        if (studentDAO.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        Teacher teacher = new Teacher();
        teacher.setEmail(email);
        teacher.setPassword(PasswordUtil.hashPassword(password));
        teacher.setFullName(fullName);
        teacher.setSpecialization(specialization);

        Teacher saved = teacherDAO.save(teacher);
        teacherDAO.save(saved);

        return saved;
    }

    public Group createGroup(String groupName, String description) {
        if (groupDAO.findByGroupName(groupName).isPresent()) {
            throw new RuntimeException("Group already exists");
        }

        Group group = new Group();

        group.setGroupName(groupName);
        group.setDescription(description);

        return groupDAO.save(group);
    }

    public void assignStudentToGroup(String studentId, String groupId) {
        Student student = studentDAO.findById(studentId).orElseThrow(() ->
                new RuntimeException("Student not found"));

        Group group = groupDAO.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        student.setGroupId(groupId);
        studentDAO.save(student);

        if (!group.getStudentID().contains(studentId)) {
            group.getStudentID().add(studentId);
            groupDAO.save(group);
        }
    }

    public void assignTeacherToGroup(String teacherId, String groupId) {
        Teacher teacher = teacherDAO.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Group group = groupDAO.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!teacher.getGroupID().contains(groupId)) {
            teacher.getGroupID().add(groupId);
            teacherDAO.save(teacher);
        }

        if (!group.getTeacherID().contains(teacherId)) {
            group.getTeacherID().add(teacherId);
            groupDAO.save(group);
        }
    }


    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    public List<Teacher> getAllTeachers() {
        return teacherDAO.findAll();
    }

    public List<Group> getAllGroups() {
        return groupDAO.findAll();
    }
}
