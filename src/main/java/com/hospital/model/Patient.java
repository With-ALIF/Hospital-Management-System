package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The parent class User carries the @JsonTypeInfo annotation of Jackson.
 * Repeating that annotation here with defaultImpl makes the "type" property
 * optional: JSON files (also hand written files without a "type" entry) can be
 * loaded back without any extra configuration.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Patient.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient extends User {
    private String bloodGroup;
    private String emergencyContact;

    public Patient() {
        super();
    }
    
    public Patient(String id, String name, String phone, String gender, String bloodGroup, String emergencyContact){
        super(id, name, phone, gender, UserRole.PATIENT);
        this.bloodGroup = bloodGroup;
        this.emergencyContact = emergencyContact;
    }

    public String getBloodGroup(){
        return bloodGroup;
    }

    public String getEmergencyContact(){
        return emergencyContact;
    }

    public void setBloodGroup(String bloodGroup){
        this.bloodGroup = bloodGroup;
    }

    public void setEmergencyContact(String emergencyContact){
        this.emergencyContact = emergencyContact;
    }

    public void displayInfo(){
        System.out.println("Patient: " + getName());
        System.out.println("ID: " + getId());
        System.out.println("Blood Group: " + bloodGroup);
        System.out.println("Emergency Contact: " + emergencyContact);
    }   
    
}
