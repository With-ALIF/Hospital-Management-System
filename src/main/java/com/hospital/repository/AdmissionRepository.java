package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.Admission;

import java.util.List;

public class AdmissionRepository extends AbstractJsonRepository<Admission> {
    public static final String DEFAULT_FILE = "data/admissions.json";

    public AdmissionRepository() { this(DEFAULT_FILE); }
    public AdmissionRepository(String filePath) {
        super(filePath, new TypeReference<List<Admission>>() {}, "Admission");
    }

    @Override
    protected String idOf(Admission entity) { return entity.getId(); }

    public String nextAdmissionId() { return nextId("ADM-", 1); }
}
