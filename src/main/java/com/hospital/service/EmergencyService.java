package com.hospital.service;

import com.hospital.enums.EmergencyCaseStatus;
import com.hospital.enums.EmergencyLevel;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.EmergencyCase;
import com.hospital.repository.EmergencyCaseRepository;

import java.util.List;
import java.util.Optional;

public class EmergencyService {

    private final EmergencyCaseRepository repository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final TriageQueue queue;
    private final EmergencyTreatmentFlow flow;

    public EmergencyService(PatientService patientService) {
        this(patientService, new DoctorService(), EmergencyCaseRepository.DEFAULT_FILE);
    }

    public EmergencyService(PatientService patientService, String filePath) {
        this(patientService, new DoctorService(), filePath);
    }

    public EmergencyService(PatientService patientService, DoctorService doctorService) {
        this(patientService, doctorService, EmergencyCaseRepository.DEFAULT_FILE);
    }

    public EmergencyService(PatientService patientService, DoctorService doctorService, String filePath) {
        if (patientService == null) {
            throw new InvalidDataException("EmergencyService needs a PatientService to verify patients.");
        }
        if (doctorService == null) {
            throw new InvalidDataException("EmergencyService needs a DoctorService to assign doctors.");
        }
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.repository = new EmergencyCaseRepository(filePath);
        this.queue = new TriageQueue();
        this.flow = new EmergencyTreatmentFlow(repository, doctorService, queue);
        queue.loadWaiting(repository.findAll());
    }

    public EmergencyCase addEmergencyCase(String patientId, EmergencyLevel level, String description) {
        validateInput(patientId, level, description);
        if (patientService.findPatientById(patientId).isEmpty()) {
            throw new InvalidDataException("Cannot register emergency case, patient not found: " + patientId);
        }
        EmergencyCase c = queue.createCase(repository.nextCaseId(), patientId, level, description);
        repository.add(c);
        queue.offer(c);
        return c;
    }

    public EmergencyCase registerCase(String patientId, EmergencyLevel level, String description) {
        return addEmergencyCase(patientId, level, description);
    }

    private static void validateInput(String pid, EmergencyLevel level, String desc) {
        if (pid == null || pid.isBlank()) throw new InvalidDataException("Patient id cannot be empty.");
        if (level == null) throw new InvalidDataException("Emergency priority cannot be empty.");
        if (desc == null || desc.isBlank()) throw new InvalidDataException("Emergency description cannot be empty.");
    }

    public List<EmergencyCase> viewEmergencyQueue() {
        return queue.orderedView();
    }

    public List<EmergencyCase> getWaitingQueue() {
        return viewEmergencyQueue();
    }

    public EmergencyCase getNextPatient() {
        return queue.peek();
    }

    public EmergencyCase startTreatment() {
        return startTreatment(getNextPatient().getId());
    }

    public EmergencyCase startTreatment(String caseId) {
        return flow.startTreatment(caseId);
    }

    public EmergencyCase completeTreatment(String caseId) {
        return flow.completeTreatment(caseId);
    }

    public EmergencyCase cancelEmergencyCase(String caseId) {
        return flow.cancelCase(caseId);
    }

    public Optional<EmergencyCase> findCaseById(String caseId) {
        return flow.findCase(caseId);
    }

    public EmergencyCase updateStatus(String caseId, EmergencyCaseStatus status) {
        return flow.updateStatus(caseId, status);
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
}
