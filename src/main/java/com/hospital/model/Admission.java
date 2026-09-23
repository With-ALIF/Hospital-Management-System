package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.AdmissionStatus;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Admission {
    private String id;
    private String patientId;
    private String doctorId;
    private String wardId;
    private String bedId;
    private LocalDate admissionDate;
    private String reason;
    private AdmissionStatus status;

    public Admission() {
        this.status = AdmissionStatus.ADMITTED;
    }

    public Admission(String id, String patientId, String doctorId, String wardId, String bedId, String reason) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.wardId = wardId;
        this.bedId = bedId;
        this.reason = reason;
        this.admissionDate = LocalDate.now();
        this.status = AdmissionStatus.ADMITTED;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public String getWardId() { return wardId; }
    public void setWardId(String wardId) { this.wardId = wardId; }
    public String getBedId() { return bedId; }
    public void setBedId(String bedId) { this.bedId = bedId; }
    public LocalDate getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public AdmissionStatus getStatus() { return status != null ? status : AdmissionStatus.ADMITTED; }
    public void setStatus(AdmissionStatus status) { this.status = status; }
}
