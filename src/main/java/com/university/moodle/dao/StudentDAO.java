package com.university.moodle.dao;

import com.university.moodle.config.DbConfig;
import com.university.moodle.enums.UserRole;
import com.university.moodle.model.Student;

import java.sql.*;
import java.util.*;

public class StudentDAO extends AbstractDAO<Student> {
    private static StudentDAO studentDAO;

    private StudentDAO() {}

    public static StudentDAO getInstance() {
        if (studentDAO == null) {
            studentDAO = new StudentDAO();
        }
        return studentDAO;
    }

    @Override
    protected void setId(Student student, String id) {
        student.setId(id);
    }

    @Override
    protected String getId(Student item) {
        return item.getId();
    }

    @Override
    public List<Student> getItems() throws SQLException {
        String query = """
        SELECT
            u.id,
            u.user_name,
            u.full_name,
            u.email,
            u.active,
            s.group_id
        FROM users u
        LEFT JOIN students s ON u.id = s.user_id
        WHERE u.role = 'STUDENT';
        """;

        List<Student> students = new ArrayList<>();

        Connection connection = DbConfig.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Student student = new Student();
            student.setId(resultSet.getString("id"));
            student.setUserName(resultSet.getString("user_name"));
            student.setFullName(resultSet.getString("full_name"));
            student.setEmail(resultSet.getString("email"));
            student.setActive(resultSet.getBoolean("active"));

            students.add(student);
        }

        return students;
    }

    private List<Student> studentsList(String query) throws SQLException {
        List<Student> students = new ArrayList<>();

        try (Connection connection = DbConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        }

        return students;
    }

    @Override
    public void create(Student item) throws SQLException {
        String userQuery = """
        INSERT INTO users (id, user_name, full_name, email, password, active, created_at, updated_at, role)
        VALUES (?, ?, ?, ?, ?, true, NOW(), NOW(), ?);
        """;

        String studentQuery = """
        INSERT INTO students (user_id, group_id)
        VALUES (?, ?);
        """;

        String newId = UUID.randomUUID().toString();
        Connection conn = DbConfig.getConnection();

        PreparedStatement stmt = conn.prepareStatement(userQuery);
        stmt.setString(1, newId);
        stmt.setString(2, item.getUserName() != null ? item.getUserName() : item.getEmail());
        stmt.setString(3, item.getFullName());
        stmt.setString(4, item.getEmail());
        stmt.setString(5, item.getPassword());
        stmt.setString(6, UserRole.STUDENT.name());
        stmt.executeUpdate();

        // -----

        PreparedStatement stmt2 = conn.prepareStatement(studentQuery);
        stmt2.setString(1, newId);
        stmt2.setString(2, item.getGroupId());
        item.setId(newId);
        stmt2.executeUpdate();
    }

    @Override
    public void update(Student item) throws SQLException {
        Optional<Student> existing = findById(item.getId());
        if (existing.isPresent() && !existing.get().getEmail().equals(item.getEmail())) {
            if (existsByEmail(item.getEmail())) {
                throw new SQLException("Student with email " + item.getEmail() + " already exists");
            }
        }

        String userQuery = """
            UPDATE users
            SET user_name = ?, full_name = ?, email = ?, password = ?, active = ?, updated_at = NOW()
            WHERE id = ?
        """;

        String studentQuery = """
            UPDATE students
            SET group_id = ?
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

            try (PreparedStatement stmt = conn.prepareStatement(studentQuery)) {
                stmt.setString(1, item.getGroupId());
                stmt.setString(2, item.getId());
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
    public void save(Student item) throws SQLException {
        if (exists(item.getId())) {
            update(item);
        } else {
            create(item);
        }
    }

    @Override
    public List<Student> getAll() throws SQLException {
        return getItems();
    }

    @Override
    public List<Student> findAll() throws SQLException {
        return getItems();
    }

    @Override
    public Optional<Student> findById(String id) throws SQLException {
        String query = """
            SELECT u.*, s.group_id
                    FROM users u
                             LEFT JOIN students s ON u.id = s.user_id
                    WHERE u.id = ? AND u.role = 'STUDENT'
        """;

        return findByParam(id, query);
    }

    /*public Optional<Student> findByEmail(String email) throws SQLException {
        String query = """
            SELECT u.*, u.group_id
            FROM users u
            LEFT JOIN students s ON u.id = s.user_id
            WHERE u.email = ? AND u.role = 'STUDENT'
        """;

        return findByParam(email, query);
    }*/

    public Optional<Student> findByEmail(String email) throws SQLException {
        String query = """
        SELECT u.*, s.group_id
        FROM users u
        LEFT JOIN students s ON u.id = s.user_id
        WHERE u.email = ?""";

        System.out.println("StudentDAO.findByEmail query: " + query);
        System.out.println("StudentDAO.findByEmail param: " + email);

        return findByParam(email, query);
    }

    private Optional<Student> findByParam(String param, String query) throws SQLException {
        try (Connection connection = DbConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, param);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        }

        return Optional.empty();
    }

    public boolean existsByEmail(String email) throws SQLException {
        String query = """
            SELECT COUNT(*)
            FROM users u
            LEFT JOIN students s ON u.id = s.user_id
            WHERE u.email = ? AND u.role = 'STUDENT'
        """;
        return checkExists(email, query);
    }

    @Override
    public boolean exists(String id) throws SQLException {
        String query = """
            SELECT COUNT(*)
            FROM users u
            LEFT JOIN students s ON u.id = s.user_id
            WHERE u.id = ? AND u.role = 'STUDENT'
        """;
        return checkExists(id, query);
    }

    private boolean checkExists(String param, String query) throws SQLException {
        return getConnect(param, query);
    }

    static boolean getConnect(String param, String query) throws SQLException {
        try (Connection connection = DbConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, param);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();

        student.setId(rs.getString("id"));
        student.setUserName(rs.getString("user_name"));
        student.setFullName(rs.getString("full_name"));
        student.setEmail(rs.getString("email"));
        student.setPassword(rs.getString("password"));
        student.setActive(rs.getBoolean("active"));
        student.setGroupId(rs.getString("group_id"));
        student.setRole(UserRole.valueOf(rs.getString("role")));

        return student;
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
