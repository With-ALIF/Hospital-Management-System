package com.hospital.enums;

public enum TripStatus {
    ASSIGNED,
    EN_ROUTE,
    PICKED_UP,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(TripStatus next) {
        if (next == null || next == this) return false;
        return switch (this) {
            case ASSIGNED -> next == EN_ROUTE || next == CANCELLED;
            case EN_ROUTE -> next == PICKED_UP || next == COMPLETED || next == CANCELLED;
            case PICKED_UP -> next == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
