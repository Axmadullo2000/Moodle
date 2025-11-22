package com.university.moodle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
