package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.TripStatus;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AmbulanceTrip {
    private String id;
    private String ambulanceId;
    private String driverName;
    private String emergencyCaseId;
    private String patientId;
    private String pickupLocation;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private TripStatus status;
    private String notes;

    public AmbulanceTrip() {
        this.status = TripStatus.ASSIGNED;
        this.startTime = LocalDateTime.now();
    }

    public AmbulanceTrip(String id, String ambulanceId, String driverName,
                         String emergencyCaseId, String patientId, String pickupLocation) {
        this.id = id;
        this.ambulanceId = ambulanceId;
        this.driverName = driverName;
        this.emergencyCaseId = emergencyCaseId;
        this.patientId = patientId;
        this.pickupLocation = pickupLocation;
        this.status = TripStatus.ASSIGNED;
        this.startTime = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAmbulanceId() { return ambulanceId; }
    public void setAmbulanceId(String ambulanceId) { this.ambulanceId = ambulanceId; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public String getEmergencyCaseId() { return emergencyCaseId; }
    public void setEmergencyCaseId(String emergencyCaseId) { this.emergencyCaseId = emergencyCaseId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public TripStatus getStatus() { return status; }
    public void setStatus(TripStatus status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
