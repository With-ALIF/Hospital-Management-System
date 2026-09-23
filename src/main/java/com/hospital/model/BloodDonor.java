package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.BloodGroup;
import com.hospital.enums.RhType;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BloodDonor {
    private String id;
    private String name;
    private String phone;
    private String address;
    private BloodGroup bloodGroup;
    private RhType rhType;
    private int age;
    private String gender;
    private LocalDate lastDonationDate;
    private int totalDonations;

    public BloodDonor() {}

    public BloodDonor(String id, String name, String phone, BloodGroup bloodGroup, RhType rhType) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.rhType = rhType;
        this.totalDonations = 0;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public BloodGroup getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }
    public RhType getRhType() { return rhType; }
    public void setRhType(RhType rhType) { this.rhType = rhType; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public LocalDate getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(LocalDate lastDonationDate) { this.lastDonationDate = lastDonationDate; }
    public int getTotalDonations() { return totalDonations; }
    public void setTotalDonations(int totalDonations) { this.totalDonations = totalDonations; }
}
