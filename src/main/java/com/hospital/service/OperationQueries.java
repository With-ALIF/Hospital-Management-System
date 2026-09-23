package com.hospital.service;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.Operation;
import com.hospital.repository.OperationRepository;

import java.util.List;

class OperationQueries {
    private final OperationRepository operations;

    OperationQueries(OperationRepository operations) {
        this.operations = operations;
    }

    Operation require(String id) {
        if (id == null || id.isBlank()) throw new InvalidDataException("Operation id required.");
        return operations.findById(id)
                .orElseThrow(() -> new InvalidDataException("Operation not found: " + id));
    }

    List<Operation> search(String query) {
        if (query == null || query.isBlank()) return operations.findAll();
        String q = query.trim().toLowerCase();
        return operations.findAll().stream()
                .filter(o -> (o.getId() != null && o.getId().toLowerCase().contains(q))
                        || (o.getPatientId() != null && o.getPatientId().toLowerCase().contains(q))
                        || (o.getSurgeonId() != null && o.getSurgeonId().toLowerCase().contains(q))
                        || (o.getRoomId() != null && o.getRoomId().toLowerCase().contains(q))
                        || (o.getOperationType() != null
                            && o.getOperationType().toLowerCase().contains(q))
                        || (o.getStatus() != null && o.getStatus().name().toLowerCase().contains(q)))
                .toList();
    }
}
