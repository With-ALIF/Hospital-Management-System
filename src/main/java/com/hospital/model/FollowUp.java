package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.FollowUpStatus;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FollowUp {
    private String id;
    private String patientId;
    private String doctorId;
    private String originalVisitId;
    private LocalDate followUpDate;
    private String reason;
    private String instructions;
    private FollowUpStatus status;
    private String dischargeId;

    public FollowUp() {
        this.status = FollowUpStatus.SCHEDULED;
    }

    public FollowUp(String id, String patientId, String doctorId,
                    LocalDate followUpDate, String reason, String instructions) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.followUpDate = followUpDate;
        this.reason = reason;
        this.instructions = instructions;
        this.status = FollowUpStatus.SCHEDULED;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public String getOriginalVisitId() { return originalVisitId; }
    public void setOriginalVisitId(String originalVisitId) { this.originalVisitId = originalVisitId; }
    public LocalDate getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(LocalDate followUpDate) { this.followUpDate = followUpDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public FollowUpStatus getStatus() { return status; }
    public void setStatus(FollowUpStatus status) { this.status = status; }
    public String getDischargeId() { return dischargeId; }
    public void setDischargeId(String dischargeId) { this.dischargeId = dischargeId; }
}
