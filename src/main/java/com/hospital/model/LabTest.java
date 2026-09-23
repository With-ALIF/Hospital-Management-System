package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.LabCategory;
import com.hospital.enums.LabStatus;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LabTest {
    private String id;
    private String patientId;
    private String doctorId;
    private String testName;
    private LabCategory category;
    private LocalDate requestedDate;
    private LabStatus status;
    private String result;
    private String technicianId;
    private LocalDate reportDate;
    private double cost;

    public LabTest() {
        this.status = LabStatus.REQUESTED;
    }

    public LabTest(String id, String patientId, String doctorId, String testName,
                   LabCategory category, double cost) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.testName = testName;
        this.category = category;
        this.cost = cost;
        this.status = LabStatus.REQUESTED;
        this.requestedDate = LocalDate.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }
    public LabCategory getCategory() { return category; }
    public void setCategory(LabCategory category) { this.category = category; }
    public LocalDate getRequestedDate() { return requestedDate; }
    public void setRequestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; }
    public LabStatus getStatus() { return status != null ? status : LabStatus.REQUESTED; }
    public void setStatus(LabStatus status) { this.status = status; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public String getTechnicianId() { return technicianId; }
    public void setTechnicianId(String technicianId) { this.technicianId = technicianId; }
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
}
