package com.hospital.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    MALE, FEMALE, OTHER;

    @JsonCreator
    public static Gender from(String value) {
        if (value == null || value.isBlank()) {
            return OTHER;
        }
        return switch (value.trim().toUpperCase()) {
            case "MALE", "M" -> MALE;
            case "FEMALE", "F" -> FEMALE;
            default -> OTHER;
        };
    }
}
