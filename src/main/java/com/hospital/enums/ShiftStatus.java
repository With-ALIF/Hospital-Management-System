package com.hospital.enums;

public enum ShiftStatus {
    ASSIGNED,
    ACTIVE,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(ShiftStatus next) {
        if (next == null || next == this) return false;
        return switch (this) {
            case ASSIGNED -> next == ACTIVE || next == CANCELLED;
            case ACTIVE -> next == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
