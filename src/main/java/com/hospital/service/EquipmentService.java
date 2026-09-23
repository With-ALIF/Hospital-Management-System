package com.hospital.service;

import com.hospital.enums.EquipmentStatus;
import com.hospital.exception.ConflictException;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Equipment;
import com.hospital.repository.EquipmentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EquipmentService {
    private final EquipmentRepository equipment;
    private final NotificationService notifications;
    private final AuditLogService auditLogs;

    public EquipmentService() { this(EquipmentRepository.DEFAULT_FILE); }

    public EquipmentService(String filePath) {
        this.equipment = new EquipmentRepository(filePath);
        this.notifications = new NotificationService();
        this.auditLogs = new AuditLogService();
    }

    public Equipment add(String name, String category, String department, int quantity) {
        if (name == null || name.isBlank()) {
            throw new InvalidDataException("Equipment name required.");
        }
        if (quantity < 0) throw new InvalidDataException("Quantity cannot be negative.");
        Equipment e = new Equipment(equipment.nextId(), name, category, department, quantity);
        equipment.add(e);
        return e;
    }

    public Equipment update(Equipment e) {
        if (e == null || e.getId() == null) {
            throw new InvalidDataException("Equipment required.");
        }
        if (e.getQuantity() < 0) throw new InvalidDataException("Quantity cannot be negative.");
        equipment.update(e);
        return e;
    }

    public void remove(String id) {
        Equipment e = require(id);
        if (e.getStatus() == EquipmentStatus.IN_USE) {
            throw new ConflictException("Equipment in use cannot be deleted: " + id);
        }
        equipment.delete(e.getId());
    }

    public Equipment assign(String id, String assignee, int qty) {
        return EquipmentMutations.assign(equipment, auditLogs, id, assignee, qty);
    }

    public Equipment returnItem(String id, int qty) {
        return EquipmentMutations.returnItem(equipment, auditLogs, id, qty);
    }

    public Equipment setMaintenance(String id, LocalDate last, LocalDate next) {
        return EquipmentMutations.setMaintenance(equipment, auditLogs, id, last, next);
    }

    public Equipment completeMaintenance(String id) {
        return EquipmentMutations.completeMaintenance(equipment, id);
    }

    public List<Equipment> getMaintenanceDue(LocalDate from, LocalDate to) {
        return equipment.findAll().stream()
                .filter(e -> e.getNextMaintenance() != null)
                .filter(e -> !e.getNextMaintenance().isBefore(from)
                        && !e.getNextMaintenance().isAfter(to))
                .toList();
    }

    public List<Equipment> getByStatus(EquipmentStatus status) {
        return equipment.findByStatus(status);
    }

    public List<Equipment> getAll() { return equipment.findAll(); }
    public Optional<Equipment> find(String id) { return equipment.findById(id); }
    public int getCount() { return equipment.count(); }
    public String nextId() { return equipment.nextId(); }
    public List<Equipment> search(String q) { return EquipmentQueries.search(equipment.findAll(), q); }

    private Equipment require(String id) {
        return EquipmentMutations.require(equipment, id);
    }
}
