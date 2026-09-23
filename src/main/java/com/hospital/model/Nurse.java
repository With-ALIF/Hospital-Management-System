package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hospital.enums.UserRole;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Nurse.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Nurse extends Staff {
    private String wardId;
    private String certification;

    public Nurse() {
        super();
    }

    public Nurse(String id, String name, String phone, String gender) {
        super(id, name, phone, com.hospital.enums.Gender.from(gender), UserRole.NURSE);
    }

    public String getWardId() { return wardId; }
    public void setWardId(String wardId) { this.wardId = wardId; }
    public String getCertification() { return certification; }
    public void setCertification(String certification) { this.certification = certification; }

    @Override
    public void displayInfo() {
        System.out.println("Nurse: " + getName() + " | ID: " + getId() + " | Ward: " + wardId);
    }
}
