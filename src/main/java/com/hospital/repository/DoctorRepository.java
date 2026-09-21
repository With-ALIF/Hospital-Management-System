package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.Doctor;

import java.util.List;

/**
 * Stores doctors (including their duty slots) in {@code data/doctors.json}.
 * Every modification writes the file again, so no explicit save call is needed.
 */
public class DoctorRepository extends AbstractJsonRepository<Doctor> {

    public static final String DEFAULT_FILE = "data/doctors.json";

    public DoctorRepository() {
        this(DEFAULT_FILE);
    }

    /** Used by the tests so that they can work in a temporary directory. */
    public DoctorRepository(String filePath) {
        super(filePath, new TypeReference<List<Doctor>>() {
        }, "Doctor");
    }

    @Override
    protected String idOf(Doctor doctor) {
        return doctor.getId();
    }

    public String nextDoctorId() {
        return nextId("D-", 1001);
    }
}