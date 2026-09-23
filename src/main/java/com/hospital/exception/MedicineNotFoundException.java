package com.hospital.exception;

public class MedicineNotFoundException extends InvalidDataException {
    public MedicineNotFoundException(String message) {
        super(message);
    }
}
