package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.Patient;

import java.util.List;

/**
 * Stores patients in {@code data/patients.json}.
 * Every modification writes the file again, so no explicit save call is needed.
 */
public class PatientRepository extends AbstractJsonRepository<Patient> {

    public static final String DEFAULT_FILE = "data/patients.json";

    public PatientRepository() {
        this(DEFAULT_FILE);
    }

    /** Used by the tests so that they can work in a temporary directory. */
    public PatientRepository(String filePath) {
        super(filePath, new TypeReference<List<Patient>>() {
        }, "Patient");
    }

    @Override
    protected String idOf(Patient patient) {
        return patient.getId();
    }

    public String nextPatientId() {
        return nextId("P-", 1001);
    }

    public List<Patient> findByBloodGroup(String bloodGroup) {
        if (bloodGroup == null) {
            return List.of();
        }
        return findAll().stream()
                .filter(p -> bloodGroup.equalsIgnoreCase(p.getBloodGroup()))
                .toList();
    }
}