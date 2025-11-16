package com.university.service;

import com.university.moodle.dao.AssignmentDAO;
import com.university.moodle.dao.SubmissionDAO;
import com.university.moodle.dao.TeacherDAO;
import com.university.moodle.enums.SubmissionStatus;
import com.university.moodle.model.Assignment;
import com.university.moodle.model.Submission;
import com.university.moodle.model.Teacher;

import java.time.LocalDateTime;

public class GradingService {
    private final SubmissionDAO submissionDAO = SubmissionDAO.getInstance();
    private final AssignmentDAO assignmentDAO = AssignmentDAO.getInstance();
    private final TeacherDAO teacherDAO = TeacherDAO.getInstance();

    public Submission gradeSubmission(String teacherId, String submissionId, Integer score, String feedback) {

        Submission submission = submissionDAO.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        Assignment assignment = assignmentDAO.findById(submission.getAssignmentId())
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        Teacher teacher = teacherDAO.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // проверка: учитель ли этот?
        if (!assignment.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("Teacher cannot grade this submission");
        }

        if (score < 0 || score > assignment.getMaxScore()) {
            throw new RuntimeException("Score is out of range");
        }

        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setStatus(SubmissionStatus.GRADED);
        submission.setGradedAt(LocalDateTime.now());
        submission.setGradedId(teacherId);

        return submissionDAO.save(submission);
    }
}
