package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.BloodGroup;
import com.hospital.enums.BloodUnitStatus;
import com.hospital.enums.RhType;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BloodUnit {
    private String id;
    private BloodGroup bloodGroup;
    private RhType rhType;
    private LocalDate collectionDate;
    private LocalDate expiryDate;
    private String donorId;
    private BloodUnitStatus status;
    private String storageLocation;
    private String issuedToPatientId;
    private String reservedForPatientId;
    private LocalDate issueDate;

    public BloodUnit() {
        this.status = BloodUnitStatus.AVAILABLE;
    }

    public BloodUnit(String id, BloodGroup bloodGroup, RhType rhType,
                     LocalDate collectionDate, LocalDate expiryDate, String donorId) {
        this.id = id;
        this.bloodGroup = bloodGroup;
        this.rhType = rhType;
        this.collectionDate = collectionDate;
        this.expiryDate = expiryDate;
        this.donorId = donorId;
        this.status = BloodUnitStatus.AVAILABLE;
        this.storageLocation = "BANK-A1";
    }

    public boolean isExpired(LocalDate today) {
        return expiryDate != null && today != null && today.isAfter(expiryDate);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public BloodGroup getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }
    public RhType getRhType() { return rhType; }
    public void setRhType(RhType rhType) { this.rhType = rhType; }
    public LocalDate getCollectionDate() { return collectionDate; }
    public void setCollectionDate(LocalDate collectionDate) { this.collectionDate = collectionDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public String getDonorId() { return donorId; }
    public void setDonorId(String donorId) { this.donorId = donorId; }
    public BloodUnitStatus getStatus() { return status; }
    public void setStatus(BloodUnitStatus status) { this.status = status; }
    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
    public String getIssuedToPatientId() { return issuedToPatientId; }
    public void setIssuedToPatientId(String issuedToPatientId) { this.issuedToPatientId = issuedToPatientId; }
    public String getReservedForPatientId() { return reservedForPatientId; }
    public void setReservedForPatientId(String reservedForPatientId) { this.reservedForPatientId = reservedForPatientId; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
}
