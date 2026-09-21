package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Admin extends User {
    public Admin() {
        super();
    }

    public Admin(String id, String name, String phone, String gender) {
        super(id, name, phone, gender, UserRole.ADMIN);
    }

    @Override
    public void displayInfo() {
        System.out.println("Admin: " + getName());
        System.out.println("ID: " + getId());
        System.out.println("Phone: " + getPhone());
        System.out.println("Role: " + getRole());
    }
    
}
