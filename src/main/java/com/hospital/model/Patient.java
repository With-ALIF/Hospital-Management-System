package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hospital.enums.Gender;
import com.hospital.enums.PatientStatus;
import com.hospital.enums.UserRole;

import java.time.LocalDate;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Patient.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient extends User {
    private String bloodGroup;
    private String emergencyContact;
    private int age;
    private String address;
    private PatientStatus status;
    private LocalDate registrationDate;

    public Patient() {
        super();
    }

    public Patient(String id, String name, String phone, String gender, String bloodGroup, String emergencyContact) {
        super(id, name, phone, Gender.from(gender), UserRole.PATIENT);
        this.bloodGroup = bloodGroup;
        this.emergencyContact = emergencyContact;
        this.status = PatientStatus.ACTIVE;
        this.registrationDate = LocalDate.now();
    }

    public Patient(String id, String name, String phone, String gender, String bloodGroup,
                   String emergencyContact, int age, String address) {
        this(id, name, phone, gender, bloodGroup, emergencyContact);
        this.age = age;
        this.address = address;
    }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public PatientStatus getStatus() { return status != null ? status : PatientStatus.ACTIVE; }
    public void setStatus(PatientStatus status) { this.status = status; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    @Override
    public void displayInfo() {
        System.out.println("Patient: " + getName() + " | ID: " + getId()
                + " | Blood: " + bloodGroup + " | Phone: " + getPhone());
    }
}
