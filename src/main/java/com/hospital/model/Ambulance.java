package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.AmbulanceStatus;
import com.hospital.enums.AmbulanceType;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ambulance {
    private String id;
    private String vehicleNumber;
    private String driverName;
    private String driverPhone;
    private AmbulanceType type;
    private AmbulanceStatus status;
    private String currentLocation;
    private LocalDate lastServiceDate;
    private String assignedTripId;

    public Ambulance() {
        this.status = AmbulanceStatus.AVAILABLE;
        this.type = AmbulanceType.BASIC;
    }

    public Ambulance(String id, String vehicleNumber, String driverName,
                     String driverPhone, AmbulanceType type) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.type = type != null ? type : AmbulanceType.BASIC;
        this.status = AmbulanceStatus.AVAILABLE;
        this.currentLocation = "HOSPITAL";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }
    public AmbulanceType getType() { return type; }
    public void setType(AmbulanceType type) { this.type = type; }
    public AmbulanceStatus getStatus() { return status; }
    public void setStatus(AmbulanceStatus status) { this.status = status; }
    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
    public LocalDate getLastServiceDate() { return lastServiceDate; }
    public void setLastServiceDate(LocalDate lastServiceDate) { this.lastServiceDate = lastServiceDate; }
    public String getAssignedTripId() { return assignedTripId; }
    public void setAssignedTripId(String assignedTripId) { this.assignedTripId = assignedTripId; }
}
