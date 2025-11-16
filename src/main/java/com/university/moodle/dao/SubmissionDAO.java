package com.university.moodle.dao;

import com.university.moodle.model.Submission;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class SubmissionDAO extends AbstractDAO<Submission> {
    private static SubmissionDAO submissionDAO;
    private SubmissionDAO() {}

    public static SubmissionDAO getInstance() {
        if (submissionDAO == null) {
            submissionDAO = new SubmissionDAO();
        }

        return submissionDAO;
    }

    @Override
    public List<Submission> getItems() {
        return items;
    }

    @Override
    protected void setId(Submission submission, String id) {
        submission.setId(id);
    }

    @Override
    protected String getId(Submission item) {
        return item.getId();
    }

    public List<Submission> findByAssignmentId(String assignmentId) {
        return items.stream()
                .filter(submission -> submission.getAssignmentId().equals(assignmentId))
                .collect(Collectors.toList());
    }

    public Optional<Submission> findByAssignmentAndStudent(String assignmentId, String studentId) {
        return items.stream()
                .filter(submission -> submission.getAssignmentId().equals(assignmentId)
                && submission.getStudentId().equals(studentId))
                .findFirst();
    }

    public List<Submission> findByStudent(String studentId) {
        return items.stream()
                .filter(submission -> submission.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }
}
