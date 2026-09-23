package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.VitalStatus;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VitalRecord {
    private String id;
    private String patientId;
    private LocalDateTime recordedAt;
    private double temperature;
    private int heartRate;
    private String bloodPressure;
    private int spo2;
    private int respiratoryRate;
    private String recordedBy;
    private VitalStatus overallStatus;
    private String notes;

    public VitalRecord() {
        this.recordedAt = LocalDateTime.now();
    }

    public VitalRecord(String id, String patientId, double temperature, int heartRate,
                       String bloodPressure, int spo2, int respiratoryRate, String recordedBy) {
        this.id = id;
        this.patientId = patientId;
        this.temperature = temperature;
        this.heartRate = heartRate;
        this.bloodPressure = bloodPressure;
        this.spo2 = spo2;
        this.respiratoryRate = respiratoryRate;
        this.recordedBy = recordedBy;
        this.recordedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public int getHeartRate() { return heartRate; }
    public void setHeartRate(int heartRate) { this.heartRate = heartRate; }
    public String getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }
    public int getSpo2() { return spo2; }
    public void setSpo2(int spo2) { this.spo2 = spo2; }
    public int getRespiratoryRate() { return respiratoryRate; }
    public void setRespiratoryRate(int respiratoryRate) { this.respiratoryRate = respiratoryRate; }
    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }
    public VitalStatus getOverallStatus() { return overallStatus; }
    public void setOverallStatus(VitalStatus overallStatus) { this.overallStatus = overallStatus; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
