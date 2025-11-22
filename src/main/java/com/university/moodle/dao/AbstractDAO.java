package com.university.moodle.dao;

import com.university.moodle.dao.daoImpl.CheckForExisting;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDAO<T> implements CheckForExisting {
    protected abstract void setId(T t, String id);

    public abstract List<T> getItems() throws SQLException;

    protected abstract String getId(T item);

    public abstract void create(T item) throws SQLException;

    public abstract void update(T item) throws SQLException;

    public void save(T item) throws SQLException {
        String id = getId(item);

        if (id == null || id.isEmpty()) {
            create(item);
        }

        if (exists(id)) {
            update(item);
        }
    }

    public abstract List<T> getAll() throws SQLException;

    public abstract List<T> findAll() throws SQLException;

    public abstract Optional<T> findById(String groupId) throws SQLException;
}
