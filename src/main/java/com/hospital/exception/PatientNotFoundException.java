package com.hospital.exception;

public class PatientNotFoundException extends InvalidDataException {
    public PatientNotFoundException(String message) {
        super(message);
    }
}
