package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.EmergencyCase;

import java.util.List;

public class EmergencyCaseRepository extends AbstractJsonRepository<EmergencyCase> {

    public static final String DEFAULT_FILE = "data/emergency_cases.json";

    public EmergencyCaseRepository() {
        this(DEFAULT_FILE);
    }

    public EmergencyCaseRepository(String filePath) {
        super(filePath, new TypeReference<List<EmergencyCase>>() {}, "Emergency case");
    }

    @Override
    protected String idOf(EmergencyCase emergencyCase) {
        return emergencyCase.getId();
    }

    public String nextCaseId() {
        return nextId("EMG-", 1);
    }

    public List<EmergencyCase> findByPatientId(String patientId) {
        if (patientId == null) {
            return List.of();
        }
        return findAll().stream()
                .filter(c -> patientId.equalsIgnoreCase(c.getPatientId()))
                .toList();
    }
}
