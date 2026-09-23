package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hospital.enums.Gender;
import com.hospital.enums.UserRole;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Admin.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Admin extends Staff {
    public Admin() {
        super();
    }

    public Admin(String id, String name, String phone, String gender) {
        super(id, name, phone, Gender.from(gender), UserRole.ADMIN);
    }

    @Override
    public void displayInfo() {
        System.out.println("Admin: " + getName() + " | ID: " + getId() + " | Phone: " + getPhone());
    }
}
