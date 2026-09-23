package com.hospital.exception;

public class ConflictException extends InvalidDataException {
    public ConflictException(String message) {
        super(message);
    }
}
