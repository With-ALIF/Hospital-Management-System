package com.hospital.enums;

public enum BloodUnitStatus {
    AVAILABLE,
    RESERVED,
    USED,
    EXPIRED;

    public boolean canTransitionTo(BloodUnitStatus next) {
        if (next == null || next == this) return false;
        return switch (this) {
            case AVAILABLE -> next == RESERVED || next == USED || next == EXPIRED;
            case RESERVED -> next == AVAILABLE || next == USED || next == EXPIRED;
            case USED, EXPIRED -> false;
        };
    }
}
