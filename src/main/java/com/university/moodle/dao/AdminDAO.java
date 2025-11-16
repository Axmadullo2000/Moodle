package com.university.moodle.dao;

import com.university.moodle.model.Admin;

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
    public List<Admin> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    protected void setId(Admin admin, String id) {
        admin.setId(id);
    }

    @Override
    protected String getId(Admin admin) {
        return admin.getId();
    }

    /**
     * Найти администратора по email
     */
    public Optional<Admin> findByEmail(String email) {
        return items.stream()
                .filter(admin -> admin.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    /**
     * Проверить существование администратора по email
     */
    public boolean existsByEmail(String email) {
        return items.stream()
                .anyMatch(admin -> admin.getEmail().equalsIgnoreCase(email));
    }

    /**
     * Найти всех активных администраторов
     */
    public List<Admin> findActiveAdmins() {
        return items.stream()
                .filter(Admin::isActive)
                .toList();
    }

    /**
     * Деактивировать администратора
     */
    public boolean deactivateAdmin(String adminId) {
        Optional<Admin> adminOpt = findById(adminId);

        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            admin.setActive(false);
            return true;
        }

        return false;
    }

    /**
     * Активировать администратора
     */
    public boolean activateAdmin(String adminId) {
        Optional<Admin> adminOpt = findById(adminId);

        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            admin.setActive(true);
            return true;
        }

        return false;
    }
}
