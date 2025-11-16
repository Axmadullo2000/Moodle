package com.university.moodle.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Group {
    private String id;
    private String groupName;
    private String description;

    private List<String> studentIDs = new ArrayList<>();
    private List<String> teacherIDs = new ArrayList<>();
    private List<String> assignmentIDs = new ArrayList<>();

    // Защита от null и перезаписи
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