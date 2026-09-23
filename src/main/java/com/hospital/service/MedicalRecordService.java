package com.hospital.service;

import com.hospital.exception.InvalidDataException;
import com.hospital.model.MedicalRecord;
import com.hospital.repository.MedicalRecordRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MedicalRecordService {
    private final MedicalRecordRepository repository;
    private final PatientService patientService;

    public MedicalRecordService() {
        this(new PatientService(), MedicalRecordRepository.DEFAULT_FILE);
    }

    public MedicalRecordService(PatientService patientService, String filePath) {
        this.patientService = patientService;
        this.repository = new MedicalRecordRepository(filePath);
    }

    public MedicalRecord createRecord(String patientId, String doctorId, String symptoms,
                                      String diagnosis, String treatment, String notes) {
        if (patientId == null || patientService.findPatientById(patientId).isEmpty()) {
            throw new InvalidDataException("Patient not found: " + patientId);
        }
        if (diagnosis == null || diagnosis.isBlank()) {
            throw new InvalidDataException("Diagnosis cannot be empty.");
        }
        MedicalRecord record = new MedicalRecord(repository.nextRecordId(), patientId,
                doctorId, LocalDate.now(), symptoms, diagnosis, treatment);
        record.setNotes(notes);
        repository.add(record);
        return record;
    }

    public MedicalRecord updateRecord(MedicalRecord record) {
        if (record == null || record.getId() == null) {
            throw new InvalidDataException("Medical record id cannot be empty.");
        }
        repository.update(record);
        return record;
    }

    public void removeRecord(String recordId) {
        if (recordId == null || recordId.isBlank()) {
            throw new InvalidDataException("Medical record id cannot be empty.");
        }
        repository.delete(recordId);
    }

    public Optional<MedicalRecord> findRecord(String id) { return repository.findById(id); }
    public List<MedicalRecord> getAllRecords() { return repository.findAll(); }
    public List<MedicalRecord> getRecordsForPatient(String patientId) {
        return repository.findByPatient(patientId);
    }
    public int getRecordCount() { return repository.count(); }
    public String nextRecordId() { return repository.nextRecordId(); }
}
