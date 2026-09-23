package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Prescription {
    private String id;
    private String patientId;
    private String doctorId;
    private LocalDate date;
    private String diagnosis;
    private List<PrescriptionItem> items;
    private String instructions;

    public Prescription() {
        this.items = new ArrayList<>();
    }

    public Prescription(String id, String patientId, String doctorId, LocalDate date, String diagnosis) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date != null ? date : LocalDate.now();
        this.diagnosis = diagnosis;
        this.items = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public List<PrescriptionItem> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<PrescriptionItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public void addItem(PrescriptionItem item) {
        if (item != null) {
            getItems().add(item);
        }
    }
}
