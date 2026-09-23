package com.hospital.service;

import com.hospital.enums.Permission;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Medicine;
import com.hospital.repository.MedicineRepository;

import java.util.List;
import java.util.Optional;

public class PharmacyService {
    private final MedicineRepository repository;

    public PharmacyService() {
        this(MedicineRepository.DEFAULT_FILE);
    }

    public PharmacyService(String filePath) {
        this.repository = new MedicineRepository(filePath);
    }

    public Medicine addMedicine(Medicine medicine) {
        PermissionService.getInstance().require(Permission.MANAGE_PHARMACY);
        if (medicine == null || medicine.getName() == null || medicine.getName().isBlank()) {
            throw new InvalidDataException("Medicine name cannot be empty.");
        }
        if (medicine.getPrice() < 0) {
            throw new InvalidDataException("Medicine price cannot be negative.");
        }
        if (medicine.getId() == null || medicine.getId().isBlank()) {
            medicine.setId(repository.nextMedicineId());
        }
        repository.add(medicine);
        return medicine;
    }

    public Medicine updateMedicine(Medicine medicine) {
        PermissionService.getInstance().require(Permission.MANAGE_PHARMACY);
        if (medicine == null || medicine.getId() == null) {
            throw new InvalidDataException("Medicine id cannot be empty.");
        }
        repository.update(medicine);
        return medicine;
    }

    public void removeMedicine(String medicineId) {
        PermissionService.getInstance().require(Permission.MANAGE_PHARMACY);
        if (medicineId == null || medicineId.isBlank()) {
            throw new InvalidDataException("Medicine id cannot be empty.");
        }
        repository.delete(medicineId);
    }

    public Medicine dispense(String medicineId, int quantity) {
        PermissionService.getInstance().require(Permission.MANAGE_PHARMACY);
        if (quantity <= 0) {
            throw new InvalidDataException("Dispense quantity must be positive.");
        }
        Medicine medicine = require(medicineId);
        if (medicine.getQuantity() < quantity) {
            throw new InvalidDataException("Not enough stock for " + medicine.getName()
                    + " (available " + medicine.getQuantity() + ").");
        }
        medicine.setQuantity(medicine.getQuantity() - quantity);
        repository.update(medicine);
        return medicine;
    }

    public Medicine restock(String medicineId, int quantity) {
        PermissionService.getInstance().require(Permission.MANAGE_PHARMACY);
        if (quantity <= 0) {
            throw new InvalidDataException("Restock quantity must be positive.");
        }
        Medicine medicine = require(medicineId);
        medicine.setQuantity(medicine.getQuantity() + quantity);
        repository.update(medicine);
        return medicine;
    }

    public Optional<Medicine> findMedicine(String id) { return repository.findById(id); }
    public List<Medicine> getAllMedicines() { return repository.findAll(); }
    public int getMedicineCount() { return repository.count(); }
    public String nextMedicineId() { return repository.nextMedicineId(); }

    private Medicine require(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("Medicine id cannot be empty.");
        }
        return repository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Medicine not found: " + id));
    }
}
