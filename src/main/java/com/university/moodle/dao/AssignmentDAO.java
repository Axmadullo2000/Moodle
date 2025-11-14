package com.university.moodle.dao;

import com.university.moodle.model.Assignment;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AssignmentDAO extends AbstractDAO<Assignment> {
    @Override
    public List<Assignment> getItems() {
        return items;
    }

    @Override
    protected void setId(Assignment assignments, String id) {
        assignments.setId(id);
    }

    @Override
    protected String getId(Assignment assignment) {
        return assignment.getId();
    }

    public List<Assignment> findByTeacherId(String teacherId) {
        return items.stream()
                .filter(assignment -> assignment.getTeacherId().equals(teacherId))
                .collect(Collectors.toList());
    }

    public List<Assignment> findByGroupId(String groupId) {
        return items.stream()
                .filter(assignment -> assignment.getGroupId().equals(groupId))
                .collect(Collectors.toList());
    }

    public List<Assignment> findActiveAssignments() {
        LocalDateTime now = LocalDateTime.now();

        return findActiveAssignments().stream()
                .filter(assignment -> assignment.getDeadline().isAfter(now))
                .collect(Collectors.toList());
    }

}
