package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hospital.enums.Gender;
import com.hospital.enums.UserRole;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Pharmacist.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pharmacist extends Staff {
    private String licenseNumber;

    public Pharmacist() {
        super();
    }

    public Pharmacist(String id, String name, String phone, String gender) {
        super(id, name, phone, Gender.from(gender), UserRole.PHARMACIST);
    }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    @Override
    public void displayInfo() {
        System.out.println("Pharmacist: " + getName() + " | ID: " + getId());
    }
}
