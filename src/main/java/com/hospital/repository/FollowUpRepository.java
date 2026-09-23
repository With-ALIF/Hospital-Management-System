package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.enums.FollowUpStatus;
import com.hospital.model.FollowUp;

import java.time.LocalDate;
import java.util.List;

public class FollowUpRepository extends AbstractJsonRepository<FollowUp> {
    public static final String DEFAULT_FILE = "data/follow_ups.json";

    public FollowUpRepository() { this(DEFAULT_FILE); }
    public FollowUpRepository(String filePath) {
        super(filePath, new TypeReference<List<FollowUp>>() {}, "FollowUp");
    }

    @Override
    protected String idOf(FollowUp e) { return e.getId(); }

    public String nextId() { return nextId("FLW-", 1); }

    public List<FollowUp> findUpcoming() {
        LocalDate today = LocalDate.now();
        return findAll().stream()
                .filter(f -> f.getStatus() == FollowUpStatus.SCHEDULED)
                .filter(f -> f.getFollowUpDate() != null && !f.getFollowUpDate().isBefore(today))
                .toList();
    }

    public List<FollowUp> findByDate(LocalDate date) {
        return findAll().stream()
                .filter(f -> f.getFollowUpDate() != null && f.getFollowUpDate().equals(date))
                .toList();
    }

    public List<FollowUp> findByPatient(String patientId) {
        return findAll().stream()
                .filter(f -> patientId != null && patientId.equals(f.getPatientId()))
                .toList();
    }

    public List<FollowUp> findByDoctor(String doctorId) {
        return findAll().stream()
                .filter(f -> doctorId != null && doctorId.equals(f.getDoctorId()))
                .toList();
    }
}
