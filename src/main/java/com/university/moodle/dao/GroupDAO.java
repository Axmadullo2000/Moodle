package com.university.moodle.dao;

import com.university.moodle.model.Group;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class GroupDAO extends AbstractDAO<Group> {
    @Override
    public List<Group> getItems() {
        return List.of();
    }

    @Override
    protected void setId(Group group, String id) {
        group.setId(id);
    }

    @Override
    protected String getId(Group group) {
        return group.getId();
    }

    public Optional<Group> findByGroupName(String groupName) {
        return items.stream()
                .filter(group -> group.getGroupName().equalsIgnoreCase(groupName))
                .findFirst();
    }


}
