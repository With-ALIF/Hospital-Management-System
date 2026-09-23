package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.BloodRequest;

import java.util.List;

public class BloodRequestRepository extends AbstractJsonRepository<BloodRequest> {
    public static final String DEFAULT_FILE = "data/blood_requests.json";

    public BloodRequestRepository() { this(DEFAULT_FILE); }
    public BloodRequestRepository(String filePath) {
        super(filePath, new TypeReference<List<BloodRequest>>() {}, "BloodRequest");
    }

    @Override
    protected String idOf(BloodRequest e) { return e.getId(); }

    public String nextId() { return nextId("BRQ-", 1); }

    public List<BloodRequest> findPending() {
        return findAll().stream()
                .filter(r -> r.getStatus() == BloodRequest.Status.PENDING)
                .toList();
    }

    public List<BloodRequest> findEmergency() {
        return findAll().stream().filter(BloodRequest::isEmergency).toList();
    }
}
