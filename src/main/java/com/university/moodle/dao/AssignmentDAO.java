package com.university.moodle.dao;

import com.university.moodle.config.DbConfig;
import com.university.moodle.model.Assignment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AssignmentDAO extends AbstractDAO<Assignment> {
    private static AssignmentDAO assignmentDAO;

    private AssignmentDAO() {}

    public static AssignmentDAO getInstance() {
        if (assignmentDAO == null) {
            assignmentDAO = new AssignmentDAO();
        }
        return assignmentDAO;
    }

    @Override
    protected void setId(Assignment assignment, String id) {
        assignment.setId(id);
    }

    @Override
    protected String getId(Assignment assignment) {
        return assignment.getId();
    }

    @Override
    public List<Assignment> getItems() {
        try {
            return getAll();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void create(Assignment item) throws SQLException {
        String sql = """
            INSERT INTO assignments (
                id, title, description, teacher_id, group_id, deadline,
                max_score, created_at, submission_ids, file_path
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getId());
            stmt.setString(2, item.getTitle());
            stmt.setString(3, item.getDescription());
            stmt.setString(4, item.getTeacherId());
            stmt.setString(5, item.getGroupId());
            stmt.setTimestamp(6, Timestamp.valueOf(item.getDeadline()));

            if (item.getMaxScore() != null) {
                stmt.setInt(7, item.getMaxScore());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

            stmt.setArray(9, conn.createArrayOf("VARCHAR",
                    item.getSubmissionID() != null ? item.getSubmissionID().toArray() : new String[0]));

            stmt.setString(10, item.getFilePath());

            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Assignment item) throws SQLException {
        String sql = """
            UPDATE assignments
            SET title = ?,
                description = ?,
                teacher_id = ?,
                group_id = ?,
                deadline = ?,
                max_score = ?,
                submission_ids = ?,
                file_path = ?
            WHERE id = ?
        """;

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getTitle());
            stmt.setString(2, item.getDescription());
            stmt.setString(3, item.getTeacherId());
            stmt.setString(4, item.getGroupId());
            stmt.setTimestamp(5, Timestamp.valueOf(item.getDeadline()));

            if (item.getMaxScore() != null) {
                stmt.setInt(6, item.getMaxScore());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setArray(7, conn.createArrayOf("VARCHAR",
                    item.getSubmissionID() != null ? item.getSubmissionID().toArray() : new String[0]));

            stmt.setString(8, item.getFilePath());
            stmt.setString(9, item.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void save(Assignment item) throws SQLException {
        if (exists(item.getId())) {
            update(item);
        } else {
            create(item);
        }
    }

    @Override
    public List<Assignment> getAll() throws SQLException {
        String query = "SELECT * FROM assignments DESC";
        List<Assignment> assignments = new ArrayList<>();

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                assignments.add(mapResultSetToAssignment(rs));
            }
        }

        return assignments;
    }

    @Override
    public List<Assignment> findAll() {
        try {
            return getAll();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Assignment> findById(String id) {
        String sql = "SELECT * FROM assignments WHERE id = ?";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAssignment(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM assignments WHERE id = ?";

        return GroupDAO.getConnections(id, sql);
    }

    public List<Assignment> findByTeacherId(String teacherId) {
        String query = "SELECT * FROM assignments WHERE teacher_id = ? ORDER BY deadline";
        return getAssignments(teacherId, query);
    }

    public List<Assignment> findByGroupId(String groupId) {
        String query = "SELECT * FROM assignments WHERE group_id = ? ORDER BY deadline";
        return getAssignments(groupId, query);
    }

    private List<Assignment> getAssignments(String groupId, String query) {
        List<Assignment> assignments = new ArrayList<>();

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, groupId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assignments.add(mapResultSetToAssignment(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignments;
    }

    public List<Assignment> findActiveAssignments() {
        String query = "SELECT * FROM assignments WHERE deadline > ? ORDER BY deadline";
        return getAssignments(query);
    }

    private List<Assignment> getAssignments(String query) {
        List<Assignment> assignments = new ArrayList<>();

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assignments.add(mapResultSetToAssignment(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignments;
    }

    public List<Assignment> findExpiredAssignments() {
        String query = "SELECT * FROM assignments WHERE deadline <= ? ORDER BY deadline DESC";
        return getAssignments(query);
    }

    public List<Assignment> findByDeadlineRange(LocalDateTime start, LocalDateTime end) {
        String query = "SELECT * FROM assignments WHERE deadline BETWEEN ? AND ? ORDER BY deadline";
        List<Assignment> assignments = new ArrayList<>();

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assignments.add(mapResultSetToAssignment(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignments;
    }

    public boolean isActiveAssignment(String assignmentId) {
        Optional<Assignment> assignmentOpt = findById(assignmentId);

        if (assignmentOpt.isPresent()) {
            Assignment assignment = assignmentOpt.get();
            return assignment.getDeadline().isAfter(LocalDateTime.now());
        }

        return false;
    }

    public boolean addSubmissionToAssignment(String assignmentId, String submissionId) {
        Optional<Assignment> assignmentOpt = findById(assignmentId);

        if (assignmentOpt.isPresent()) {
            Assignment assignment = assignmentOpt.get();

            if (assignment.getSubmissionID() == null) {
                assignment.setSubmissionID(new ArrayList<>());
            }

            if (!assignment.getSubmissionID().contains(submissionId)) {
                assignment.getSubmissionID().add(submissionId);
                try {
                    save(assignment);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public boolean removeSubmissionFromAssignment(String assignmentId, String submissionId) {
        Optional<Assignment> assignmentOpt = findById(assignmentId);

        if (assignmentOpt.isPresent()) {
            Assignment assignment = assignmentOpt.get();

            if (assignment.getSubmissionID() != null) {
                boolean removed = assignment.getSubmissionID().remove(submissionId);

                if (removed) {
                    try {
                        save(assignment);
                        return true;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return false;
    }

    public int getSubmissionCount(String assignmentId) {
        Optional<Assignment> assignmentOpt = findById(assignmentId);
        return assignmentOpt.map(assignment ->
                assignment.getSubmissionID() != null ? assignment.getSubmissionID().size() : 0
        ).orElse(0);
    }

    public List<Assignment> findUpcomingAssignments(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(days);

        return findByDeadlineRange(now, future);
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM assignments WHERE id = ?";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Assignment mapResultSetToAssignment(ResultSet rs) throws SQLException {
        return Assignment.builder()
                .id(rs.getString("id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .teacherId(rs.getString("teacher_id"))
                .groupId(rs.getString("group_id"))
                .deadline(rs.getTimestamp("deadline") != null
                        ? rs.getTimestamp("deadline").toLocalDateTime()
                        : null)
                .maxScore(rs.getInt("max_score"))
                .createdAt(LocalDateTime.now())
                .submissionID(convertSqlArrayToList(rs, "submission_ids"))
                .filePath(rs.getString("file_path"))
                .build();
    }

    private List<String> convertSqlArrayToList(ResultSet rs, String columnLabel) throws SQLException {
        Array sqlArray = rs.getArray(columnLabel);

        if (sqlArray != null) {
            String[] array = (String[]) sqlArray.getArray();
            return new ArrayList<>(Arrays.asList(array));
        }

        return new ArrayList<>();
    }
}
