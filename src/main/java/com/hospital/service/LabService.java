package com.hospital.service;

import com.hospital.enums.LabCategory;
import com.hospital.enums.LabStatus;
import com.hospital.enums.Permission;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.LabTest;
import com.hospital.repository.LabTestRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LabService {
    private final LabTestRepository repository;
    private final PatientService patientService;

    public LabService() {
        this(new PatientService(), LabTestRepository.DEFAULT_FILE);
    }

    public LabService(PatientService patientService, String filePath) {
        this.patientService = patientService;
        this.repository = new LabTestRepository(filePath);
    }

    public LabTest orderTest(String patientId, String doctorId, String testName,
                             LabCategory category, double cost) {
        PermissionService.getInstance().require(Permission.CREATE_LAB);
        if (patientId == null || patientService.findPatientById(patientId).isEmpty()) {
            throw new InvalidDataException("Patient not found: " + patientId);
        }
        if (testName == null || testName.isBlank()) {
            throw new InvalidDataException("Test name cannot be empty.");
        }
        LabTest test = new LabTest(repository.nextLabTestId(), patientId, doctorId,
                testName, category, cost);
        repository.add(test);
        return test;
    }

    public LabTest updateStatus(String testId, LabStatus next) {
        PermissionService.getInstance().require(Permission.UPDATE_LAB_RESULT);
        LabTest test = require(testId);
        if (!test.getStatus().canTransitionTo(next)) {
            throw new InvalidDataException("Invalid lab status transition: "
                    + test.getStatus() + " -> " + next);
        }
        test.setStatus(next);
        if (next == LabStatus.COMPLETED) {
            test.setReportDate(LocalDate.now());
        }
        repository.update(test);
        return test;
    }

    public LabTest enterResult(String testId, String result, String technicianId) {
        PermissionService.getInstance().require(Permission.UPDATE_LAB_RESULT);
        LabTest test = require(testId);
        if (test.getStatus() != LabStatus.PROCESSING && test.getStatus() != LabStatus.SAMPLE_COLLECTED) {
            throw new InvalidDataException("Lab test must be in PROCESSING before a result is entered.");
        }
        if (result == null || result.isBlank()) {
            throw new InvalidDataException("Result cannot be empty.");
        }
        test.setResult(result);
        test.setTechnicianId(technicianId);
        test.setStatus(LabStatus.COMPLETED);
        test.setReportDate(LocalDate.now());
        repository.update(test);
        return test;
    }

    public Optional<LabTest> findTest(String id) { return repository.findById(id); }
    public List<LabTest> getAllTests() { return repository.findAll(); }
    public List<LabTest> getTestsForPatient(String patientId) {
        return repository.findByPatient(patientId);
    }
    public int getTestCount() { return repository.count(); }
    public String nextTestId() { return repository.nextLabTestId(); }

    private LabTest require(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("Lab test id cannot be empty.");
        }
        return repository.findById(id)
                .orElseThrow(() -> new InvalidDataException("Lab test not found: " + id));
    }
}
