package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.EmergencyCaseStatus;
import com.hospital.enums.EmergencyLevel;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmergencyCase {
    private String id;
    private String patientId;
    private EmergencyLevel priority;
    private String description;
    private EmergencyCaseStatus status;
    private LocalDateTime arrivalTime;
    private String assignedDoctorId;
    private String assignedNurseId;
    private VitalSigns vitals;
    private int priorityScore;

    public EmergencyCase() {
    }

    public EmergencyCase(String id, String patientId, EmergencyLevel priority,
                         String description, EmergencyCaseStatus status) {
        this(id, patientId, priority, description, status, LocalDateTime.now(), null);
    }

    public EmergencyCase(String id, String patientId, EmergencyLevel priority,
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

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public EmergencyLevel getPriority() { return priority; }
    public void setPriority(EmergencyLevel priority) { this.priority = priority; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public EmergencyCaseStatus getStatus() { return status; }
    public void setStatus(EmergencyCaseStatus status) { this.status = status; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public String getAssignedDoctorId() { return assignedDoctorId; }
    public void setAssignedDoctorId(String assignedDoctorId) { this.assignedDoctorId = assignedDoctorId; }
    public String getAssignedNurseId() { return assignedNurseId; }
    public void setAssignedNurseId(String assignedNurseId) { this.assignedNurseId = assignedNurseId; }
    public VitalSigns getVitals() { return vitals; }
    public void setVitals(VitalSigns vitals) { this.vitals = vitals; }
    public int getPriorityScore() { return priorityScore; }
    public void setPriorityScore(int priorityScore) { this.priorityScore = priorityScore; }

    public void displayInfo() {
        System.out.println("Emergency " + id + " | Patient: " + patientId
                + " | Level: " + priority + " | Score: " + priorityScore
                + " | Status: " + status + " | " + description);
    }
}
