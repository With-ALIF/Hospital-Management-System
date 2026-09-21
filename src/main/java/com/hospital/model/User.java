package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Patient.class, name = "PATIENT"),
    @JsonSubTypes.Type(value = Doctor.class, name = "DOCTOR"),
    @JsonSubTypes.Type(value = Admin.class, name = "ADMIN")
})
public abstract class User {
    // id and role need setters (Jackson writes them with the getters and must be
    // able to read them back again when a JSON file is loaded).
    private String id;
    private String name;
    private String phone;
    private String gender;
    private UserRole role;

    protected User() {
        this.id = null;
        this.name = null;
        this.phone = null;
        this.gender = null;
        this.role = null;
    }

    protected User(String id, String name, String phone, String gender, UserRole role) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public UserRole getRole() {
        return role;
    }   

    public void setId(String id) {
        this.id = id;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public abstract void displayInfo();
}
