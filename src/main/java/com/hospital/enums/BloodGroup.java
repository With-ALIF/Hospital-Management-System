package com.hospital.enums;

public enum BloodGroup {
    A_POS, A_NEG, B_POS, B_NEG, AB_POS, AB_NEG, O_POS, O_NEG;

    public boolean compatibleDonorTo(BloodGroup recipient) {
        if (this == recipient) return true;
        return switch (this) {
            case O_NEG -> true;
            case O_POS -> recipient == O_POS;
            case A_NEG -> recipient == A_POS || recipient == AB_POS;
            case A_POS -> recipient == A_POS;
            case B_NEG -> recipient == B_POS || recipient == AB_POS;
            case B_POS -> recipient == B_POS;
            case AB_NEG -> recipient == AB_POS;
            case AB_POS -> recipient == AB_POS;
        };
    }

    public static BloodGroup from(String v) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException("Blood group required.");
        String s = v.trim().toUpperCase().replace("+", "_POS").replace("-", "_NEG");
        return valueOf(s);
    }
}
