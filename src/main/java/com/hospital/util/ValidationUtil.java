package com.hospital.util;

import com.hospital.exception.InvalidDataException;

import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9\\- ]{7,15}$");
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtil() {
    }

    public static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    public static void requirePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new InvalidDataException(fieldName + " must be greater than zero.");
        }
    }

    public static void requireNonNegative(double value, String fieldName) {
        if (value < 0) {
            throw new InvalidDataException(fieldName + " cannot be negative.");
        }
    }

    public static void validatePhone(String phone, String fieldName) {
        requireNonBlank(phone, fieldName);
        if (!PHONE.matcher(phone.trim()).matches()) {
            throw new InvalidDataException(fieldName + " is invalid: " + phone);
        }
    }

    public static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        if (!EMAIL.matcher(email.trim()).matches()) {
            throw new InvalidDataException("Email is invalid: " + email);
        }
    }

    public static void validateAge(int age) {
        if (age < 0 || age > 150) {
            throw new InvalidDataException("Age must be between 0 and 150.");
        }
    }
}
