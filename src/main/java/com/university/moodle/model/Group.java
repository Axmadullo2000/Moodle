package com.university.moodle.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Group {
    private String id;
    private String groupName;
    private String description;
    private List<String> studentID;
    private List<String> teacherID;
    private List<String> assignmentID;

    public Group(List<String> assignmentID, List<String> teacherID, List<String> studentID) {
        this.assignmentID = assignmentID;
        this.teacherID = teacherID;
        this.studentID = studentID;
    }



}
