package com.university.moodle.dao;

import com.university.moodle.config.DbConfig;
import com.university.moodle.model.Grade;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GradeDAO extends AbstractDAO<Grade> {
    private static GradeDAO instance;

    private GradeDAO() {}

    public static GradeDAO getInstance() {
        if (instance == null) {
            instance = new GradeDAO();
        }
        return instance;
    }

    @Override
    protected void setId(Grade grade, String id) {
        grade.setId(id);
    }

    @Override
    protected String getId(Grade item) {
        return item.getId();
    }

    @Override
    public List<Grade> getItems() {
        try {
            return getAll();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void create(Grade item) throws SQLException {
        String sql = """
            INSERT INTO grades (id, submission_id, score, feedback, graded_at)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getId());
            stmt.setString(2, item.getSubmissionID());

            if (item.getScore() != null) {
                stmt.setInt(3, item.getScore());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, item.getFeedback());
            stmt.setTimestamp(5, Timestamp.valueOf(item.getGradedAt() != null
                    ? item.getGradedAt()
                    : LocalDateTime.now()));

            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Grade item) throws SQLException {
        String sql = """
            UPDATE grades 
            SET submission_id = ?, 
                score = ?, 
                feedback = ?, 
                graded_at = ?
            WHERE id = ?
        """;

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getSubmissionID());

            if (item.getScore() != null) {
                stmt.setInt(2, item.getScore());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, item.getFeedback());
            stmt.setTimestamp(4, Timestamp.valueOf(item.getGradedAt() != null
                    ? item.getGradedAt()
                    : LocalDateTime.now()));
            stmt.setString(5, item.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void save(Grade item) throws SQLException {
        if (exists(item.getId())) {
            update(item);
        } else {
            create(item);
        }
    }

    @Override
    public List<Grade> getAll() throws SQLException {
        String sql = "SELECT * FROM grades ORDER BY graded_at DESC";
        List<Grade> grades = new ArrayList<>();

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                grades.add(mapResultSetToGrade(rs));
            }
        }

        return grades;
    }

    @Override
    public List<Grade> findAll() {
        try {
            return getAll();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Grade> findById(String id) {
        String sql = "SELECT * FROM grades WHERE id = ?";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGrade(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM grades WHERE id = ?";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Optional<Grade> findBySubmissionId(String submissionId) {
        String sql = "SELECT * FROM grades WHERE submission_id = ?";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, submissionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGrade(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public List<Grade> findByScoreRange(int minScore, int maxScore) {
        String sql = "SELECT * FROM grades WHERE score BETWEEN ? AND ? ORDER BY score DESC";
        List<Grade> grades = new ArrayList<>();

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, minScore);
            stmt.setInt(2, maxScore);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapResultSetToGrade(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return grades;
    }

    public List<Grade> findGradesAbove(int minScore) {
        String sql = "SELECT * FROM grades WHERE score >= ? ORDER BY score DESC";
        List<Grade> grades = new ArrayList<>();

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, minScore);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapResultSetToGrade(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return grades;
    }

    public List<Grade> findRecentGrades() {
        String sql = "SELECT * FROM grades";
        List<Grade> grades = new ArrayList<>();

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapResultSetToGrade(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return grades;
    }

    public Double getAverageScore() {
        String sql = "SELECT AVG(score) FROM grades WHERE score IS NOT NULL";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    public Integer getHighestScore() {
        String sql = "SELECT MAX(score) FROM grades WHERE score IS NOT NULL";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public Integer getLowestScore() {
        String sql = "SELECT MIN(score) FROM grades WHERE score IS NOT NULL";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getTotalGradesCount() {
        String sql = "SELECT COUNT(*) FROM grades";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM grades WHERE id = ?";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteBySubmissionId(String submissionId) {
        String sql = "DELETE FROM grades WHERE submission_id = ?";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, submissionId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Grade mapResultSetToGrade(ResultSet rs) throws SQLException {
        Grade grade = new Grade();

        grade.setId(rs.getString("id"));
        grade.setSubmissionID(rs.getString("submission_id"));

        int score = rs.getInt("score");
        grade.setScore(rs.wasNull() ? null : score);

        grade.setFeedback(rs.getString("feedback"));

        Timestamp gradedAt = rs.getTimestamp("graded_at");
        if (gradedAt != null) {
            grade.setGradedAt(gradedAt.toLocalDateTime());
        }

        return grade;
    }
}
