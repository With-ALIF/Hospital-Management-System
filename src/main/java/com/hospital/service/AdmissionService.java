package com.hospital.service;

import com.hospital.enums.AdmissionStatus;
import com.hospital.enums.BedStatus;
import com.hospital.exception.InvalidDataException;
import com.hospital.model.Admission;
import com.hospital.model.Bed;
import com.hospital.model.Discharge;
import com.hospital.model.Patient;
import com.hospital.repository.AdmissionRepository;
import com.hospital.repository.DischargeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AdmissionService {
    private final AdmissionRepository admissions;
    private final DischargeRepository discharges;
    private final BedService bedService;
    private final PatientService patientService;

    public AdmissionService() {
        this(new BedService(), new PatientService(),
                AdmissionRepository.DEFAULT_FILE, DischargeRepository.DEFAULT_FILE);
    }

    public AdmissionService(BedService bedService, PatientService patientService,
                            String admissionFile, String dischargeFile) {
        this.bedService = bedService;
        this.patientService = patientService;
        this.admissions = new AdmissionRepository(admissionFile);
        this.discharges = new DischargeRepository(dischargeFile);
    }

    public Admission admit(String patientId, String bedId, String doctorId, String reason) {
        Patient patient = patientService.findPatientById(patientId)
                .orElseThrow(() -> new InvalidDataException("Patient not found: " + patientId));
        Bed bed = bedService.findBed(bedId)
                .orElseThrow(() -> new InvalidDataException("Bed not found: " + bedId));
        if (bed.getStatus() != BedStatus.AVAILABLE) {
            throw new InvalidDataException("Bed is not available: " + bedId);
        }
        bedService.assignBed(bedId, patientId);
        String wardId = bed.getWardId();
        Admission admission = new Admission(admissions.nextAdmissionId(), patient.getId(),
                doctorId, wardId, bedId, reason);
        admissions.add(admission);
        return admission;
    }

    public Discharge discharge(String admissionId, String diagnosis, String notes, LocalDate followUp) {
        Admission admission = requireAdmission(admissionId);
        if (admission.getStatus() != AdmissionStatus.ADMITTED) {
            throw new InvalidDataException("Admission is not active: " + admissionId);
        }
        bedService.releaseBed(admission.getBedId());
        admission.setStatus(AdmissionStatus.DISCHARGED);
        admissions.update(admission);
        Discharge discharge = new Discharge(discharges.nextDischargeId(),
                admission.getPatientId(), admissionId, diagnosis);
        discharge.setFinalNotes(notes);
        discharge.setFollowUpDate(followUp);
        discharges.add(discharge);
        return discharge;
    }

    public Optional<Admission> findAdmission(String id) { return admissions.findById(id); }
    public List<Admission> getAllAdmissions() { return admissions.findAll(); }
    public List<Discharge> getAllDischarges() { return discharges.findAll(); }
    public int getAdmissionCount() { return admissions.count(); }
    public int getDischargeCount() { return discharges.count(); }
    public String nextAdmissionId() { return admissions.nextAdmissionId(); }

    private Admission requireAdmission(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("Admission id cannot be empty.");
        }
        return admissions.findById(id)
                .orElseThrow(() -> new InvalidDataException("Admission not found: " + id));
    }
}
