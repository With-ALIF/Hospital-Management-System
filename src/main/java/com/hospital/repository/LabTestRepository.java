package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.LabTest;

import java.util.List;

public class LabTestRepository extends AbstractJsonRepository<LabTest> {
    public static final String DEFAULT_FILE = "data/lab_tests.json";

    public LabTestRepository() { this(DEFAULT_FILE); }
    public LabTestRepository(String filePath) {
        super(filePath, new TypeReference<List<LabTest>>() {}, "LabTest");
    }

    @Override
    protected String idOf(LabTest entity) { return entity.getId(); }

    public String nextLabTestId() { return nextId("LAB-", 1); }

    public List<LabTest> findByPatient(String patientId) {
        return findAll().stream()
                .filter(t -> patientId != null && patientId.equals(t.getPatientId())).toList();
    }
}
