package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.Discharge;

import java.util.List;

public class DischargeRepository extends AbstractJsonRepository<Discharge> {
    public static final String DEFAULT_FILE = "data/discharges.json";

    public DischargeRepository() { this(DEFAULT_FILE); }
    public DischargeRepository(String filePath) {
        super(filePath, new TypeReference<List<Discharge>>() {}, "Discharge");
    }

    @Override
    protected String idOf(Discharge entity) { return entity.getId(); }

    public String nextDischargeId() { return nextId("DIS-", 1); }
}
