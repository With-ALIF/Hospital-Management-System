package com.hospital.enums;

public enum LabStatus {
    REQUESTED,
    SAMPLE_COLLECTED,
    PROCESSING,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(LabStatus next) {
        if (next == null || next == this) {
            return false;
        }
        return switch (this) {
            case REQUESTED -> next == SAMPLE_COLLECTED || next == CANCELLED;
            case SAMPLE_COLLECTED -> next == PROCESSING || next == CANCELLED;
            case PROCESSING -> next == COMPLETED || next == CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}
