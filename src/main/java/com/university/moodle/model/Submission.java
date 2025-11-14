package com.university.moodle.model;

import com.university.moodle.enums.SubmissionStatus;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
public class Submission {
    private String id;
    private String assignmentId;
    private String studentId;
    private String content;
    private String fileUrl;
    private LocalDateTime submittedAt;
    @Getter
    private SubmissionStatus status;
    private String gradedId;

    public Submission() {
        this.submittedAt = LocalDateTime.now();
        this.status = SubmissionStatus.PENDING;
    }
}
