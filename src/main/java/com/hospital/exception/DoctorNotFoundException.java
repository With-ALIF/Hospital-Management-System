package com.hospital.exception;

public class DoctorNotFoundException extends InvalidDataException {
    public DoctorNotFoundException(String message) {
        super(message);
    }
}
