package com.university.moodle.dao;


import com.university.moodle.model.Admin;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AdminDAO extends AbstractDAO<Admin> {
    @Override
    public List<Admin> getItems() {
        return items;
    }

    @Override
    protected void setId(Admin admin, String id) {
        admin.setId(id);
    }

    @Override
    protected String getId(Admin admin) {
        return admin.getId();
    }
}
