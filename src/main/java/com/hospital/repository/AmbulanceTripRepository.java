package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.enums.TripStatus;
import com.hospital.model.AmbulanceTrip;

import java.util.List;

public class AmbulanceTripRepository extends AbstractJsonRepository<AmbulanceTrip> {
    public static final String DEFAULT_FILE = "data/ambulance_trips.json";

    public AmbulanceTripRepository() { this(DEFAULT_FILE); }
    public AmbulanceTripRepository(String filePath) {
        super(filePath, new TypeReference<List<AmbulanceTrip>>() {}, "AmbulanceTrip");
    }

    @Override
    protected String idOf(AmbulanceTrip e) { return e.getId(); }

    public String nextId() { return nextId("TRP-", 1); }

    public List<AmbulanceTrip> findActive() {
        return findAll().stream()
                .filter(t -> t.getStatus() != TripStatus.COMPLETED && t.getStatus() != TripStatus.CANCELLED)
                .toList();
    }

    public List<AmbulanceTrip> findByAmbulance(String ambulanceId) {
        return findAll().stream()
                .filter(t -> ambulanceId != null && ambulanceId.equals(t.getAmbulanceId()))
                .toList();
    }
}
