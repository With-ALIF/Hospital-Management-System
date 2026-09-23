package com.hospital.enums;

public enum FollowUpStatus {
    SCHEDULED,
    COMPLETED,
    MISSED,
    CANCELLED;

    public boolean canTransitionTo(FollowUpStatus next) {
        if (next == null || next == this) return false;
        return switch (this) {
            case SCHEDULED -> next == COMPLETED || next == MISSED || next == CANCELLED;
            case COMPLETED, MISSED, CANCELLED -> false;
        };
    }
}
