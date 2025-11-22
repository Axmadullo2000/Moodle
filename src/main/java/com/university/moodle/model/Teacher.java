package com.university.moodle.model;

import com.university.moodle.enums.UserRole;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static com.university.moodle.enums.UserRole.TEACHER;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Teacher extends User {
    private String specialization;
    private List<String> groupID;
    private List<String> assignmentID;

    public Teacher(List<String> groupID, List<String> assignmentID) {
        super();
        this.groupID = groupID != null ? groupID : new ArrayList<>();
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
