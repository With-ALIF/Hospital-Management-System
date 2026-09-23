package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.BloodDonor;

import java.util.List;
import java.util.Optional;

public class BloodDonorRepository extends AbstractJsonRepository<BloodDonor> {
    public static final String DEFAULT_FILE = "data/blood_donors.json";

    public BloodDonorRepository() { this(DEFAULT_FILE); }
    public BloodDonorRepository(String filePath) {
        super(filePath, new TypeReference<List<BloodDonor>>() {}, "BloodDonor");
    }

    @Override
    protected String idOf(BloodDonor e) { return e.getId(); }

    public String nextId() { return nextId("BDR-", 1); }

    public Optional<BloodDonor> findByPhone(String phone) {
        if (phone == null) return Optional.empty();
        return findAll().stream().filter(d -> phone.equals(d.getPhone())).findFirst();
    }
}
