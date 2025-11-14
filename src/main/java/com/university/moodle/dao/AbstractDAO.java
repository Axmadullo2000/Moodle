package com.university.moodle.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractDAO<T> {
    protected final List<T> items = new ArrayList<>();

    public abstract List<T> getItems();
    protected abstract void setId(T item, String id);
    protected abstract String getId(T item);

    public T save(T item) {
        String id = getId(item);

        if (id == null || id.isEmpty()) {
            // Новый объект
            id = UUID.randomUUID().toString();
            setId(item, id);
            items.add(item);
        } else {
            // Обновление существующего
            int index = findIndexById(id);
            if (index >= 0) {
                items.set(index, item);
            } else {
                items.add(item);
            }
        }

        return item;
    }

    public int findIndexById(String id) {
        for (int i = 0; i < items.size(); i++) {
            String itemId = getId(items.get(i));
            if (itemId != null && itemId.equals(id)) {
                return i;
            }
        }

        return -1;
    }

    public Optional<T> findById(String id) {
        return items.stream()
                .filter(t -> {
                    String itemId = getId(t);
                    return itemId != null && itemId.equals(id);
                })
                .findFirst();
    }

    public List<T> findAll() {
        return new ArrayList<>(items);
    }

    public void delete(String id) {
        items.removeIf(item -> {
            String itemId = getId(item);
            return itemId != null && itemId.equals(id);
        });
    }
}
