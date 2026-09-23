package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Discharge {
    private String id;
    private String patientId;
    private String admissionId;
    private LocalDate dischargeDate;
    private String diagnosis;
    private String finalNotes;
    private String finalPrescription;
    private LocalDate followUpDate;
    private String billId;

    public Discharge() {
    }

    public Discharge(String id, String patientId, String admissionId, String diagnosis) {
        this.id = id;
        this.patientId = patientId;
        this.admissionId = admissionId;
        this.diagnosis = diagnosis;
        this.dischargeDate = LocalDate.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getAdmissionId() { return admissionId; }
    public void setAdmissionId(String admissionId) { this.admissionId = admissionId; }
    public LocalDate getDischargeDate() { return dischargeDate; }
    public void setDischargeDate(LocalDate dischargeDate) { this.dischargeDate = dischargeDate; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getFinalNotes() { return finalNotes; }
    public void setFinalNotes(String finalNotes) { this.finalNotes = finalNotes; }
    public String getFinalPrescription() { return finalPrescription; }
    public void setFinalPrescription(String finalPrescription) { this.finalPrescription = finalPrescription; }
    public LocalDate getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(LocalDate followUpDate) { this.followUpDate = followUpDate; }
    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }
}
