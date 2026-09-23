package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.enums.EquipmentStatus;
import com.hospital.model.Equipment;

import java.util.List;

public class EquipmentRepository extends AbstractJsonRepository<Equipment> {
    public static final String DEFAULT_FILE = "data/equipment.json";

    public EquipmentRepository() { this(DEFAULT_FILE); }
    public EquipmentRepository(String filePath) {
        super(filePath, new TypeReference<List<Equipment>>() {}, "Equipment");
    }

    @Override
    protected String idOf(Equipment e) { return e.getId(); }

    public String nextId() { return nextId("EQP-", 1); }

    public List<Equipment> findByStatus(EquipmentStatus status) {
        return findAll().stream().filter(e -> e.getStatus() == status).toList();
    }

    public List<Equipment> findByDepartment(String department) {
        return findAll().stream()
                .filter(e -> department != null && department.equalsIgnoreCase(e.getDepartment()))
                .toList();
    }
}
