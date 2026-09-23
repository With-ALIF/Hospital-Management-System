package com.hospital.service;

import com.hospital.enums.EmergencyCaseStatus;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Doctor;
import com.hospital.model.EmergencyCase;
import com.hospital.repository.EmergencyCaseRepository;

import java.util.Optional;

public class EmergencyTreatmentFlow {

    private final EmergencyCaseRepository repository;
    private final DoctorService doctorService;
    private final TriageQueue queue;

    public EmergencyTreatmentFlow(EmergencyCaseRepository repository,
                                  DoctorService doctorService, TriageQueue queue) {
        this.repository = repository;
        this.doctorService = doctorService;
        this.queue = queue;
    }

    public EmergencyCase startTreatment(String caseId) {
        EmergencyCase emergencyCase = requireCase(caseId);
        ensureNotFinished(emergencyCase);
        if (emergencyCase.getStatus() == EmergencyCaseStatus.IN_TREATMENT) {
            throw new InvalidDataException("Emergency case " + caseId + " is already in treatment.");
        }
        Doctor doctor = doctorService.getAvailableDoctors().stream()
                .findFirst()
                .orElseThrow(() -> new InvalidDataException(
                        "No available doctor right now. Patient keeps WAITING in the emergency queue."));
        emergencyCase.setStatus(EmergencyCaseStatus.IN_TREATMENT);
        emergencyCase.setAssignedDoctorId(doctor.getId());
        queue.remove(emergencyCase);
        repository.update(emergencyCase);
        doctorService.setAvailability(doctor.getId(), false);
        return emergencyCase;
    }

    public EmergencyCase completeTreatment(String caseId) {
        EmergencyCase emergencyCase = requireCase(caseId);
        ensureNotFinished(emergencyCase);
        if (emergencyCase.getStatus() == EmergencyCaseStatus.WAITING) {
            throw new InvalidDataException(
                    "Invalid status transition: WAITING -> COMPLETED for case " + caseId
                            + ". Start the treatment first.");
        }
        emergencyCase.setStatus(EmergencyCaseStatus.COMPLETED);
        queue.remove(emergencyCase);
        repository.update(emergencyCase);
        String doctorId = emergencyCase.getAssignedDoctorId();
        if (doctorId != null && doctorService.findDoctorById(doctorId).isPresent()) {
            doctorService.setAvailability(doctorId, true);
        }
        return emergencyCase;
    }

    public EmergencyCase cancelCase(String caseId) {
        EmergencyCase emergencyCase = requireCase(caseId);
        ensureNotFinished(emergencyCase);
        if (emergencyCase.getStatus() == EmergencyCaseStatus.IN_TREATMENT) {
            throw new InvalidDataException(
                    "Invalid status transition: IN_TREATMENT -> CANCELLED for case " + caseId
                            + ". Treatment already started.");
        }
        emergencyCase.setStatus(EmergencyCaseStatus.CANCELLED);
        queue.remove(emergencyCase);
        repository.update(emergencyCase);
        return emergencyCase;
    }

    public EmergencyCase updateStatus(String caseId, EmergencyCaseStatus status) {
        if (status == null) {
            throw new InvalidDataException("Emergency case status cannot be empty.");
        }
        EmergencyCase emergencyCase = requireCase(caseId);
        EmergencyCaseStatus current = emergencyCase.getStatus();
        if (current == status) {
            throw new InvalidDataException(
                    "Emergency case " + caseId + " is already marked as " + status + ".");
        }
        if (!current.canTransitionTo(status)) {
            throw new InvalidDataException(
                    "Invalid status transition: " + current + " -> " + status
                            + " for case " + caseId + ".");
        }
        emergencyCase.setStatus(status);
        if (status != EmergencyCaseStatus.WAITING) {
            queue.remove(emergencyCase);
        } else {
            queue.offer(emergencyCase);
        }
        repository.update(emergencyCase);
        return emergencyCase;
    }

    private EmergencyCase requireCase(String caseId) {
        if (caseId == null || caseId.isBlank()) {
            throw new InvalidDataException("Emergency case id cannot be empty.");
        }
        return repository.findById(caseId)
                .orElseThrow(() -> new InvalidDataException("Emergency case not found: " + caseId));
    }

    private static void ensureNotFinished(EmergencyCase c) {
        EmergencyCaseStatus status = c.getStatus();
        if (status == EmergencyCaseStatus.COMPLETED) {
            throw new InvalidDataException("Emergency case " + c.getId() + " is already completed.");
        }
        if (status == EmergencyCaseStatus.CANCELLED) {
            throw new InvalidDataException("Emergency case " + c.getId() + " is already cancelled.");
        }
    }

    public Optional<EmergencyCase> findCase(String caseId) {
        return repository.findById(caseId);
    }
}
