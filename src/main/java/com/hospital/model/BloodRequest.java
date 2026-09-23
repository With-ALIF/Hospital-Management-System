package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.BloodGroup;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BloodRequest {
    public enum Status { PENDING, FULFILLED, CANCELLED }

    private String id;
    private String patientId;
    private BloodGroup requiredGroup;
    private int unitsNeeded;
    private boolean emergency;
    private String requestedBy;
    private LocalDateTime requestedAt;
    private Status status;
    private String notes;

    public BloodRequest() {
        this.status = Status.PENDING;
        this.requestedAt = LocalDateTime.now();
    }

    public BloodRequest(String id, String patientId, BloodGroup requiredGroup,
                        int unitsNeeded, boolean emergency, String requestedBy) {
        this.id = id;
        this.patientId = patientId;
        this.requiredGroup = requiredGroup;
        this.unitsNeeded = unitsNeeded;
        this.emergency = emergency;
        this.requestedBy = requestedBy;
        this.status = Status.PENDING;
        this.requestedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public BloodGroup getRequiredGroup() { return requiredGroup; }
    public void setRequiredGroup(BloodGroup requiredGroup) { this.requiredGroup = requiredGroup; }
    public int getUnitsNeeded() { return unitsNeeded; }
    public void setUnitsNeeded(int unitsNeeded) { this.unitsNeeded = unitsNeeded; }
    public boolean isEmergency() { return emergency; }
    public void setEmergency(boolean emergency) { this.emergency = emergency; }
    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
