package com.university.moodle.dao;

import com.university.moodle.config.DbConfig;
import com.university.moodle.model.Group;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GroupDAO extends AbstractDAO<Group> {
    private static GroupDAO groupDAO;

    private GroupDAO() {}

    public static GroupDAO getInstance() {
        if (groupDAO == null) {
            groupDAO = new GroupDAO();
        }
        return groupDAO;
    }

    @Override
    protected void setId(Group group, String id) {
        group.setId(id);
    }

    @Override
    protected String getId(Group group) {
        return group.getId();
    }

    @Override
    public List<Group> getItems() throws SQLException {
        return getAll();
    }

    @Override
    public void create(Group item) throws SQLException {
        String sql = """
            INSERT INTO groups (id, group_name, description, student_ids, teacher_ids, assignment_ids)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getId());
            stmt.setString(2, item.getGroupName());
            stmt.setString(3, item.getDescription());
            stmt.setArray(4, conn.createArrayOf("VARCHAR",
                    item.getStudentIDs() != null ? item.getStudentIDs().toArray() : new String[0]));
            stmt.setArray(5, conn.createArrayOf("VARCHAR",
                    item.getTeacherIDs() != null ? item.getTeacherIDs().toArray() : new String[0]));
            stmt.setArray(6, conn.createArrayOf("VARCHAR",
                    item.getAssignmentIDs() != null ? item.getAssignmentIDs().toArray() : new String[0]));

            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Group item) throws SQLException {
        String sql = """
            UPDATE groups
            SET group_name = ?,
                description = ?,
                student_ids = ?,
                teacher_ids = ?,
                assignment_ids = ?
            WHERE id = ?""";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getGroupName());
            stmt.setString(2, item.getDescription());
            stmt.setArray(3, conn.createArrayOf("VARCHAR",
                    item.getStudentIDs() != null ? item.getStudentIDs().toArray() : new String[0]));
            stmt.setArray(4, conn.createArrayOf("VARCHAR",
                    item.getTeacherIDs() != null ? item.getTeacherIDs().toArray() : new String[0]));
            stmt.setArray(5, conn.createArrayOf("VARCHAR",
                    item.getAssignmentIDs() != null ? item.getAssignmentIDs().toArray() : new String[0]));
            stmt.setString(6, item.getId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void save(Group item) throws SQLException {
        if (exists(item.getId())) {
            update(item);
        } else {
            create(item);
        }
    }

    @Override
    public List<Group> getAll() throws SQLException {
        String query = "SELECT * FROM groups ORDER BY group_name";
        List<Group> groups = new ArrayList<>();

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                groups.add(mapResultSetToGroup(rs));
            }
        }

        return groups;
    }

    @Override
    public List<Group> findAll() {
        try {
            return getAll();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Group> findById(String id) {
        String sql = "SELECT * FROM groups WHERE id = ?";

        return getGroupById(id, sql);
    }

    private Optional<Group> getGroupById(String id, String sql) {
        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGroup(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM groups WHERE id = ?";

        return getConnections(id, sql);
    }

    static boolean getConnections(String id, String sql) {
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

    public Optional<Group> findByGroupName(String groupName) {
        String sql = "SELECT * FROM groups WHERE group_name ILIKE ?";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + groupName + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGroup(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public List<Group> findByTeacherId(String teacherId) {
        String sql = "SELECT * FROM groups WHERE ? = ANY(teacher_ids)";
        List<Group> groups = new ArrayList<>();

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, teacherId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    groups.add(mapResultSetToGroup(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groups;
    }

    public Optional<Group> findByStudentId(String studentId) {
        String sql = "SELECT * FROM groups WHERE ? = ANY(student_ids)";

        return getGroupById(studentId, sql);
    }

    public boolean addStudentToGroup(String groupId, String studentId) {
        Optional<Group> groupOpt = findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();

            if (!group.getStudentIDs().contains(studentId)) {
                group.getStudentIDs().add(studentId);
                try {
                    save(group);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean removeStudentFromGroup(String groupId, String studentId) {
        Optional<Group> groupOpt = findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            boolean removed = group.getStudentIDs().remove(studentId);

            if (removed) {
                try {
                    save(group);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean addTeacherToGroup(String groupId, String teacherId) {
        Optional<Group> groupOpt = findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();

            if (!group.getTeacherIDs().contains(teacherId)) {
                group.getTeacherIDs().add(teacherId);
                try {
                    save(group);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean removeTeacherFromGroup(String groupId, String teacherId) {
        Optional<Group> groupOpt = findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            boolean removed = group.getTeacherIDs().remove(teacherId);

            if (removed) {
                try {
                    save(group);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean addAssignmentToGroup(String groupId, String assignmentId) {
        Optional<Group> groupOpt = findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();

            if (!group.getAssignmentIDs().contains(assignmentId)) {
                group.getAssignmentIDs().add(assignmentId);
                try {
                    save(group);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean removeAssignmentFromGroup(String groupId, String assignmentId) {
        Optional<Group> groupOpt = findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            boolean removed = group.getAssignmentIDs().remove(assignmentId);

            if (removed) {
                try {
                    save(group);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean existsByGroupName(String groupName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM groups WHERE group_name = ?";

        Connection connection = DbConfig.getConnection();

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, groupName);

        statement.execute();
        ResultSet rs = statement.getResultSet();

        if (rs.next()) {
            int count = rs.getInt(1);
            return count > 0;
        }

        return false;
    }

    public int getStudentCount(String groupId) {
        Optional<Group> groupOpt = findById(groupId);
        return groupOpt.map(group -> group.getStudentIDs().size()).orElse(0);
    }

    public int getTeacherCount(String groupId) {
        Optional<Group> groupOpt = findById(groupId);
        return groupOpt.map(group -> group.getTeacherIDs().size()).orElse(0);
    }

    public List<Group> findEmptyGroups() {
        String sql = "SELECT * FROM groups WHERE array_length(student_ids, 1) IS NULL OR array_length(student_ids, 1) = 0";
        List<Group> groups = new ArrayList<>();

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                groups.add(mapResultSetToGroup(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groups;
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM groups WHERE id = ?";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Group mapResultSetToGroup(ResultSet rs) throws SQLException {
        return Group.builder()
                .id(rs.getString("id"))
                .groupName(rs.getString("group_name"))
                .description(rs.getString("description"))
                .studentIDs(convertSqlArrayToList(rs, "student_ids"))
                .teacherIDs(convertSqlArrayToList(rs, "teacher_ids"))
                .assignmentIDs(convertSqlArrayToList(rs, "assignment_ids"))
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
