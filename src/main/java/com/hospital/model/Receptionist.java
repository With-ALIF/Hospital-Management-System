package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hospital.enums.Gender;
import com.hospital.enums.UserRole;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Receptionist.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Receptionist extends Staff {
    private String counter;

    public Receptionist() {
        super();
    }

    public Receptionist(String id, String name, String phone, String gender) {
        super(id, name, phone, Gender.from(gender), UserRole.RECEPTIONIST);
    }

    public String getCounter() { return counter; }
    public void setCounter(String counter) { this.counter = counter; }

    @Override
    public void displayInfo() {
        System.out.println("Receptionist: " + getName() + " | ID: " + getId());
    }
}
