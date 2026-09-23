package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.enums.BedStatus;
import com.hospital.model.Bed;

import java.util.List;

public class BedRepository extends AbstractJsonRepository<Bed> {
    public static final String DEFAULT_FILE = "data/beds.json";

    public BedRepository() { this(DEFAULT_FILE); }
    public BedRepository(String filePath) {
        super(filePath, new TypeReference<List<Bed>>() {}, "Bed");
    }

    @Override
    protected String idOf(Bed entity) { return entity.getId(); }

    public String nextBedId() { return nextId("BED-", 1); }

    public List<Bed> findByWard(String wardId) {
        return findAll().stream().filter(b -> wardId != null && wardId.equals(b.getWardId())).toList();
    }

    public List<Bed> findAvailable() {
        return findAll().stream().filter(b -> b.getStatus() == BedStatus.AVAILABLE).toList();
    }

    public List<Bed> findByPatient(String patientId) {
        return findAll().stream()
                .filter(b -> patientId != null && patientId.equals(b.getPatientId())).toList();
    }
}
