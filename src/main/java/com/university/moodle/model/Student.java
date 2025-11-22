package com.university.moodle.model;

import com.university.moodle.enums.UserRole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.university.moodle.enums.UserRole.STUDENT;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Student extends User {
    private String groupId;
    private List<String> submissionID;

    public Student(List<String> submissionID) {
        super();
        this.submissionID = submissionID != null ? submissionID : new ArrayList<>();
    }

    @Override
    public String toString() {
        return super.toString() + " Student{" +
                "groupId='" + groupId + '\'' +
                ", submissionID=" + submissionID +
                '}';
    }
}
