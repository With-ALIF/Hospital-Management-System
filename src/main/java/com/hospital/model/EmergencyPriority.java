package com.hospital.model;

/**
 * Severity of an emergency case.
 * The numeric severity allows the emergency service to build a triage queue
 * (CRITICAL cases are treated before LOW ones).
 */
public enum EmergencyPriority {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRITICAL(4);

    private final int severity;

    EmergencyPriority(int severity) {
        this.severity = severity;
    }

    public int getSeverity() {
        return severity;
    }
}