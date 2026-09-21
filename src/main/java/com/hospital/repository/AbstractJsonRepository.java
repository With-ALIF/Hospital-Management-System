package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.exception.InvalidDataException;
import com.hospital.storage.JsonStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Shared implementation of {@link Repository} for entities that are kept in a
 * JSON array. Concrete repositories (PatientRepository, DoctorRepository, ...)
 * only have to say which file to use and how to read an entity's id.
 *
 * Auto sync: the JSON file is written after every add / update / delete, so the
 * service layer never has to call a save method itself.
 *
 * <pre>
 * service  ->  repository (in memory list + file name)  ->  JsonStorage  ->  *.json
 * </pre>
 */
public abstract class AbstractJsonRepository<T> implements Repository<T> {

    private final JsonStorage storage;
    private final List<T> items = new ArrayList<>();
    private final String entityLabel;

    protected AbstractJsonRepository(String filePath, TypeReference<List<T>> typeReference, String entityLabel) {
        this.entityLabel = entityLabel;
        this.storage = new JsonStorage(filePath);
        // Startup loading: whatever is already inside the JSON file is read into memory.
        this.items.addAll(storage.loadList(typeReference));
    }

    /** Subclasses tell the repository how to read the id of an entity. */
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

    /**
     * Builds the next free id for this entity, e.g. "P-1002" when "P-1001" is
     * the highest id currently stored. Keeps ids unique even after deletions.
     */
    public String nextId(String prefix, int firstNumber) {
        int highest = firstNumber - 1;
        for (T item : items) {
            String id = idOf(item);
            if (id == null || !id.startsWith(prefix)) {
                continue;
            }
            try {
                highest = Math.max(highest, Integer.parseInt(id.substring(prefix.length()).trim()));
            } catch (NumberFormatException ignored) {
                // id does not follow the "P-1001" pattern - simply ignore it
            }
        }
        return prefix + (highest + 1);
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

    /** Writes the whole in memory list back to the JSON file (auto sync). */
    private void sync() {
        storage.saveList(items);
    }
}