package com.university.moodle.service;

import com.university.moodle.dao.AssignmentDAO;
import com.university.moodle.dao.StudentDAO;
import com.university.moodle.dao.SubmissionDAO;
import com.university.moodle.enums.SubmissionStatus;
import com.university.moodle.model.Assignment;
import com.university.moodle.model.Student;
import com.university.moodle.model.Submission;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubmissionService {

    private final SubmissionDAO submissionDAO = SubmissionDAO.getInstance();
    private final AssignmentDAO assignmentDAO = AssignmentDAO.getInstance();
    private final StudentDAO studentDAO = StudentDAO.getInstance();

    public Assignment getAssignmentById(String assignmentId) {
        Optional<Assignment> assignment = assignmentDAO.findById(assignmentId);
        if (assignment.isPresent()) {
            return assignment.get();
        } else {
            throw new RuntimeException("Assignment not found");
        }
    }

    public void submitAssignment(String assignmentId, String studentId, String content, String fileUrl) throws SQLException {
        Assignment assignment = assignmentDAO.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        Student student = studentDAO.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // проверка: есть ли уже отправка?
        submissionDAO.findByAssignmentAndStudent(assignmentId, studentId)
                .ifPresent(s -> { throw new RuntimeException("Submission already exists"); });

        // дедлайн
        if (LocalDateTime.now().isAfter(assignment.getDeadline())) {
            throw new RuntimeException("Deadline is over — submission not allowed");
        }

        Submission submission = new Submission();
        submission.setAssignmentId(assignmentId);
        submission.setStudentId(studentId);
        submission.setContent(content);
        submission.setFileUrl(fileUrl);
        submission.setStatus(SubmissionStatus.SUBMITTED);

        submissionDAO.save(submission);

        // привязка к assignment
        if (assignment.getSubmissionID() == null) assignment.setSubmissionID(new ArrayList<>());
        assignment.getSubmissionID().add(submission.getId());
        assignmentDAO.save(assignment);

        // привязка к student
        if (student.getSubmissionID() == null) student.setSubmissionID(new ArrayList<>());
        student.getSubmissionID().add(submission.getId());
        studentDAO.save(student);
    }

    public List<Submission> getSubmissionsByAssignment(String assignmentId) throws SQLException {
        return submissionDAO.findByAssignmentId(assignmentId);
    }

    public List<Submission> getSubmissionsByStudent(String studentId) throws SQLException {
        return submissionDAO.findByStudent(studentId);
    }


    public void gradeSubmission(String submissionId, int grade) throws SQLException {
        Submission submission = submissionDAO.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        submission.setScore(grade);
        submission.setStatus(SubmissionStatus.GRADED);
        submission.setGradedAt(LocalDateTime.now());

        submissionDAO.save(submission);
    }

}

