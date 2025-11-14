package com.university.service;

import com.university.moodle.dao.AssignmentDAO;
import com.university.moodle.dao.GradeDAO;
import com.university.moodle.dao.StudentDAO;
import com.university.moodle.dao.SubmissionDAO;
import com.university.moodle.model.Assignment;
import com.university.moodle.model.Grade;
import com.university.moodle.model.Student;
import com.university.moodle.model.Submission;

import com.university.moodle.enums.SubmissionStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class SubmissionService {
    @Inject
    private SubmissionDAO submissionDAO;

    @Inject
    private AssignmentDAO assignmentDAO;

    @Inject
    private StudentDAO studentDAO;

    @Inject
    private GradeDAO gradeDAO;

    public Submission submitAssignment(String assignmentId, String studentId,
                                       String content, String fileUrl) {
        Assignment assignment = assignmentDAO.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        Student student = studentDAO.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (submissionDAO.findByAssignmentAndStudent(assignmentId, studentId).isPresent()) {
            throw new RuntimeException("Assignment already exists");
        }

        Submission submission = new Submission();
        submission.setAssignmentId(assignmentId);
        submission.setStudentId(studentId);
        submission.setContent(content);
        submission.setFileUrl(fileUrl);

        if (LocalDateTime.now().isAfter(assignment.getDeadline())) {
            submission.setStatus(SubmissionStatus.LATE);
        }

        Submission saved = submissionDAO.save(submission);

        student.getSubmissionID().add(saved.getId());
        studentDAO.save(student);

        assignment.getSubmissionID().add(saved.getId());
        assignmentDAO.save(assignment);

        return saved;
    }

    public Grade gradeSubmission(String submissionId, Integer score, String feedback) {
        Submission submission = submissionDAO.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        if (score < 0 || score > 100) {
            throw new RuntimeException("Score must be between 0 and 100");
        }

        Grade grade = new Grade();

        grade.setSubmissionID(submissionId);
        grade.setScore(score);
        grade.setFeedback(feedback);

        Grade saved = gradeDAO.save(grade);

        submission.setGradedId(saved.getId());
        submission.setStatus(SubmissionStatus.GRADED);
        submissionDAO.save(submission);

        return saved;
    }

    public List<Submission> getSubmissionsByAssignment(String assignmentId) {
        return submissionDAO.findByAssignmentId(assignmentId);
    }

    public List<Submission> getSubmissionsByStudent(String studentId) {
        return submissionDAO.findByStudent(studentId);
    }

    public Submission getSubmission(String id) {
        return submissionDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
    }
}
