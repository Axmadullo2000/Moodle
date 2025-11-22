package com.university.moodle.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConfig {
    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");

            // Проверка соединения
            Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/moodle",
                    "postgres", // Имя пользователя
                    "2001"  // Пароль
            );

            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }
}
