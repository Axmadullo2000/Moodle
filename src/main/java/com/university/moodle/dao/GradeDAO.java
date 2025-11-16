package com.university.moodle.dao;

import com.university.moodle.model.Grade;

import java.util.List;
import java.util.Optional;


public class GradeDAO extends AbstractDAO<Grade> {
    private static GradeDAO instance;

    private GradeDAO() {}

    public static GradeDAO getInstance() {
        if (instance == null) {
            instance = new GradeDAO();
        }

        return instance;
    }

    @Override
    public List<Grade> getItems() {
        return items;
    }

    @Override
    protected void setId(Grade grades, String id) {
        grades.setId(id);
    }

    @Override
    protected String getId(Grade item) {
        return item.getId();
    }

    public Optional<Grade> findBySubmissionId(String submissionId) {
        return items.stream()
                .filter(grade -> grade.getSubmissionID().equals(submissionId))
                .findFirst();
    }
}
