package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Appointment {
    private String id;
    private Patient patient;
    private Doctor doctor;
    // Day5: flat ids + createdAt (kept alongside objects for backward compat)
    private String patientId;
    private String doctorId;
    private LocalDate date;
    private LocalTime time;
    private String reason;
    private AppointmentStatus status;
    private LocalDateTime createdAt;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public Appointment() {
    }

    public Appointment(String id, Patient patient, Doctor doctor, LocalDate date, LocalTime time, String reason, AppointmentStatus status) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.patientId = patient != null ? patient.getId() : null;
        this.doctorId = doctor != null ? doctor.getId() : null;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.status = status != null ? status : AppointmentStatus.SCHEDULED;
        this.createdAt = LocalDateTime.now();
    }

    // Day5 constructor with ids
    public Appointment(String id, String patientId, String doctorId, LocalDate date, LocalTime time, String reason, AppointmentStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.status = status != null ? status : AppointmentStatus.SCHEDULED;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    /** Needed so that the appointment id survives a load / save round trip. */
    public void setId(String id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
        if (patient != null && patient.getId() != null) {
            this.patientId = patient.getId();
        }
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        if (doctor != null && doctor.getId() != null) {
            this.doctorId = doctor.getId();
        }
    }

    // Day5: flat id accessors — resolve from embedded object if flat id is missing (legacy JSON)
    public String getPatientId() {
        if (patientId != null && !patientId.isBlank()) return patientId;
        if (patient != null && patient.getId() != null) return patient.getId();
        return null;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        if (doctorId != null && !doctorId.isBlank()) return doctorId;
        if (doctor != null && doctor.getId() != null) return doctor.getId();
        return null;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Convenience getters for UI display (not part of the saved JSON)
    @JsonIgnore
    public String getPatientName() {
        return patient != null ? patient.getName() : (patientId != null ? patientId : "N/A");
    }

    @JsonIgnore
    public String getDoctorName() {
        return doctor != null ? doctor.getName() : (doctorId != null ? doctorId : "N/A");
    }

    @JsonIgnore
    public String getFormattedTime() {
        return time != null ? time.format(TIME_FORMATTER) : "N/A";
    }

    /** Null safe date text, used by the tables so that an incomplete record cannot break the UI. */
    @JsonIgnore
    public String getFormattedDate() {
        return date != null ? date.toString() : "N/A";
    }

    /** Null safe status text, used by the tables. */
    @JsonIgnore
    public String getStatusName() {
        return status != null ? status.name() : "UNKNOWN";
    }

    public void displayInfo() {
        System.out.println("=== Appointment " + id + " ===");
        System.out.println("Patient: " + getPatientId() + " " + getPatientName());
        System.out.println("Doctor: " + getDoctorId() + " " + getDoctorName());
        System.out.println("Date: " + date);
        System.out.println("Time: " + getFormattedTime());
        System.out.println("Reason: " + reason);
        System.out.println("Status: " + status);
        System.out.println("CreatedAt: " + createdAt);
    }
}
