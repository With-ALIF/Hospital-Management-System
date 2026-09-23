package com.hospital.exception;

public class ExpiredResourceException extends InvalidDataException {
    public ExpiredResourceException(String message) {
        super(message);
    }
}
