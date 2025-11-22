package com.university.moodle.dao;

import com.university.moodle.config.DbConfig;
import com.university.moodle.enums.UserRole;
import com.university.moodle.model.Admin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminDAO extends AbstractDAO<Admin> {
    private static AdminDAO adminDAO;

    private AdminDAO() {}

    public static AdminDAO getInstance() {
        if (adminDAO == null) {
            adminDAO = new AdminDAO();
        }
        return adminDAO;
    }

    @Override
    protected void setId(Admin admin, String id) {
        admin.setId(id);
    }

    @Override
    protected String getId(Admin admin) {
        return admin.getId();
    }

    @Override
    public List<Admin> getItems() {
        try {
            return getAll();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void create(Admin item) throws SQLException {
        Connection conn = DbConfig.getConnection();

        try {
            conn.setAutoCommit(false);


            String userSql = """
                INSERT INTO users (id, user_name, full_name, email, password, active, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

            String adminSql = "INSERT INTO admins (user_id, department) VALUES (?, ?)";

            try (PreparedStatement adminStmt = conn.prepareStatement(adminSql)) {
                adminStmt.setString(1, item.getId());
                adminStmt.setString(2, item.getDepartment());

                adminStmt.executeUpdate();
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
    public void update(Admin item) throws SQLException {
        /*Connection conn = DbConfig.getConnection();

        try {
            conn.setAutoCommit(false);

            // Обновляем пользователя
            String userSql = """
                UPDATE users 
                SET user_name = ?, 
                    full_name = ?, 
                    email = ?, 
                    password = ?, 
                    active = ?, 
                    updated_at = ?
                WHERE id = ?
            """;

            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setString(7, item.getId());

                userStmt.executeUpdate();
            }

            // Обновляем администратора
            String adminSql = "UPDATE admins SET department = ? WHERE user_id = ?";

            try (PreparedStatement adminStmt = conn.prepareStatement(adminSql)) {
                adminStmt.setString(1, item.getDepartment());
                adminStmt.setString(2, item.getId());

                adminStmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }*/
    }

    @Override
    public void save(Admin item) throws SQLException {
        if (exists(item.getId())) {
            update(item);
        } else {
            create(item);
        }
    }

    @Override
    public List<Admin> getAll() throws SQLException {
        String query = """
            SELECT u.*, a.department
            FROM admins a
            JOIN users u ON a.user_id = u.id
            ORDER BY u.full_name
        """;

        List<Admin> admins = new ArrayList<>();

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                admins.add(mapResultSetToAdmin(rs));
            }
        }

        return admins;
    }

    @Override
    public List<Admin> findAll() {
        try {
            return getAll();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Admin> findById(String id) {
        String query = """
            SELECT u.*, a.department
            FROM admins a
            JOIN users u ON a.user_id = u.id
            WHERE u.id = ?
        """;

        return getAdmin(id, query);
    }

    /*private Optional<Admin> getAdmin(String id, String query) {
        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, id);
            System.out.println("DEBUG: Executing query with param: " + id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("DEBUG: ResultSet found!");
                    return Optional.of(mapResultSetToAdmin(rs));
                }else {
                    System.out.println("DEBUG: No ResultSet found!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }*/

    private Optional<Admin> getAdmin(String id, String query) {
        System.out.println("=== AdminDAO.getAdmin START ===");
        System.out.println("Query: " + query);
        System.out.println("Param: " + id);

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, id);
            System.out.println("PreparedStatement: " + stmt);

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("ResultSet retrieved");

                if (rs.next()) {
                    System.out.println("=== ResultSet HAS DATA ===");

                    // Выведите ВСЕ колонки
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    System.out.println("Total columns: " + columnCount);
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String columnValue = rs.getString(i);
                        System.out.println("  Column[" + i + "]: " + columnName + " = " + columnValue);
                    }

                    Admin admin = mapResultSetToAdmin(rs);
                    System.out.println("Mapped Admin: " + admin);
                    System.out.println("Admin role: " + admin.getRole());
                    return Optional.of(admin);
                } else {
                    System.out.println("=== ResultSet is EMPTY ===");
                }
            }

        } catch (SQLException e) {
            System.out.println("=== SQL EXCEPTION ===");
            System.out.println("Error message: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }

        System.out.println("=== AdminDAO.getAdmin END (returning empty) ===");
        return Optional.empty();
    }

    @Override
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM admins WHERE user_id = ?";

        return GroupDAO.getConnections(id, sql);
    }

    public Optional<Admin> findByEmail(String email) {
        String query = """
            SELECT u.*, a.department
            FROM admins a
            INNER JOIN users u ON a.user_id = u.id
            WHERE u.email = ? AND u.role = 'ADMIN'
        """;

        System.out.println("DEBUG: AdminDAO.findByEmail query: " + query);

        return getAdmin(email, query);
    }

    public Optional<Admin> findByUserName(String userName) {
        String query = """
            SELECT u.*, a.department
            FROM admins a
            JOIN users u ON a.user_id = u.id
            WHERE u.user_name = ?
        """;

        return getAdmin(userName, query);
    }

    public List<Admin> findByDepartment(String department) {
        String query = """
            SELECT u.*, a.department 
            FROM admins a 
            JOIN users u ON a.user_id = u.id 
            WHERE a.department = ?
            ORDER BY u.full_name
        """;

        List<Admin> admins = new ArrayList<>();

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, department);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    admins.add(mapResultSetToAdmin(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return admins;
    }

    public List<Admin> findActiveAdmins() {
        String query = """
            SELECT u.*, a.department 
            FROM admins a 
            JOIN users u ON a.user_id = u.id 
            WHERE u.active = true
            ORDER BY u.full_name
        """;

        List<Admin> admins = new ArrayList<>();

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                admins.add(mapResultSetToAdmin(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return admins;
    }

    public boolean existsByEmail(String email) {
        String query = """
            SELECT COUNT(*) 
            FROM admins a 
            JOIN users u ON a.user_id = u.id 
            WHERE u.email = ?
        """;

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);

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

    public boolean existsByUserName(String userName) {
        String query = """
            SELECT COUNT(*) 
            FROM admins a 
            JOIN users u ON a.user_id = u.id 
            WHERE u.user_name = ?
        """;

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userName);

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

    public boolean delete(String id) {
        // Каскадное удаление настроено в БД, поэтому просто удаляем из users
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deactivateAdmin(String id) {
        String sql = "UPDATE users SET active = false WHERE id = ?";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean activateAdmin(String id) {
        String sql = "UPDATE users SET active = true WHERE id = ?";

        try (Connection conn = DbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();

        admin.setId(rs.getString("id"));
        admin.setUserName(rs.getString("user_name"));
        admin.setFullName(rs.getString("full_name"));
        admin.setEmail(rs.getString("email"));
        admin.setPassword(rs.getString("password"));
        admin.setActive(rs.getBoolean("active"));
        admin.setDepartment(rs.getString("department"));
        admin.setRole(UserRole.valueOf(rs.getString("role")));

        // Отладка
        String roleStr = rs.getString("role");
        System.out.println("DEBUG AdminDAO: role from DB = '" + roleStr + "'");

        if (roleStr != null && !roleStr.isBlank()) {
            admin.setRole(UserRole.valueOf(roleStr));
            System.out.println("DEBUG AdminDAO: role set to = " + admin.getRole());
        } else {
            System.out.println("DEBUG AdminDAO: role is NULL or blank!");
            admin.setRole(UserRole.ADMIN);
        }



        return admin;
    }
}
