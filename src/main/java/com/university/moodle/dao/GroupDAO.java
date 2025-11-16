package com.university.moodle.dao;

import com.university.moodle.model.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GroupDAO extends AbstractDAO<Group> {
    private static GroupDAO groupDAO;

    private GroupDAO() {}

    public static GroupDAO getInstance() {
        if (groupDAO == null) {
            groupDAO = new GroupDAO();
        }
        return groupDAO;
    }

    @Override
    public List<Group> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    protected void setId(Group group, String id) {
        group.setId(id);
    }

    @Override
    protected String getId(Group group) {
        return group.getId();
    }

    /**
     * Найти группу по имени
     */
    public Optional<Group> findByGroupName(String groupName) {
        return items.stream()
                .filter(group -> group.getGroupName().equalsIgnoreCase(groupName))
                .findFirst();
    }

    /**
     * Найти группы по ID учителя
     */
    public List<Group> findByTeacherId(String teacherId) {
        return items.stream()
                .filter(group -> group.getTeacherIDs() != null &&
                        group.getTeacherIDs().contains(teacherId))
                .collect(Collectors.toList());
    }

    /**
     * Найти группу по ID студента
     */
    public Optional<Group> findByStudentId(String studentId) {
        return items.stream()
                .filter(group -> group.getStudentIDs() != null &&
                        group.getStudentIDs().contains(studentId))
                .findFirst();
    }

    /**
     * Добавить студента в группу
     */
    public boolean addStudentToGroup(String groupId, String studentId) {
        Optional<Group> groupOpt = findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();

            if (group.getStudentIDs() == null) {
                group.setStudentIDs(new ArrayList<>());
            }

            if (!group.getStudentIDs().contains(studentId)) {
                group.getStudentIDs().add(studentId);
                save(group);  // ← ДОБАВЬ ЭТУ СТРОЧКУ!
                System.out.println("Student " + studentId + " added to group " + groupId);
                return true;
            }
        }
        return false;
    }

    /**
     * Удалить студента из группы
     */
    public boolean removeStudentFromGroup(String groupId, String studentId) {
        Optional<Group> groupOpt = findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();

            if (group.getStudentIDs() != null) {
                boolean removed = group.getStudentIDs().remove(studentId);
                if (removed) {
                    save(group);  // ← ДОБАВЬ ЭТУ СТРОЧКУ!
                    System.out.println("Student " + studentId + " removed from group " + groupId);
                }
                return removed;
            }
        }
        return false;
    }

    /**
     * Добавить учителя в группу
     */
    public boolean addTeacherToGroup(String groupId, String teacherId) {
        Optional<Group> groupOpt = findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();

            if (group.getTeacherIDs() == null) {
                group.setTeacherIDs(new ArrayList<>());
            }

            if (!group.getTeacherIDs().contains(teacherId)) {
                group.getTeacherIDs().add(teacherId);
                save(group);  // ДОБАВИТЬ!
                System.out.println("Teacher " + teacherId + " added to group " + groupId);
                return true;
            }
        }

        return false;
    }

    /**
     * Удалить учителя из группы
     */
    public boolean removeTeacherFromGroup(String groupId, String teacherId) {
        Optional<Group> groupOpt = findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();

            // ГАРАНТИРУЕМ, что список мутабельный
            if (group.getTeacherIDs() == null) {
                group.setTeacherIDs(new ArrayList<>()); // пустой, но изменяемый
            } else if (!(group.getTeacherIDs() instanceof ArrayList)) {
                // Если кто-то поставил immutable список — заменяем на ArrayList
                group.setTeacherIDs(new ArrayList<>(group.getTeacherIDs()));
            }

            boolean removed = group.getTeacherIDs().remove(teacherId);

            if (removed) {
                System.out.println("Teacher " + teacherId + " removed from group " + groupId);
                save(group); // важно!
            }
            return removed;
        }

        return false;
    }

    /**
     * Добавить задание в группу
     */
    // В методе addAssignmentToGroup() — замени полностью на этот:
    public boolean addAssignmentToGroup(String groupId, String assignmentId) {
        Optional<Group> opt = findById(groupId);
        if (opt.isEmpty()) {
            System.err.println("Группа не найдена по ID: " + groupId);
            return false;
        }

        Group group = opt.get();

        if (group.getAssignmentIDs() == null) {
            group.setAssignmentIDs(new ArrayList<>());
        }

        if (group.getAssignmentIDs().contains(assignmentId)) {
            System.out.println("Задание уже есть в группе");
            save(group); // всё равно сохраняем на всякий
            return true;
        }

        boolean add = group.getAssignmentIDs().add(assignmentId);
        save(group); // ← ЭТО ГЛАВНОЕ

        System.out.println("УСПЕШНО: Задание " + assignmentId + " добавлено в группу " + group.getGroupName() + " (ID: " + groupId + ")");
        return add;
    }

    /**
     * Получить количество студентов в группе
     */
    public int getStudentCount(String groupId) {
        Optional<Group> groupOpt = findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            return group.getStudentIDs() != null ? group.getStudentIDs().size() : 0;
        }

        return 0;
    }

    /**
     * Получить количество учителей в группе
     */
    public int getTeacherCount(String groupId) {
        Optional<Group> groupOpt = findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            return group.getTeacherIDs() != null ? group.getTeacherIDs().size() : 0;
        }

        return 0;
    }

    /**
     * Проверить существование группы по имени
     */
    public boolean existsByGroupName(String groupName) {
        return items.stream()
                .anyMatch(group -> group.getGroupName().equalsIgnoreCase(groupName));
    }

    /**
     * Удалить студента из всех групп
     */
    public void removeStudentFromAllGroups(String studentId) {
        items.forEach(group -> {
            if (group.getStudentIDs() != null) {
                group.getStudentIDs().remove(studentId);
            }
        });
        System.out.println("✅ Student " + studentId + " removed from all groups");
    }

    /**
     * Удалить учителя из всех групп
     */
    public void removeTeacherFromAllGroups(String teacherId) {
        items.forEach(group -> {
            if (group.getTeacherIDs() != null) {
                group.getTeacherIDs().remove(teacherId);
            }
        });
        System.out.println("✅ Teacher " + teacherId + " removed from all groups");
    }

    /**
     * Найти группы без учителей
     */
    public List<Group> findGroupsWithoutTeacher() {
        return items.stream()
                .filter(group -> group.getTeacherIDs() == null ||
                        group.getTeacherIDs().isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Найти пустые группы (без студентов)
     */
    public List<Group> findEmptyGroups() {
        return items.stream()
                .filter(group -> group.getStudentIDs() == null ||
                        group.getStudentIDs().isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Получить всех студентов группы
     */
    public List<String> getGroupStudents(String groupId) {
        Optional<Group> groupOpt = findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            return group.getStudentIDs() != null ?
                    new ArrayList<>(group.getStudentIDs()) :
                    new ArrayList<>();
        }

        return new ArrayList<>();
    }

    /**
     * Получить всех учителей группы
     */
    public List<String> getGroupTeachers(String groupId) {
        Optional<Group> groupOpt = findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            return group.getTeacherIDs() != null ?
                    new ArrayList<>(group.getTeacherIDs()) :
                    new ArrayList<>();
        }

        return new ArrayList<>();
    }

    /**
     * Проверить, является ли студент членом группы
     */
    public boolean isStudentInGroup(String groupId, String studentId) {
        Optional<Group> groupOpt = findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            return group.getStudentIDs() != null &&
                    group.getStudentIDs().contains(studentId);
        }

        return false;
    }

    /**
     * Проверить, является ли учитель членом группы
     */
    public boolean isTeacherInGroup(String groupId, String teacherId) {
        Optional<Group> groupOpt = findById(groupId);

        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            return group.getTeacherIDs() != null &&
                    group.getTeacherIDs().contains(teacherId);
        }

        return false;
    }

    @Override
    public Group save(Group group) {
        return super.save(group);
    }

}
