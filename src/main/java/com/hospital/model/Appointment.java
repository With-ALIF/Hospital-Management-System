package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Appointment {
    private String id;
    private Patient patient;
    private Doctor doctor;
    private String patientId;
    private String doctorId;
    private LocalDate date;
    private LocalTime time;
    private String reason;
    private AppointmentStatus status;
    private LocalDateTime createdAt;

    public Appointment() {
    }

    public Appointment(String id, Patient patient, Doctor doctor, LocalDate date,
                       LocalTime time, String reason, AppointmentStatus status) {
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

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Patient getPatient() { return patient; }

    public void setPatient(Patient patient) {
        this.patient = patient;
        if (patient != null && patient.getId() != null) {
            this.patientId = patient.getId();
        }
    }

    public Doctor getDoctor() { return doctor; }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        if (doctor != null && doctor.getId() != null) {
            this.doctorId = doctor.getId();
        }
    }

    public String getPatientId() {
        if (patientId != null && !patientId.isBlank()) return patientId;
        return patient != null ? patient.getId() : null;
    }

    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getDoctorId() {
        if (doctorId != null && !doctorId.isBlank()) return doctorId;
        return doctor != null ? doctor.getId() : null;
    }

    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @JsonIgnore
    public String getPatientName() {
        if (patient != null) return patient.getName();
        return patientId != null ? patientId : "N/A";
    }

    @JsonIgnore
    public String getDoctorName() {
        if (doctor != null) return doctor.getName();
        return doctorId != null ? doctorId : "N/A";
    }

    @JsonIgnore
    public String getFormattedTime() {
        return time != null ? time.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) : "N/A";
    }

    @JsonIgnore
    public String getFormattedDate() {
        return date != null ? date.toString() : "N/A";
    }

    @JsonIgnore
    public String getStatusName() {
        return status != null ? status.name() : "UNKNOWN";
    }

    public void displayInfo() {
        System.out.println("=== Appointment " + id + " ===");
        System.out.println("Patient: " + getPatientId() + " " + getPatientName());
        System.out.println("Doctor: " + getDoctorId() + " " + getDoctorName());
        System.out.println("Date: " + getFormattedDate() + " " + getFormattedTime());
        System.out.println("Reason: " + reason + " | Status: " + status);
    }
}
