package com.university.moodle.model;

import com.university.moodle.enums.SubmissionStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Submission {
    private String id;
    private String assignmentId;
    private String studentId;
    private String content;
    private String fileUrl;
    private LocalDateTime submittedAt;
    private SubmissionStatus status;
    private String gradedId;
    private Integer score;
    private String feedback;
    private LocalDateTime gradedAt;

    public Submission() {
        this.submittedAt = LocalDateTime.now();
        this.status = SubmissionStatus.PENDING;
    }
}
