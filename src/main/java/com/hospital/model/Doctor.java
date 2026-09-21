package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Same optional "type" handling as {@link Patient}, see the comment there.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Doctor.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Doctor extends User {
    private String specialization;
    private boolean available;
    private List<TimeSlot> dutySlots;

    public Doctor() {
        super();
        this.dutySlots = new ArrayList<>();
    }

    public Doctor(String id, String name, String phone, String gender, String specialty){
        super(id, name, phone, gender, UserRole.DOCTOR);
        this.specialization = specialty;
        this.available = true;
        this.dutySlots = new ArrayList<>();
    }

    public String getSpecialization(){
        return specialization;
    }

    public boolean getAvailable(){
        return available;
    }

    public void setSpecialization(String specialization){
        this.specialization = specialization;
    }

    public void setAvailable(boolean available){
        this.available = available;
    }

    public void addDutySlot(TimeSlot slot) {
        if (slot != null && !dutySlots.contains(slot)) {
            dutySlots.add(slot);
        }
    }

    public List<TimeSlot> getDutySlots() {
        return Collections.unmodifiableList(dutySlots);
    }

    /**
     * Used by Jackson when {@code doctors.json} is loaded: the saved duty slots
     * are copied back into the internal list.
     */
    public void setDutySlots(List<TimeSlot> slots) {
        dutySlots.clear();
        if (slots != null) {
            slots.forEach(this::addDutySlot);
        }
    }

    public boolean isAvailableAt(LocalTime time) {
        if (!available) {
            return false;
        }
        if (dutySlots.isEmpty()) {
            return true;
        }
        return dutySlots.stream().anyMatch(slot -> slot.includes(time));
    }

    /** Text used in the tables and messages; the duty slots themselves are saved in the JSON file. */
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
    public void displayInfo(){
        System.out.println("Doctor: " + getName());
        System.out.println("ID: " + getId());
        System.out.println("Specialization: " + specialization);
        System.out.println("Available: " + available);
        System.out.println("Schedule: " + getDutyScheduleString());
    }
}
