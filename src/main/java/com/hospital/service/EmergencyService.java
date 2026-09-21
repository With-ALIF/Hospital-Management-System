package com.hospital.service;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.EmergencyCase;
import com.hospital.model.EmergencyCaseStatus;
import com.hospital.model.EmergencyPriority;
import com.hospital.repository.EmergencyCaseRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Business logic for emergency cases.
 *
 * The service checks that the patient really exists (via {@link PatientService})
 * before a case is registered, and {@code data/emergency_cases.json} is written
 * again automatically after every change (auto sync).
 */
public class EmergencyService {

    private final EmergencyCaseRepository repository;
    private final PatientService patientService;

    public EmergencyService(PatientService patientService) {
        this(patientService, EmergencyCaseRepository.DEFAULT_FILE);
    }

    public EmergencyService(PatientService patientService, String filePath) {
        if (patientService == null) {
            throw new InvalidDataException("EmergencyService needs a PatientService to verify patients.");
        }
        this.patientService = patientService;
        this.repository = new EmergencyCaseRepository(filePath);
    }

    /** Registers a new waiting emergency case for an existing patient. */
    public EmergencyCase registerCase(String patientId, EmergencyPriority priority, String description) {
        if (patientId == null || patientId.isBlank()) {
            throw new InvalidDataException("Patient id cannot be empty.");
        }
        if (priority == null) {
            throw new InvalidDataException("Emergency priority cannot be empty.");
        }
        if (description == null || description.isBlank()) {
            throw new InvalidDataException("Emergency description cannot be empty.");
        }
        if (patientService.findPatientById(patientId).isEmpty()) {
            throw new InvalidDataException("Cannot register emergency case, patient not found: " + patientId);
        }

        EmergencyCase emergencyCase = new EmergencyCase(
                repository.nextCaseId(), patientId, priority, description, EmergencyCaseStatus.WAITING);
        repository.add(emergencyCase);
        return emergencyCase;
    }

    /** Looks up one emergency case by its id. */
    public Optional<EmergencyCase> findCaseById(String caseId) {
        return repository.findById(caseId);
    }

    /** Moves a case to the next status; the JSON file is updated immediately. */
    public EmergencyCase updateStatus(String caseId, EmergencyCaseStatus status) {
        if (status == null) {
            throw new InvalidDataException("Emergency case status cannot be empty.");
        }
        EmergencyCase emergencyCase = repository.findById(caseId)
                .orElseThrow(() -> new InvalidDataException("Emergency case not found: " + caseId));
        if (emergencyCase.getStatus() == status) {
            throw new InvalidDataException("Emergency case " + caseId + " is already marked as " + status + ".");
        }
        emergencyCase.setStatus(status);
        repository.update(emergencyCase);
        return emergencyCase;
    }

    /** Waiting cases, most severe first (simple triage order). */
    public List<EmergencyCase> getWaitingQueue() {
        return repository.findAll().stream()
                .filter(c -> c.getStatus() == EmergencyCaseStatus.WAITING)
                .sorted(Comparator.comparingInt(EmergencyService::severityOf).reversed())
                .toList();
    }

    public List<EmergencyCase> getCasesForPatient(String patientId) {
        return repository.findByPatientId(patientId);
    }

    public List<EmergencyCase> getAllCases() {
        return repository.findAll();
    }

    public int getCaseCount() {
        return repository.count();
    }

    private static int severityOf(EmergencyCase emergencyCase) {
        return emergencyCase.getPriority() != null ? emergencyCase.getPriority().getSeverity() : 0;
    }
}