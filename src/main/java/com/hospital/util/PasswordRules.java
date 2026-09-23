package com.hospital.util;

import com.hospital.exception.WeakPasswordException;

public final class PasswordRules {
    private PasswordRules() {}

    public static void validate(String password) {
        if (password == null || password.length() < 8) {
            throw new WeakPasswordException(
                    "Password does not meet security requirements.");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new WeakPasswordException(
                    "Password does not meet security requirements.");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new WeakPasswordException(
                    "Password does not meet security requirements.");
        }
        if (!password.matches(".*\\d.*")) {
            throw new WeakPasswordException(
                    "Password does not meet security requirements.");
        }
    }

    public static boolean isValid(String password) {
        try {
            validate(password);
            return true;
        } catch (WeakPasswordException e) {
            return false;
        }
    }
}
