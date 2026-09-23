package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.enums.AmbulanceStatus;
import com.hospital.model.Ambulance;

import java.util.List;

public class AmbulanceRepository extends AbstractJsonRepository<Ambulance> {
    public static final String DEFAULT_FILE = "data/ambulances.json";

    public AmbulanceRepository() { this(DEFAULT_FILE); }
    public AmbulanceRepository(String filePath) {
        super(filePath, new TypeReference<List<Ambulance>>() {}, "Ambulance");
    }

    @Override
    protected String idOf(Ambulance e) { return e.getId(); }

    public String nextId() { return nextId("AMB-", 1); }

    public List<Ambulance> findAvailable() {
        return findAll().stream().filter(a -> a.getStatus() == AmbulanceStatus.AVAILABLE).toList();
    }

    public List<Ambulance> findByStatus(AmbulanceStatus status) {
        return findAll().stream().filter(a -> a.getStatus() == status).toList();
    }
}
