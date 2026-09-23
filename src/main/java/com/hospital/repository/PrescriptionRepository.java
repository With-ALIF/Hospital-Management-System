package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.Prescription;

import java.util.List;

public class PrescriptionRepository extends AbstractJsonRepository<Prescription> {
    public static final String DEFAULT_FILE = "data/prescriptions.json";

    public PrescriptionRepository() { this(DEFAULT_FILE); }
    public PrescriptionRepository(String filePath) {
        super(filePath, new TypeReference<List<Prescription>>() {}, "Prescription");
    }

    @Override
    protected String idOf(Prescription entity) { return entity.getId(); }

    public String nextPrescriptionId() { return nextId("PRE-", 1); }

    public List<Prescription> findByPatient(String patientId) {
        return findAll().stream()
                .filter(p -> patientId != null && patientId.equals(p.getPatientId())).toList();
    }
}
