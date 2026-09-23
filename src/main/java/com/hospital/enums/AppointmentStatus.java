package com.hospital.enums;

public enum AppointmentStatus {
    SCHEDULED,
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    NO_SHOW;

    public boolean canTransitionTo(AppointmentStatus next) {
        if (next == null || next == this) {
            return false;
        }
        return switch (this) {
            case SCHEDULED -> next == CONFIRMED || next == CANCELLED || next == COMPLETED || next == NO_SHOW;
            case CONFIRMED -> next == COMPLETED || next == CANCELLED || next == IN_PROGRESS;
            case IN_PROGRESS -> next == COMPLETED || next == CANCELLED;
            case COMPLETED, CANCELLED, NO_SHOW -> false;
        };
    }
}
