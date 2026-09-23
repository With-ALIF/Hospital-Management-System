package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.AuditLog;

import java.util.List;

public class AuditLogRepository extends AbstractJsonRepository<AuditLog> {
    public static final String DEFAULT_FILE = "data/audit_logs.json";

    public AuditLogRepository() { this(DEFAULT_FILE); }
    public AuditLogRepository(String filePath) {
        super(filePath, new TypeReference<List<AuditLog>>() {}, "AuditLog");
    }

    @Override
    protected String idOf(AuditLog entity) { return entity.getId(); }

    public String nextLogId() { return nextId("LOG-", 1); }
}
