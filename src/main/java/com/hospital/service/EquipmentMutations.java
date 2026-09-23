package com.hospital.service;

import com.hospital.enums.EquipmentStatus;
import com.hospital.exception.ConflictException;
import com.hospital.exception.InvalidDataException;
import com.hospital.exception.ResourceUnavailableException;
import com.hospital.model.Equipment;
import com.hospital.repository.EquipmentRepository;

import java.time.LocalDate;

final class EquipmentMutations {
    private EquipmentMutations() {}

    static Equipment assign(EquipmentRepository repo, AuditLogService audit,
                           String id, String assignee, int qty) {
        Equipment e = require(repo, id);
        if (!e.getStatus().assignable()) {
            throw new ResourceUnavailableException("Equipment not assignable: "
                    + id + " status=" + e.getStatus());
        }
        if (qty <= 0) throw new InvalidDataException("Assign quantity must be positive.");
        if (qty > e.getAvailableQuantity()) {
            throw new ResourceUnavailableException("Not enough available quantity: "
                    + e.getAvailableQuantity());
        }
        e.setAvailableQuantity(e.getAvailableQuantity() - qty);
        e.setAssignedTo(assignee);
        if (e.getAvailableQuantity() == 0) e.setStatus(EquipmentStatus.IN_USE);
        repo.update(e);
        audit.record("SYSTEM", com.hospital.enums.AuditAction.EQUIPMENT_ASSIGN,
                "Equipment", id, "Assigned " + qty + " to " + assignee);
        return e;
    }

    static Equipment returnItem(EquipmentRepository repo, AuditLogService audit,
                                String id, int qty) {
        Equipment e = require(repo, id);
        if (qty <= 0) throw new InvalidDataException("Return quantity must be positive.");
        int returned = Math.min(qty, e.getQuantity() - e.getAvailableQuantity());
        e.setAvailableQuantity(e.getAvailableQuantity() + returned);
        if (e.getAvailableQuantity() >= e.getQuantity()
                && e.getStatus() == EquipmentStatus.IN_USE) {
            e.setStatus(EquipmentStatus.AVAILABLE);
            e.setAssignedTo(null);
        }
        repo.update(e);
        audit.record("SYSTEM", com.hospital.enums.AuditAction.EQUIPMENT_RETURN,
                "Equipment", id, "Returned " + returned);
        return e;
    }

    static Equipment setMaintenance(EquipmentRepository repo, AuditLogService audit,
                                    String id, LocalDate last, LocalDate next) {
        Equipment e = require(repo, id);
        if (e.getStatus() == EquipmentStatus.IN_USE) {
            throw new ConflictException("In-use equipment cannot enter maintenance: " + id);
        }
        e.setStatus(EquipmentStatus.MAINTENANCE);
        e.setLastMaintenance(last);
        e.setNextMaintenance(next);
        repo.update(e);
        audit.record("SYSTEM", com.hospital.enums.AuditAction.MAINTENANCE,
                "Equipment", id, "Maintenance scheduled");
        return e;
    }

    static Equipment completeMaintenance(EquipmentRepository repo, String id) {
        Equipment e = require(repo, id);
        if (e.getStatus() != EquipmentStatus.MAINTENANCE) {
            throw new InvalidDataException("Equipment not in maintenance: " + id);
        }
        e.setStatus(EquipmentStatus.AVAILABLE);
        e.setLastMaintenance(LocalDate.now());
        repo.update(e);
        return e;
    }

    static Equipment require(EquipmentRepository repo, String id) {
        if (id == null || id.isBlank()) throw new InvalidDataException("Equipment id required.");
        return repo.findById(id)
                .orElseThrow(() -> new InvalidDataException("Equipment not found: " + id));
    }
}
