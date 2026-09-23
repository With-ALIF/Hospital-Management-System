package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.BedStatus;
import com.hospital.enums.BedType;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bed {
    private String id;
    private String wardId;
    private BedType type;
    private BedStatus status;
    private String patientId;
    private LocalDate assignedDate;

    public Bed() {
        this.status = BedStatus.AVAILABLE;
        this.type = BedType.GENERAL;
    }

    public Bed(String id, String wardId, BedType type) {
        this.id = id;
        this.wardId = wardId;
        this.type = type != null ? type : BedType.GENERAL;
        this.status = BedStatus.AVAILABLE;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getWardId() { return wardId; }
    public void setWardId(String wardId) { this.wardId = wardId; }
    public BedType getType() { return type; }
    public void setType(BedType type) { this.type = type; }
    public BedStatus getStatus() { return status != null ? status : BedStatus.AVAILABLE; }
    public void setStatus(BedStatus status) { this.status = status; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public LocalDate getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDate assignedDate) { this.assignedDate = assignedDate; }
}
