package com.university.moodle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {
    private String id;
    private String groupName;
    private String description;
    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at;

    private List<String> studentIDs;
    private List<String> teacherIDs;
    private List<String> assignmentIDs;

    public List<String> getAssignmentIDs() {
        if (assignmentIDs == null) assignmentIDs = new ArrayList<>();
        return assignmentIDs;
    }

    public List<String> getTeacherIDs() {
        if (teacherIDs == null) teacherIDs = new ArrayList<>();
        return teacherIDs;
    }

    public List<String> getStudentIDs() {
        if (studentIDs == null) studentIDs = new ArrayList<>();
        return studentIDs;
    }
}
