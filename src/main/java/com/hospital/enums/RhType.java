package com.hospital.enums;

public enum RhType {
    POSITIVE,
    NEGATIVE;

    public static RhType from(String v) {
        if (v == null || v.isBlank()) return POSITIVE;
        return v.trim().toUpperCase().startsWith("N") ? NEGATIVE : POSITIVE;
    }
}
