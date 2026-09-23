package com.hospital.service;

import com.hospital.enums.Permission;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Prescription;
import com.hospital.model.PrescriptionItem;
import com.hospital.repository.PrescriptionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PrescriptionService {
    private final PrescriptionRepository repository;

    public PrescriptionService() {
        this(PrescriptionRepository.DEFAULT_FILE);
    }

    public PrescriptionService(String filePath) {
        this.repository = new PrescriptionRepository(filePath);
    }

    public Prescription createPrescription(String patientId, String doctorId,
                                           String diagnosis, String instructions) {
        PermissionService.getInstance().require(Permission.CREATE_PRESCRIPTION);
        if (patientId == null || patientId.isBlank()) {
            throw new InvalidDataException("Patient id cannot be empty.");
        }
        if (doctorId == null || doctorId.isBlank()) {
            throw new InvalidDataException("Doctor id cannot be empty.");
        }
        Prescription p = new Prescription(repository.nextPrescriptionId(), patientId,
                doctorId, LocalDate.now(), diagnosis);
        p.setInstructions(instructions);
        repository.add(p);
        return p;
    }

    public Prescription addMedicine(String prescriptionId, String medicineName, String dosage,
                                    String frequency, String duration, String instructions) {
        PermissionService.getInstance().require(Permission.CREATE_PRESCRIPTION);
        Prescription p = require(prescriptionId);
        if (medicineName == null || medicineName.isBlank()) {
            throw new InvalidDataException("Medicine name cannot be empty.");
        }
        if (dosage == null || dosage.isBlank()) {
            throw new InvalidDataException("Dosage cannot be empty.");
        }
        p.addItem(new PrescriptionItem(medicineName, dosage, frequency, duration, instructions));
        repository.update(p);
        return p;
    }

    public List<Prescription> getAllPrescriptions() { return repository.findAll(); }
    public List<Prescription> getPrescriptionsForPatient(String patientId) {
        return repository.findByPatient(patientId);
    }
    public Optional<Prescription> findPrescription(String id) { return repository.findById(id); }
    public int getPrescriptionCount() { return repository.count(); }
    public String nextPrescriptionId() { return repository.nextPrescriptionId(); }

    private Prescription require(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("Prescription id cannot be empty.");
        }
        return repository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Prescription not found: " + id));
    }
}
