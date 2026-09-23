package com.hospital.service;

import com.hospital.enums.AuditAction;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.AuditLog;
import com.hospital.repository.AuditLogRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AuditLogService {
    private final AuditLogRepository repository;

    public AuditLogService() {
        this(AuditLogRepository.DEFAULT_FILE);
    }

    public AuditLogService(String filePath) {
        this.repository = new AuditLogRepository(filePath);
    }

    public AuditLog record(String actor, AuditAction action, String entity,
                           String entityId, String description) {
        if (action == null) {
            throw new InvalidDataException("Audit action cannot be empty.");
        }
        AuditLog log = new AuditLog(repository.nextLogId(), actor, action,
                entity, entityId, description);
        repository.add(log);
        return log;
    }

    public List<AuditLog> getAll() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(AuditLog::getTimestamp,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public List<AuditLog> getRecent(int limit) {
        return getAll().stream().limit(Math.max(0, limit)).toList();
    }

    public Optional<AuditLog> find(String id) { return repository.findById(id); }
    public int getCount() { return repository.count(); }
    public String nextId() { return repository.nextLogId(); }
}
