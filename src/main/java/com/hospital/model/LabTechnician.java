package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hospital.enums.Gender;
import com.hospital.enums.UserRole;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = LabTechnician.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LabTechnician extends Staff {
    private String section;

    public LabTechnician() {
        super();
    }

    public LabTechnician(String id, String name, String phone, String gender) {
        super(id, name, phone, Gender.from(gender), UserRole.LAB_TECHNICIAN);
    }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    @Override
    public void displayInfo() {
        System.out.println("Lab Technician: " + getName() + " | ID: " + getId());
    }
}
