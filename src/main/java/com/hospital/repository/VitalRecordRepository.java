package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.VitalRecord;

import java.util.List;

public class VitalRecordRepository extends AbstractJsonRepository<VitalRecord> {
    public static final String DEFAULT_FILE = "data/vital_records.json";

    public VitalRecordRepository() { this(DEFAULT_FILE); }
    public VitalRecordRepository(String filePath) {
        super(filePath, new TypeReference<List<VitalRecord>>() {}, "VitalRecord");
    }

    @Override
    protected String idOf(VitalRecord e) { return e.getId(); }

    public String nextId() { return nextId("VIT-", 1); }

    public List<VitalRecord> findByPatient(String patientId) {
        return findAll().stream()
                .filter(v -> patientId != null && patientId.equals(v.getPatientId()))
                .toList();
    }

    public List<VitalRecord> findCritical() {
        return findAll().stream()
                .filter(v -> v.getOverallStatus() == com.hospital.enums.VitalStatus.CRITICAL)
                .toList();
    }

    public List<VitalRecord> findWarning() {
        return findAll().stream()
                .filter(v -> v.getOverallStatus() == com.hospital.enums.VitalStatus.WARNING)
                .toList();
    }
}
