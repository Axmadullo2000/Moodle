package com.university.moodle.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Assignment {
    private String id;
    private String title;
    private String description;
    private String teacherId;
    private String groupId;
    private LocalDateTime deadline;
    private Integer maxScore;
    private LocalDateTime createdAt;
    private List<String> submissionID;

    public Assignment() {
        this.maxScore = 100;
        this.createdAt = LocalDateTime.now();
        this.submissionID = new ArrayList<>();
    }
}
