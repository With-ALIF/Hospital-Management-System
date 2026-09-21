package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Appointment {
    private String id;
    private Patient patient;
    private Doctor doctor;
    private LocalDate date;
    private LocalTime time;
    private String reason;
    private AppointmentStatus status;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public Appointment() {
    }

    public Appointment(String id, Patient patient, Doctor doctor, LocalDate date, LocalTime time, String reason, AppointmentStatus status) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.status = status != null ? status : AppointmentStatus.SCHEDULED;
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
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
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

    // Convenience getters for UI display (not part of the saved JSON)
    @JsonIgnore
    public String getPatientName() {
        return patient != null ? patient.getName() : "N/A";
    }

    @JsonIgnore
    public String getDoctorName() {
        return doctor != null ? doctor.getName() : "N/A";
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
        System.out.println("Patient: " + (patient != null ? patient.getId() + " " + patient.getName() : "N/A"));
        System.out.println("Doctor: " + (doctor != null ? doctor.getId() + " " + doctor.getName() : "N/A"));
        System.out.println("Date: " + date);
        System.out.println("Time: " + getFormattedTime());
        System.out.println("Reason: " + reason);
        System.out.println("Status: " + status);
    }
}
