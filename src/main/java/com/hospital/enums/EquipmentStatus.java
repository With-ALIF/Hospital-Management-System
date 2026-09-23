package com.hospital.enums;

public enum EquipmentStatus {
    AVAILABLE,
    IN_USE,
    MAINTENANCE,
    DAMAGED,
    RETIRED;

    public boolean assignable() {
        return this == AVAILABLE;
    }
}
