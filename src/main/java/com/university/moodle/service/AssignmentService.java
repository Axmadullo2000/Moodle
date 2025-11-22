package com.university.moodle.service;

import com.university.moodle.dao.AssignmentDAO;
import com.university.moodle.dao.GroupDAO;
import com.university.moodle.dao.TeacherDAO;
import com.university.moodle.model.Assignment;
import com.university.moodle.model.Group;
import com.university.moodle.model.Teacher;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AssignmentService {

    private static AssignmentService instance;

    private final AssignmentDAO assignmentDAO = AssignmentDAO.getInstance();
    private final GroupDAO groupDAO = GroupDAO.getInstance();
    private final TeacherDAO teacherDAO = TeacherDAO.getInstance();

    public Assignment getAssignmentById(String assignmentId) {
        // Используем DAO для поиска задания по ID
        return assignmentDAO.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
    }


    private AssignmentService() {}

    public static AssignmentService getInstance() {
        if (instance == null) instance = new AssignmentService();
        return instance;
    }

    public void createAssignment(
            String teacherId,
            String groupId,
            String title,
            String description,
            LocalDateTime deadline,
            Integer maxScore
    ) throws SQLException {
        Teacher teacher = teacherDAO.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Group group = groupDAO.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // teacher must teach this group
        if (!teacher.getGroupID().contains(groupId)) {
            throw new RuntimeException("Teacher is not assigned to this group");
        }

        Assignment assignment = Assignment.builder()
                .title(title)
                .description(description)
                .deadline(deadline)
                .maxScore(maxScore)
                .teacherId(teacherId)
                .groupId(groupId)
                .createdAt(deadline)
                .build();

        assignmentDAO.save(assignment);

        // link to group
        if (group.getAssignmentIDs() == null)
            group.setAssignmentIDs(new ArrayList<>());
        group.getAssignmentIDs().add(assignment.getId());
        groupDAO.save(group);

        // link to teacher
        if (teacher.getAssignmentID() == null)
            teacher.setAssignmentID(new ArrayList<>());
        teacher.getAssignmentID().add(assignment.getId());
        teacherDAO.save(teacher);

        return;
    }

    public List<Assignment> getAssignmentsByGroup(String groupId) throws SQLException {
        return assignmentDAO.findByGroupId(groupId);
    }

    public List<Assignment> getAssignmentsByTeacher(String teacherId) throws SQLException {
        return assignmentDAO.findByTeacherId(teacherId);
    }
}
