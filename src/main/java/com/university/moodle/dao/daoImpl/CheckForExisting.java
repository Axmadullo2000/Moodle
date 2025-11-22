package com.university.moodle.dao.daoImpl;

import java.sql.SQLException;

public interface CheckForExisting {
    boolean exists(String id) throws SQLException;
}
