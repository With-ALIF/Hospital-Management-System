package com.hospital.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    void add(T entity);

    void update(T entity);

    void delete(String id);

    Optional<T> findById(String id);

    List<T> findAll();

    int count();

    default boolean existsById(String id) {
        return findById(id).isPresent();
    }
}
