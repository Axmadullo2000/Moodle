package com.university.moodle.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
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
    private String filePath;

}
