package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

/**
 * An emergency case reported for an already registered patient.
 *
 * The case keeps the patient id instead of the whole patient object, so the
 * JSON file stays readable (see data/emergency_cases.json) and a case can be
 * loaded independently from the patient file.
 *
 * Day 4 adds {@code arrivalTime} (FIFO order inside one priority level) and
 * {@code assignedDoctorId} (the doctor treating the case). Both fields are
 * optional when loading, so older JSON files without them still work.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmergencyCase {
    private String id;
    private String patientId;
    private EmergencyPriority priority;
    private String description;
    private EmergencyCaseStatus status;
    private LocalDateTime arrivalTime;
    private String assignedDoctorId;

    public EmergencyCase() {
    }

    public EmergencyCase(String id, String patientId, EmergencyPriority priority,
                         String description, EmergencyCaseStatus status) {
        this(id, patientId, priority, description, status, LocalDateTime.now(), null);
    }

    public EmergencyCase(String id, String patientId, EmergencyPriority priority,
                         String description, EmergencyCaseStatus status,
                         LocalDateTime arrivalTime, String assignedDoctorId) {
        this.id = id;
        this.patientId = patientId;
        this.priority = priority;
        this.description = description;
        this.status = status != null ? status : EmergencyCaseStatus.WAITING;
        this.arrivalTime = arrivalTime;
        this.assignedDoctorId = assignedDoctorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public EmergencyPriority getPriority() {
        return priority;
    }

    public void setPriority(EmergencyPriority priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EmergencyCaseStatus getStatus() {
        return status;
    }

    public void setStatus(EmergencyCaseStatus status) {
        this.status = status;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getAssignedDoctorId() {
        return assignedDoctorId;
    }

    public void setAssignedDoctorId(String assignedDoctorId) {
        this.assignedDoctorId = assignedDoctorId;
    }

    public void displayInfo() {
        System.out.println("Emergency Case: " + id);
        System.out.println("Patient ID: " + patientId);
        System.out.println("Priority: " + priority);
        System.out.println("Description: " + description);
        System.out.println("Status: " + status);
        System.out.println("Arrival Time: " + (arrivalTime != null ? arrivalTime : "-"));
        System.out.println("Assigned Doctor: " + (assignedDoctorId != null ? assignedDoctorId : "-"));
    }
}
