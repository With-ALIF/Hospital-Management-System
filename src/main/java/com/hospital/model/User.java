package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hospital.enums.Gender;
import com.hospital.enums.UserRole;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Patient.class, name = "PATIENT"),
    @JsonSubTypes.Type(value = Doctor.class, name = "DOCTOR"),
    @JsonSubTypes.Type(value = Nurse.class, name = "NURSE"),
    @JsonSubTypes.Type(value = Receptionist.class, name = "RECEPTIONIST"),
    @JsonSubTypes.Type(value = Pharmacist.class, name = "PHARMACIST"),
    @JsonSubTypes.Type(value = LabTechnician.class, name = "LAB_TECHNICIAN"),
    @JsonSubTypes.Type(value = Admin.class, name = "ADMIN")
})
public abstract class User {
    private String id;
    private String name;
    private String phone;
    private Gender gender;
    private UserRole role;

    protected User() {
    }

    protected User(String id, String name, String phone, Gender gender, UserRole role) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.role = role;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getGenderName() {
        return gender == null ? "—" : gender.name();
    }

    public abstract void displayInfo();
}
