package com.hospital.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EmergencyLevel {
    LOW(1),
    MODERATE(2),
    SERIOUS(3),
    CRITICAL(4);

    private final int severity;

    EmergencyLevel(int severity) {
        this.severity = severity;
    }

    public int getSeverity() {
        return severity;
    }

    @JsonCreator
    public static EmergencyLevel from(String value) {
        if (value == null || value.isBlank()) {
            return LOW;
        }
        return switch (value.trim().toUpperCase()) {
            case "CRITICAL" -> CRITICAL;
            case "SERIOUS", "HIGH" -> SERIOUS;
            case "MODERATE", "MEDIUM" -> MODERATE;
            case "LOW" -> LOW;
            default -> throw new IllegalArgumentException("Unknown emergency level: " + value);
        };
    }
}
