package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.MedicalRecord;

import java.util.List;

public class MedicalRecordRepository extends AbstractJsonRepository<MedicalRecord> {
    public static final String DEFAULT_FILE = "data/medical_records.json";

    public MedicalRecordRepository() { this(DEFAULT_FILE); }
    public MedicalRecordRepository(String filePath) {
        super(filePath, new TypeReference<List<MedicalRecord>>() {}, "MedicalRecord");
    }

    @Override
    protected String idOf(MedicalRecord entity) { return entity.getId(); }

    public String nextRecordId() { return nextId("MRC-", 1); }

    public List<MedicalRecord> findByPatient(String patientId) {
        return findAll().stream()
                .filter(r -> patientId != null && patientId.equals(r.getPatientId())).toList();
    }
}
