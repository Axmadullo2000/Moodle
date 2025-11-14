package com.university.service;

import com.university.moodle.dao.AssignmentDAO;
import com.university.moodle.dao.GroupDAO;
import com.university.moodle.dao.TeacherDAO;
import com.university.moodle.model.Assignment;
import com.university.moodle.model.Group;
import com.university.moodle.model.Teacher;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;

public class AssigmentService {
    @Inject
    private AssignmentDAO assignmentDAO;

    @Inject
    private TeacherDAO teacherDAO;

    @Inject
    private GroupDAO groupDAO;

    private Assignment createAssignmentDAO(
            String teacherId, String groupId,
            String title, String description,
            LocalDateTime deadline, Integer maxScore
    ) {
        Teacher teacher = teacherDAO.findById(teacherId)
                .orElseThrow(() ->
                        new RuntimeException("Teacher not found"));

        Group group = groupDAO.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Assignment assignment = new Assignment();

        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setTeacherId(teacherId);
        assignment.setGroupId(groupId);
        assignment.setDeadline(deadline);

        if (maxScore != null) {
            assignment.setMaxScore(maxScore);
        }

        Assignment saved = assignmentDAO.save(assignment);

        teacher.getAssignmentID().add(saved.getId());
        groupDAO.save(group);

        return saved;
    }

    public List<Assignment> getAssignmentsByGroup(String groupId) {
        return assignmentDAO.findByGroupId(groupId);
    }

    public List<Assignment> getAssignmentsByTeacher(String teacherId) {
        return assignmentDAO.findByTeacherId(teacherId);
    }

    public List<Assignment> getActiveAssignment() {
        return assignmentDAO.findActiveAssignments();
    }

    public Assignment getAssignment(String id) {
        return assignmentDAO.findById(id).orElseThrow(() ->
                new RuntimeException("Assignment not found"));
    }

    public Assignment updateAssignment(String id, String title,
                                       String description, LocalDateTime deadline, Integer maxScore) {
        Assignment assignment = assignmentDAO.findById(id).orElseThrow(() ->
                new RuntimeException("Assignment not found"));

        if (title != null) assignment.setTitle(title);
        if (description != null) assignment.setDescription(description);
        if (deadline != null) assignment.setDeadline(deadline);
        if (maxScore != null) assignment.setMaxScore(maxScore);

        return assignmentDAO.save(assignment);
    }

    public void deleteAssignment(String id) {
        Assignment assignment = assignmentDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        Teacher teacher = teacherDAO.findById(assignment.getTeacherId()).orElse(null);

        if (teacher != null) {
            teacher.getAssignmentID().remove(id);
            teacherDAO.save(teacher);
        }

        Group group = groupDAO.findById(assignment.getGroupId()).orElse(null);
        if (group != null) {
            group.getAssignmentID().remove(id);
            groupDAO.save(group);
        }

        assignmentDAO.delete(id);
    }

}
