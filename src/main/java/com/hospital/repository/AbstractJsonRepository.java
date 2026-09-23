package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.exception.InvalidDataException;
import com.hospital.storage.JsonStorage;
import com.hospital.util.IdGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class AbstractJsonRepository<T> implements Repository<T> {

    private final JsonStorage storage;
    private final List<T> items = new ArrayList<>();
    private final String entityLabel;

    protected AbstractJsonRepository(String filePath, TypeReference<List<T>> typeReference, String entityLabel) {
        this.entityLabel = entityLabel;
        this.storage = new JsonStorage(filePath);
        this.items.addAll(storage.loadList(typeReference));
    }

    protected abstract String idOf(T entity);

    @Override
    public void add(T entity) {
        if (entity == null) {
            throw new InvalidDataException(entityLabel + " cannot be null.");
        }
        String id = idOf(entity);
        if (id == null || id.isBlank()) {
            throw new InvalidDataException(entityLabel + " id cannot be empty.");
        }
        if (findById(id).isPresent()) {
            throw new InvalidDataException(entityLabel + " id already exists: " + id);
        }
        items.add(entity);
        sync();
    }

    @Override
    public void update(T entity) {
        if (entity == null) {
            throw new InvalidDataException(entityLabel + " cannot be null.");
        }
        String id = idOf(entity);
        int index = indexOf(id);
        if (index < 0) {
            throw new InvalidDataException(entityLabel + " not found: " + id);
        }
        items.set(index, entity);
        sync();
    }

    @Override
    public void delete(String id) {
        boolean removed = items.removeIf(item -> idOf(item) != null && idOf(item).equalsIgnoreCase(id));
        if (!removed) {
            throw new InvalidDataException(entityLabel + " not found: " + id);
        }
        sync();
    }

    @Override
    public Optional<T> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return items.stream()
                .filter(item -> idOf(item) != null && idOf(item).equalsIgnoreCase(id))
                .findFirst();
    }

    @Override
    public List<T> findAll() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public int count() {
        return items.size();
    }

    public String nextId(String prefix, int firstNumber) {
        List<String> ids = new ArrayList<>();
        for (T item : items) {
            ids.add(idOf(item));
        }
        return IdGenerator.next(ids, prefix, firstNumber);
    }

    private int indexOf(String id) {
        if (id == null) {
            return -1;
        }
        for (int i = 0; i < items.size(); i++) {
            String itemId = idOf(items.get(i));
            if (itemId != null && itemId.equalsIgnoreCase(id)) {
                return i;
            }
        }
        return -1;
    }

    private void sync() {
        storage.saveList(items);
    }
}
