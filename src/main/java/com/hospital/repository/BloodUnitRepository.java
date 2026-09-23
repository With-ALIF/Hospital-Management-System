package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.enums.BloodGroup;
import com.hospital.enums.BloodUnitStatus;
import com.hospital.model.BloodUnit;

import java.time.LocalDate;
import java.util.List;

public class BloodUnitRepository extends AbstractJsonRepository<BloodUnit> {
    public static final String DEFAULT_FILE = "data/blood_units.json";

    public BloodUnitRepository() { this(DEFAULT_FILE); }
    public BloodUnitRepository(String filePath) {
        super(filePath, new TypeReference<List<BloodUnit>>() {}, "BloodUnit");
    }

    @Override
    protected String idOf(BloodUnit e) { return e.getId(); }

    public String nextId() { return nextId("BLD-", 1); }

    public List<BloodUnit> findAvailable(BloodGroup group) {
        LocalDate today = LocalDate.now();
        return findAll().stream()
                .filter(u -> u.getStatus() == BloodUnitStatus.AVAILABLE)
                .filter(u -> !u.isExpired(today))
                .filter(u -> group == null || u.getBloodGroup() == group)
                .toList();
    }

    public List<BloodUnit> findByStatus(BloodUnitStatus status) {
        return findAll().stream().filter(u -> u.getStatus() == status).toList();
    }

    public List<BloodUnit> findExpired() {
        LocalDate today = LocalDate.now();
        return findAll().stream().filter(u -> u.isExpired(today)).toList();
    }
}
