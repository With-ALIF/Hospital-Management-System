package com.hospital.enums;

public enum OperationStatus {
    SCHEDULED,
    PREPARING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(OperationStatus next) {
        if (next == null || next == this) return false;
        return switch (this) {
            case SCHEDULED -> next == PREPARING || next == CANCELLED;
            case PREPARING -> next == IN_PROGRESS || next == CANCELLED;
            case IN_PROGRESS -> next == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
