package com.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hospital.enums.EquipmentCondition;
import com.hospital.enums.EquipmentStatus;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Equipment {
    private String id;
    private String name;
    private String category;
    private String department;
    private int quantity;
    private EquipmentCondition condition;
    private LocalDate purchaseDate;
    private LocalDate lastMaintenance;
    private LocalDate nextMaintenance;
    private EquipmentStatus status;
    private String assignedTo;
    private int availableQuantity;

    public Equipment() {
        this.status = EquipmentStatus.AVAILABLE;
        this.condition = EquipmentCondition.GOOD;
    }

    public Equipment(String id, String name, String category, String department, int quantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.department = department;
        this.quantity = quantity;
        this.availableQuantity = quantity;
        this.status = EquipmentStatus.AVAILABLE;
        this.condition = EquipmentCondition.GOOD;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public EquipmentCondition getCondition() { return condition; }
    public void setCondition(EquipmentCondition condition) { this.condition = condition; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    public LocalDate getLastMaintenance() { return lastMaintenance; }
    public void setLastMaintenance(LocalDate lastMaintenance) { this.lastMaintenance = lastMaintenance; }
    public LocalDate getNextMaintenance() { return nextMaintenance; }
    public void setNextMaintenance(LocalDate nextMaintenance) { this.nextMaintenance = nextMaintenance; }
    public EquipmentStatus getStatus() { return status; }
    public void setStatus(EquipmentStatus status) { this.status = status; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }
}
