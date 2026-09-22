package com.hospital.service;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.Doctor;
import com.hospital.model.EmergencyCase;
import com.hospital.model.EmergencyCaseStatus;
import com.hospital.model.EmergencyPriority;
import com.hospital.repository.EmergencyCaseRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

/**
 * Business logic for emergency cases (Day 4: triage + priority queue).
 *
 * <p>Waiting cases live in a {@link PriorityQueue} ordered by
 * {@link EmergencyPriority} (CRITICAL first) and, within the same priority,
 * by arrival time (first come, first served). Every change is written back to
 * {@code data/emergency_cases.json} automatically through the repository
 * (auto sync), and starting / finishing a treatment also toggles the assigned
 * doctor's availability in {@code data/doctors.json}.</p>
 */
public class EmergencyService {

    /**
     * Triage order: most severe first; equal priority keeps arrival order
     * (older case first). The id is only a final tie-breaker for cases that
     * share the exact same arrival timestamp.
     */
    private static final Comparator<EmergencyCase> TRIAGE_ORDER =
            Comparator.comparingInt(EmergencyService::severityOf).reversed()
                    .thenComparing(EmergencyCase::getArrivalTime,
                            Comparator.nullsFirst(Comparator.naturalOrder()))
                    .thenComparing(EmergencyCase::getId,
                            Comparator.nullsLast(Comparator.naturalOrder()));

    private final EmergencyCaseRepository repository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final PriorityQueue<EmergencyCase> waitingQueue = new PriorityQueue<>(TRIAGE_ORDER);

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
        // Rebuild the in-memory queue from the JSON file (restart support).
        for (EmergencyCase emergencyCase : repository.findAll()) {
            if (emergencyCase.getStatus() == EmergencyCaseStatus.WAITING) {
                waitingQueue.offer(emergencyCase);
            }
        }
    }

    // ------------------------------------------------------------- triage input

    /**
     * Registers a new waiting emergency case for an existing patient and
     * offers it to the priority queue immediately (auto sync).
     */
    public EmergencyCase addEmergencyCase(String patientId, EmergencyPriority priority, String description) {
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
        waitingQueue.offer(emergencyCase);
        return emergencyCase;
    }

    /** Kept for Day 3 compatibility; same as {@link #addEmergencyCase}. */
    public EmergencyCase registerCase(String patientId, EmergencyPriority priority, String description) {
        return addEmergencyCase(patientId, priority, description);
    }

    // ------------------------------------------------------------ queue views

    /**
     * The waiting cases in exact triage order: CRITICAL → HIGH → MEDIUM → LOW,
     * arrival order inside the same priority.
     */
    public List<EmergencyCase> viewEmergencyQueue() {
        // PriorityQueue iteration order is not sorted, so sort a copy explicitly.
        List<EmergencyCase> ordered = new ArrayList<>(waitingQueue);
        ordered.sort(TRIAGE_ORDER);
        return List.copyOf(ordered);
    }

    /** Day 3 name kept; identical to {@link #viewEmergencyQueue()}. */
    public List<EmergencyCase> getWaitingQueue() {
        return viewEmergencyQueue();
    }

    /**
     * Peeks the next patient that should be treated without removing it.
     *
     * @throws InvalidDataException if the emergency queue is empty
     */
    public EmergencyCase getNextPatient() {
        EmergencyCase next = waitingQueue.peek();
        if (next == null) {
            throw new InvalidDataException("Emergency queue is empty. No patient is waiting.");
        }
        return next;
    }

    // --------------------------------------------------------- treatment flow

    /**
     * Starts treatment for the next patient in the queue: finds an available
     * doctor, assigns it and moves the case to IN_TREATMENT.
     *
     * @throws InvalidDataException if the queue is empty or no doctor is free
     */
    public EmergencyCase startTreatment() {
        return startTreatment(getNextPatient().getId());
    }

    /**
     * Starts treatment for one waiting case.
     *
     * <p>Flow: find available doctor → assign → IN_TREATMENT. If no doctor is
     * available the case stays WAITING and an error is thrown.</p>
     */
    public EmergencyCase startTreatment(String caseId) {
        EmergencyCase emergencyCase = requireCase(caseId);
        ensureNotFinished(emergencyCase);
        if (emergencyCase.getStatus() == EmergencyCaseStatus.IN_TREATMENT) {
            throw new InvalidDataException("Emergency case " + caseId + " is already in treatment.");
        }

        // Find the doctor first: on failure the case must stay WAITING untouched.
        Doctor doctor = doctorService.getAvailableDoctors().stream()
                .findFirst()
                .orElseThrow(() -> new InvalidDataException(
                        "No available doctor right now. Patient keeps WAITING in the emergency queue."));

        emergencyCase.setStatus(EmergencyCaseStatus.IN_TREATMENT);
        emergencyCase.setAssignedDoctorId(doctor.getId());
        waitingQueue.remove(emergencyCase);
        repository.update(emergencyCase);                 // → emergency_cases.json
        doctorService.setAvailability(doctor.getId(), false); // → doctors.json
        return emergencyCase;
    }

    /**
     * Completes an in-treatment case and frees the assigned doctor
     * (both JSON files are updated automatically).
     */
    public EmergencyCase completeTreatment(String caseId) {
        EmergencyCase emergencyCase = requireCase(caseId);
        ensureNotFinished(emergencyCase);
        if (emergencyCase.getStatus() == EmergencyCaseStatus.WAITING) {
            throw new InvalidDataException(
                    "Invalid status transition: WAITING -> COMPLETED for case " + caseId
                            + ". Start the treatment first.");
        }

        emergencyCase.setStatus(EmergencyCaseStatus.COMPLETED);
        waitingQueue.remove(emergencyCase);
        repository.update(emergencyCase); // → emergency_cases.json

        String doctorId = emergencyCase.getAssignedDoctorId();
        if (doctorId != null && doctorService.findDoctorById(doctorId).isPresent()) {
            doctorService.setAvailability(doctorId, true); // doctor is free again
        }
        return emergencyCase;
    }

    /** Cancels a case that is still waiting (WAITING → CANCELLED). */
    public EmergencyCase cancelEmergencyCase(String caseId) {
        EmergencyCase emergencyCase = requireCase(caseId);
        ensureNotFinished(emergencyCase);
        if (emergencyCase.getStatus() == EmergencyCaseStatus.IN_TREATMENT) {
            throw new InvalidDataException(
                    "Invalid status transition: IN_TREATMENT -> CANCELLED for case " + caseId
                            + ". Treatment already started.");
        }

        emergencyCase.setStatus(EmergencyCaseStatus.CANCELLED);
        waitingQueue.remove(emergencyCase);
        repository.update(emergencyCase); // → emergency_cases.json
        return emergencyCase;
    }

    // -------------------------------------------------------------- status API

    /** Looks up one emergency case by its id. */
    public Optional<EmergencyCase> findCaseById(String caseId) {
        return repository.findById(caseId);
    }

    /**
     * Low-level status move with transition validation
     * (WAITING → IN_TREATMENT → COMPLETED, WAITING → CANCELLED).
     * The JSON file is updated immediately.
     */
    public EmergencyCase updateStatus(String caseId, EmergencyCaseStatus status) {
        if (status == null) {
            throw new InvalidDataException("Emergency case status cannot be empty.");
        }
        EmergencyCase emergencyCase = requireCase(caseId);
        EmergencyCaseStatus current = emergencyCase.getStatus();
        if (current == status) {
            throw new InvalidDataException("Emergency case " + caseId + " is already marked as " + status + ".");
        }
        if (!current.canTransitionTo(status)) {
            throw new InvalidDataException(
                    "Invalid status transition: " + current + " -> " + status + " for case " + caseId + ".");
        }

        emergencyCase.setStatus(status);
        if (status != EmergencyCaseStatus.WAITING) {
            waitingQueue.remove(emergencyCase);
        }
        repository.update(emergencyCase);
        return emergencyCase;
    }

    // ----------------------------------------------------------------- queries

    public List<EmergencyCase> getCasesForPatient(String patientId) {
        return repository.findByPatientId(patientId);
    }

    public List<EmergencyCase> getAllCases() {
        return repository.findAll();
    }

    public int getCaseCount() {
        return repository.count();
    }

    // ----------------------------------------------------------------- helpers

    private EmergencyCase requireCase(String caseId) {
        if (caseId == null || caseId.isBlank()) {
            throw new InvalidDataException("Emergency case id cannot be empty.");
        }
        return repository.findById(caseId)
                .orElseThrow(() -> new InvalidDataException("Emergency case not found: " + caseId));
    }

    private static void ensureNotFinished(EmergencyCase emergencyCase) {
        EmergencyCaseStatus status = emergencyCase.getStatus();
        if (status == EmergencyCaseStatus.COMPLETED) {
            throw new InvalidDataException("Emergency case " + emergencyCase.getId() + " is already completed.");
        }
        if (status == EmergencyCaseStatus.CANCELLED) {
            throw new InvalidDataException("Emergency case " + emergencyCase.getId() + " is already cancelled.");
        }
    }

    private static int severityOf(EmergencyCase emergencyCase) {
        EmergencyPriority priority = emergencyCase.getPriority();
        return priority != null ? priority.getSeverity() : 0;
    }
}
