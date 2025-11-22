package com.university.moodle.dao;

import com.university.moodle.config.DbConfig;
import com.university.moodle.enums.UserRole;
import com.university.moodle.model.Teacher;

import java.sql.*;
import java.util.*;

import static com.university.moodle.dao.StudentDAO.getConnect;

public class TeacherDAO extends AbstractDAO<Teacher> {
    private static TeacherDAO teacherDAO;

    private TeacherDAO() {}

    public static TeacherDAO getInstance() {
        if (teacherDAO == null) {
            teacherDAO = new TeacherDAO();
        }

        return teacherDAO;
    }

    @Override
    protected void setId(Teacher teacher, String id) {
        teacher.setId(id);
    }

    @Override
    protected String getId(Teacher teacher) {
        return teacher.getId();
    }

    @Override
    public List<Teacher> getItems() throws SQLException {
        String query = """
            SELECT u.*, t.specialization, t.group_ids, t.assignment_ids
            FROM users u
            INNER JOIN teachers t ON u.id = t.user_id
        """;

        return teachersList(query);
    }

    private List<Teacher> teachersList(String query) throws SQLException {
        List<Teacher> teachers = new ArrayList<>();

        try (Connection connection = DbConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                teachers.add(mapResultSetToTeacher(rs));
            }
        }

        return teachers;
    }

    @Override
    public void create(Teacher teacher) throws SQLException {
        String userSql = """
        INSERT INTO users (
            id, user_name, full_name, email, password,
            active, created_at, updated_at, role
        ) VALUES (
            ?, ?, ?, ?, ?, true, NOW(), NOW(), ?
        )
        """;

        String teacherSql = """
        INSERT INTO teachers (user_id, specialization, group_ids, assignment_ids)
        VALUES (?, ?, ?, ?)
        """;

        String userId = UUID.randomUUID().toString();
        Connection conn = DbConfig.getConnection();

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                ps.setString(1, userId);
                ps.setString(2, getUserNameOrEmail(teacher));
                ps.setString(3, teacher.getFullName());
                ps.setString(4, teacher.getEmail());
                ps.setString(5, teacher.getPassword());
                ps.setString(6, teacher.getRole().name());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(teacherSql)) {
                ps.setString(1, userId);
                ps.setString(2, teacher.getSpecialization());

                ps.setArray(3, conn.createArrayOf("text", new String[0]));
                ps.setArray(4, conn.createArrayOf("text", new String[0]));

                ps.executeUpdate();
            }

            conn.commit();
            teacher.setId(userId);

        } catch (SQLException e) {
            safeRollback(conn);
            throw e;
        } finally {
            safeSetAutoCommitAndClose(conn);
        }
    }

    private String getUserNameOrEmail(Teacher t) {
        return t.getUserName() != null && !t.getUserName().isBlank()
                ? t.getUserName()
                : t.getEmail();
    }

    private void safeRollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void safeSetAutoCommitAndClose(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void update(Teacher item) throws SQLException {
        String userQuery = """
            UPDATE users
            SET user_name = ?, full_name = ?, email = ?, password = ?, active = ?, updated_at = NOW()
            WHERE id = ?
        """;

        String teacherQuery = """
            UPDATE teachers
            SET specialization = ?, group_ids = ?, assignment_ids = ?
            WHERE user_id = ?
        """;

        Connection conn = DbConfig.getConnection();

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(userQuery)) {
                stmt.setString(1, item.getUserName());
                stmt.setString(2, item.getFullName());
                stmt.setString(3, item.getEmail());
                stmt.setString(4, item.getPassword());
                stmt.setBoolean(5, item.isActive());
                stmt.setString(6, item.getId());
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(teacherQuery)) {
                stmt.setString(1, item.getSpecialization());
                stmt.setArray(2, conn.createArrayOf("text",
                        item.getGroupID() != null ? item.getGroupID().toArray() : new String[0]));
                stmt.setArray(3, conn.createArrayOf("text",
                        item.getAssignmentID() != null ? item.getAssignmentID().toArray() : new String[0]));
                stmt.setString(4, item.getId());
                stmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    @Override
    public void save(Teacher item) throws SQLException {
        if (exists(item.getId())) {
            update(item);
        } else {
            create(item);
        }
    }

    @Override
    public List<Teacher> getAll() throws SQLException {
        return getItems();
    }

    @Override
    public List<Teacher> findAll() throws SQLException {
        return getItems();
    }

    @Override
    public Optional<Teacher> findById(String id) throws SQLException {
        String query = """
            SELECT u.*, t.specialization, t.group_ids, t.assignment_ids
            FROM users u
            LEFT JOIN teachers t ON u.id = t.user_id
            WHERE u.id = ?
        """;

        return optimizeConnection(id, query);
    }

    public Optional<Teacher> findByEmail(String email) throws SQLException {
        String query = """
            SELECT u.*, t.specialization, t.group_ids, t.assignment_ids
            FROM users u
            LEFT JOIN teachers t ON u.id = t.user_id
            WHERE u.email = ? AND u.role = 'TEACHER'
        """;

        return optimizeConnection(email, query);
    }

    private Optional<Teacher> optimizeConnection(String param, String query) throws SQLException {
        try (Connection connection = DbConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, param);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTeacher(rs));
                }
            }
        }

        return Optional.empty();
    }

    public boolean existsByEmail(String email) throws SQLException {
        String sql = """
            SELECT COUNT(*) 
            FROM users u
            INNER JOIN teachers t ON u.id = t.user_id
            WHERE u.email = ?
        """;
        return checkExists(email, sql);
    }

    private boolean checkExists(String param, String sql) throws SQLException {
        return getConnect(param, sql);
    }

    @Override
    public boolean exists(String id) throws SQLException {
        String query = """
            SELECT COUNT(*)
            FROM users u
            INNER JOIN teachers t ON u.id = t.user_id
            WHERE u.id = ?
        """;

        return checkExists(id, query);
    }

    private Teacher mapResultSetToTeacher(ResultSet rs) throws SQLException {
        Teacher teacher = new Teacher();

        teacher.setId(rs.getString("id"));
        teacher.setUserName(rs.getString("user_name"));
        teacher.setFullName(rs.getString("full_name"));
        teacher.setEmail(rs.getString("email"));
        teacher.setPassword(rs.getString("password"));
        teacher.setActive(rs.getBoolean("active"));
        teacher.setSpecialization(rs.getString("specialization"));
        teacher.setGroupID(convertSqlArrayToList(rs, "group_ids"));
        teacher.setAssignmentID(convertSqlArrayToList(rs, "assignment_ids"));
        teacher.setRole(UserRole.valueOf(rs.getString("role")));

        return teacher;
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
