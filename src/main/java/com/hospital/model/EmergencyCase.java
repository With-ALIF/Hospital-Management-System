package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * An emergency case reported for an already registered patient.
 *
 * The case keeps the patient id instead of the whole patient object, so the
 * JSON file stays readable (see data/emergency_cases.json) and a case can be
 * loaded independently from the patient file.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmergencyCase {
    private String id;
    private String patientId;
    private EmergencyPriority priority;
    private String description;
    private EmergencyCaseStatus status;

    public EmergencyCase() {
    }

    public EmergencyCase(String id, String patientId, EmergencyPriority priority,
                         String description, EmergencyCaseStatus status) {
        this.id = id;
        this.patientId = patientId;
        this.priority = priority;
        this.description = description;
        this.status = status != null ? status : EmergencyCaseStatus.WAITING;
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

    public void displayInfo() {
        System.out.println("Emergency Case: " + id);
        System.out.println("Patient ID: " + patientId);
        System.out.println("Priority: " + priority);
        System.out.println("Description: " + description);
        System.out.println("Status: " + status);
    }
}