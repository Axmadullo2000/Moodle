package com.university.moodle.dao;

import com.university.moodle.model.Teacher;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TeacherDAO extends AbstractDAO<Teacher> {
    @Override
    public List<Teacher> getItems() {
        return items;
    }

    @Override
    protected void setId(Teacher teacher, String id) {
        teacher.setId(id);
    }

    @Override
    protected String getId(Teacher teacher) {
        return teacher.getId();
    }

}
