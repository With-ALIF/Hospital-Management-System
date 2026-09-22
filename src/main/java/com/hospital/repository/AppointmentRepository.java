package com.hospital.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.model.Appointment;

import java.time.LocalDate;
import java.util.List;

/**
 * Stores appointments in {@code data/appointments.json}.
 * Every modification writes the file again (auto sync).
 */
public class AppointmentRepository extends AbstractJsonRepository<Appointment> {

    public static final String DEFAULT_FILE = "data/appointments.json";

    public AppointmentRepository() {
        this(DEFAULT_FILE);
    }

    public AppointmentRepository(String filePath) {
        super(filePath, new TypeReference<List<Appointment>>() {}, "Appointment");
    }

    @Override
    protected String idOf(Appointment appointment) {
        return appointment.getId();
    }

    public String nextAppointmentId() {
        return nextId("A-", 1001);
    }

    public List<Appointment> findByPatientId(String patientId) {
        if (patientId == null) return List.of();
        return findAll().stream()
                .filter(a -> patientId.equalsIgnoreCase(a.getPatientId()))
                .toList();
    }

    public List<Appointment> findByDoctorId(String doctorId) {
        if (doctorId == null) return List.of();
        return findAll().stream()
                .filter(a -> doctorId.equalsIgnoreCase(a.getDoctorId()))
                .toList();
    }

    public List<Appointment> findByDate(LocalDate date) {
        if (date == null) return List.of();
        return findAll().stream()
                .filter(a -> date.equals(a.getDate()))
                .toList();
    }
}
