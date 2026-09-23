package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hospital.enums.Gender;
import com.hospital.enums.UserRole;

import java.time.LocalDate;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Doctor.class, name = "DOCTOR"),
    @JsonSubTypes.Type(value = Nurse.class, name = "NURSE"),
    @JsonSubTypes.Type(value = Receptionist.class, name = "RECEPTIONIST"),
    @JsonSubTypes.Type(value = Pharmacist.class, name = "PHARMACIST"),
    @JsonSubTypes.Type(value = LabTechnician.class, name = "LAB_TECHNICIAN"),
    @JsonSubTypes.Type(value = Admin.class, name = "ADMIN")
})
public abstract class Staff extends User {
    private String email;
    private LocalDate joiningDate;
    private String shift;
    private boolean available;

    protected Staff() {
        this.available = true;
    }

    protected Staff(String id, String name, String phone, Gender gender, UserRole role) {
        super(id, name, phone, gender, role);
        this.available = true;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }
    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }
    public boolean isAvailable() { return available; }
    public boolean getAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
