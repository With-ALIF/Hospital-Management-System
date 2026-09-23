package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.Staff;

import java.util.List;
import java.util.Optional;

public class StaffRepository extends AbstractJsonRepository<Staff> {
    public static final String DEFAULT_FILE = "data/staff.json";

    public StaffRepository() { this(DEFAULT_FILE); }
    public StaffRepository(String filePath) {
        super(filePath, new TypeReference<List<Staff>>() {}, "Staff");
    }

    @Override
    protected String idOf(Staff entity) { return entity.getId(); }

    public String nextStaffId() { return nextId("STF-", 1); }

    public Optional<Staff> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return findAll().stream()
                .filter(s -> email.equalsIgnoreCase(s.getEmail()))
                .findFirst();
    }
}
