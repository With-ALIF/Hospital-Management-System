package com.hospital.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic contract for a JSON backed repository.
 *
 * Every entity of this system is identified by a String id and is stored as a
 * JSON array in a file, so the same interface can be reused by the patient,
 * doctor and emergency case repositories.
 */
public interface Repository<T> {

    /** Stores a new entity and writes the JSON file again (auto sync). */
    void add(T entity);

    /** Replaces an existing entity (same id) and writes the JSON file again (auto sync). */
    void update(T entity);

    /** Removes an entity by id and writes the JSON file again (auto sync). */
    void delete(String id);

    /** Finds one entity by id; empty when the id is unknown. */
    Optional<T> findById(String id);

    /** All stored entities (read only view). */
    List<T> findAll();

    /** Number of stored entities. */
    int count();
}