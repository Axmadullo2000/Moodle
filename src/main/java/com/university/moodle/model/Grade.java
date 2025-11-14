package com.university.moodle.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Grade {
    private String id;
    private String submissionID;
    private Integer score;
    private String feedback;
    private LocalDateTime gradedAt;

    public Grade() {
        this.gradedAt = LocalDateTime.now();
    }
}
