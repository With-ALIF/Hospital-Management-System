package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hospital.enums.Gender;
import com.hospital.enums.UserRole;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Doctor.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Doctor extends Staff {
    private String specialization;
    private List<TimeSlot> dutySlots;
    private String licenseNumber;
    private double consultationFee;

    public Doctor() {
        super();
        this.dutySlots = new ArrayList<>();
    }

    public Doctor(String id, String name, String phone, String gender, String specialty) {
        super(id, name, phone, Gender.from(gender), UserRole.DOCTOR);
        this.specialization = specialty;
        this.dutySlots = new ArrayList<>();
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public void addDutySlot(TimeSlot slot) {
        if (slot != null && !dutySlots.contains(slot)) {
            dutySlots.add(slot);
        }
    }

    public List<TimeSlot> getDutySlots() {
        return Collections.unmodifiableList(dutySlots);
    }

    public void setDutySlots(List<TimeSlot> slots) {
        dutySlots.clear();
        if (slots != null) {
            slots.forEach(this::addDutySlot);
        }
    }

    public boolean isAvailableAt(LocalTime time) {
        if (!getAvailable()) {
            return false;
        }
        if (dutySlots.isEmpty()) {
            return true;
        }
        return dutySlots.stream().anyMatch(slot -> slot.includes(time));
    }

    @JsonIgnore
    public String getDutyScheduleString() {
        if (dutySlots.isEmpty()) {
            return "Flexible / All Hours";
        }
        return dutySlots.stream().map(TimeSlot::toString).collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return getName() + " (" + specialization + ")";
    }

    @Override
    public void displayInfo() {
        System.out.println("Doctor: " + getName() + " | ID: " + getId()
                + " | " + specialization + " | Available: " + getAvailable());
    }
}
