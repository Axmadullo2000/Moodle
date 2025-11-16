package com.university.moodle.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractDAO<T> {
    protected List<T> items = new ArrayList<>();

    public abstract List<T> getItems();
    protected abstract void setId(T item, String id);
    protected abstract String getId(T item);

    /**
     * Создать новый элемент
     */
    public T create(T item) {
        String id = UUID.randomUUID().toString();
        setId(item, id);
        items.add(item);
        return item;
    }

    /**
     * Получить элемент по ID
     */
    public Optional<T> getById(String id) {
        return items.stream()
                .filter(item -> getId(item).equals(id))
                .findFirst();
    }

    /**
     * Альтернативное имя для getById (для обратной совместимости)
     */
    public Optional<T> findById(String id) {
        return getById(id);
    }

    /**
     * Обновить элемент
     */
    public T update(T item) {
        String id = getId(item);
        for (int i = 0; i < items.size(); i++) {
            if (getId(items.get(i)).equals(id)) {
                items.set(i, item);
                return item;
            }
        }
        throw new RuntimeException("Item not found with id: " + id);
    }

    /**
     * Сохранить элемент (создать новый или обновить существующий)
     */
    public T save(T item) {
        String id = getId(item);

        // Если ID null или пустой, создаём новый элемент
        if (id == null || id.isEmpty()) {
            return create(item);
        }

        // Если элемент существует, обновляем его
        if (exists(id)) {
            return update(item);
        }

        // Если ID есть, но элемента нет, добавляем как новый
        items.add(item);
        return item;
    }

    /**
     * Удалить элемент по ID
     */
    public boolean delete(String id) {
        return items.removeIf(item -> getId(item).equals(id));
    }

    /**
     * Получить все элементы
     */
    public List<T> getAll() {
        return new ArrayList<>(items);
    }

    /**
     * Альтернативное имя для getAll (для обратной совместимости)
     */
    public List<T> findAll() {
        return getAll();
    }

    /**
     * Проверить существование по ID
     */
    public boolean exists(String id) {
        return items.stream()
                .anyMatch(item -> getId(item).equals(id));
    }

    /**
     * Получить количество элементов
     */
    public int count() {
        return items.size();
    }

    /**
     * Очистить все элементы
     */
    public void clear() {
        items.clear();
    }
}
