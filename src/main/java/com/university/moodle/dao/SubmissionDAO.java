package com.university.moodle.dao;

import com.university.moodle.config.DbConfig;
import com.university.moodle.enums.SubmissionStatus;
import com.university.moodle.model.Submission;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.university.moodle.dao.GroupDAO.getConnections;

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
    protected void setId(Submission submission, String id) {
        submission.setId(id);
    }

    @Override
    protected String getId(Submission item) {
        return item.getId();
    }

    @Override
    public List<Submission> getItems() {
        try {
            return getAll();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void create(Submission item) throws SQLException {
        String sql = """
            INSERT INTO submissions (id, assignment_id, student_id, group_id, teacher_id,\s
                                    content, file_url, submitted_at, status, graded_id,\s
                                    score, feedback, graded_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
       \s""";

        Connection conn = DbConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setString(1, item.getId());
        stmt.setString(2, item.getAssignmentId());
        stmt.setString(3, item.getStudentId());
        stmt.setString(4, item.getGroupId());
        stmt.setString(5, item.getTeacherId());
        stmt.setString(6, item.getContent());
        stmt.setString(7, item.getFileUrl());
        stmt.setTimestamp(8, Timestamp.valueOf(item.getSubmittedAt() != null ? item.getSubmittedAt() : LocalDateTime.now()));
        stmt.setString(9, item.getStatus() != null ? item.getStatus().name() : SubmissionStatus.LATE.name());
        stmt.setString(10, item.getGradedId());

        if (item.getScore() != null) {
            stmt.setInt(11, item.getScore());
        } else {
            stmt.setNull(11, Types.INTEGER);
        }

        stmt.setString(12, item.getFeedback());

        if (item.getGradedAt() != null) {
            stmt.setTimestamp(13, Timestamp.valueOf(item.getGradedAt()));
        } else {
            stmt.setNull(13, Types.TIMESTAMP);
        }

        stmt.executeUpdate();
    }

    @Override
    public void update(Submission item) throws SQLException {
        String sql = """
            UPDATE submissions\s
            SET assignment_id = ?, student_id = ?, group_id = ?, teacher_id = ?,\s
                content = ?, file_url = ?, submitted_at = ?, status = ?,\s
                graded_id = ?, score = ?, feedback = ?, graded_at = ?
            WHERE id = ?
       \s""";

        Connection conn = DbConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setString(1, item.getAssignmentId());
        stmt.setString(2, item.getStudentId());
        stmt.setString(3, item.getGroupId());
        stmt.setString(4, item.getTeacherId());
        stmt.setString(5, item.getContent());
        stmt.setString(6, item.getFileUrl());
        stmt.setTimestamp(7, Timestamp.valueOf(item.getSubmittedAt() != null ? item.getSubmittedAt() : LocalDateTime.now()));
        stmt.setString(8, item.getStatus() != null ? item.getStatus().name() : SubmissionStatus.LATE.name());
        stmt.setString(9, item.getGradedId());

        if (item.getScore() != null) {
            stmt.setInt(10, item.getScore());
        } else {
            stmt.setNull(10, Types.INTEGER);
        }

        stmt.setString(11, item.getFeedback());

        if (item.getGradedAt() != null) {
            stmt.setTimestamp(12, Timestamp.valueOf(item.getGradedAt()));
        } else {
            stmt.setNull(12, Types.TIMESTAMP);
        }

        stmt.setString(13, item.getId());
        stmt.executeUpdate();
    }

    @Override
    public void save(Submission item) throws SQLException {
        if (exists(item.getId())) {
            update(item);
        } else {
            create(item);
        }
    }

    @Override
    public List<Submission> getAll() throws SQLException {
        return executeQuery("SELECT * FROM submissions ORDER BY submitted_at DESC");
    }

    @Override
    public List<Submission> findAll() {
        try {
            return getAll();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Submission> findById(String id) throws SQLException {
        String sql = "SELECT * FROM submissions WHERE id = ?";

        Connection conn = DbConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, id);

        ResultSet resultSet = stmt.executeQuery();

        if (resultSet.next()) {
            return Optional.of(mapResultSetToSubmission(resultSet));
        }

        return Optional.empty();
    }

    @Override
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM submissions WHERE id = ?";
        return getConnections(id, sql);
    }

    public List<Submission> findByAssignmentId(String assignmentId) throws SQLException {
        return executeQueryWithParam("SELECT * FROM submissions WHERE assignment_id = ? ORDER BY submitted_at DESC", assignmentId);
    }

    public Optional<Submission> findByAssignmentAndStudent(String assignmentId, String studentId) throws SQLException {
        String sql = "SELECT * FROM submissions WHERE assignment_id = ? AND student_id = ?";

        Connection conn = DbConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setString(1, assignmentId);
        stmt.setString(2, studentId);

        ResultSet resultSet = stmt.executeQuery();

        if (resultSet.next()) {
            return Optional.of(mapResultSetToSubmission(resultSet));
        }

        return Optional.empty();
    }

    public List<Submission> findByStudent(String studentId) throws SQLException {
        return executeQueryWithParam("SELECT * FROM submissions WHERE student_id = ? ORDER BY submitted_at DESC", studentId);
    }

    public List<Submission> findByGroup(String groupId) throws SQLException {
        return executeQueryWithParam("SELECT * FROM submissions WHERE group_id = ? ORDER BY submitted_at DESC", groupId);
    }

    public List<Submission> findByStatus(SubmissionStatus status) throws SQLException {
        return executeQueryWithParam("SELECT * FROM submissions WHERE status = ? ORDER BY submitted_at DESC", status.name());
    }

    public boolean gradeSubmission(String submissionId, Integer score, String feedback, String gradedId) throws SQLException {
        String sql = "UPDATE submissions SET score = ?, feedback = ?, graded_id = ?, graded_at = ?, status = ? WHERE id = ?";

        Connection conn = DbConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setInt(1, score);
        stmt.setString(2, feedback);
        stmt.setString(3, gradedId);
        stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
        stmt.setString(5, SubmissionStatus.GRADED.name());
        stmt.setString(6, submissionId);

        return stmt.executeUpdate() > 0;
    }

    // Вспомогательные методы
    private List<Submission> executeQuery(String sql) throws SQLException {
        List<Submission> list = new ArrayList<>();

        Connection conn = DbConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            list.add(mapResultSetToSubmission(rs));
        }

        return list;
    }

    private List<Submission> executeQueryWithParam(String sql, String param) throws SQLException {
        Connection conn = DbConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);

        List<Submission> list = new ArrayList<>();

        stmt.setString(1, param);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            list.add(mapResultSetToSubmission(rs));
        }

        return list;
    }

    private Submission mapResultSetToSubmission(ResultSet rs) throws SQLException {
        Submission submission = new Submission();
        submission.setId(rs.getString("id"));
        submission.setAssignmentId(rs.getString("assignment_id"));
        submission.setStudentId(rs.getString("student_id"));
        submission.setGroupId(rs.getString("group_id"));
        submission.setTeacherId(rs.getString("teacher_id"));
        submission.setContent(rs.getString("content"));
        submission.setFileUrl(rs.getString("file_url"));

        Timestamp submittedAt = rs.getTimestamp("submitted_at");
        if (submittedAt != null) {
            submission.setSubmittedAt(submittedAt.toLocalDateTime());
        }

        String status = rs.getString("status");
        if (status != null) {
            try {
                submission.setStatus(SubmissionStatus.valueOf(status));
            } catch (IllegalArgumentException e) {
                submission.setStatus(SubmissionStatus.LATE);
            }
        }

        submission.setGradedId(rs.getString("graded_id"));
        int score = rs.getInt("score");
        submission.setScore(rs.wasNull() ? null : score);
        submission.setFeedback(rs.getString("feedback"));

        Timestamp gradedAt = rs.getTimestamp("graded_at");
        if (gradedAt != null) {
            submission.setGradedAt(gradedAt.toLocalDateTime());
        }

        return submission;
    }
}
