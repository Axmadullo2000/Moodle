package com.university.moodle.model;

import com.university.moodle.enums.UserRole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Teacher extends User {
    private String specialization;
    private List<String> groupID;
    private List<String> assignmentID;

    public Teacher(List<String> groupID, List<String> assignmentID) {
        super();
        setRole(UserRole.TEACHER);
        this.groupID = groupID != null ? groupID : new ArrayList<String>();
        this.assignmentID = assignmentID;
    }

    @Override
    public String toString() {
        return super.toString() + "Teacher{" +
                "specialization='" + specialization + '\'' +
                ", groupID=" + groupID +
                ", assignmentID=" + assignmentID +
                '}';
    }
}
