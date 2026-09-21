package com.hospital.service;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.Patient;
import com.hospital.repository.PatientRepository;

import java.util.List;
import java.util.Optional;

/**
 * Business logic for patients.
 *
 * The service validates the data, the repository stores it and
 * {@code data/patients.json} is written automatically after every change
 * (auto sync) - no manual save call is needed anywhere.
 */
public class PatientService {

    private final PatientRepository repository;

    public PatientService() {
        this(PatientRepository.DEFAULT_FILE);
    }

    public PatientService(String filePath) {
        this.repository = new PatientRepository(filePath);
    }

    public Patient registerPatient(Patient patient) {
        validate(patient);
        repository.add(patient);
        return patient;
    }

    public void updatePatient(Patient patient) {
        validate(patient);
        repository.update(patient);
    }

    public void removePatient(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            throw new InvalidDataException("Patient id cannot be empty.");
        }
        repository.delete(patientId);
    }

    public Optional<Patient> findPatientById(String patientId) {
        return repository.findById(patientId);
    }

    public List<Patient> getAllPatients() {
        return repository.findAll();
    }

    public int getPatientCount() {
        return repository.count();
    }

    public String nextPatientId() {
        return repository.nextPatientId();
    }

    private void validate(Patient patient) {
        if (patient == null) {
            throw new InvalidDataException("Patient cannot be null.");
        }
        if (patient.getId() == null || patient.getId().isBlank()) {
            throw new InvalidDataException("Patient id cannot be empty.");
        }
        if (patient.getName() == null || patient.getName().isBlank()) {
            throw new InvalidDataException("Patient name cannot be empty.");
        }
        if (patient.getPhone() == null || patient.getPhone().isBlank()) {
            throw new InvalidDataException("Patient phone number cannot be empty.");
        }
    }
}