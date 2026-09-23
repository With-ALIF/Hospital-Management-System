package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.Operation;

import java.time.LocalDate;
import java.util.List;

public class OperationRepository extends AbstractJsonRepository<Operation> {
    public static final String DEFAULT_FILE = "data/operations.json";

    public OperationRepository() { this(DEFAULT_FILE); }
    public OperationRepository(String filePath) {
        super(filePath, new TypeReference<List<Operation>>() {}, "Operation");
    }

    @Override
    protected String idOf(Operation e) { return e.getId(); }

    public String nextId() { return nextId("OPR-", 1); }

    public List<Operation> findByDate(LocalDate date) {
        return findAll().stream().filter(o -> o.getDate() != null && o.getDate().equals(date)).toList();
    }

    public List<Operation> findActive() {
        return findAll().stream()
                .filter(o -> o.getStatus() == com.hospital.enums.OperationStatus.SCHEDULED
                        || o.getStatus() == com.hospital.enums.OperationStatus.PREPARING
                        || o.getStatus() == com.hospital.enums.OperationStatus.IN_PROGRESS)
                .toList();
    }
}
