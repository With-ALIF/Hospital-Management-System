package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.enums.HospitalEventType;
import com.hospital.model.HospitalEvent;

import java.util.List;

public class HospitalEventRepository extends AbstractJsonRepository<HospitalEvent> {
    public static final String DEFAULT_FILE = "data/events.json";

    public HospitalEventRepository() { this(DEFAULT_FILE); }
    public HospitalEventRepository(String filePath) {
        super(filePath, new TypeReference<List<HospitalEvent>>() {}, "HospitalEvent");
    }

    @Override
    protected String idOf(HospitalEvent e) { return e.getId(); }

    public String nextId() { return nextId("EVT-", 1); }

    public List<HospitalEvent> findByType(HospitalEventType type) {
        return findAll().stream().filter(e -> e.getType() == type).toList();
    }
}
