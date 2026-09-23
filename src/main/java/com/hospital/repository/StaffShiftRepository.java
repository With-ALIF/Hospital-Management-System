package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.StaffShift;

import java.time.LocalDate;
import java.util.List;

public class StaffShiftRepository extends AbstractJsonRepository<StaffShift> {
    public static final String DEFAULT_FILE = "data/staff_shifts.json";

    public StaffShiftRepository() { this(DEFAULT_FILE); }
    public StaffShiftRepository(String filePath) {
        super(filePath, new TypeReference<List<StaffShift>>() {}, "StaffShift");
    }

    @Override
    protected String idOf(StaffShift e) { return e.getId(); }

    public String nextId() { return nextId("SHF-", 1); }

    public List<StaffShift> findByDate(LocalDate date) {
        return findAll().stream()
                .filter(s -> s.getDate() != null && s.getDate().equals(date))
                .toList();
    }

    public List<StaffShift> findByStaff(String staffId) {
        return findAll().stream()
                .filter(s -> staffId != null && staffId.equals(s.getStaffId()))
                .toList();
    }

    public List<StaffShift> findByDepartment(String department) {
        return findAll().stream()
                .filter(s -> department != null && department.equalsIgnoreCase(s.getDepartment()))
                .toList();
    }
}
