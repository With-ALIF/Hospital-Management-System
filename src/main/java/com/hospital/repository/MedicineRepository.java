package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.Medicine;

import java.util.List;

public class MedicineRepository extends AbstractJsonRepository<Medicine> {
    public static final String DEFAULT_FILE = "data/medicines.json";

    public MedicineRepository() { this(DEFAULT_FILE); }
    public MedicineRepository(String filePath) {
        super(filePath, new TypeReference<List<Medicine>>() {}, "Medicine");
    }

    @Override
    protected String idOf(Medicine entity) { return entity.getId(); }

    public String nextMedicineId() { return nextId("MED-", 1); }
}
