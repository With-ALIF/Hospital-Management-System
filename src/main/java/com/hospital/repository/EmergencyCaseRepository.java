package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.EmergencyCase;

import java.util.List;

/**
 * Stores emergency cases in {@code data/emergency_cases.json}.
 * Every modification writes the file again, so no explicit save call is needed.
 */
public class EmergencyCaseRepository extends AbstractJsonRepository<EmergencyCase> {

    public static final String DEFAULT_FILE = "data/emergency_cases.json";

    public EmergencyCaseRepository() {
        this(DEFAULT_FILE);
    }

    /** Used by the tests so that they can work in a temporary directory. */
    public EmergencyCaseRepository(String filePath) {
        super(filePath, new TypeReference<List<EmergencyCase>>() {
        }, "Emergency case");
    }

    @Override
    protected String idOf(EmergencyCase emergencyCase) {
        return emergencyCase.getId();
    }

    public String nextCaseId() {
        return nextId("E-", 1001);
    }

    /** All emergency cases that belong to one patient. */
    public List<EmergencyCase> findByPatientId(String patientId) {
        if (patientId == null) {
            return List.of();
        }
        return findAll().stream()
                .filter(c -> patientId.equalsIgnoreCase(c.getPatientId()))
                .toList();
    }
}