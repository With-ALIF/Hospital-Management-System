package com.hospital.exception;

public class StaffNotFoundException extends InvalidDataException {
    public StaffNotFoundException(String message) {
        super(message);
    }
}
