package com.hospital.exception;

public class AppointmentConflictException extends InvalidDataException {
    public AppointmentConflictException(String message) {
        super(message);
    }
}
