package com.hospital.service;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.Doctor;
import com.hospital.repository.DoctorRepository;

import java.util.List;
import java.util.Optional;

/**
 * Business logic for doctors.
 *
 * Duty slots, availability and specialization are stored in
 * {@code data/doctors.json} and the file is written again automatically after
 * every change (auto sync).
 */
public class DoctorService {

    private final DoctorRepository repository;

    public DoctorService() {
        this(DoctorRepository.DEFAULT_FILE);
    }

    public DoctorService(String filePath) {
        this.repository = new DoctorRepository(filePath);
    }

    public Doctor registerDoctor(Doctor doctor) {
        validate(doctor);
        repository.add(doctor);
        return doctor;
    }

    public void updateDoctor(Doctor doctor) {
        validate(doctor);
        repository.update(doctor);
    }

    public void removeDoctor(String doctorId) {
        if (doctorId == null || doctorId.isBlank()) {
            throw new InvalidDataException("Doctor id cannot be empty.");
        }
        repository.delete(doctorId);
    }

    /** Turns a doctor on / off duty; the change is saved immediately. */
    public Doctor setAvailability(String doctorId, boolean available) {
        Doctor doctor = repository.findById(doctorId)
                .orElseThrow(() -> new InvalidDataException("Doctor not found: " + doctorId));
        doctor.setAvailable(available);
        repository.update(doctor);
        return doctor;
    }

    public Optional<Doctor> findDoctorById(String doctorId) {
        return repository.findById(doctorId);
    }

    public List<Doctor> getAllDoctors() {
        return repository.findAll();
    }

    public List<Doctor> getAvailableDoctors() {
        return repository.findAll().stream()
                .filter(Doctor::getAvailable)
                .toList();
    }

    public int getDoctorCount() {
        return repository.count();
    }

    public String nextDoctorId() {
        return repository.nextDoctorId();
    }

    private void validate(Doctor doctor) {
        if (doctor == null) {
            throw new InvalidDataException("Doctor cannot be null.");
        }
        if (doctor.getId() == null || doctor.getId().isBlank()) {
            throw new InvalidDataException("Doctor id cannot be empty.");
        }
        if (doctor.getName() == null || doctor.getName().isBlank()) {
            throw new InvalidDataException("Doctor name cannot be empty.");
        }
        if (doctor.getSpecialization() == null || doctor.getSpecialization().isBlank()) {
            throw new InvalidDataException("Doctor specialization cannot be empty.");
        }
    }
}