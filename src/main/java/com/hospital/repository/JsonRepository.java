package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.storage.JsonStorage;

import java.util.List;

public class JsonRepository<T> {

    private final JsonStorage storage;
    private final TypeReference<List<T>> typeReference;

    public JsonRepository(String filePath, TypeReference<List<T>> typeReference) {
        this.storage = new JsonStorage(filePath);
        this.typeReference = typeReference;
    }

    public List<T> loadAll() {
        return storage.loadList(typeReference);
    }

    public void saveAll(List<T> items) {
        storage.saveList(items);
    }
}
