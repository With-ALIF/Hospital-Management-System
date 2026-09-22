package com.hospital.model;

public enum AppointmentStatus {
    SCHEDULED,
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    NO_SHOW;

    /**
     * Day5 valid transitions:
     * SCHEDULED -> CONFIRMED -> COMPLETED
     * SCHEDULED -> CANCELLED
     * CONFIRMED -> CANCELLED
     * COMPLETED / CANCELLED are terminal.
     * Legacy statuses (IN_PROGRESS, NO_SHOW) kept for Day3 compat.
     */
    public boolean canTransitionTo(AppointmentStatus next) {
        if (next == null || next == this) return false;
        return switch (this) {
            case SCHEDULED -> next == CONFIRMED || next == CANCELLED;
            case CONFIRMED -> next == COMPLETED || next == CANCELLED;
            case IN_PROGRESS -> next == COMPLETED || next == CANCELLED;
            case COMPLETED, CANCELLED, NO_SHOW -> false;
        };
    }
}
