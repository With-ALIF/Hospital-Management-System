package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.Ward;

import java.util.List;

public class WardRepository extends AbstractJsonRepository<Ward> {
    public static final String DEFAULT_FILE = "data/wards.json";

    public WardRepository() { this(DEFAULT_FILE); }
    public WardRepository(String filePath) {
        super(filePath, new TypeReference<List<Ward>>() {}, "Ward");
    }

    @Override
    protected String idOf(Ward entity) { return entity.getId(); }

    public String nextWardId() { return nextId("WARD-", 1); }
}
