package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.Doctor;

import java.util.List;

public class DoctorRepository extends AbstractJsonRepository<Doctor> {

    public static final String DEFAULT_FILE = "data/doctors.json";

    public DoctorRepository() {
        this(DEFAULT_FILE);
    }

    public DoctorRepository(String filePath) {
        super(filePath, new TypeReference<List<Doctor>>() {}, "Doctor");
    }

    @Override
    protected String idOf(Doctor doctor) {
        return doctor.getId();
    }

    public String nextDoctorId() {
        return nextId("DOC-", 1);
    }
}
