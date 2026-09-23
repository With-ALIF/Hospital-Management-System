package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.WardType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ward {
    private String id;
    private String name;
    private WardType type;
    private int capacity;

    public Ward() {
        this.type = WardType.GENERAL;
    }

    public Ward(String id, String name, WardType type, int capacity) {
        this.id = id;
        this.name = name;
        this.type = type != null ? type : WardType.GENERAL;
        this.capacity = capacity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public WardType getType() { return type; }
    public void setType(WardType type) { this.type = type; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}
