package com.hospital.enums;

public enum EmergencyCaseStatus {
    WAITING,
    IN_TREATMENT,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(EmergencyCaseStatus next) {
        if (next == null || next == this) {
            return false;
        }
        return switch (this) {
            case WAITING -> next == IN_TREATMENT || next == CANCELLED;
            case IN_TREATMENT -> next == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
