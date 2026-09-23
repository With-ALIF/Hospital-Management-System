package com.hospital.enums;

public enum AmbulanceStatus {
    AVAILABLE,
    ON_TRIP,
    MAINTENANCE,
    UNAVAILABLE;

    public boolean assignable() {
        return this == AVAILABLE;
    }
}
