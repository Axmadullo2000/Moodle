package com.university.moodle.init;

import com.university.moodle.dao.*;
import com.university.moodle.model.*;
import com.university.moodle.enums.UserRole;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebListener
public class DefaultDataInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        System.out.println("=== Default data loading... ===");

        GroupDAO groupDAO = GroupDAO.getInstance();
        TeacherDAO teacherDAO = TeacherDAO.getInstance();
        StudentDAO studentDAO = StudentDAO.getInstance();

        // --- Default Teacher ---
        Teacher t = new Teacher();
        t.setId(UUID.randomUUID().toString());
        t.setFullName("John Teacher");
        t.setUserName("teacher");
        t.setEmail("teacher@example.com");
        t.setPassword("123456");
        t.setSpecialization("Mathematics");
        t.setRole(UserRole.TEACHER);
        teacherDAO.save(t);
        System.out.println("✓ Teacher created: " + t.getUserName());

        // --- Default Group ---
        Group g = new Group();
        g.setId(UUID.randomUUID().toString());
        g.setGroupName("Group A");
        g.setDescription("Default test group");
        g.setTeacherIDs(List.of(t.getId()));
        g.setStudentIDs(new ArrayList<>());
        groupDAO.save(g);
        System.out.println("✓ Group created: " + g.getGroupName());

        // Привязка группы учителю
        t.setGroupID(List.of(g.getId()));
        teacherDAO.save(t);

        // --- Default Student ---
        Student s = new Student();
        s.setId(UUID.randomUUID().toString());
        s.setFullName("Alice Student");
        s.setUserName("student");
        s.setEmail("student@example.com");
        s.setPassword("123456");
        s.setGroupId(g.getId());
        s.setRole(UserRole.STUDENT);

        studentDAO.save(s);
        System.out.println("✓ Student created: " + s.getUserName());

        // Добавляем студента в группу
        List<String> studentIds = g.getStudentIDs();
        studentIds.add(s.getId());
        g.setStudentIDs(studentIds);
        groupDAO.save(g);
        System.out.println("✓ Student added to group");

        System.out.println("=== Default data loaded successfully ===");
    }
}
